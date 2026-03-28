package com.example.User.Service.Order;

import com.example.User.Models.OrdersData.DeliveryBoyStatus;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeliveryBoyOrderStatusChange {
    private String phoneNumber;
    private String sellerPhoneNumber;
    private DeliveryBoyStatus status;
    private String orderId;
}
