package com.example.User.FeignInterface;

import com.example.User.Models.Seller.ProductCategory;
import lombok.*;
import org.locationtech.jts.geom.Point;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BestProductInfo {
    private String productId;
    private String name;
    private String type;
    private String brand;
    private ProductCategory productCategory;

    private String defaultImageUrl;
    private Double defaultPrice;

    private Double ratings;
    private Double score;

}
