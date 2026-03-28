package com.example.User.Models.Customer;
import com.fasterxml.jackson.annotation.JsonBackReference;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Type;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "customer_cart",schema = "customers")
public class Customer_Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String sellerId;
    private String name;
    private String shopName;
    private String street;
    private String city;
    private String state;

    @Type(JsonType.class)
    @Column(columnDefinition = "jsonb")
    private List<CartProductDetails> productDetails;


    private LocalDate date;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    @JsonBackReference
    private Customers customer;
}
