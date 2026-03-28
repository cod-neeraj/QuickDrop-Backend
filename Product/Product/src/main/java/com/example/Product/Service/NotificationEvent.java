package com.example.Product.Service;

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
