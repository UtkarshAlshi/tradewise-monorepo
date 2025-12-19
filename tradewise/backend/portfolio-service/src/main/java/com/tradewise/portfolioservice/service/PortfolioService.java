package com.tradewise.portfolioservice.service;

import com.tradewise.portfolioservice.dto.AddAssetRequest;
import com.tradewise.portfolioservice.dto.CreatePortfolioRequest;
import com.tradewise.portfolioservice.dto.response.PortfolioAssetResponse;
import com.tradewise.portfolioservice.dto.response.PortfolioResponse;
import com.tradewise.portfolioservice.model.Portfolio;
import com.tradewise.portfolioservice.model.PortfolioAsset;
import com.tradewise.portfolioservice.repository.PortfolioAssetRepository;
import com.tradewise.portfolioservice.repository.PortfolioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PortfolioService {

    private final PortfolioRepository portfolioRepository;
    private final PortfolioAssetRepository portfolioAssetRepository;

    public PortfolioService(PortfolioRepository portfolioRepository,
                            PortfolioAssetRepository portfolioAssetRepository) {
        this.portfolioRepository = portfolioRepository;
        this.portfolioAssetRepository = portfolioAssetRepository;
    }

    @Transactional
    public Portfolio createPortfolio(CreatePortfolioRequest request, String userEmail) {
        Portfolio portfolio = Portfolio.builder()
                .name(request.getName())
                .description(request.getDescription())
                .userEmail(userEmail) // SET THE EMAIL DIRECTLY
                .build();

        return portfolioRepository.save(portfolio);
    }

    @Transactional(readOnly = true)
    public List<PortfolioResponse> getPortfoliosByUser(String userEmail) {
        // Find by email instead of ID
        List<Portfolio> portfolios = portfolioRepository.findByUserEmail(userEmail);

        // Map the response
        return portfolios.stream()
                .map(portfolio -> new PortfolioResponse(
                        portfolio.getId(),
                        portfolio.getName(),
                        portfolio.getDescription(),
                        portfolio.getCreatedAt(),
                        portfolio.getUserEmail() // Use the email field
                ))
                .collect(Collectors.toList());
    }

    @Transactional
    public PortfolioAsset addAssetToPortfolio(UUID portfolioId, AddAssetRequest request, String userEmail) {
        Portfolio portfolio = portfolioRepository.findById(portfolioId)
                .orElseThrow(() -> new RuntimeException("Portfolio not found with id: " + portfolioId));

        // --- NEW, SIMPLE CHECK ---
        if (!portfolio.getUserEmail().equals(userEmail)) {
            throw new RuntimeException("Access Denied: You do not own this portfolio.");
        }

        PortfolioAsset newAsset = PortfolioAsset.builder()
                .portfolio(portfolio)
                .symbol(request.getSymbol().toUpperCase())
                .quantity(request.getQuantity())
                .purchasePrice(request.getPurchasePrice())
                .purchaseDate(request.getPurchaseDate())
                .build();

        return portfolioAssetRepository.save(newAsset);
    }

    @Transactional(readOnly = true)
    public List<PortfolioAssetResponse> getAssetsForPortfolio(UUID portfolioId, String userEmail) {
        Portfolio portfolio = portfolioRepository.findById(portfolioId)
                .orElseThrow(() -> new RuntimeException("Portfolio not found with id: " + portfolioId));

        // --- NEW, SIMPLE CHECK ---
        if (!portfolio.getUserEmail().equals(userEmail)) {
            throw new RuntimeException("Access Denied: You do not own this portfolio.");
        }

        List<PortfolioAsset> assets = portfolioAssetRepository.findAllByPortfolioId(portfolioId);

        return assets.stream()
                .map(asset -> new PortfolioAssetResponse(
                        asset.getId(),
                        asset.getSymbol(),
                        asset.getQuantity(),
                        asset.getPurchasePrice(),
                        asset.getPurchaseDate(),
                        asset.getPortfolio().getId()
                ))
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteAssetFromPortfolio(UUID portfolioId, UUID assetId, String userEmail) {
        Portfolio portfolio = portfolioRepository.findById(portfolioId)
                .orElseThrow(() -> new RuntimeException("Portfolio not found with id: " + portfolioId));

        PortfolioAsset asset = portfolioAssetRepository.findById(assetId)
                .orElseThrow(() -> new RuntimeException("Asset not found with id: " + assetId));

        // --- NEW, SIMPLE CHECK ---
        if (!portfolio.getUserEmail().equals(userEmail)) {
            throw new RuntimeException("Access Denied: You do not own this portfolio.");
        }

        if (!asset.getPortfolio().getId().equals(portfolioId)) {
            throw new RuntimeException("Access Denied: Asset does not belong to this portfolio.");
        }

        portfolioAssetRepository.delete(asset);
    }
}
