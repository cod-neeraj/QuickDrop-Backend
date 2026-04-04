package com.example.Delivery.Service;

import com.example.Delivery.Models.DeliveryBoyStatus;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeliveryBoyOrderStatusChange {
    private String phoneNumber;
    private DeliveryBoyStatus status;
    private String orderId;
    private String sellerId;
}
