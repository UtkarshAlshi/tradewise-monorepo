package com.tradewise.backtestingservice.service;

import com.tradewise.backtestingservice.dto.BacktestRequest;
import com.tradewise.backtestingservice.dto.BarDTO;
import com.tradewise.backtestingservice.dto.internal.InternalStrategyConditionResponse;
import com.tradewise.backtestingservice.dto.internal.InternalStrategyResponse;
import com.tradewise.backtestingservice.dto.internal.InternalStrategyRuleResponse;
import com.tradewise.backtestingservice.dto.response.BacktestReportResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.ta4j.core.*;
import org.ta4j.core.AnalysisCriterion.PositionFilter;
import org.ta4j.core.Trade;
import org.ta4j.core.analysis.CashFlow;
import org.ta4j.core.backtest.BarSeriesManager;
import org.ta4j.core.criteria.MaximumDrawdownCriterion;
import org.ta4j.core.criteria.NumberOfPositionsCriterion;
import org.ta4j.core.criteria.PositionsRatioCriterion;
import org.ta4j.core.indicators.EMAIndicator;
import org.ta4j.core.indicators.RSIIndicator;
import org.ta4j.core.indicators.SMAIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.num.DecimalNum;
import org.ta4j.core.rules.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.util.*;

@Service
public class BacktestingService {

    private static final Logger logger = LoggerFactory.getLogger(BacktestingService.class);

    private final RestTemplate restTemplate;

    private static final String STRATEGY_SERVICE_HOST =
            System.getenv().getOrDefault("STRATEGY_SERVICE_HOST", "localhost:8083");

    private static final String MARKET_DATA_SERVICE_HOST =
            System.getenv().getOrDefault("MARKET_DATA_SERVICE_HOST", "localhost:8084");

    public BacktestingService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public BacktestReportResponse runBacktest(BacktestRequest request, String userEmail) {
        logger.info("Starting backtest for symbol: {}, strategyId: {}", request.getSymbol(), request.getStrategyId());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-User-Email", userEmail);

        // 1. Fetch strategy
        String strategyUrl = "http://" + STRATEGY_SERVICE_HOST + "/api/strategies/" + request.getStrategyId() + "/internal";
        HttpEntity<Void> strategyEntity = new HttpEntity<>(headers);

        ResponseEntity<InternalStrategyResponse> strategyResponse = restTemplate.exchange(
                strategyUrl,
                HttpMethod.GET,
                strategyEntity,
                InternalStrategyResponse.class
        );

        InternalStrategyResponse strategy = strategyResponse.getBody();
        if (strategy == null) {
            throw new RuntimeException("Strategy not found");
        }

        if (!userEmail.equals(strategy.getUserEmail())) {
            throw new RuntimeException("Access Denied: You do not own this strategy.");
        }

        if (strategy.getRules() == null || strategy.getRules().isEmpty()) {
            throw new RuntimeException("Strategy has no rules to backtest");
        }

        logger.info("Fetched strategy: {}, rules count: {}", strategy.getName(), strategy.getRules().size());

        // 2. Fetch historical bars
        String dataUrl = String.format(
                "http://" + MARKET_DATA_SERVICE_HOST + "/api/market-data/history/internal?symbol=%s&startDate=%s&endDate=%s",
                request.getSymbol(), request.getStartDate(), request.getEndDate()
        );

        HttpEntity<Void> dataEntity = new HttpEntity<>(headers);

        ResponseEntity<List<BarDTO>> dataResponse = restTemplate.exchange(
                dataUrl,
                HttpMethod.GET,
                dataEntity,
                new ParameterizedTypeReference<List<BarDTO>>() {}
        );

        List<BarDTO> barDTOs = dataResponse.getBody();
        if (barDTOs == null || barDTOs.isEmpty()) {
            logger.warn("No market data found for symbol: {}", request.getSymbol());
            return emptyReport(strategy.getName(), request.getSymbol());
        }

        barDTOs.sort(Comparator.comparing(BarDTO::getEndTime));
        BarSeries barSeries = convertToBarSeries(request.getSymbol(), barDTOs);

        // 3. Build ta4j strategy
        BaseStrategy ta4jStrategy = buildTa4jStrategy(strategy, barSeries);

        // 4. Run simulation
        BarSeriesManager manager = new BarSeriesManager(barSeries);

        double firstPrice = barSeries.getBar(barSeries.getBeginIndex()).getClosePrice().doubleValue();
        if (firstPrice <= 0 || Double.isNaN(firstPrice)) {
            logger.warn("Invalid first price: {}", firstPrice);
            return emptyReport(strategy.getName(), request.getSymbol());
        }

        double amountToTrade = Math.floor(request.getInitialCash() / firstPrice);
        if (amountToTrade < 1) {
            amountToTrade = 1;
        }

        logger.info("Calculated trade amount: {}", amountToTrade);

        TradingRecord tradingRecord = manager.run(
                ta4jStrategy,
                Trade.TradeType.BUY,
                DecimalNum.valueOf(amountToTrade)
        );

        if (tradingRecord.getCurrentPosition().isOpened()) {
            logger.info("Closing open position at end of series");
            tradingRecord.exit(
                    barSeries.getEndIndex(),
                    barSeries.getBar(barSeries.getEndIndex()).getClosePrice(),
                    tradingRecord.getCurrentPosition().getEntry().getAmount()
            );
        }

        logger.info("Backtest finished. Total trades: {}", tradingRecord.getTrades().size());

        return calculateReport(
                strategy.getName(),
                request.getSymbol(),
                barSeries,
                tradingRecord,
                request.getInitialCash()
        );
    }

