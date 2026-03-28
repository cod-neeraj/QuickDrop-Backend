package com.example.User.DataToShow;

import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.*;

@Getter @Setter @Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductIds {

    private Long productSizeId;
    private Integer quantity;
    private String shopkeeperId;

}
