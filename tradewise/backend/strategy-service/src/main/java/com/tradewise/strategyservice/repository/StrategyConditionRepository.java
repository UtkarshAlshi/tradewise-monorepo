package com.tradewise.strategyservice.repository;

import com.tradewise.strategyservice.model.StrategyCondition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StrategyConditionRepository extends JpaRepository<StrategyCondition, Long> {
}
