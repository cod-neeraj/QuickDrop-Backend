package com.example.User.Service.KafkaConsumer;

import com.example.User.DTO.KafkaDTO.ProductAddDTO;
import com.example.User.Models.Seller.Seller_Info;
import com.example.User.Repository.SellerRepositories.SellerRepo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.discovery.converters.Auto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProductConsumer {
    @Autowired
    SellerRepo sellerRepo;

    @Autowired
    private ObjectMapper objectMapper = new ObjectMapper();
    @KafkaListener(topics = "product-add-topic", groupId = "shopkeeper-consume")
    public void consumeProduct(String productJson) throws JsonProcessingException {
//        ProductAddDTO product = objectMapper.readValue(productJson, ProductAddDTO.class);
//       Optional<Seller_Info> sellerInfo=  sellerRepo.findByPhoneNumber(product.getShopkeeperPhoneNumber());
//
//
//        System.out.println("📩 Received product event: " + productJson);

    }

}
