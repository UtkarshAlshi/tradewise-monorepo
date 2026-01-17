package com.tradewise.leaderboardservice.service;

import com.tradewise.leaderboardservice.dto.LeaderboardEntryResponse;
import com.tradewise.leaderboardservice.dto.PortfolioAssetResponse;
import com.tradewise.leaderboardservice.dto.PortfolioResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@Slf4j
public class LeaderboardService {

    private final WebClient webClient;
    private volatile List<LeaderboardEntryResponse> cachedLeaderboard = new ArrayList<>();

    @Value("${portfolio.service.url}")
    private String portfolioServiceUrl;

    @Value("${market-data.service.url}")
    private String marketDataServiceUrl;

    public LeaderboardService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    public List<LeaderboardEntryResponse> getLeaderboard() {
        return cachedLeaderboard;
    }

    @Scheduled(fixedRate = 3600000) // Run every hour
    public void updateLeaderboard() {
        log.info("Starting leaderboard update...");

        fetchPortfolioIds()
                .flatMap(this::fetchPortfolioDetails)
                .flatMap(this::calculatePortfolioAnalytics)
                .collectList()
                .map(this::rankAndCache)
                .subscribe(
                        result -> log.info("Leaderboard update successful. Cached {} entries.", result.size()),
                        error -> log.error("Error updating leaderboard", error)
                );
    }

    private Flux<UUID> fetchPortfolioIds() {
        return webClient.get()
                .uri(portfolioServiceUrl + "/api/portfolios/internal/ids")
                .retrieve()
                .bodyToFlux(UUID.class);
    }

    private Mono<PortfolioResponse> fetchPortfolioDetails(UUID portfolioId) {
        return webClient.get()
                .uri(portfolioServiceUrl + "/api/portfolios/" + portfolioId + "/analytics/internal")
                .retrieve()
                .bodyToMono(PortfolioResponse.class);
    }

    private Mono<LeaderboardEntryResponse> calculatePortfolioAnalytics(PortfolioResponse portfolio) {
        return fetchAssetsForPortfolio(portfolio.getId())
                .collectList()
                .flatMap(assets -> {
                    if (assets.isEmpty()) {
                        return Mono.empty();
                    }

                    return calculateTotalReturn(assets)
                            .map(totalReturn -> new LeaderboardEntryResponse(
                                    0,
                                    portfolio.getName(),
                                    portfolio.getUserEmail(),
                                    totalReturn
                            ));
                });
    }

    private Flux<PortfolioAssetResponse> fetchAssetsForPortfolio(UUID portfolioId) {
        return webClient.get()
                .uri(portfolioServiceUrl + "/api/portfolios/" + portfolioId + "/assets")
                .header("X-User-Email", "leaderboard-service") // Dummy header
                .retrieve()
                .bodyToFlux(PortfolioAssetResponse.class);
    }

    private Mono<BigDecimal> calculateTotalReturn(List<PortfolioAssetResponse> assets) {
        ConcurrentHashMap<String, BigDecimal> currentPrices = new ConcurrentHashMap<>();
        BigDecimal totalPurchaseCost = BigDecimal.ZERO;

        for (PortfolioAssetResponse asset : assets) {
            totalPurchaseCost = totalPurchaseCost.add(asset.getPurchasePrice().multiply(asset.getQuantity()));
        }

        final BigDecimal finalTotalPurchaseCost = totalPurchaseCost;

        return Flux.fromIterable(assets)
                .flatMap(asset -> fetchCurrentPrice(asset.getSymbol())
                        .doOnNext(price -> currentPrices.put(asset.getSymbol(), price)))
                .then(Mono.fromCallable(() -> {
                    BigDecimal totalMarketValue = assets.stream()
                            .map(asset -> currentPrices.getOrDefault(asset.getSymbol(), asset.getPurchasePrice())
                                    .multiply(asset.getQuantity()))
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    if (finalTotalPurchaseCost.compareTo(BigDecimal.ZERO) == 0) {
                        return BigDecimal.ZERO;
                    }

                    return totalMarketValue.subtract(finalTotalPurchaseCost)
                            .divide(finalTotalPurchaseCost, 4, RoundingMode.HALF_UP)
                            .multiply(BigDecimal.valueOf(100));
                }));
    }

    private Mono<BigDecimal> fetchCurrentPrice(String symbol) {
        return webClient.get()
                .uri(marketDataServiceUrl + "/api/market-data/" + symbol)
                .header("X-User-Email", "leaderboard-service") // Dummy header
                .retrieve()
                .bodyToMono(String.class)
                .map(BigDecimal::new)
                .onErrorResume(e -> Mono.empty()); // Ignore assets with no current price
    }

    private List<LeaderboardEntryResponse> rankAndCache(List<LeaderboardEntryResponse> entries) {
        entries.sort(Comparator.comparing(LeaderboardEntryResponse::getTotalReturnPercent).reversed());

        AtomicInteger rank = new AtomicInteger(1);
        List<LeaderboardEntryResponse> top10 = entries.stream()
                .peek(entry -> entry.setRank(rank.getAndIncrement()))
                .limit(10)
                .collect(Collectors.toList());

        this.cachedLeaderboard = top10;
        return top10;
    }
}
