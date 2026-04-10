package com.example.User.Configuration;

import com.example.User.DTO.PaymentDTO.ProductSave;
import com.example.User.DTO.ProductStatRedis;
import com.example.User.DataToShow.OrderDetailsInRedis;
import com.example.User.DataToShow.OrderDetailsTosend;
import com.example.User.DataToShow.ProductAdd;
import com.razorpay.Order;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.List;

@Configuration
public class RedisConfig {

        @Bean
        @Primary
        public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory factory) {
            RedisTemplate<String, String> template = new RedisTemplate<>();
            template.setConnectionFactory(factory);
            template.setKeySerializer(new StringRedisSerializer());
            template.setValueSerializer(new StringRedisSerializer());
            return template;
        }

    @Bean
    public RedisTemplate<String, Integer> redisTemplateInteger(
            RedisConnectionFactory factory) {

        RedisTemplate<String, Integer> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());

        template.afterPropertiesSet();
        return template;
    }


    @Bean
    public RedisTemplate<String, ProductStatRedis> redisTemplateProductRedisStat(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, ProductStatRedis> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // Key as String
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());

        // Value as JSON
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());

        template.afterPropertiesSet();
        return template;
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplateObject(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        return template;
    }

    @Bean
    public RedisTemplate<String, List<ProductAdd>> redisTemplateProductSave(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, List<ProductAdd>> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // Key as String
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());

        // Value as JSON
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());

        template.afterPropertiesSet();
        return template;
    }
//    @Bean
//    public RedisTemplate<String, OrderDetailsTosend> redisTemplateSaveOrderInRedis(RedisConnectionFactory connectionFactory) {
//        RedisTemplate<String, OrderDetailsTosend> template = new RedisTemplate<>();
//        template.setConnectionFactory(connectionFactory);
//
//        // Key as String
//        template.setKeySerializer(new StringRedisSerializer());
//        template.setHashKeySerializer(new StringRedisSerializer());
//
//        // Value as JSON
//        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
//        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
//
//        template.afterPropertiesSet();
//        return template;
//    }


}

