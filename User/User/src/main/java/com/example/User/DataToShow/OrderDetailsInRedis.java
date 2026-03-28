package com.example.User.DataToShow;

import lombok.*;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderDetailsInRedis {
    private String deliveryAddressId;
    private String userName;
    private String userId;
    private Double totalAmount;
    private Map<String,SellerAndProduct> sellers;

}
