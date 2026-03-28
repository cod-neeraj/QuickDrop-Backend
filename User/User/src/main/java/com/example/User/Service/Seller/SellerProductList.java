package com.example.User.Service.Seller;

import com.example.User.Models.Seller.ProductCategory;
import lombok.*;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class SellerProductList {
    private String productId;
    private String name;
    private String type;
    private String brand;
    private ProductCategory productCategory;
    private LocalDate createdAt;
    private LocalDate updatedAt;
    private String defaultImageUrl;
    private Double defaultPrice;



}
