package com.example.User.DataToShow.SellerDashBoard;

import lombok.*;

import java.util.Set;

@Getter @Setter @Builder
@AllArgsConstructor
@NoArgsConstructor
public class RecentOrder {

    private String orderId;
    private String orderStatus;
    private Double price;
    private Set<BasicProductDetails> basicProductDetailsSet;
}
