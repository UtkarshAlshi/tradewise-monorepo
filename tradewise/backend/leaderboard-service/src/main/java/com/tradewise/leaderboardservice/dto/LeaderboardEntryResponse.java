package com.tradewise.leaderboardservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LeaderboardEntryResponse {
    private int rank;
    private String portfolioName;
    private String userEmail;
    private BigDecimal totalReturnPercent;
}
