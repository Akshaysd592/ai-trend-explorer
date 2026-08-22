package com.aitrend.trend.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface SpringDataTrendRepository 
        extends JpaRepository<TrendJpaEntity, Long>, JpaSpecificationExecutor<TrendJpaEntity> {
}
