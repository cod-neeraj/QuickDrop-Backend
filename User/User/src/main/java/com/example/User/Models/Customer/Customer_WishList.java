package com.example.User.Models.Customer;

import com.example.User.Models.User;
import com.example.User.Models.Users;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "customer_wishlist",schema = "customers")
public class Customer_WishList {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String productId;
    private String name;
    private String type;
    private String brand;
    private String defaultImage;

    private LocalDate date;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    @JsonBackReference
    private Customers customer;
}
