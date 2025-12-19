package com.tradewise.backtestingservice.controller;

import com.tradewise.backtestingservice.dto.BacktestRequest;
import com.tradewise.backtestingservice.dto.response.BacktestReportResponse;
import com.tradewise.backtestingservice.service.BacktestingService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
// import org.springframework.security.core.annotation.AuthenticationPrincipal;
// import org.springframework.security.core.userdetails.UserDetails;

@RestController
@RequestMapping("/api/backtest")
public class BacktestingController {

    private final BacktestingService backtestingService;

    public BacktestingController(BacktestingService backtestingService) {
        this.backtestingService = backtestingService;
    }

    @PostMapping
    public ResponseEntity<BacktestReportResponse> runBacktest(
            @Valid @RequestBody BacktestRequest request,
            @RequestHeader("X-User-Email") String userEmail){ // Changed from @AuthenticationPrincipal UserDetails

        try {
            BacktestReportResponse report = backtestingService.runBacktest(request, userEmail);
             return ResponseEntity.ok(report);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    BacktestReportResponse.builder()
                            .strategyName("Error")
                            .symbol(e.getMessage())
                            .build()
            );
        }
    }
}
