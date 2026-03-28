package com.example.paymentService.DTO;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SellerMiniDetails {
    private String sellerId;
    private String name;
    private String shopName;
    private String street;
    private String city;
    private String state;
}
