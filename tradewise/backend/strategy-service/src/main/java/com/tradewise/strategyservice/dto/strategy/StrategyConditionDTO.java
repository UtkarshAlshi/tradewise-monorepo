package com.tradewise.strategyservice.dto.strategy;

import lombok.Data;

@Data
public class StrategyConditionDTO {
    private String indicator;
    private String operator;
    private Double value;
    private String action;
}
