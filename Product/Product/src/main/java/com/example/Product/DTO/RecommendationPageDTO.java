package com.example.Product.DTO;

import com.example.Product.Model.RecommendationDataDTO;
import lombok.*;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RecommendationPageDTO {
    List<SalesDataToFrontend> list;
    List<RecommendationDataDTO> list1;
}
