package com.tradewise.leaderboardservice.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class PortfolioResponse {
    private UUID id;
    private String name;
    private String description;
    private LocalDateTime createdAt;
    private String userEmail;
}
