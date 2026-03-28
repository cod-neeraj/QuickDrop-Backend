package com.example.Delivery.Models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.vladmihalcea.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Type;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Getter @Setter @Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "deliveries",schema = "delivery")
public class Orders {
    @Id
    private String orderId;

    private String customerName;

    private String deliveryLocation;

    private String userReview;

    private String deliveryBoyReview;

    private Double orderEarnings;

    private String orderStatus;

    private LocalDateTime deliveredAt;

    @ManyToOne
    @JoinColumn(name = "userId")
    @JsonBackReference
    private User user;

}
