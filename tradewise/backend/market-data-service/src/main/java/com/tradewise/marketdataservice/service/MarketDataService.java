package com.tradewise.marketdataservice.service;

import com.tradewise.marketdataservice.dto.BarDTO;
import com.tradewise.marketdataservice.dto.alphavantage.AlphaVantageResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.tradewise.marketdataservice.dto.alphavantage.AlphaVantageDailyQuote;
import com.tradewise.marketdataservice.dto.alphavantage.AlphaVantageDailyResponse;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class MarketDataService {

    private final RestTemplate restTemplate;

    @Value("${tradewise.app.alphavantage.baseurl}")
    private String baseUrl;

    @Value("${tradewise.app.alphavantage.apikey}")
    private String apiKey;

    public MarketDataService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Fetches the latest quote for a given stock symbol.
     */
    public Optional<BigDecimal> getLatestPrice(String symbol) {
        // 1. Build the URL
        String url = String.format("%s?function=GLOBAL_QUOTE&symbol=%s&apikey=%s",
                baseUrl, symbol, apiKey);

        try {
            // 2. Call the API and map the response to our DTO
            AlphaVantageResponse response = restTemplate.getForObject(url, AlphaVantageResponse.class);

            // 3. Check for valid response
            if (response != null && response.getGlobalQuote() != null) {
                return Optional.of(response.getGlobalQuote().getPrice());
            } else {
                return Optional.empty(); // No quote found
            }
        } catch (Exception e) {
            // Log the error in a real app
            System.err.println("Error fetching market data for " + symbol + ": " + e.getMessage());
            return Optional.empty();
        }
    }

    public List<BarDTO> getHistoricalData(String symbol, LocalDate startDate, LocalDate endDate) {

        // Use "outputsize=full" to get up to 20+ years of data
        String url = String.format("%s?function=TIME_SERIES_DAILY_ADJUSTED&symbol=%s&outputsize=full&apikey=%s",
                baseUrl, symbol, apiKey);

        try {
            AlphaVantageDailyResponse response = restTemplate.getForObject(url, AlphaVantageDailyResponse.class);

            if (response == null || response.getTimeSeries() == null) {
                throw new RuntimeException("No time series data found for " + symbol);
            }

            // 1. Convert the Map to a List of BarDTOs
            List<BarDTO> bars = response.getTimeSeries().entrySet().stream()
                    .map(entry -> {
                        LocalDate date = LocalDate.parse(entry.getKey());
                        AlphaVantageDailyQuote quote = entry.getValue();

                        return new BarDTO(
                                ZonedDateTime.of(date.atTime(16, 0), ZoneId.of("America/New_York")), // Market close
                                new BigDecimal(quote.getOpen()),
                                new BigDecimal(quote.getHigh()),
                                new BigDecimal(quote.getLow()),
                                new BigDecimal(quote.getAdjustedClose()),
                                new BigDecimal(quote.getVolume())
                        );
                    })
                    .collect(Collectors.toList());

            // 2. Sort the bars by date (oldest to newest)
            bars.sort(Comparator.comparing(BarDTO::getEndTime));

            // 3. Filter by our requested date range
            List<BarDTO> filteredBars = bars.stream()
                    .filter(bar -> !bar.getEndTime().toLocalDate().isBefore(startDate) && !bar.getEndTime().toLocalDate().isAfter(endDate))
                    .collect(Collectors.toList());

            if (filteredBars.isEmpty()) {
                throw new RuntimeException("No data found for the specified date range.");
            }

            return filteredBars;

        } catch (Exception e) {
            System.err.println("Error fetching historical data: " + e.getMessage());
            throw new RuntimeException("Could not fetch historical data for " + symbol, e);
        }
    }
}
