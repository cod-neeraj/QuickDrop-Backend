package com.example.User.Models.OrdersData;

import com.example.User.Models.Customer.Customers;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "productInfo",schema = "Orders")
public class ProductDetailsInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String productId;
    private String name;
    private String type;
    private String brand;
    private String color;
    private String attribute;
    private Double price;
    private Long quantity;

    @ManyToOne
    @JoinColumn(name = "sellerInfo_id")
    @JsonBackReference
    private SellerInfo sellerInfo;



}
