package com.tradewise.backtestingservice.dto.internal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InternalStrategyResponse {
    private UUID id;
    private String name;
    private String description;
    private String userEmail;
    private LocalDateTime createdAt;
    private List<InternalStrategyRuleResponse> rules = new ArrayList<>();
}