package com.example.User.DTO;

import com.example.User.DataToShow.SellerLocationRepo;
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
