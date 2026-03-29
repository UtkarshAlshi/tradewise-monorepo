package com.tradewise.backtestingservice.dto.internal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InternalStrategyConditionResponse {
    private Long id;
    private String indicatorA;
    private Map<String, String> indicatorAParams = new HashMap<>();
    private String operator;
    private String indicatorBType;
    private String indicatorBValue;
    private Map<String, String> indicatorBParams = new HashMap<>();
}