package com.tradewise.portfolioservice.controller;

import com.tradewise.portfolioservice.dto.AddAssetRequest;
import com.tradewise.portfolioservice.dto.CreatePortfolioRequest;
import com.tradewise.portfolioservice.dto.response.PortfolioAssetResponse;
import com.tradewise.portfolioservice.dto.response.PortfolioResponse;
import com.tradewise.portfolioservice.model.Portfolio;
import com.tradewise.portfolioservice.model.PortfolioAsset;
import com.tradewise.portfolioservice.service.PortfolioService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/portfolios")
public class PortfolioController {

    private static final Logger log = LoggerFactory.getLogger(PortfolioController.class);
    private final PortfolioService portfolioService;

    public PortfolioController(PortfolioService portfolioService) {
        this.portfolioService = portfolioService;
    }

    @PostMapping
    public ResponseEntity<PortfolioResponse> createPortfolio(
            @Valid @RequestBody CreatePortfolioRequest request,
            @RequestHeader("X-User-Email") String userEmail) {

        log.info("Received request to create portfolio for user: {}", userEmail);
        Portfolio newPortfolio = portfolioService.createPortfolio(request, userEmail);

        PortfolioResponse response = new PortfolioResponse(
                newPortfolio.getId(),
                newPortfolio.getName(),
                newPortfolio.getDescription(),
                newPortfolio.getCreatedAt(),
                newPortfolio.getUserEmail()
        );
        
        log.info("Successfully created portfolio with ID: {}", newPortfolio.getId());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<PortfolioResponse>> getPortfolios(
            @RequestHeader("X-User-Email") String userEmail) {

        log.info("Fetching portfolios for user: {}", userEmail);
        List<PortfolioResponse> portfolios = portfolioService.getPortfoliosByUser(userEmail);
        log.info("Found {} portfolios for user {}", portfolios.size(), userEmail);

        return ResponseEntity.ok(portfolios);
    }

    @PostMapping("/{portfolioId}/assets")
    public ResponseEntity<PortfolioAssetResponse> addAssetToPortfolio(
            @PathVariable UUID portfolioId,
            @Valid @RequestBody AddAssetRequest request,
            @RequestHeader("X-User-Email") String userEmail) {

        log.info("Adding asset {} to portfolio {} for user {}", request.getSymbol(), portfolioId, userEmail);
        PortfolioAsset savedAsset = portfolioService.addAssetToPortfolio(portfolioId, request, userEmail);

        PortfolioAssetResponse response = new PortfolioAssetResponse(
                savedAsset.getId(),
                savedAsset.getSymbol(),
                savedAsset.getQuantity(),
                savedAsset.getPurchasePrice(),
                savedAsset.getPurchaseDate(),
                savedAsset.getPortfolio().getId()
        );

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{portfolioId}/assets")
    public ResponseEntity<List<PortfolioAssetResponse>> getAssetsForPortfolio(
            @PathVariable UUID portfolioId,
            @RequestHeader("X-User-Email") String userEmail) {

        log.info("Fetching assets for portfolio {} and user {}", portfolioId, userEmail);
        List<PortfolioAssetResponse> response = portfolioService.getAssetsForPortfolio(portfolioId, userEmail);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{portfolioId}/assets/{assetId}")
    public ResponseEntity<Void> deleteAsset(
            @PathVariable UUID portfolioId,
            @PathVariable UUID assetId,
            @RequestHeader("X-User-Email") String userEmail) {

        log.info("Deleting asset {} from portfolio {} for user {}", assetId, portfolioId, userEmail);
        portfolioService.deleteAssetFromPortfolio(portfolioId, assetId, userEmail);

        return ResponseEntity.noContent().build();
    }

    // --- INTERNAL ENDPOINTS FOR LEADERBOARD SERVICE ---

    @GetMapping("/internal/ids")
    public ResponseEntity<List<UUID>> getAllPortfolioIds() {
        log.info("Internal request for all portfolio IDs");
        List<UUID> ids = portfolioService.getAllPortfolioIds();
        return ResponseEntity.ok(ids);
    }

    @GetMapping("/{id}/analytics/internal")
    public ResponseEntity<PortfolioResponse> getPortfolioForAnalytics(@PathVariable UUID id) {
        log.info("Internal request for portfolio analytics with ID: {}", id);
        PortfolioResponse portfolio = portfolioService.getPortfolioForAnalytics(id);
        return ResponseEntity.ok(portfolio);
    }
}
