package com.example.User.Service;

import lombok.*;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Builder
public class BestSellerInfo {

    private String sellerId;
    private String name;
    private String thumbnailImage;
    private String shopName;
    private String street;
    private String city;

    public BestSellerInfo(String sellerId,String name,String thumbnailImage,String shopName,String street,
                          String city
    ) {
        this.sellerId = sellerId;
        this.name = name;
        this.thumbnailImage = thumbnailImage;
        this.shopName = shopName;
        this.street = street;
        this.city = city;
    }
}
