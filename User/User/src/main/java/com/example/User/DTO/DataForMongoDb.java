package com.example.User.DTO;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DataForMongoDb {
    private String id;
    private String customerId;
    private String productId;
    private long visitedAt;
    private Boolean inWishList;
    private Boolean inCart;
}
