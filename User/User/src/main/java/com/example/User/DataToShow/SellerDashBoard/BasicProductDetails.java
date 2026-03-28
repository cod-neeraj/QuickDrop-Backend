package com.example.User.DataToShow.SellerDashBoard;

import com.example.User.Models.Seller.ProductCategory;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BasicProductDetails {
    private String productId;
    private String productName;
    private Double price;
    private ProductCategory productCategory;
    private Integer quantity;
}
