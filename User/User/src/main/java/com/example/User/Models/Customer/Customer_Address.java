package com.example.User.Models.Customer;

import com.example.User.Models.User;
import com.example.User.Models.Users;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name="customer_addresses" , schema="customers")
public class Customer_Address {
    @Id
    @Column(name = "id", updatable = false, unique = true,nullable = false)
    private String id;

    private String street;
    private String city;
    private String state;
    private String country;
    private String postalCode;
    private String phoneNumber;
    private Boolean addressType;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    @JsonBackReference
    private Customers customer;
}
