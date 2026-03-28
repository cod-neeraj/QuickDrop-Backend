package com.example.User.Service.Order;

import com.example.User.Models.OrdersData.ProductDetailsInfo;
import com.example.User.Models.Seller.SellerStatus;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetDetailOfOrderForSeller {
    private String order_id;
    private String username;
    private Double totalAmount;
    private String deliveryBoyName;
    private SellerStatus orderStatus;
    private List<ProductDetailsInfo> productDetailsInfo;
}
