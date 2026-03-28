package com.example.Product.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaService {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    public void sendProductEvent(Object product) {
        try {
            // Convert product object to JSON string
            String productJson = objectMapper.writeValueAsString(product);

            // Send JSON to Kafka topic
            kafkaTemplate.send("product-add-topic", productJson);


            System.out.println("✅ Sent product event: " + productJson);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to convert product to JSON", e);

        }
    }

    public void sendDeliveryBoyRequest(String name){
        kafkaTemplate.send("find-closest-delivery-boy",name);
        System.out.println("Sent ❤️❤️❤️❤️❤️❤️");

    }
    public void sendRazorPayDetails(Object product) {
        try {
            // Convert product object to JSON string
            String productJson = objectMapper.writeValueAsString(product);

            // Send JSON to Kafka topic
            kafkaTemplate.send("razorpay-payment-details", productJson);


            System.out.println("✅ Sent product event: " + productJson);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to convert product to JSON", e);

        }
    }



}