    private BacktestReportResponse emptyReport(String strategyName, String symbol) {
        return BacktestReportResponse.builder()
                .strategyName(strategyName)
                .symbol(symbol)
                .totalTrades(0)
                .totalProfitLoss(BigDecimal.ZERO)
                .totalReturnPercent(BigDecimal.ZERO)
                .winRatePercent(BigDecimal.ZERO)
                .maxDrawdownPercent(BigDecimal.ZERO)
                .build();
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

    private BaseStrategy buildTa4jStrategy(InternalStrategyResponse dbStrategy, BarSeries barSeries) {
        Map<String, Indicator> indicatorCache = new HashMap<>();

        Rule entryRule = new BooleanRule(false);
        Rule exitRule = new BooleanRule(false);

        boolean hasBuyRule = false;
        boolean hasSellRule = false;

        for (InternalStrategyRuleResponse dbRule : dbStrategy.getRules()) {
            if (dbRule.getConditions() == null || dbRule.getConditions().isEmpty()) {
                logger.warn("Skipping rule {} because it has no conditions", dbRule.getId());
                continue;
            }

            String action = requireNonBlank(dbRule.getAction(), "Rule action");
            logger.info("Processing Rule ID: {}, Action: {}", dbRule.getId(), action);

            Rule combinedRuleForConditions = new BooleanRule(true);

            for (InternalStrategyConditionResponse dbCondition : dbRule.getConditions()) {
                logger.info("Condition: {} {} {} {}",
                        dbCondition.getIndicatorA(),
                        dbCondition.getOperator(),
                        dbCondition.getIndicatorBType(),
                        dbCondition.getIndicatorBValue());

                Rule conditionRule = buildTa4jRule(dbCondition, barSeries, indicatorCache);
                combinedRuleForConditions = combinedRuleForConditions.and(conditionRule);
            }

            if ("BUY".equalsIgnoreCase(action)) {
                entryRule = entryRule.or(combinedRuleForConditions);
                hasBuyRule = true;
            } else if ("SELL".equalsIgnoreCase(action)) {
                exitRule = exitRule.or(combinedRuleForConditions);
                hasSellRule = true;
            } else {
                throw new RuntimeException("Unsupported rule action: " + action);
            }
        }

        if (!hasBuyRule) {
            throw new RuntimeException("Strategy must contain at least one BUY rule");
        }
        if (!hasSellRule) {
            logger.warn("Strategy does not contain a SELL rule. Open positions will only close at series end.");
        }

        return new BaseStrategy(entryRule, exitRule);
    }

    private Rule buildTa4jRule(
            InternalStrategyConditionResponse condition,
            BarSeries series,
            Map<String, Indicator> cache
    ) {
        String indicatorAName = requireNonBlank(condition.getIndicatorA(), "indicatorA");
        String operator = requireNonBlank(condition.getOperator(), "operator");
        String indicatorBType = requireNonBlank(condition.getIndicatorBType(), "indicatorBType");
        String indicatorBValue = requireNonBlank(condition.getIndicatorBValue(), "indicatorBValue");

        Indicator indicatorA = getIndicator(indicatorAName, condition.getIndicatorAParams(), series, cache);

        if ("VALUE".equalsIgnoreCase(indicatorBType)) {
            DecimalNum valueB;
            try {
                valueB = DecimalNum.valueOf(new BigDecimal(indicatorBValue.trim()));
            } catch (Exception ex) {
                throw new RuntimeException("Invalid numeric indicatorBValue: " + indicatorBValue);
            }

            switch (operator.toUpperCase(Locale.ROOT)) {
                case "GREATER_THAN":
                    return new OverIndicatorRule(indicatorA, valueB);
                case "LESS_THAN":
                    return new UnderIndicatorRule(indicatorA, valueB);
                default:
                    throw new RuntimeException("Unsupported operator for VALUE comparison: " + operator);
            }
        }

        if ("INDICATOR".equalsIgnoreCase(indicatorBType)) {
            Indicator indicatorB = getIndicator(indicatorBValue, condition.getIndicatorBParams(), series, cache);

            switch (operator.toUpperCase(Locale.ROOT)) {
                case "GREATER_THAN":
                    return new OverIndicatorRule(indicatorA, indicatorB);
                case "LESS_THAN":
                    return new UnderIndicatorRule(indicatorA, indicatorB);
                case "CROSSES_ABOVE":
                    return new CrossedUpIndicatorRule(indicatorA, indicatorB);
                case "CROSSES_BELOW":
                    return new CrossedDownIndicatorRule(indicatorA, indicatorB);
                default:
                    throw new RuntimeException("Unsupported operator for INDICATOR comparison: " + operator);
            }
        }

        throw new RuntimeException("Unsupported indicatorBType: " + indicatorBType);
    }

    private Indicator getIndicator(
            String name,
            Map<String, String> params,
            BarSeries series,
            Map<String, Indicator> cache
    ) {
        String normalizedName = requireNonBlank(name, "indicator name").toUpperCase(Locale.ROOT);
        Map<String, String> safeParams = params == null ? Collections.emptyMap() : new TreeMap<>(params);
        String cacheKey = normalizedName + "|" + safeParams;

        if (cache.containsKey(cacheKey)) {
            return cache.get(cacheKey);
        }

        logger.info("Creating indicator: {} with params: {}", normalizedName, safeParams);

        ClosePriceIndicator closePrice = new ClosePriceIndicator(series);
        Indicator indicator;

        switch (normalizedName) {
            case "PRICE":
                indicator = closePrice;
                break;
            case "SMA":
                indicator = new SMAIndicator(closePrice, getRequiredPositiveIntParam(safeParams, "period", "SMA"));
                break;
            case "EMA":
                indicator = new EMAIndicator(closePrice, getRequiredPositiveIntParam(safeParams, "period", "EMA"));
                break;
            case "RSI":
                indicator = new RSIIndicator(closePrice, getRequiredPositiveIntParam(safeParams, "period", "RSI"));
                break;
            default:
                throw new RuntimeException("Unsupported indicator: " + normalizedName);
        }

        cache.put(cacheKey, indicator);
        return indicator;
    }

    private int getRequiredPositiveIntParam(Map<String, String> params, String key, String indicatorName) {
        String raw = params.get(key);
        if (raw == null || raw.isBlank()) {
            throw new RuntimeException(indicatorName + " requires parameter: " + key);
        }

        try {
            int value = Integer.parseInt(raw.trim());
            if (value <= 0) {
                throw new RuntimeException(indicatorName + " parameter " + key + " must be > 0");
            }
            return value;
        } catch (NumberFormatException ex) {
            throw new RuntimeException(indicatorName + " parameter " + key + " must be an integer");
        }
    }

    private String requireNonBlank(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new RuntimeException(fieldName + " is missing in strategy definition");
        }
        return value.trim();
    }

    private BacktestReportResponse calculateReport(
            String strategyName,
            String symbol,
            BarSeries series,
            TradingRecord record,
            Double initialCash
    ) {
        if (record.getTrades().isEmpty()) {
            return emptyReport(strategyName, symbol);
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

        BigDecimal maxDrawdown = ((BigDecimal) new MaximumDrawdownCriterion()
                .calculate(series, record)
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