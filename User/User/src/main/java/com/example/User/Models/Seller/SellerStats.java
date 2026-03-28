package com.example.User.Models.Seller;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.scheduling.annotation.EnableScheduling;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "shopkeeper_stats",schema = "seller")
public class SellerStats {
    @Id
    private String shopkeeperId;

    private Long totalOrders;
    private Long totalOrderReturn;
    private Double ratings;

    private Long score;

    private String geoHash;


    @OneToOne
    @JoinColumn(name = "seller_id", referencedColumnName = "sellerId")
    private Seller_Info seller;

}
