package com.tradewise.leaderboardservice.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class PortfolioAssetResponse {
    private UUID id;
    private String symbol;
    private BigDecimal quantity;
    private BigDecimal purchasePrice;
    private LocalDateTime purchaseDate;
    private UUID portfolioId;
}
