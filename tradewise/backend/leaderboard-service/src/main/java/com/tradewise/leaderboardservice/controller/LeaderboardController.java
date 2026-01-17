package com.tradewise.leaderboardservice.controller;

import com.tradewise.leaderboardservice.dto.LeaderboardEntryResponse;
import com.tradewise.leaderboardservice.service.LeaderboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/leaderboard")
@RequiredArgsConstructor
public class LeaderboardController {

    private final LeaderboardService leaderboardService;

    @GetMapping
    public ResponseEntity<List<LeaderboardEntryResponse>> getLeaderboard() {
        List<LeaderboardEntryResponse> leaderboard = leaderboardService.getLeaderboard();
        return ResponseEntity.ok(leaderboard);
    }
}
