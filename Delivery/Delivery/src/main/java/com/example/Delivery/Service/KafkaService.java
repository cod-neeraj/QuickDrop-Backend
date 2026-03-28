package com.example.Delivery.Service;

import com.example.Delivery.Models.NotificationEvent;
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

    public void sendOrderNotification() throws JsonProcessingException {
        NotificationEvent notificationEvent = NotificationEvent.builder()
                .deliveryBoyId("aa")
                .orderId("bb")
                .shopkeeperLocation("f")
                .build();
        String data = objectMapper.writeValueAsString(notificationEvent);
        kafkaTemplate.send("order-notifications",data);

    }
}
