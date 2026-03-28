package com.example.User.Models.Seller;

import com.example.User.Models.Customer.Customers;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.net.ProxySelector;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name="seller_products",schema = "seller")
public class Seller_product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String productId;
    private String productName;
    private Double price;
    private String defaultImageUrl;
    private Integer quantity;

    private Long totalOrders;
    private Double ratings;
    @Enumerated(EnumType.STRING)
    private ProductCategory productCategory;

    @ManyToOne
    @JoinColumn(name = "seller_id")
    @JsonBackReference
    private Seller_Info seller;



}
