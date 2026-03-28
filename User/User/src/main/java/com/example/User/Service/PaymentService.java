package com.example.User.Service;

import com.example.User.DTO.PaymentDTO.ProductSave;
import com.example.User.DTO.ProductStatRedis;
import com.example.User.DataToShow.ProductAdd;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Service
public class PaymentService {

    @Autowired
    private KafkaService kafkaService;
    @Autowired
    private RedisTemplate<String, List<ProductAdd>> redisTemplate;

    private static final long CACHE_TTL_MINUTES = 15;

    private String getRedisKey(String phoneNumber) {
        return "user:" + phoneNumber + ":";
    }

    public boolean addProductToRedisOrder(String phoneNumber, ProductAdd productSave) {
        String redisKey = getRedisKey(phoneNumber);

        List<ProductAdd> existingList = redisTemplate.opsForValue().get(redisKey);
        if (existingList == null) {
            existingList = new ArrayList<>();
        }

        existingList.add(productSave);

        redisTemplate.opsForValue().set(redisKey, existingList, CACHE_TTL_MINUTES, TimeUnit.MINUTES);
        return true;
    }

    public boolean updateProductToRedisOrder(String phoneNumber, String productId) throws ExecutionException, InterruptedException, TimeoutException {
return false;

    }

    public List<ProductAdd> fetchProductFromRedisOrder(String phoneNumber) {
        String redisKey = getRedisKey(phoneNumber);
        List<ProductAdd> products = redisTemplate.opsForValue().get(redisKey);
        return products;
    }

    public boolean deleteRedisOrderProduct(String phoneNumber, ProductAdd productToRemove) {
        String redisKey = getRedisKey(phoneNumber);

        List<ProductAdd> existingList = redisTemplate.opsForValue().get(redisKey);
        if (existingList == null || existingList.isEmpty()) {
            return false;
        }
        kafkaService.addProduct(productToRemove.getProductId());

        existingList.removeIf(p -> p.getProductId().equals(productToRemove.getProductId()));


        redisTemplate.opsForValue().set(redisKey, existingList, CACHE_TTL_MINUTES, TimeUnit.MINUTES);
        return true;
    }
}
