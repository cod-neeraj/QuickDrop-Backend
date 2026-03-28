package com.example.Product.Model.ProductData;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.persistence.*;
import lombok.*;
import org.locationtech.jts.geom.Point;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.util.Set;

// ❤️❤️❤️❤️❤️
@Entity
@Table(
        name = "product",
        schema = "product",
        indexes = {
                @Index(name = "idx_product_id", columnList = "productId"),
                @Index(name = "idx_product_city_category", columnList = "city_id, product_category"),
                @Index(name = "idx_product_brand", columnList = "brand"),
                @Index(name = "idx_product_type", columnList = "type"),
                @Index(name = "idx_product_city_createdAt", columnList = "city_id, createdAt")
        }
)
@Getter @Setter @Builder
@AllArgsConstructor @NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Product {

    @Id
    private String productId;

    private String name;
    private String description;
    private String type;
    private String brand;
    private Integer quantity;

    @Enumerated(EnumType.STRING)
    private ProductCategory productCategory;

    private String shopkeeperId;
    private String phoneNumber;

    @CreatedDate
    @Column(updatable = false)
    private LocalDate createdAt;

    @LastModifiedDate
    private LocalDate updatedAt;

    private String defaultImageUrl;
    private Double defaultPrice;

    @JsonSerialize(using = PointSerializer.class)
    @Column(columnDefinition = "GEOGRAPHY(Point, 4326)")
    private Point location;

    private String geohash;

    @OneToMany(mappedBy = "products", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference
    private Set<Product_Variants> productVariants;

    @OneToMany(mappedBy = "products", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference
    private Set<ProductColorVariant> productColorVariants;

    private Double ratings;

    @OneToOne(mappedBy = "product", cascade = CascadeType.ALL)
    private ProductStats productStats;

}

