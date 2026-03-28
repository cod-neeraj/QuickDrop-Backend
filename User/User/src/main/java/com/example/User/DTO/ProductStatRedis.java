package com.example.User.DTO;


import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductStatRedis {

    private Long user_clicks;
    private Long order;
    private Long wishlist_count;
    private Long cart_count;
}
