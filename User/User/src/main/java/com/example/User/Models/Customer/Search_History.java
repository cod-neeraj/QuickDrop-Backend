package com.example.User.Models.Customer;

import com.example.User.Models.User;
import com.example.User.Models.Users;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "search_history",
        indexes = {
                @Index(name = "idx_searchHistory", columnList = "userId")
        })
public class Search_History {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String query;
    private String userId;
    private LocalDate date;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    @JsonBackReference
    private Customers customer;
}
