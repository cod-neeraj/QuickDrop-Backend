package com.example.User.Service.Order;

import com.example.User.Models.OrdersData.SellerInfo;
import com.example.User.Models.Seller.OrderStatus;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeliveryBoyActiveDelivery {
    private  String order_id;
    private OrderStatus orderStatus;
    private String userAddress;
    private String userId;
    private String username;
    private LocalDate orderDate;
    private Double totalAmount;

    private List<DeliveryBoyActiveDeliverySellerInfo> sellerInfoList;
}
