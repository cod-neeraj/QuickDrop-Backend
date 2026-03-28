package com.example.User.Service.KafkaConsumer;

import org.springframework.kafka.annotation.KafkaListener;

public class OrderConsumer {

    @KafkaListener(topics = "order-notification", groupId = "order-consume")
    public void acceptOrder(String result){
        System.out.println("❤️❤️❤️❤️❤️");
        System.out.println(result);
    }
}
