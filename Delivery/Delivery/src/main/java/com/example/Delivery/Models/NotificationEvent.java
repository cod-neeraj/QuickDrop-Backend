package com.example.Delivery.Models;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NotificationEvent {

    private String deliveryBoyId;
    private String orderId;
    private String shopkeeperLocation;
}
