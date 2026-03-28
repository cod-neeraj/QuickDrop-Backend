package com.example.Product.Model;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.Column;
import lombok.*;
import org.hibernate.annotations.Type;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductSearchReturnModel {

    private Page<ProductSearchAbleObject> content;
    private Map<String, Set<String>> attributes;
    private List<String> colors;
    private List<String> brands;

}
