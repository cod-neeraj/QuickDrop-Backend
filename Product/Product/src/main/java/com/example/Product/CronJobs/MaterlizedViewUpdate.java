package com.example.Product.CronJobs;

import com.example.Product.Repository.SalesDataRepo;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class MaterlizedViewUpdate {

    @PersistenceContext
    private EntityManager entityManager;

    @Scheduled(fixedDelay = 60000)
    @Transactional
    public void expireSales() {
        String sql = "REFRESH MATERIALIZED VIEW CONCURRENTLY product.product_search_view";
        try {
            log.info("Refreshing materialized view: product.product_search");
            entityManager.createNativeQuery(sql).executeUpdate();
            log.info("✅ Materialized view refreshed successfully: product.product_search");
        } catch (Exception e) {
            log.error("❌ Failed to refresh materialized view", e);
        }
    }


}
