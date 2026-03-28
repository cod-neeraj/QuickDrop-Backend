package com.example.User.FeignInterface;

import com.example.User.DTO.DeliveryBoyOrderDetails;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name="delivery",url="http://localhost:1234")
public interface deliveryAllocationInterface {

    @GetMapping("/deliveryBoy/order/allocateDelivery")
    public ResponseEntity<Boolean> allocateDelivery(@RequestBody @Valid DeliveryBoyOrderDetails deliveryBoyOrderDetails) throws JsonProcessingException;
}
