package com.example.User.DataToShow.ProductCreateDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductColorVariant {
    private String color;
    private Set<String> urls;
}
