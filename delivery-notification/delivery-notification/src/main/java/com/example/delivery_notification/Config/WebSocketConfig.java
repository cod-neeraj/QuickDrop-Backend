package com.example.delivery_notification.Config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

        @Override
        public void configureMessageBroker(MessageBrokerRegistry config) {
            // Prefix for client subscriptions
            config.enableSimpleBroker("/queue", "/topic");
            // Prefix for sending messages from client
            config.setApplicationDestinationPrefixes("/app");
            // Enable user-based destinations
            config.setUserDestinationPrefix("/user");
        }

        @Override
        public void registerStompEndpoints(StompEndpointRegistry registry) {
            // This is the endpoint frontend will connect to
            registry.addEndpoint("/ws").setAllowedOriginPatterns("*").withSockJS();

        }

    }


