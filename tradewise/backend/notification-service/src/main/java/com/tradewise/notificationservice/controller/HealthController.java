package com.tradewise.notificationservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

@RestController
public class HealthController {

    @Autowired(required = false)
    private DataSource dataSource;

    @GetMapping("/health/ready")
    public Map<String, Object> readiness() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "ready");
        response.put("kafka_consumers", "active");
        response.put("database", checkDatabase());
        return response;
    }

    @GetMapping("/health/live")
    public Map<String, Object> liveness() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "alive");
        response.put("timestamp", System.currentTimeMillis());
        return response;
    }

    private String checkDatabase() {
        if (dataSource == null) {
            return "not-configured";
        }

        try (Connection conn = dataSource.getConnection()) {
            return "connected";
        } catch (Exception e) {
            return "disconnected: " + e.getMessage();
        }
    }
}