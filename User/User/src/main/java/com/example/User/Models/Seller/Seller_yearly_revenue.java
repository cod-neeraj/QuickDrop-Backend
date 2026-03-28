package com.example.User.Models.Seller;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name="seller_yearly_revenue",schema = "seller")
public class Seller_yearly_revenue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long year;

    private Double revenue;
    private Integer orderCount;
    private Double averageOrderValue;

    @ManyToOne
    @JoinColumn(name = "sellerId")
    @JsonBackReference
    private Seller_Info seller;
}
