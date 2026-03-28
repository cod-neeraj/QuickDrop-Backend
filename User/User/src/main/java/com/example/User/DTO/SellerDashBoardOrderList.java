package com.example.User.DTO;

import com.example.User.Models.Seller.SellerStatus;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SellerDashBoardOrderList {
    private String order_id;
    private String username;
    private Double totalAmount;
    private String deliveryBoyName;
    private SellerStatus orderStatus;
}
