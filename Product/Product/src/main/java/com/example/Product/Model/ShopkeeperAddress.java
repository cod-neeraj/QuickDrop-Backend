package com.example.Product.Model;

import jakarta.persistence.Embeddable;

@Embeddable
public class ShopkeeperAddress {
    private String shopName;
    private String street;
    private String city;
    private String state;
    private String postalCode;
    private String phoneNumber;
}
