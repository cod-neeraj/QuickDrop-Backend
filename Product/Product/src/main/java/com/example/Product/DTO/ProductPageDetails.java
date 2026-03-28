package com.example.Product.DTO;

import com.example.Product.DTO.ProductCreateDTO.ProductColorVarientDTO;
import com.example.Product.Model.ProductData.ProductCategory;
import com.example.Product.Model.ProductData.Product_Variants;
import lombok.*;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductPageDetails {
    private String id;
    private String shopkeeperId;
    private String name;
    private String description;
    private String type;
    private String brand;
    private String defaultImage;
    private ProductCategory productCategory;
    private SellerMiniDetails sellerMiniDetails;
    private Set<ProductColorVarientDTO> colors;
    private Set<Product_Variants> productVariants;
}
