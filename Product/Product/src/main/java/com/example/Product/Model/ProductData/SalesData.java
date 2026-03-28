package com.example.Product.Model.ProductData;

import ch.hsr.geohash.GeoHash;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Type;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "discount_data")
public class SalesData {

    @Id
    private String id;
    private String sellerId;
    private String shopName;
    private String shopAddress;
    private String imageUrl;
    private String heading;
    private String description;

    private LocalDate startDate;
    private LocalDate endDate;
    @Enumerated(EnumType.STRING)
    private Status status;

    private String geoHashCode;
}
