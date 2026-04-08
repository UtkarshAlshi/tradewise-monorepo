package com.tradewise.marketdataservice.controller;

import com.tradewise.marketdataservice.dto.BarDTO;
import com.tradewise.marketdataservice.service.MarketDataService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/market-data")
public class MarketDataController {

    private final MarketDataService marketDataService;

    public MarketDataController(MarketDataService marketDataService) {
        this.marketDataService = marketDataService;
    }

    @GetMapping("/{symbol}")
    public ResponseEntity<String> getMarketData(
            @PathVariable String symbol,
            @RequestHeader("X-User-Email") String userEmail) {

        Optional<BigDecimal> price = marketDataService.getLatestPrice(symbol.toUpperCase());

        return price.map(bigDecimal -> ResponseEntity.ok(bigDecimal.toString()))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{symbol}/internal")
    public ResponseEntity<String> getMarketDataInternal(@PathVariable String symbol) {
        Optional<BigDecimal> price = marketDataService.getLatestPrice(symbol.toUpperCase());

        return price.map(bigDecimal -> ResponseEntity.ok(bigDecimal.toString()))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/history/internal")
    public ResponseEntity<List<BarDTO>> getHistoricalData(
            @RequestParam String symbol,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate,
            @RequestHeader("X-User-Email") String userEmail) {

        List<BarDTO> series = marketDataService.getHistoricalData(symbol.toUpperCase(), startDate, endDate);
        return ResponseEntity.ok(series);
    }
}