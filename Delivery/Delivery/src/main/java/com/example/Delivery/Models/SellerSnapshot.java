package com.example.Delivery.Models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table
public class SellerSnapshot {
    @Id
    private Long id;
    private String sellerName;
    private String sellerShopName;
    private String sellerAddress;
    private String sellerPhoneNumber;

    @ManyToOne
    @JoinColumn(name = "orderId")
    @JsonBackReference
    private Orders orders;
}
