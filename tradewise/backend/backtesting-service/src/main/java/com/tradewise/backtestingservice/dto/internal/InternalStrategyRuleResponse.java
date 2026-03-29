package com.tradewise.backtestingservice.dto.internal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InternalStrategyRuleResponse {
    private Long id;
    private String action;
    private List<InternalStrategyConditionResponse> conditions = new ArrayList<>();
}