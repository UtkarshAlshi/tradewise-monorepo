package com.tradewise.userservice.controller;

import com.tradewise.userservice.dto.response.DashboardAnalyticsResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @GetMapping("/analytics")
    public ResponseEntity<DashboardAnalyticsResponse> getDashboardAnalytics() {
        // Mock data for now
        List<DashboardAnalyticsResponse.PortfolioGrowthPoint> growth = new ArrayList<>();
        growth.add(new DashboardAnalyticsResponse.PortfolioGrowthPoint("2023-01-01", 10000));
        growth.add(new DashboardAnalyticsResponse.PortfolioGrowthPoint("2023-02-01", 10500));
        growth.add(new DashboardAnalyticsResponse.PortfolioGrowthPoint("2023-03-01", 11000));

        DashboardAnalyticsResponse response = new DashboardAnalyticsResponse(
                25000.00,
                5.2,
                3,
                1,
                2,
                4,
                growth
        );

        return ResponseEntity.ok(response);
    }
}
