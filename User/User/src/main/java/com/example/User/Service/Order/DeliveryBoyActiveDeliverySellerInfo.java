package com.example.User.Service.Order;

import com.example.User.Models.OrdersData.DeliveryBoyStatus;
import com.example.User.Models.OrdersData.MainOrder;
import com.example.User.Models.OrdersData.ProductDetailsInfo;
import com.example.User.Models.Seller.SellerStatus;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeliveryBoyActiveDeliverySellerInfo {

    private String sellerId;
    private String name;
    private String shopName;
    private String street;
    private String city;
    private String state;

    private SellerStatus orderStatus;

    private DeliveryBoyStatus deliveryBoyStatus;

}
