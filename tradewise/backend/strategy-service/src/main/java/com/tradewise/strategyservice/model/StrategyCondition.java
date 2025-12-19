package com.tradewise.strategyservice.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private String indicator; // e.g., "RSI", "SMA"

    @Column(nullable = false)
    private String operator; // e.g., ">", "<", "="

    @Column(nullable = false)
    private Double value;

    @Column(nullable = false)
    private String action; // e.g., "BUY", "SELL"
}
