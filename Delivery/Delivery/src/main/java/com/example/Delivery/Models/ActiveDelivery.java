package com.example.Delivery.Models;

import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "activeDelivery_data")
public class ActiveDelivery {
    @Id
    private String id;
    private String orderId;
    private String deliveryBoyPhoneNumber;
    private Map<String,SellerStatus> sellerOrderStatus;
    private Map<String,DeliveryBoyStatus> deliveryBoyOrderStatus;

}
