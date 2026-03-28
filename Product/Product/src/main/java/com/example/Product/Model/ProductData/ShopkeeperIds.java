package com.example.Product.Model.ProductData;

import com.example.Product.DTO.ProductIds;
import com.fasterxml.jackson.annotation.JsonBackReference;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Type;

import java.util.List;

@Getter @Setter @Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "shopkeeperIds",schema = "product")
public class ShopkeeperIds {
    @Id
    private String id;
    private String shopkeeperId;

    @Type(JsonType.class)
    @Column(columnDefinition = "jsonb")
    private List<ProductIds> productDetailsList ;

    @ManyToOne
    @JoinColumn(name = "order_id")
    @JsonBackReference
    private Orders orders;
}
