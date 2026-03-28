package com.example.Delivery.Models;

import jakarta.persistence.*;
import lombok.*;

@Getter @Setter @Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "ratings_reviews" ,schema = "delivery")
public class Rating_Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String user_id;
    private Double ratings;
    private Long no_ofOrders;
    private Long no_ofSuccessfulOrders;

}
