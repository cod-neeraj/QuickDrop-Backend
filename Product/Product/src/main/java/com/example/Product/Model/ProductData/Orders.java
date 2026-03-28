package com.example.Product.Model.ProductData;

import com.example.Product.DTO.ProductDetailsForRedis;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Type;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;


//❤️❤️❤️❤️❤️
@Getter @Setter @Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="orders",schema = "product")
public class Orders {
    @Id
    private String orderId;

    private String userId;
    private String username;


    private String paymentId;

    private String paymentStatus;
    private String orderStatus;
    private Double amount;
    private LocalDate orderDate;
    private LocalDate deliverDate;

    @Type(JsonType.class)
    @Column(columnDefinition = "jsonb")
    private Map<Long,List<ProductDetailsForRedis>> productDetails;


}
