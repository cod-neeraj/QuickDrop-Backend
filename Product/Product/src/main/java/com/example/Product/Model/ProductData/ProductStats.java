package com.example.Product.Model.ProductData;

import jakarta.persistence.*;
import lombok.*;

//❤️❤️❤️❤️

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(
        name = "product_stats",
        schema = "product",
        indexes={
                @Index(name="idx_productStat_productId",columnList = "productId"),
                @Index(name="idx_productStat_geoHashCode",columnList = "geoHashCode,score DESC")
        }
)
public class ProductStats {
    @Id
    private String productId;

    private Long totalOrders;
    private Long totalVisits;
    private Long add_to_cart_count;
    private Long add_to_wishlist_count;
    private String geohashCode;
    private Double ratings;
    private Double score;


    @OneToOne
    @JoinColumn(name = "product_id", referencedColumnName = "product_id")
    private Product product;

}
