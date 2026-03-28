package com.example.User.Models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class ProductOrders {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private Long productId;
    private Integer quantity;
    private Long shopkeeperId;

    @ManyToOne
    @JoinColumn(name = "order_id")
    @JsonBackReference
    private Orders orders;
}
