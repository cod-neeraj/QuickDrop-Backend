package com.example.paymentService.DTO;

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
