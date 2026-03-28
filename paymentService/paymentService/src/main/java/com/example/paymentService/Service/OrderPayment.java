package com.example.paymentService.Service;

import com.example.paymentService.DTO.*;
import com.example.paymentService.Model.Payment;
import com.example.paymentService.Repository.PaymentRepo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OrderPayment {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private RedisTemplate<String, OrderDetailsTosend> redisTemplateorder;

    private ObjectMapper objectMapper;

    @Autowired
    private PaymentRepo paymentRepo;
    private final RazorpayClient razorpayClient;

    public OrderPayment(@Value("${razorpay.key_id}") String keyId,
                         @Value("${razorpay.key_secret}") String keySecret) throws Exception {

        this.razorpayClient = new RazorpayClient(keyId,keySecret);
    }

    public Payment makePayment(String key,Double amount,String phoneNumber) throws JsonProcessingException, RazorpayException {

        JSONObject options = new JSONObject();
        options.put("amount", amount * 100); //
        options.put("currency", "INR");
        options.put("receipt", "txn_" + System.currentTimeMillis());

        com.razorpay.Order razorpayOrder = razorpayClient.orders.create(options);

        Payment razorPayOrderDetails = Payment.builder()
                .razorpayId(razorpayOrder.get("id"))                  // order ID
                .entity(razorpayOrder.get("entity"))
                .amount((Integer)razorpayOrder.get("amount"))// total amount in paise
                .amountPaid((Integer) razorpayOrder.get("amount_paid"))   // usually 0 initially
                .amountDue((Integer) razorpayOrder.get("amount_due"))     // equals amount initially
                .currency(razorpayOrder.get("currency"))            // "INR"
                .receipt(razorpayOrder.get("receipt"))              // your receipt string
                .status(razorpayOrder.get("status"))
                .orderId(key)
                .phoneNumber(phoneNumber)// "created"
                .attempts((Integer) razorpayOrder.get("attempts"))
                .expiryAt(new Date(System.currentTimeMillis() + 13 * 60 * 1000))
                .build();
        paymentRepo.save(razorPayOrderDetails);
        return razorPayOrderDetails;


    }
}
