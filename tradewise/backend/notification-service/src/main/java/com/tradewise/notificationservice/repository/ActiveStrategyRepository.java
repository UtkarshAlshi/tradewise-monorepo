package com.tradewise.notificationservice.repository;

import com.tradewise.notificationservice.model.ActiveStrategy;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ActiveStrategyRepository extends JpaRepository<ActiveStrategy, Long> {
    List<ActiveStrategy> findAllBySymbolAndIsActive(String symbol, boolean isActive);
}
