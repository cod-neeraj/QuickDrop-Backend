package com.example.User.Service.Order;

import com.example.User.Models.OrdersData.SellerInfo;
import com.example.User.Models.Seller.OrderStatus;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Builder
public class DeliveryBoyActiveDeliveryRepoData {
    private String order_id;
    private OrderStatus orderStatus;
    private String userAddress;
    private String userId;
    private String username;
    private LocalDate orderDate;
    private Double totalAmount;
    public DeliveryBoyActiveDeliveryRepoData(
            String order_id,
            OrderStatus orderStatus,
            String userAddress,
            String userId,
            String username,
            LocalDate orderDate,
            Double totalAmount
    ) {
        this.order_id = order_id;
        this.orderStatus = orderStatus;
        this.userAddress = userAddress;
        this.userId = userId;
        this.username = username;
        this.orderDate = orderDate;
        this.totalAmount = totalAmount;
    }


}
