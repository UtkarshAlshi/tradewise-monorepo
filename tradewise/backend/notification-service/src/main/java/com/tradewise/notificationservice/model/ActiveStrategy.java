package com.tradewise.notificationservice.model;

import jakarta.persistence.*;

@Entity
@Table(name = "active_strategies")
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

    public ActiveStrategy() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
    public Strategy getStrategy() { return strategy; }
    public void setStrategy(Strategy strategy) { this.strategy = strategy; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}
