package com.example.Delivery.Config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    @Bean
    public NewTopic newTopic12(){
        return TopicBuilder
                .name("delivery-notifications")
                .partitions(3)
                .replicas(1)
                .build();

    }

    @Bean
    public NewTopic orderNotification(){
        return TopicBuilder
                .name("order-notifications")
                .partitions(4)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic deliveryBoytoUserRepo(){
        return TopicBuilder
                .name("deliveryBoy-userRepo")
                .partitions(4)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic updateSellerOrderStatus(){
        return TopicBuilder
                .name("deliveryBoy-updateOrderStatus")
                .partitions(4)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic updateOrderByDeliveryBoy(){
        return TopicBuilder
                .name("deliveryBoy-delivered-order")
                .partitions(4)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic addOrderInDeliveryBoy(){
        return TopicBuilder
                .name("addOrdersInDeliveryBoy")
                .partitions(4)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic outForDelivery(){
        return TopicBuilder
                .name("deliveryBoy-outForDelivery-order")
                .partitions(4)
                .replicas(1)
                .build();
    }
}
