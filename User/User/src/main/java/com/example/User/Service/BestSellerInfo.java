package com.example.User.Service;


import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BestSellerInfo {

    private String sellerId;
    private String name;
    private String phoneNumber;
    private String thumbnailImage;
    private String shopName;
    private String timings;
    private String street;
    private String city;
    private String state;
}
