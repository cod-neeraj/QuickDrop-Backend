package com.example.paymentService.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class verifyPayment {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public void verifyPayment(String paymentId,String orderId,String userId,Boolean success){
        kafkaTemplate.send("order-success",paymentId+","+orderId+","+userId+","+success);


    }
}
