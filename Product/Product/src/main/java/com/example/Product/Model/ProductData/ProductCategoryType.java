package com.example.Product.Model.ProductData;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Type;

import java.util.List;
import java.util.Map;
import java.util.Set;


//❤️❤️❤️❤️
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(
        name = "product_category_type",
        schema = "product",
        indexes = @Index(name = "idx_product_category",columnList = "category")
)
public class ProductCategoryType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private ProductCategory category;

    private String type;


    @Type(JsonType.class)
    @Column(columnDefinition = "jsonb")
    private Map<String, Set<String>> attributes;


    @Type(JsonType.class)
    @Column(columnDefinition = "jsonb")
    private List<String> colors;

    @Type(JsonType.class)
    @Column(columnDefinition = "jsonb")
    private List<String> brands;


}
