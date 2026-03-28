package com.example.User.DTO;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddAddressRequest {
    private String street;
    private String city;
    private String state;
    private String country;
    private String zipCode;
    private Boolean isDefault;
}
