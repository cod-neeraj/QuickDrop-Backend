package com.example.User.Service;

import com.example.User.FeignInterface.BestProductInfo;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RecommendationResponse {
    List<BestSellerInfo> bestSellerInfoList;
    List<BestProductInfo> bestProductInfoList;
}
