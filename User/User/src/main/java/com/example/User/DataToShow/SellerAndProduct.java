package com.example.User.DataToShow;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SellerAndProduct {
    private SellerMiniDetails seller;
    private List<ProductDetailsForRedis> products;
}
