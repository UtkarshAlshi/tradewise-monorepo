package com.tradewise.userservice.controller;

import com.tradewise.userservice.dto.response.DashboardAnalyticsResponse;
import org.springframework.beans.factory.annotation.Autowired;
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

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final RestTemplate restTemplate;

    @Autowired
    public DashboardController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping("/analytics")
    public ResponseEntity<DashboardAnalyticsResponse> getDashboardAnalytics(Authentication authentication) {
        String userEmail = authentication.getName();
        
        // 1. Fetch Portfolios Count
        int totalPortfolios = 0;
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-User-Email", userEmail);
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            // Assuming portfolio-service is reachable via service name in Docker network
            // Default port for Spring Boot is 8080
            ResponseEntity<List> response = restTemplate.exchange(
                "http://portfolio-service:8080/api/portfolios", 
                HttpMethod.GET, 
                entity, 
                List.class
            );
            
            if (response.getBody() != null) {
                totalPortfolios = response.getBody().size();
            }
        } catch (Exception e) {
            // Log error but don't fail the whole dashboard
            System.err.println("Error fetching portfolios: " + e.getMessage());
        }

        // 2. Fetch Strategies Count
        int activeStrategies = 0;
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-User-Email", userEmail);
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            ResponseEntity<List> response = restTemplate.exchange(
                "http://strategy-service:8080/api/strategies", 
                HttpMethod.GET, 
                entity, 
                List.class
            );
            
            if (response.getBody() != null) {
                activeStrategies = response.getBody().size();
            }
        } catch (Exception e) {
            System.err.println("Error fetching strategies: " + e.getMessage());
        }

        // 3. Mock Data for other fields (until other services are ready)
        List<DashboardAnalyticsResponse.PortfolioGrowthPoint> growth = new ArrayList<>();
        growth.add(new DashboardAnalyticsResponse.PortfolioGrowthPoint("2023-01-01", 10000));
        growth.add(new DashboardAnalyticsResponse.PortfolioGrowthPoint("2023-02-01", 10500));
        growth.add(new DashboardAnalyticsResponse.PortfolioGrowthPoint("2023-03-01", 11000));

        DashboardAnalyticsResponse response = new DashboardAnalyticsResponse(
                0.00, // Total Balance (Placeholder)
                0.0,  // Balance Change (Placeholder)
                activeStrategies,
                0,    // Paused Strategies (Placeholder)
                totalPortfolios,
                0,    // Notifications (Placeholder)
                growth
        );

        return ResponseEntity.ok(response);
    }
}
