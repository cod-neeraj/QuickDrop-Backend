package com.example.User.Models.Customer;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "customer_visited_product")
public class Customer_Visited_product {

    @Id
    private String id;

    private String customerId;
    private String productId;
    private long visitedAt;
    private Boolean inWishList;
    private Boolean inCart;
    private Integer score;
}
