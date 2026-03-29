package com.tradewise.userservice.controller;

import com.tradewise.userservice.dto.response.DashboardAnalyticsResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final RestTemplate restTemplate;

    @Value("${portfolio.service.url}")
    private String portfolioServiceUrl;

    @Value("${strategy.service.url}")
    private String strategyServiceUrl;

    public DashboardController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping("/analytics")
    public ResponseEntity<DashboardAnalyticsResponse> getDashboardAnalytics(Authentication authentication) {
        String userEmail = authentication.getName();

        int totalPortfolios = 0;
        int activeStrategies = 0;

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-User-Email", userEmail);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                    portfolioServiceUrl + "/api/portfolios",
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<List<Map<String, Object>>>() {}
            );

            if (response.getBody() != null) {
                totalPortfolios = response.getBody().size();
            }
        } catch (Exception e) {
            System.err.println("Error fetching portfolios: " + e.getMessage());
        }

        try {
            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                    strategyServiceUrl + "/api/strategies",
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<List<Map<String, Object>>>() {}
            );

            if (response.getBody() != null) {
                activeStrategies = response.getBody().size();
            }
        } catch (Exception e) {
            System.err.println("Error fetching strategies: " + e.getMessage());
        }

        List<DashboardAnalyticsResponse.PortfolioGrowthPoint> growth = new ArrayList<>();
        growth.add(new DashboardAnalyticsResponse.PortfolioGrowthPoint("2023-01-01", 10000));
        growth.add(new DashboardAnalyticsResponse.PortfolioGrowthPoint("2023-02-01", 10500));
        growth.add(new DashboardAnalyticsResponse.PortfolioGrowthPoint("2023-03-01", 11000));

        DashboardAnalyticsResponse response = new DashboardAnalyticsResponse(
                0.00,
                0.0,
                activeStrategies,
                0,
                totalPortfolios,
                0,
                growth
        );

        return ResponseEntity.ok(response);
    }
}