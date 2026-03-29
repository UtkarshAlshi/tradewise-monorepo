package com.tradewise.strategyservice.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "strategy_conditions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StrategyCondition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rule_id", nullable = false)
    @JsonBackReference
    private StrategyRule rule;

    @Column(nullable = false)
    private String indicatorA; // PRICE, SMA, EMA, RSI

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "strategy_condition_indicator_a_params",
            joinColumns = @JoinColumn(name = "condition_id")
    )
    @MapKeyColumn(name = "param_key")
    @Column(name = "param_value")
    private Map<String, String> indicatorAParams = new HashMap<>();

    @Column(nullable = false)
    private String operator; // GREATER_THAN, LESS_THAN, CROSSES_ABOVE, CROSSES_BELOW

    @Column(nullable = false)
    private String indicatorBType; // VALUE or INDICATOR

    @Column(nullable = false)
    private String indicatorBValue; // numeric string if VALUE, indicator name if INDICATOR

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "strategy_condition_indicator_b_params",
            joinColumns = @JoinColumn(name = "condition_id")
    )
    @MapKeyColumn(name = "param_key")
    @Column(name = "param_value")
    private Map<String, String> indicatorBParams = new HashMap<>();
}