package com.example.User.DTO;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BasicSellerInfo {
    private String sellerId;
    private String name;
    private String shopName;
    private String street;
    private String city;
    private String geohash;
}
