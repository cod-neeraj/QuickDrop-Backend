package com.example.Delivery.Service.KafkaConsumer;

import com.example.Delivery.Models.User;
import com.example.Delivery.Service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.core.GeoOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.security.authorization.method.AuthorizeReturnObject;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Service
public class KafkaConsumer {
    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private UserService userService;


    @KafkaListener(topics="find-closest-delivery-boy",groupId = "order-consum")
    public void findDeliveryBoy(String name) throws JsonProcessingException {

        String[] parts = name.split(",");
        String longitude = parts[0];
        String latitude = parts[1];
        double longitude12 = Double.parseDouble(longitude);
        double latitude12 = Double.parseDouble(latitude);

//        GeoOperations<String, String> geoOps = redisTemplate.opsForGeo();
//        List<Point> positions = geoOps.position("shopLocations", name);
        userService.findBestDeliveryBoy(longitude12,latitude12,5);


    }


}
