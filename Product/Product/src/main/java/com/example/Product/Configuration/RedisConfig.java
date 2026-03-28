package com.example.Product.Configuration;

import com.example.Product.DTO.ProductPageDetails;
import com.example.Product.Model.FilterFetchModel;
import com.example.Product.Service.ProductStatRedis;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new StringRedisSerializer());
        return template;
    }

    @Bean
    public RedisTemplate<String, FilterFetchModel> redisTemplateObject(RedisConnectionFactory connectionFactory) {

        RedisTemplate<String, FilterFetchModel> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // Key as String
        template.setKeySerializer(new StringRedisSerializer());

        // Value as JSON (modern, no deprecated API)
        GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer();

        template.setValueSerializer(serializer);
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(serializer);

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
    public RedisTemplate<String, ProductPageDetails> redisTemplateProductPageDetails(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, ProductPageDetails> template = new RedisTemplate<>();
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
}