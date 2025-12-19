package com.tradewise.portfolioservice.controller;

import com.tradewise.portfolioservice.dto.AddAssetRequest;
import com.tradewise.portfolioservice.dto.CreatePortfolioRequest;
import com.tradewise.portfolioservice.dto.response.PortfolioAssetResponse;
import com.tradewise.portfolioservice.dto.response.PortfolioResponse;
import com.tradewise.portfolioservice.model.Portfolio;
import com.tradewise.portfolioservice.model.PortfolioAsset;
import com.tradewise.portfolioservice.service.PortfolioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/portfolios")
public class PortfolioController {

    private final PortfolioService portfolioService;

    public PortfolioController(PortfolioService portfolioService) {
        this.portfolioService = portfolioService;
    }

    @PostMapping
    public ResponseEntity<PortfolioResponse> createPortfolio(
            @Valid @RequestBody CreatePortfolioRequest request,
            @RequestHeader("X-User-Email") String userEmail) {

        Portfolio newPortfolio = portfolioService.createPortfolio(request, userEmail);

        PortfolioResponse response = new PortfolioResponse(
                newPortfolio.getId(),
                newPortfolio.getName(),
                newPortfolio.getDescription(),
                newPortfolio.getCreatedAt(),
                newPortfolio.getUserEmail()
        );

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<PortfolioResponse>> getPortfolios(
            @RequestHeader("X-User-Email") String userEmail) {

        List<PortfolioResponse> portfolios = portfolioService.getPortfoliosByUser(userEmail);

        return ResponseEntity.ok(portfolios);
    }

    @PostMapping("/{portfolioId}/assets")
    public ResponseEntity<PortfolioAssetResponse> addAssetToPortfolio(
            @PathVariable UUID portfolioId,
            @Valid @RequestBody AddAssetRequest request,
            @RequestHeader("X-User-Email") String userEmail) {

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

        List<PortfolioAssetResponse> response = portfolioService.getAssetsForPortfolio(portfolioId, userEmail);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{portfolioId}/assets/{assetId}")
    public ResponseEntity<Void> deleteAsset(
            @PathVariable UUID portfolioId,
            @PathVariable UUID assetId,
            @RequestHeader("X-User-Email") String userEmail) {

        portfolioService.deleteAssetFromPortfolio(portfolioId, assetId, userEmail);

        return ResponseEntity.noContent().build();
    }
}
