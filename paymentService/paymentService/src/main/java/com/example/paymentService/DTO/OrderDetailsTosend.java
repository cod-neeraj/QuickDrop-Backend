package com.example.paymentService.DTO;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderDetailsTosend {
    private String order_Id;
    private OrderDetailsInRedis orderDetailsInRedis;
}
