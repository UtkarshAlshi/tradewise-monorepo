package com.tradewise.backtestingservice.service;

import com.tradewise.backtestingservice.dto.BacktestRequest;
import com.tradewise.backtestingservice.dto.BarDTO;
import com.tradewise.backtestingservice.dto.response.BacktestReportResponse;
import com.tradewise.backtestingservice.model.Strategy;
import com.tradewise.backtestingservice.model.StrategyCondition;
import com.tradewise.backtestingservice.model.StrategyRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.ta4j.core.*;
import org.ta4j.core.analysis.CashFlow;

import org.ta4j.core.backtest.BarSeriesManager;
import org.ta4j.core.criteria.MaximumDrawdownCriterion;
import org.ta4j.core.criteria.NumberOfPositionsCriterion;
import org.ta4j.core.criteria.PositionsRatioCriterion;
import org.ta4j.core.AnalysisCriterion.PositionFilter;

import org.ta4j.core.indicators.EMAIndicator;
import org.ta4j.core.indicators.RSIIndicator;
import org.ta4j.core.indicators.SMAIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.num.DecimalNum;

import org.ta4j.core.rules.*;
import org.ta4j.core.Trade;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

@Service
public class BacktestingService {

    private static final Logger logger = LoggerFactory.getLogger(BacktestingService.class);

    private final RestTemplate restTemplate;
    
    // Service URLs (use environment variables for Docker compatibility)
    private static final String STRATEGY_SERVICE_HOST = System.getenv().getOrDefault("STRATEGY_SERVICE_HOST", "localhost:8083");
    private static final String MARKET_DATA_SERVICE_HOST = System.getenv().getOrDefault("MARKET_DATA_SERVICE_HOST", "localhost:8084");

    public BacktestingService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Main method to run the backtest.
     */
    public BacktestReportResponse runBacktest(BacktestRequest request, String userEmail) {
        logger.info("Starting backtest for symbol: {}, strategyId: {}", request.getSymbol(), request.getStrategyId());

        // Prepare headers for authenticated requests
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-User-Email", userEmail);
        
        // 1. Fetch Strategy via HTTP from strategy-service with authentication header
        String strategyUrl = "http://" + STRATEGY_SERVICE_HOST + "/api/strategies/" + request.getStrategyId() + "/internal";
        HttpEntity<Void> strategyEntity = new HttpEntity<>(headers);
        ResponseEntity<Strategy> strategyResponse = restTemplate.exchange(
            strategyUrl, 
            HttpMethod.GET, 
            strategyEntity, 
            Strategy.class
        );
        Strategy strategy = strategyResponse.getBody();

        if (strategy == null) {
            throw new RuntimeException("Strategy not found");
        }
        logger.info("Fetched strategy: {}, rules count: {}", strategy.getName(), strategy.getRules().size());

        // --- CRITICAL SECURITY CHECK ---
        if (!strategy.getUserEmail().equals(userEmail)) {
            throw new RuntimeException("Access Denied: You do not own this strategy.");
        }

        // 2. Fetch Historical Data via HTTP from market-data-service
        String dataUrl = String.format(
            "http://" + MARKET_DATA_SERVICE_HOST + "/api/market-data/history/internal?symbol=%s&startDate=%s&endDate=%s",
            request.getSymbol(), request.getStartDate(), request.getEndDate()
        );
        logger.info("Fetching market data from: {}", dataUrl);

        HttpEntity<Void> dataEntity = new HttpEntity<>(headers);
        
        // Use ParameterizedTypeReference to fetch List<BarDTO>
        ResponseEntity<List<BarDTO>> dataResponse = restTemplate.exchange(
            dataUrl,
            HttpMethod.GET,
            dataEntity,
            new ParameterizedTypeReference<List<BarDTO>>() {}
        );
        List<BarDTO> barDTOs = dataResponse.getBody();

        if (barDTOs == null || barDTOs.isEmpty()) {
            logger.warn("No market data found for symbol: {}", request.getSymbol());
            return BacktestReportResponse.builder()
                    .strategyName(strategy.getName()).symbol(request.getSymbol()).totalTrades(0)
                    .totalProfitLoss(BigDecimal.ZERO).totalReturnPercent(BigDecimal.ZERO)
                    .winRatePercent(BigDecimal.ZERO).maxDrawdownPercent(BigDecimal.ZERO)
                    .build();
        }
        logger.info("Fetched {} bars.", barDTOs.size());

        // Convert DTOs to ta4j BarSeries
        BarSeries barSeries = convertToBarSeries(request.getSymbol(), barDTOs);

        // 3. Build ta4j Strategy from our DB entities
        BaseStrategy ta4jStrategy = buildTa4jStrategy(strategy, barSeries);

        // 4. Run the Simulation
        BarSeriesManager manager = new BarSeriesManager(barSeries);
        
        // Calculate amount to trade based on initial cash and first bar's price
        // This is a simplified approach: we calculate how many units we can buy with the initial cash at the start.
        // We use this fixed amount for all trades.
        double firstPrice = barSeries.getBar(barSeries.getBeginIndex()).getClosePrice().doubleValue();
        double amountToTrade = request.getInitialCash() / firstPrice;
        amountToTrade = Math.floor(amountToTrade);
        if (amountToTrade < 1) amountToTrade = 1; // Ensure we trade at least 1 unit if cash is low (or allow fractional?)
        
        logger.info("Calculated trade amount: {} (Initial Cash: {}, First Price: {})", amountToTrade, request.getInitialCash(), firstPrice);

        TradingRecord tradingRecord = manager.run(
                ta4jStrategy,
                Trade.TradeType.BUY,
                DecimalNum.valueOf(amountToTrade)
        );

        logger.info("Backtest finished. Total trades: {}", tradingRecord.getTrades().size());

        // 5. Calculate Metrics
        return calculateReport(strategy.getName(), request.getSymbol(), barSeries, tradingRecord, request.getInitialCash());
    }

