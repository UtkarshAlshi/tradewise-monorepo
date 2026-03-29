package com.tradewise.strategyservice.dto.strategy;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class StrategyRuleDTO {

    @NotBlank
    private String action; // BUY or SELL

    @Valid
    @NotEmpty
    private List<StrategyConditionDTO> conditions = new ArrayList<>();
}