package com.example.Delivery.Service;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeliveryBoyDetails {
    private String orderId;
    private String phoneNumber;
    private String deliveryBoyName;

}
