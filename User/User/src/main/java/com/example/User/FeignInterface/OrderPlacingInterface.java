package com.example.User.FeignInterface;

import com.example.User.DTO.Payment;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.razorpay.RazorpayException;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name="paymentservice",url="http://localhost:9000")
public interface OrderPlacingInterface {
    @GetMapping("/payment/payment-order/{key}/{amount}/{phoneNumber}")
    public ResponseEntity<Payment> makePayment(@PathVariable String key,
                                               @PathVariable Double amount,
                                               @PathVariable String phoneNumber) throws JsonProcessingException, RazorpayException ;
}
