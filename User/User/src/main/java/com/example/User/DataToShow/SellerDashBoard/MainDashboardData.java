package com.example.User.DataToShow.SellerDashBoard;

import lombok.*;

import java.util.Set;

@Getter @Setter @Builder
@AllArgsConstructor
@NoArgsConstructor
public class MainDashboardData {
    private String seller_name; //..
    private Long total_products;  //..
    private Long total_orders; //..
    private Long prev_month_orders; //..
    private Long pending_order;  //..
    private Double monthly_revenue;  //..
    private Double prev_month_revenue;  //..


}
