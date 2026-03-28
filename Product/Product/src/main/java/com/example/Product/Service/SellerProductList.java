package com.example.Product.Service;

import com.example.Product.Model.ProductData.*;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.persistence.*;
import lombok.*;
import org.locationtech.jts.geom.Point;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDate;
import java.util.Set;

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
