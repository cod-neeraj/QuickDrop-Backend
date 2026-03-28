package com.example.Delivery.Controller;

import com.example.Delivery.DTO.DeliveryBoyOrderDetails;
import com.example.Delivery.Service.OrderService;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.Valid;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/deliveryBoy/order")
public class OrderController {

    @Autowired
    OrderService orderService;


    @PostMapping("/acceptOrder/{orderId}")
    public ResponseEntity<?> acceptOrder(@PathVariable String orderId, @AuthenticationPrincipal UserDetails userDetails) throws JsonProcessingException {
         orderService.acceptOrder(orderId, userDetails.getUsername());
         return ResponseEntity.ok("ok");

    }

}
