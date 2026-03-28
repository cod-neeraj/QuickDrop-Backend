package com.example.Delivery.DTO;


import com.example.Delivery.Service.OrderService;
import lombok.*;
import org.springframework.data.geo.Point;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SellerDeliveryDetails {

    private String sellerName;
    private String sellerShopName;
    private OrderStatus orderState;
    private String sellerAddress;
    private Double longitude;
    private Double latitude;


}
