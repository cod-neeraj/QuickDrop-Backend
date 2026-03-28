package com.example.Product.Model.ProductData;

import com.example.Product.Model.ProductCard;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Type;
import org.locationtech.jts.geom.Point;


//❤️❤️❤️❤️❤️
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Immutable
@Entity
@Table(name = "product_search",schema = "product")
public class ProductSearchView {

    @Id
    @Column(name = "product_id")
    private String productId;

     @Column(name = "searchable")
     private String searchable;

    @Type(JsonType.class)
    @Column(columnDefinition = "jsonb")
    private ProductCard attributes;

    @JsonSerialize(using = PointSerializer.class)
    @Column(columnDefinition = "geography(Point, 4326)")
    private Point location;
}


