package com.example.Product.Configuration;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    @Bean
    public NewTopic newTopic(){
        return TopicBuilder
                .name("product-add-topic")
                .partitions(3)
                .replicas(1)
                .build();


    }

    @Bean
    public NewTopic newTopic1(){
        return TopicBuilder
                .name("find-closest-delivery-boy")
                .partitions(4)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic newTopic12(){
        return TopicBuilder
                .name("razorpay-payment-details")
                .partitions(3)
                .replicas(1)
                .build();

    }


    //add product order count




}
