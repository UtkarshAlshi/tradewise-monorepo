package com.tradewise.strategyservice.repository;

import com.tradewise.strategyservice.model.StrategyRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StrategyRuleRepository extends JpaRepository<StrategyRule, Long> {
}
