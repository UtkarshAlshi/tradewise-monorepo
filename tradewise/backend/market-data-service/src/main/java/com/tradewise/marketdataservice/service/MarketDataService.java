package com.tradewise.marketdataservice.service;

import com.tradewise.marketdataservice.dto.BarDTO;
import com.tradewise.marketdataservice.dto.alphavantage.AlphaVantageDailyQuote;
import com.tradewise.marketdataservice.dto.alphavantage.AlphaVantageDailyResponse;
import com.tradewise.marketdataservice.dto.alphavantage.AlphaVantageResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MarketDataService {

    private static final Logger logger = LoggerFactory.getLogger(MarketDataService.class);

    private final RestTemplate restTemplate;

    @Value("${tradewise.app.alphavantage.baseurl}")
    private String baseUrl;

    @Value("${tradewise.app.alphavantage.apikey}")
    private String apiKey;

    public MarketDataService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Optional<BigDecimal> getLatestPrice(String symbol) {
        String normalizedSymbol = symbol.toUpperCase();
        String url = String.format("%s?function=GLOBAL_QUOTE&symbol=%s&apikey=%s",
                baseUrl, normalizedSymbol, apiKey);

        try {
            AlphaVantageResponse response = restTemplate.getForObject(url, AlphaVantageResponse.class);

            if (response == null || response.getGlobalQuote() == null || response.getGlobalQuote().getPrice() == null) {
                logger.warn("No latest price found for symbol {}", normalizedSymbol);
                return Optional.empty();
            }

            return Optional.of(response.getGlobalQuote().getPrice());
        } catch (Exception e) {
            logger.error("Error fetching latest price for {}: {}", normalizedSymbol, e.getMessage());
            return Optional.empty();
        }
    }

    public List<BarDTO> getHistoricalData(String symbol, LocalDate startDate, LocalDate endDate) {
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("startDate cannot be after endDate");
        }

        String normalizedSymbol = symbol.toUpperCase();
        String url = String.format("%s?function=TIME_SERIES_DAILY&symbol=%s&outputsize=full&apikey=%s",
                baseUrl, normalizedSymbol, apiKey);

        logger.info("Fetching historical data for {}", normalizedSymbol);

        try {
            AlphaVantageDailyResponse response = restTemplate.getForObject(url, AlphaVantageDailyResponse.class);

            if (response == null || response.getTimeSeries() == null || response.getTimeSeries().isEmpty()) {
                logger.error("No time series data found for {}", normalizedSymbol);
                throw new RuntimeException("No time series data found for " + normalizedSymbol);
            }

            List<BarDTO> filteredBars = response.getTimeSeries().entrySet().stream()
                    .map(entry -> {
                        LocalDate date = LocalDate.parse(entry.getKey());
                        AlphaVantageDailyQuote quote = entry.getValue();

                        return new BarDTO(
                                ZonedDateTime.of(date.atTime(16, 0), ZoneId.of("America/New_York")),
                                parseDecimal(quote.getOpen()),
                                parseDecimal(quote.getHigh()),
                                parseDecimal(quote.getLow()),
                                parseDecimal(quote.getClose()),
                                parseDecimal(quote.getVolume())
                        );
                    })
                    .sorted(Comparator.comparing(BarDTO::getEndTime))
                    .filter(bar -> !bar.getEndTime().toLocalDate().isBefore(startDate)
                            && !bar.getEndTime().toLocalDate().isAfter(endDate))
                    .collect(Collectors.toList());

            if (filteredBars.isEmpty()) {
                logger.warn("No data found for {} in range {} to {}", normalizedSymbol, startDate, endDate);
                throw new RuntimeException("No data found for the specified date range.");
            }

            logger.info("Returning {} filtered bars for {}", filteredBars.size(), normalizedSymbol);
            return filteredBars;

        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error fetching historical data for {}: {}", normalizedSymbol, e.getMessage());
            throw new RuntimeException("Could not fetch historical data for " + normalizedSymbol, e);
        }
    }

    private BigDecimal parseDecimal(String value) {
        if (value == null || value.isBlank()) {
            return BigDecimal.ZERO;
        }
        return new BigDecimal(value);
    }
}