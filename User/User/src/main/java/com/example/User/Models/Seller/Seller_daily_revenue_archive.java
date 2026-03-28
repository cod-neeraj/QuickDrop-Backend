package com.example.User.Models.Seller;

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
@Table(name="seller_daily_revenue_archive",schema = "seller")
public class Seller_daily_revenue_archive {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date;

    private Double revenue;
    private Integer orderCount;
    private Double averageOrderValue;

    @ManyToOne
    @JoinColumn(name = "seller_id")
    @JsonBackReference
    private Seller_Info seller;

}
