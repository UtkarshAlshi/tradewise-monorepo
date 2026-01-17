package com.tradewise.userservice.dto.response;

import java.util.List;

public class DashboardAnalyticsResponse {
    private double totalBalance;
    private double balanceChangePercent;
    private int activeStrategies;
    private int pausedStrategies;
    private int totalPortfolios;
    private int unreadNotifications;
    private List<PortfolioGrowthPoint> portfolioGrowth;

    public DashboardAnalyticsResponse(double totalBalance, double balanceChangePercent, int activeStrategies, int pausedStrategies, int totalPortfolios, int unreadNotifications, List<PortfolioGrowthPoint> portfolioGrowth) {
        this.totalBalance = totalBalance;
        this.balanceChangePercent = balanceChangePercent;
        this.activeStrategies = activeStrategies;
        this.pausedStrategies = pausedStrategies;
        this.totalPortfolios = totalPortfolios;
        this.unreadNotifications = unreadNotifications;
        this.portfolioGrowth = portfolioGrowth;
    }

    // Getters and Setters
    public double getTotalBalance() { return totalBalance; }
    public void setTotalBalance(double totalBalance) { this.totalBalance = totalBalance; }

    public double getBalanceChangePercent() { return balanceChangePercent; }
    public void setBalanceChangePercent(double balanceChangePercent) { this.balanceChangePercent = balanceChangePercent; }

    public int getActiveStrategies() { return activeStrategies; }
    public void setActiveStrategies(int activeStrategies) { this.activeStrategies = activeStrategies; }

    public int getPausedStrategies() { return pausedStrategies; }
    public void setPausedStrategies(int pausedStrategies) { this.pausedStrategies = pausedStrategies; }

    public int getTotalPortfolios() { return totalPortfolios; }
    public void setTotalPortfolios(int totalPortfolios) { this.totalPortfolios = totalPortfolios; }

    public int getUnreadNotifications() { return unreadNotifications; }
    public void setUnreadNotifications(int unreadNotifications) { this.unreadNotifications = unreadNotifications; }

    public List<PortfolioGrowthPoint> getPortfolioGrowth() { return portfolioGrowth; }
    public void setPortfolioGrowth(List<PortfolioGrowthPoint> portfolioGrowth) { this.portfolioGrowth = portfolioGrowth; }

    public static class PortfolioGrowthPoint {
        private String date;
        private double value;

        public PortfolioGrowthPoint(String date, double value) {
            this.date = date;
            this.value = value;
        }

        public String getDate() { return date; }
        public void setDate(String date) { this.date = date; }

        public double getValue() { return value; }
        public void setValue(double value) { this.value = value; }
    }
}
