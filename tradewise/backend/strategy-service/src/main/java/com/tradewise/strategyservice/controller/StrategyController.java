package com.tradewise.strategyservice.controller;

import com.tradewise.strategyservice.dto.response.StrategyResponse;
import com.tradewise.strategyservice.dto.strategy.CreateStrategyRequest;
import com.tradewise.strategyservice.model.Strategy;
import com.tradewise.strategyservice.service.StrategyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID; // <-- ADD
import org.springframework.web.bind.annotation.PathVariable; // <-- ADD

@RestController
@RequestMapping("/api/strategies")
@RequiredArgsConstructor
public class StrategyController {

    private final StrategyService strategyService;

    @PostMapping
    public ResponseEntity<StrategyResponse> createStrategy(
            @Valid @RequestBody CreateStrategyRequest request,
            @RequestHeader("X-User-Email") String userEmail) {

        Strategy savedStrategy = strategyService.createStrategy(request, userEmail);

        StrategyResponse response = new StrategyResponse(
                savedStrategy.getId(),
                savedStrategy.getName(),
                savedStrategy.getDescription(),
                savedStrategy.getCreatedAt(),
                savedStrategy.getUserEmail()
        );

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<StrategyResponse>> getStrategies(
            @RequestHeader("X-User-Email") String userEmail) {
        List<StrategyResponse> strategies = strategyService.getStrategiesByUser(userEmail);
        return ResponseEntity.ok(strategies);
    }

    // --- ADD THIS NEW ENDPOINT ---
    @GetMapping("/{id}/internal")
    public ResponseEntity<Strategy> getStrategyForBacktest(
            @PathVariable UUID id,
            @RequestHeader("X-User-Email") String userEmail) {
        
        // Note: We are returning the FULL entity here, not a DTO.
        // This is generally OK for a trusted, internal service call.
        Strategy strategy = strategyService.getStrategyById(id, userEmail);
        return ResponseEntity.ok(strategy);
    }
}
