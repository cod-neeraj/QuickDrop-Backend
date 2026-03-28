package com.example.User.Service.Seller;

import com.example.User.Models.Seller.SellerStatus;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Top5orderDetails {
    private String username;
    private Double amount;
    private SellerStatus status;
}
