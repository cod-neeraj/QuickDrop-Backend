package com.example.User.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SellerAnalytics {
    private Double totalRevenue;
    private Double totalRevenueLastMonth;
    private Long orders;
    private Long ordersLastMonth;
    private Long productSell;
    private Long productSellLastMonth;

}
