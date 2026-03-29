package com.tradewise.strategyservice.dto.strategy;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class StrategyConditionDTO {

    @NotBlank
    private String indicatorA;

    private Map<String, String> indicatorAParams = new HashMap<>();

    @NotBlank
    private String operator;

    @NotBlank
    private String indicatorBType; // VALUE or INDICATOR

    @NotBlank
    private String indicatorBValue;

    private Map<String, String> indicatorBParams = new HashMap<>();
}