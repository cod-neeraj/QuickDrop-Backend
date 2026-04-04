package com.example.User.Service.Order;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrdersDetailsForDeliveryBoy {
    private String orderId;

    private String customerName;
    private String deliveryBoyId;

    private String deliveryLocation;

    private String userReview;

    private String deliveryBoyReview;

    private Double orderEarnings;

    private String orderStatus;

    private LocalDateTime deliveredAt;
}
