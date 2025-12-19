package com.tradewise.backtestingservice.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StrategyCondition {

    private Long id;

    @JsonBackReference
    private StrategyRule rule;

    private String indicatorA;
    private Map<String, Object> indicatorAParams;
    private String operator;
    private String indicatorBType; // "VALUE" or "INDICATOR"
    private String indicatorBValue;
    private Map<String, Object> indicatorBParams;
}
