package com.example.Product.DTO;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserOrderDetails {
    private String order_Id;
    private String userId;
    private String userName;
    private Double totalAmount;

    private PlaceOrder orderDetailsInRedis;
}