    private BarSeries convertToBarSeries(String symbol, List<BarDTO> barDTOs) {
        BaseBarSeries series = new BaseBarSeries(symbol);
        for (BarDTO dto : barDTOs) {
            series.addBar(
                Duration.ofDays(1),
                dto.getEndTime(),
                dto.getOpen().doubleValue(),
                dto.getHigh().doubleValue(),
                dto.getLow().doubleValue(),
                dto.getClose().doubleValue(),
                dto.getVolume().doubleValue()
            );
        }
        return series;
    }

    /**
     * Translates our DB Strategy into a ta4j-compatible Strategy.
     */
    private BaseStrategy buildTa4jStrategy(Strategy dbStrategy, BarSeries barSeries) {
        Map<String, Indicator> indicatorCache = new HashMap<>();

        Rule entryRule = new BooleanRule(false);
        Rule exitRule = new BooleanRule(false);

        for (StrategyRule dbRule : dbStrategy.getRules()) {
            Rule combinedRuleForConditions = new BooleanRule(true);

            if (dbRule.getConditions() == null) continue;

            for (StrategyCondition dbCondition : dbRule.getConditions()) {
                Rule conditionRule = buildTa4jRule(dbCondition, barSeries, indicatorCache);
                combinedRuleForConditions = combinedRuleForConditions.and(conditionRule);
            }

            if ("BUY".equalsIgnoreCase(dbRule.getAction())) {
                entryRule = entryRule.or(combinedRuleForConditions);
            } else if ("SELL".equalsIgnoreCase(dbRule.getAction())) {
                exitRule = exitRule.or(combinedRuleForConditions);
            }
        }

        return new BaseStrategy(entryRule, exitRule);
    }

