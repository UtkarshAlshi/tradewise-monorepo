package com.tradewise.leaderboardservice.service;

import com.tradewise.leaderboardservice.dto.LeaderboardEntryResponse;
import com.tradewise.leaderboardservice.dto.PortfolioAssetResponse;
import com.tradewise.leaderboardservice.dto.PortfolioResponse;
import jakarta.annotation.PostConstruct;
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
    private volatile List<LeaderboardEntryResponse> cachedLeaderboard = List.of();

    @Value("${portfolio.service.url}")
    private String portfolioServiceUrl;

    @Value("${market-data.service.url}")
    private String marketDataServiceUrl;

    public LeaderboardService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    public List<LeaderboardEntryResponse> getLeaderboard() {
        return List.copyOf(cachedLeaderboard);
    }

    @PostConstruct
    public void initializeLeaderboard() {
        updateLeaderboard();
    }

    @Scheduled(fixedRate = 3600000)
    public void updateLeaderboard() {
        log.info("Starting leaderboard update...");

        fetchPortfolioIds()
                .flatMap(this::fetchPortfolioDetails)
                .flatMap(this::calculatePortfolioAnalytics
                        , 8)
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
                .bodyToMono(PortfolioResponse.class)
                .doOnError(error -> log.warn("Failed to fetch portfolio details for {}", portfolioId, error))
                .onErrorResume(error -> Mono.empty());
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
                })
                .doOnError(error -> log.warn("Failed to calculate analytics for portfolio {}", portfolio.getId(), error))
                .onErrorResume(error -> Mono.empty());
    }

    private Flux<PortfolioAssetResponse> fetchAssetsForPortfolio(UUID portfolioId) {
        return webClient.get()
                .uri(portfolioServiceUrl + "/api/portfolios/" + portfolioId + "/assets/internal")
                .retrieve()
                .bodyToFlux(PortfolioAssetResponse.class)
                .doOnError(error -> log.warn("Failed to fetch assets for portfolio {}", portfolioId, error))
                .onErrorResume(error -> Flux.empty());
    }

    private Mono<BigDecimal> calculateTotalReturn(List<PortfolioAssetResponse> assets) {
        ConcurrentHashMap<String, BigDecimal> currentPrices = new ConcurrentHashMap<>();
        BigDecimal totalPurchaseCost = assets.stream()
                .map(asset -> asset.getPurchasePrice().multiply(asset.getQuantity()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalPurchaseCost.compareTo(BigDecimal.ZERO) == 0) {
            return Mono.just(BigDecimal.ZERO);
        }

        BigDecimal finalTotalPurchaseCost = totalPurchaseCost;

        return Flux.fromIterable(assets)
                .flatMap(asset ->
                        fetchCurrentPrice(asset.getSymbol())
                                .defaultIfEmpty(asset.getPurchasePrice())
                                .map(price -> {
                                    currentPrices.put(asset.getSymbol(), price);
                                    return price;
                                })
                )
                .then(Mono.fromCallable(() -> {
                    BigDecimal totalMarketValue = assets.stream()
                            .map(asset -> currentPrices
                                    .getOrDefault(asset.getSymbol(), asset.getPurchasePrice())
                                    .multiply(asset.getQuantity()))
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    return totalMarketValue.subtract(finalTotalPurchaseCost)
                            .divide(finalTotalPurchaseCost, 4, RoundingMode.HALF_UP)
                            .multiply(BigDecimal.valueOf(100));
                }));
    }

    private Mono<BigDecimal> fetchCurrentPrice(String symbol) {
        return webClient.get()
                .uri(marketDataServiceUrl + "/api/market-data/" + symbol + "/internal")
                .retrieve()
                .bodyToMono(String.class)
                .map(BigDecimal::new)
                .doOnError(error -> log.warn("Failed to fetch current price for symbol {}", symbol, error))
                .onErrorResume(error -> Mono.empty());
    }

    private List<LeaderboardEntryResponse> rankAndCache(List<LeaderboardEntryResponse> entries) {
        entries.sort(Comparator.comparing(LeaderboardEntryResponse::getTotalReturnPercent).reversed());

        AtomicInteger rank = new AtomicInteger(1);
        List<LeaderboardEntryResponse> ranked = entries.stream()
                .peek(entry -> entry.setRank(rank.getAndIncrement()))
                .limit(10)
                .collect(Collectors.toList());

        this.cachedLeaderboard = List.copyOf(ranked);
        return ranked;
    }
}