package com.tradewise.backtestingservice.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Strategy {

    private UUID id;
    private String name;
    private String description;
    private String userEmail;
    private LocalDateTime createdAt;
    @JsonManagedReference
    private List<StrategyRule> rules = new ArrayList<>();
}
