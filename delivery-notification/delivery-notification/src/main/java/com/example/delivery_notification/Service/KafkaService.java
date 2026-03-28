package com.example.delivery_notification.Service;

import com.example.delivery_notification.Model.NotificationEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaService {

    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    public KafkaService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @KafkaListener(topics = "order-notifications", groupId = "order-consume")
    public void consume(String event) throws JsonProcessingException {
        NotificationEvent notificationEvent = objectMapper.readValue(event,NotificationEvent.class);
        System.out.println("❤️❤️❤️");
        // Send to specific delivery boy via WebSocket
        messagingTemplate.convertAndSendToUser(
                notificationEvent.getDeliveryBoyId(),
                "/topic/notifications",
                event
        );
    }
}
