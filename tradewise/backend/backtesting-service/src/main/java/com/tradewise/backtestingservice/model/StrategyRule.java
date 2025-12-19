package com.tradewise.backtestingservice.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StrategyRule {

    private Long id;

    @JsonBackReference
    private Strategy strategy;

    private String action; // "BUY" or "SELL"

    @JsonManagedReference
    private List<StrategyCondition> conditions = new ArrayList<>();
}
