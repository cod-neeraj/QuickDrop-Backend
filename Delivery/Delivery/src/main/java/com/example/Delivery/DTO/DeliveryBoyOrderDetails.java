package com.example.Delivery.DTO;

import lombok.*;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class DeliveryBoyOrderDetails {

    private String orderId;
    private String customerName;
    private String customerPhoneNumber;
    private String deliveryAddress;
    private Set<SellerDeliveryDetails> sellerDeliveryDetailsSet;


}
