package com.tradewise.notificationservice.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "active_strategies")
@Data
@NoArgsConstructor
public class ActiveStrategy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String symbol;

    private boolean isActive = true;

    @ManyToOne
    @JoinColumn(name = "strategy_id")
    private Strategy strategy;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}