package com.example.User.Models.OrdersData;

import com.example.User.Models.Customer.Customer_Address;
import com.example.User.Models.Customer.Customers;
import com.example.User.Models.PaymentMode;
import com.example.User.Models.PaymentStatus;
import com.example.User.Models.Seller.OrderStatus;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "mainOrder", schema = "Orders")
public class MainOrder {
    @Id
    private  String order_id;
    private OrderStatus orderStatus;
    private String payment_id;
    private PaymentMode paymentMethod;
    private PaymentStatus paymentStatus;
    private String userAddress;
    private String userId;
    private String username;
    private LocalDate orderDate;
    private Double totalAmount;
    private String deliveryBoyId;
    private String deliveryBoyName;

    @OneToMany(mappedBy = "mainOrder", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<SellerInfo> sellerInfo;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    @JsonBackReference
    private Customers customer;



}
