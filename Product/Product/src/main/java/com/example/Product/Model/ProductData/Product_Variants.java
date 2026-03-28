package com.example.Product.Model.ProductData;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;



// ❤️❤️❤️❤️❤️

@Getter @Setter @AllArgsConstructor
@NoArgsConstructor @Builder @Entity
@Table(name = "product_variants",schema = "product")
public class Product_Variants {
    @Id
    private String variant_id;
    private String attribute;
    private Double price;
    private Integer quantity;


    @ManyToOne
    @JoinColumn(name = "productId")
    @JsonBackReference
    private Product products;

}
