package com.example.User.Models.OrdersData;

import com.example.User.Models.Customer.Customer_Address;
import com.example.User.Models.Customer.Customers;
import com.example.User.Models.Seller.SellerStatus;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity (name = "SellerInfoOrder")
@Table(name = "sellerInfo", schema = "Orders")
public class SellerInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String sellerId;
    private String name;
    private String shopName;
    private String street;
    private String city;
    private String state;

    private SellerStatus orderStatus;

    private DeliveryBoyStatus deliveryBoyStatus;

    @OneToMany(mappedBy = "sellerInfo", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<ProductDetailsInfo> productDetailsInfo;

    @ManyToOne
    @JoinColumn(name = "mainOrder_id")
    @JsonBackReference
    private MainOrder mainOrder;


}
