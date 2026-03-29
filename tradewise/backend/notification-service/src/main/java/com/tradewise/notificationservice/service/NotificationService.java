package com.tradewise.notificationservice.service;

import com.tradewise.notificationservice.dto.StockPriceUpdate;
import com.tradewise.notificationservice.model.ActiveStrategy;
import com.tradewise.notificationservice.model.Notification;
import com.tradewise.notificationservice.repository.ActiveStrategyRepository;
import com.tradewise.notificationservice.repository.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
public class NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    private final ActiveStrategyRepository activeStrategyRepository;
    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final boolean forceTrigger;

    public NotificationService(ActiveStrategyRepository activeStrategyRepository,
                               NotificationRepository notificationRepository,
                               SimpMessagingTemplate messagingTemplate,
                               @Value("${notification.force-trigger:false}") boolean forceTrigger) {
        this.activeStrategyRepository = activeStrategyRepository;
        this.notificationRepository = notificationRepository;
        this.messagingTemplate = messagingTemplate;
        this.forceTrigger = forceTrigger;
    }

    @KafkaListener(topics = "price-updates", groupId = "price-broadcasters")
    public void broadcastPriceUpdate(StockPriceUpdate update) {
        if (update == null || update.getSymbol() == null || update.getPrice() == null) {
            return;
        }

        String normalizedSymbol = update.getSymbol().toUpperCase(Locale.ROOT);
        update.setSymbol(normalizedSymbol);

        String topic = "/topic/prices/" + normalizedSymbol;
        logger.info("Broadcasting price update: {} -> {} to topic {}", normalizedSymbol, update.getPrice(), topic);

        messagingTemplate.convertAndSend(topic, update);
    }

    @KafkaListener(topics = "price-updates", groupId = "strategy-checkers")
    @Transactional
    public void handlePriceUpdateForAlerts(StockPriceUpdate event) {
        if (event == null || event.getSymbol() == null || event.getPrice() == null) {
            return;
        }

        String symbol = event.getSymbol().toUpperCase(Locale.ROOT);
        List<ActiveStrategy> monitors = activeStrategyRepository.findAllBySymbolAndIsActive(symbol, true);

        if (monitors.isEmpty()) {
            return;
        }

        for (ActiveStrategy monitor : monitors) {
            if (monitor.getUser() == null || monitor.getUser().getEmail() == null ||
                    monitor.getStrategy() == null || monitor.getStrategy().getName() == null) {
                logger.warn("Skipping malformed ActiveStrategy id={}", monitor.getId());
                continue;
            }

            if (forceTrigger || Math.random() < 0.01) {
                logger.info("SIMULATED TRIGGER: Strategy {} for symbol {} for user {}",
                        monitor.getStrategy().getName(), symbol, monitor.getUser().getEmail());

                String message = String.format(
                        "Strategy Triggered: '%s' for %s at price $%s",
                        monitor.getStrategy().getName(),
                        symbol,
                        event.getPrice()
                );

                Notification notification = new Notification();
                notification.setUser(monitor.getUser());
                notification.setMessage(message);
                notification.setRead(false);

                Notification savedNotification = notificationRepository.save(notification);

                messagingTemplate.convertAndSendToUser(
                        monitor.getUser().getEmail(),
                        "/queue/notifications",
                        savedNotification
                );
            }
        }
    }
}