package com.example.Product.Model.ProductData;

import com.fasterxml.jackson.annotation.JsonBackReference;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Type;
import java.util.Set;


//❤️❤️❤️❤️
@Getter
@Setter
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(
        name = "product_color_variant",
        schema = "product",
        indexes = {
                @Index(name = "idx_productColorVariant_color", columnList = "color"),
        }
)
public class ProductColorVariant {
    @Id
    private String id;

    private String color;
    @Type(JsonType.class)

    @Column(columnDefinition = "jsonb")
    private Set<String> urls;

    @ManyToOne
    @JoinColumn(name = "productId")
    @JsonBackReference
    private Product products;
}
