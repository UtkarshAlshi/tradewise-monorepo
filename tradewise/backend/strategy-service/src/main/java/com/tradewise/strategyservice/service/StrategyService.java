package com.tradewise.strategyservice.service;

import com.tradewise.strategyservice.dto.strategy.CreateStrategyRequest;
import com.tradewise.strategyservice.dto.response.StrategyResponse;
import com.tradewise.strategyservice.model.Strategy;
import com.tradewise.strategyservice.repository.StrategyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID; // Make sure this is imported
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StrategyService {

    private final StrategyRepository strategyRepository;

    @Transactional
    public Strategy createStrategy(CreateStrategyRequest request, String userEmail) {
        Strategy strategy = new Strategy();
        strategy.setUserEmail(userEmail);
        strategy.setName(request.getName());
        strategy.setDescription(request.getDescription());
        // The logic to handle rules and conditions will be added later
        return strategyRepository.save(strategy);
    }

    @Transactional(readOnly = true)
    public List<StrategyResponse> getStrategiesByUser(String userEmail) {
        List<Strategy> strategies = strategyRepository.findByUserEmail(userEmail);
        return strategies.stream()
                .map(strategy -> new StrategyResponse(
                        strategy.getId(),
                        strategy.getName(),
                        strategy.getDescription(),
                        strategy.getCreatedAt(),
                        strategy.getUserEmail()
                ))
                .collect(Collectors.toList());
    }

    // --- ADD THIS NEW METHOD ---
    @Transactional(readOnly = true)
    public Strategy getStrategyById(UUID strategyId, String userEmail) {
        Strategy strategy = strategyRepository.findById(strategyId)
                .orElseThrow(() -> new RuntimeException("Strategy not found"));

        // Security check
        if (!strategy.getUserEmail().equals(userEmail)) {
            throw new RuntimeException("Access Denied");
        }
        
        // Force-load the lazy-loaded collections by accessing them
        // Assuming getRules() and getConditions() exist and are lazy-loaded
        if (strategy.getRules() != null) {
            strategy.getRules().forEach(rule -> {
                if (rule.getConditions() != null) {
                    rule.getConditions().size();
                }
            });
        }
        
        return strategy;
    }
}
