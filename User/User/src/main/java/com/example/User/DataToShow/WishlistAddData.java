package com.example.User.DataToShow;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WishlistAddData {
    private String productId;
    private String name;
    private String type;
    private String brand;
    private String defaultImage;
}
