package com.example.Product.Service;

import lombok.*;
import org.hibernate.validator.constraints.pl.NIP;

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
