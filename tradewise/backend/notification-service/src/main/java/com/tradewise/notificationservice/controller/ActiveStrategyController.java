package com.tradewise.notificationservice.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tradewise.notificationservice.model.ActiveStrategy;
import com.tradewise.notificationservice.repository.ActiveStrategyRepository;

@RestController
@RequestMapping("/api/active-strategies")
public class ActiveStrategyController {

    private final ActiveStrategyRepository repo;

    public ActiveStrategyController(ActiveStrategyRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public List<ActiveStrategy> listAll() {
        return repo.findAll();
    }

    @PostMapping
    public ActiveStrategy create(@RequestBody ActiveStrategy s) {
        return repo.save(s);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!repo.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        repo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