    /**
     * Helper to build a single ta4j Rule from our DB Condition.
     */
    private Rule buildTa4jRule(StrategyCondition condition, BarSeries series, Map<String, Indicator> cache) {
        Indicator indicatorA = getIndicator(condition.getIndicatorA(), condition.getIndicatorAParams(), series, cache);

        if ("VALUE".equalsIgnoreCase(condition.getIndicatorBType())) {
            DecimalNum valueB = DecimalNum.valueOf(condition.getIndicatorBValue());

            switch (condition.getOperator().toUpperCase()) {
                case "GREATER_THAN":
                    return new OverIndicatorRule(indicatorA, valueB);
                case "LESS_THAN":
                    return new UnderIndicatorRule(indicatorA, valueB);
                default:
                    throw new RuntimeException("Unsupported operator: " + condition.getOperator());
            }
        }
        else if ("INDICATOR".equalsIgnoreCase(condition.getIndicatorBType())) {
            Indicator indicatorB = getIndicator(condition.getIndicatorBValue(), condition.getIndicatorBParams(), series, cache);

            switch (condition.getOperator().toUpperCase()) {
                case "GREATER_THAN":
                    return new OverIndicatorRule(indicatorA, indicatorB);
                case "LESS_THAN":
                    return new UnderIndicatorRule(indicatorA, indicatorB);
                case "CROSSES_ABOVE":
                    return new CrossedUpIndicatorRule(indicatorA, indicatorB);
                case "CROSSES_BELOW":
                    return new CrossedDownIndicatorRule(indicatorA, indicatorB);
                default:
                    throw new RuntimeException("Unsupported operator: " + condition.getOperator());
            }
        }
        throw new RuntimeException("Unsupported IndicatorBType: " + condition.getIndicatorBType());
    }

    /**
     * Helper to create (or get from cache) a ta4j Indicator.
     */
    private Indicator getIndicator(String name, Map<String, Object> params, BarSeries series, Map<String, Indicator> cache) {
        String cacheKey = name + (params != null ? params.toString() : "{}");
        if (cache.containsKey(cacheKey)) {
            return cache.get(cacheKey);
        }

        Indicator indicator;
        ClosePriceIndicator closePrice = new ClosePriceIndicator(series);

        switch (name.toUpperCase()) {
            case "PRICE":
                indicator = closePrice;
                break;
            case "SMA":
                int smaPeriod = ((Number) params.get("period")).intValue();
                indicator = new SMAIndicator(closePrice, smaPeriod);
                break;
            case "EMA":
                int emaPeriod = ((Number) params.get("period")).intValue();
                indicator = new EMAIndicator(closePrice, emaPeriod);
                break;
            case "RSI":
                int rsiPeriod = ((Number) params.get("period")).intValue();
                indicator = new RSIIndicator(closePrice, rsiPeriod);
                break;
            default:
                throw new RuntimeException("Unsupported indicator: " + name);
        }

        cache.put(cacheKey, indicator);
        return indicator;
    }

    /**
     * Calculates final report metrics from the trading record.
     */
    private BacktestReportResponse calculateReport(String strategyName, String symbol, BarSeries series, TradingRecord record, Double initialCash) {
        if (record.getTrades().isEmpty()) {
            return BacktestReportResponse.builder()
                    .strategyName(strategyName).symbol(symbol).totalTrades(0)
                    .totalProfitLoss(BigDecimal.ZERO).totalReturnPercent(BigDecimal.ZERO)
                    .winRatePercent(BigDecimal.ZERO).maxDrawdownPercent(BigDecimal.ZERO)
                    .build();
        }

        CashFlow cashFlow = new CashFlow(series, record, DecimalNum.valueOf(initialCash).intValue());

        BigDecimal finalValue = (BigDecimal) cashFlow.getValue(series.getEndIndex()).getDelegate();
        BigDecimal pnl = finalValue.subtract(BigDecimal.valueOf(initialCash));
        BigDecimal returnPercent = pnl.divide(BigDecimal.valueOf(initialCash), 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));

        int totalTrades = new NumberOfPositionsCriterion().calculate(series, record).intValue();

        BigDecimal winRate = ((BigDecimal) new PositionsRatioCriterion(PositionFilter.PROFIT)
                .calculate(series, record)
                .getDelegate())
                .multiply(BigDecimal.valueOf(100));

        BigDecimal maxDrawdown = ((BigDecimal) new MaximumDrawdownCriterion().calculate(series, record)
                .getDelegate())
                .multiply(BigDecimal.valueOf(100));

        return BacktestReportResponse.builder()
                .strategyName(strategyName)
                .symbol(symbol)
                .totalTrades(totalTrades)
                .totalProfitLoss(pnl.setScale(2, RoundingMode.HALF_UP))
                .totalReturnPercent(returnPercent.setScale(2, RoundingMode.HALF_UP))
                .winRatePercent(winRate.setScale(2, RoundingMode.HALF_UP))
                .maxDrawdownPercent(maxDrawdown.setScale(2, RoundingMode.HALF_UP))
                .build();
    }
}
