package com.example.User.Models;

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
public class WishList {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private Long productId;
    private LocalDate dateAdded;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private Users user;
}
