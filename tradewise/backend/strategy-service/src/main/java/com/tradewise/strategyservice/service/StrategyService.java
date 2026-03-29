package com.tradewise.strategyservice.service;

import com.tradewise.strategyservice.dto.internal.InternalStrategyConditionResponse;
import com.tradewise.strategyservice.dto.internal.InternalStrategyResponse;
import com.tradewise.strategyservice.dto.internal.InternalStrategyRuleResponse;
import com.tradewise.strategyservice.dto.response.StrategyResponse;
import com.tradewise.strategyservice.dto.strategy.CreateStrategyRequest;
import com.tradewise.strategyservice.dto.strategy.StrategyConditionDTO;
import com.tradewise.strategyservice.dto.strategy.StrategyRuleDTO;
import com.tradewise.strategyservice.model.Strategy;
import com.tradewise.strategyservice.model.StrategyCondition;
import com.tradewise.strategyservice.model.StrategyRule;
import com.tradewise.strategyservice.repository.StrategyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StrategyService {

    private static final Set<String> SUPPORTED_INDICATORS = Set.of("PRICE", "SMA", "EMA", "RSI");
    private static final Set<String> SUPPORTED_ACTIONS = Set.of("BUY", "SELL");
    private static final Set<String> SUPPORTED_INDICATOR_B_TYPES = Set.of("VALUE", "INDICATOR");
    private static final Set<String> SUPPORTED_OPERATORS = Set.of(
            "GREATER_THAN", "LESS_THAN", "CROSSES_ABOVE", "CROSSES_BELOW"
    );

    private final StrategyRepository strategyRepository;

    @Transactional
    public Strategy createStrategy(CreateStrategyRequest request, String userEmail) {
        Strategy strategy = new Strategy();
        strategy.setUserEmail(userEmail);
        strategy.setName(request.getName().trim());
        strategy.setDescription(request.getDescription());

        List<StrategyRule> rules = new ArrayList<>();
        if (request.getRules() != null) {
            for (StrategyRuleDTO ruleDTO : request.getRules()) {
                StrategyRule rule = mapRule(ruleDTO, strategy);
                rules.add(rule);
            }
        }

        strategy.setRules(rules);
        return strategyRepository.save(strategy);
    }

    @Transactional(readOnly = true)
    public List<StrategyResponse> getStrategiesByUser(String userEmail) {
        return strategyRepository.findByUserEmail(userEmail)
                .stream()
                .map(strategy -> new StrategyResponse(
                        strategy.getId(),
                        strategy.getName(),
                        strategy.getDescription(),
                        strategy.getCreatedAt(),
                        strategy.getUserEmail()
                ))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public InternalStrategyResponse getStrategyById(UUID strategyId, String userEmail) {
        Strategy strategy = strategyRepository.findById(strategyId)
                .orElseThrow(() -> new RuntimeException("Strategy not found"));

        if (!strategy.getUserEmail().equals(userEmail)) {
            throw new RuntimeException("Access Denied");
        }

        return toInternalResponse(strategy);
    }

    private StrategyRule mapRule(StrategyRuleDTO dto, Strategy strategy) {
        String action = normalizeUpper(dto.getAction(), "Rule action");
        if (!SUPPORTED_ACTIONS.contains(action)) {
            throw new IllegalArgumentException("Unsupported rule action: " + action);
        }

        StrategyRule rule = new StrategyRule();
        rule.setStrategy(strategy);
        rule.setAction(action);

        List<StrategyCondition> conditions = new ArrayList<>();
        if (dto.getConditions() == null || dto.getConditions().isEmpty()) {
            throw new IllegalArgumentException("Each rule must have at least one condition");
        }

        for (StrategyConditionDTO conditionDTO : dto.getConditions()) {
            conditions.add(mapCondition(conditionDTO, rule));
        }

        rule.setConditions(conditions);
        return rule;
    }

    private StrategyCondition mapCondition(StrategyConditionDTO dto, StrategyRule rule) {
        StrategyCondition condition = new StrategyCondition();
        condition.setRule(rule);

        String indicatorA = normalizeUpper(dto.getIndicatorA(), "indicatorA");
        validateIndicator(indicatorA, "indicatorA");
        condition.setIndicatorA(indicatorA);

        Map<String, String> indicatorAParams = safeMap(dto.getIndicatorAParams());
        validateIndicatorParams(indicatorA, indicatorAParams, "indicatorAParams");
        condition.setIndicatorAParams(indicatorAParams);

        String operator = normalizeUpper(dto.getOperator(), "operator");
        if (!SUPPORTED_OPERATORS.contains(operator)) {
            throw new IllegalArgumentException("Unsupported operator: " + operator);
        }
        condition.setOperator(operator);

        String indicatorBType = normalizeUpper(dto.getIndicatorBType(), "indicatorBType");
        if (!SUPPORTED_INDICATOR_B_TYPES.contains(indicatorBType)) {
            throw new IllegalArgumentException("Unsupported indicatorBType: " + indicatorBType);
        }
        condition.setIndicatorBType(indicatorBType);

        if ("VALUE".equals(indicatorBType)) {
            validateNumeric(dto.getIndicatorBValue(), "indicatorBValue");
            condition.setIndicatorBValue(dto.getIndicatorBValue().trim());
            condition.setIndicatorBParams(new HashMap<>());

            if (!Set.of("GREATER_THAN", "LESS_THAN").contains(operator)) {
                throw new IllegalArgumentException("VALUE comparisons only support GREATER_THAN or LESS_THAN");
            }
        } else {
            String indicatorB = normalizeUpper(dto.getIndicatorBValue(), "indicatorBValue");
            validateIndicator(indicatorB, "indicatorBValue");
            condition.setIndicatorBValue(indicatorB);

            Map<String, String> indicatorBParams = safeMap(dto.getIndicatorBParams());
            validateIndicatorParams(indicatorB, indicatorBParams, "indicatorBParams");
            condition.setIndicatorBParams(indicatorBParams);
        }

        return condition;
    }

    private InternalStrategyResponse toInternalResponse(Strategy strategy) {
        List<InternalStrategyRuleResponse> rules = strategy.getRules()
                .stream()
                .map(rule -> new InternalStrategyRuleResponse(
                        rule.getId(),
                        rule.getAction(),
                        rule.getConditions().stream()
                                .map(condition -> new InternalStrategyConditionResponse(
                                        condition.getId(),
                                        condition.getIndicatorA(),
                                        safeMap(condition.getIndicatorAParams()),
                                        condition.getOperator(),
                                        condition.getIndicatorBType(),
                                        condition.getIndicatorBValue(),
                                        safeMap(condition.getIndicatorBParams())
                                ))
                                .collect(Collectors.toList())
                ))
                .collect(Collectors.toList());

        return new InternalStrategyResponse(
                strategy.getId(),
                strategy.getName(),
                strategy.getDescription(),
                strategy.getUserEmail(),
                strategy.getCreatedAt(),
                rules
        );
    }

    private void validateIndicator(String indicator, String fieldName) {
        if (!SUPPORTED_INDICATORS.contains(indicator)) {
            throw new IllegalArgumentException("Unsupported " + fieldName + ": " + indicator);
        }
    }

    private void validateIndicatorParams(String indicator, Map<String, String> params, String fieldName) {
        if ("PRICE".equals(indicator)) {
            return;
        }

        String rawPeriod = params.get("period");
        if (rawPeriod == null || rawPeriod.isBlank()) {
            throw new IllegalArgumentException(fieldName + ".period is required for indicator " + indicator);
        }

        try {
            int period = Integer.parseInt(rawPeriod);
            if (period <= 0) {
                throw new IllegalArgumentException(fieldName + ".period must be > 0");
            }
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(fieldName + ".period must be a valid integer");
        }
    }

    private void validateNumeric(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " is required");
        }

        try {
            new BigDecimal(value.trim());
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(fieldName + " must be numeric");
        }
    }

    private Map<String, String> safeMap(Map<String, String> source) {
        return source == null ? new HashMap<>() : new HashMap<>(source);
    }

    private String normalizeUpper(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " is required");
        }
        return value.trim().toUpperCase(Locale.ROOT);
    }
}