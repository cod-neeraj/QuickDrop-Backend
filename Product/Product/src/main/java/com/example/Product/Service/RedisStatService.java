package com.example.Product.Service;

import com.example.Product.Model.ProductData.ProductStats;
import com.example.Product.Repository.ProductStatsRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class RedisStatService {


    @Autowired
    private RedisTemplate<String, ProductStatRedis> redisTemplate;

    @Autowired
    private ProductStatsRepo productStatsRepo;

    @Scheduled(fixedRate = 300_000)
    public void flushProductStats() {

        Set<String> keys = redisTemplate.keys("product:stats:*");
        if (keys == null || keys.isEmpty()) return;

        for (String key : keys) {
            String productId = key.split(":")[2];

            ProductStatRedis redisStats = redisTemplate.opsForValue().get(key);
            if (redisStats == null) continue;

            // Find product stats or create a new one if not found
            ProductStats dbStats = productStatsRepo.findById(productId).orElseGet(() -> {
                ProductStats newStats = new ProductStats();
                newStats.setProductId(productId);
                newStats.setTotalVisits(0L);
                newStats.setAdd_to_cart_count(0L);
                newStats.setAdd_to_wishlist_count(0L);
                newStats.setTotalOrders(0L);
                newStats.setScore(0.0);
                return newStats;
            });

            // Update stats
            dbStats.setTotalVisits(dbStats.getTotalVisits() + redisStats.getUser_clicks());
            dbStats.setAdd_to_cart_count(dbStats.getAdd_to_cart_count() + redisStats.getCart_count());
            dbStats.setAdd_to_wishlist_count(dbStats.getAdd_to_wishlist_count() + redisStats.getWishlist_count());
            dbStats.setTotalOrders(dbStats.getTotalOrders() + redisStats.getOrder());

            Long score = dbStats.getAdd_to_cart_count()
                    + dbStats.getAdd_to_wishlist_count()
                    + dbStats.getTotalVisits()
                    + dbStats.getTotalOrders();
            dbStats.setScore(score*1.0);

            productStatsRepo.save(dbStats);
            redisTemplate.delete(key);
        }
    }

}
