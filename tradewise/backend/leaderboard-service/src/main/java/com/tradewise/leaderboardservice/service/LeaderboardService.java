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
import java.util.AbstractMap;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
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

    // Dev-friendly refresh. Change back to 3600000 later if you want hourly updates.
    @Scheduled(fixedRate = 60000)
    public void updateLeaderboard() {
        log.info("Starting leaderboard update...");

        fetchPortfolioIds()
                .flatMap(this::fetchPortfolioSnapshot, 8)
                .collectList()
                .flatMap(this::buildLeaderboard)
                .subscribe(
                        ranked -> log.info("Leaderboard update successful. Cached {} entries.", ranked.size()),
                        error -> log.error("Error updating leaderboard", error)
                );
    }

    private Flux<UUID> fetchPortfolioIds() {
        return webClient.get()
                .uri(portfolioServiceUrl + "/api/portfolios/internal/ids")
                .retrieve()
                .bodyToFlux(UUID.class);
    }

    private Mono<PortfolioSnapshot> fetchPortfolioSnapshot(UUID portfolioId) {
        return Mono.zip(
                        fetchPortfolioDetails(portfolioId),
                        fetchAssetsForPortfolio(portfolioId).collectList()
                )
                .flatMap(tuple -> {
                    PortfolioResponse portfolio = tuple.getT1();
                    List<PortfolioAssetResponse> assets = tuple.getT2();

                    if (assets.isEmpty()) {
                        return Mono.empty();
                    }

                    return Mono.just(new PortfolioSnapshot(portfolio, assets));
                })
                .doOnError(error -> log.warn("Failed to build portfolio snapshot for {}", portfolioId, error))
                .onErrorResume(error -> Mono.empty());
    }

    private Mono<PortfolioResponse> fetchPortfolioDetails(UUID portfolioId) {
        return webClient.get()
                .uri(portfolioServiceUrl + "/api/portfolios/" + portfolioId + "/analytics/internal")
                .retrieve()
                .bodyToMono(PortfolioResponse.class)
                .doOnError(error -> log.warn("Failed to fetch portfolio details for {}", portfolioId, error))
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

    private Mono<List<LeaderboardEntryResponse>> buildLeaderboard(List<PortfolioSnapshot> snapshots) {
        if (snapshots.isEmpty()) {
            this.cachedLeaderboard = List.of();
            return Mono.just(List.of());
        }

        Set<String> symbols = snapshots.stream()
                .flatMap(snapshot -> snapshot.assets().stream())
                .map(PortfolioAssetResponse::getSymbol)
                .collect(Collectors.toSet());

        return fetchCurrentPrices(symbols)
                .map(priceMap -> {
                    List<LeaderboardEntryResponse> entries = snapshots.stream()
                            .map(snapshot -> calculatePortfolioAnalytics(snapshot, priceMap))
                            .sorted(Comparator.comparing(LeaderboardEntryResponse::getTotalReturnPercent).reversed())
                            .collect(Collectors.toList());

                    AtomicInteger rank = new AtomicInteger(1);
                    List<LeaderboardEntryResponse> ranked = entries.stream()
                            .peek(entry -> entry.setRank(rank.getAndIncrement()))
                            .limit(10)
                            .collect(Collectors.toList());

                    this.cachedLeaderboard = List.copyOf(ranked);
                    return ranked;
                });
    }

    private Mono<Map<String, BigDecimal>> fetchCurrentPrices(Set<String> symbols) {
        return Flux.fromIterable(symbols)
                .flatMap(symbol ->
                                fetchCurrentPrice(symbol)
                                        .map(price -> new AbstractMap.SimpleEntry<>(symbol, price)),
                        8
                )
                .collectMap(Map.Entry::getKey, Map.Entry::getValue);
    }

    private LeaderboardEntryResponse calculatePortfolioAnalytics(
            PortfolioSnapshot snapshot,
            Map<String, BigDecimal> currentPrices
    ) {
        List<PortfolioAssetResponse> assets = snapshot.assets();

        BigDecimal totalPurchaseCost = assets.stream()
                .map(asset -> asset.getPurchasePrice().multiply(asset.getQuantity()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalPurchaseCost.compareTo(BigDecimal.ZERO) == 0) {
            return new LeaderboardEntryResponse(
                    0,
                    snapshot.portfolio().getName(),
                    snapshot.portfolio().getUserEmail(),
                    BigDecimal.ZERO
            );
        }

        BigDecimal totalMarketValue = assets.stream()
                .map(asset -> currentPrices
                        .getOrDefault(asset.getSymbol(), asset.getPurchasePrice())
                        .multiply(asset.getQuantity()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalReturnPercent = totalMarketValue.subtract(totalPurchaseCost)
                .divide(totalPurchaseCost, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));

        return new LeaderboardEntryResponse(
                0,
                snapshot.portfolio().getName(),
                snapshot.portfolio().getUserEmail(),
                totalReturnPercent
        );
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

    private record PortfolioSnapshot(
            PortfolioResponse portfolio,
            List<PortfolioAssetResponse> assets
    ) {}
}