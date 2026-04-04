package com.example.User.Configuration;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;

@Configuration
public class KafkaConfig {
    @Bean
    public ReplyingKafkaTemplate<String, String, String> replyingKafkaTemplate(
            ProducerFactory<String, String> pf,
            ConcurrentKafkaListenerContainerFactory<String, String> factory) {

        ConcurrentMessageListenerContainer<String, String> repliesContainer =
                factory.createContainer("product-check-response");
        repliesContainer.getContainerProperties().setGroupId("order-consume");

        return new ReplyingKafkaTemplate<>(pf, repliesContainer);
    }

    @Bean
    public NewTopic newTopic(){
        return TopicBuilder
                .name("place-order")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic addSales(){
        return TopicBuilder
                .name("add-sales")
                .partitions(3)
                .replicas(1)
                .build();
    }
    @Bean
    public NewTopic checkProductQuantity(){
        return TopicBuilder
                .name("check-product-quantity")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic addProductAgain(){
        return TopicBuilder
                .name("add-product-again")
                .partitions(3)
                .replicas(1)
                .build();
    }
    @Bean
    public NewTopic newTopic13(){
        return TopicBuilder
                .name("product-add")
                .partitions(3)
                .replicas(1)
                .build();

    }

    @Bean
    public NewTopic newTopic43(){
        return TopicBuilder
                .name("delivery-boy-allocation")
                .partitions(3)
                .replicas(1)
                .build();

    }

    @Bean
    public NewTopic newTopic47(){
        return TopicBuilder
                .name("delivery-boy-send-order-details")
                .partitions(3)
                .replicas(1)
                .build();

    }
}
