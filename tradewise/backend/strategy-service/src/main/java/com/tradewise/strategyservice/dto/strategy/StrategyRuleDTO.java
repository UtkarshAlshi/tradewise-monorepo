package com.tradewise.strategyservice.dto.strategy;

import lombok.Data;

import java.util.List;

@Data
public class StrategyRuleDTO {
    private String type;
    private List<StrategyConditionDTO> conditions;
}
