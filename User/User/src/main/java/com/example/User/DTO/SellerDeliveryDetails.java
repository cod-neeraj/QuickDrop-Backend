package com.example.User.DTO;


import com.example.User.Models.Seller.OrderStatus;
import com.example.User.Models.Seller.SellerStatus;
import lombok.*;
import org.locationtech.jts.geom.Point;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SellerDeliveryDetails {

    private String sellerName;
    private String sellerShopName;
    private SellerStatus orderState;
    private String sellerAddress;
    private Double longitude;
    private Double latitude;

}
