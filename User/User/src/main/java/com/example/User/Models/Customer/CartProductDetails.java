package com.example.User.Models.Customer;

import lombok.*;

import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartProductDetails {
    private String productId;
    private String name;
    private String type;
    private String brand;
    private String color;
    private String attribute;
    private Double price;
    private Long quantity;
    private String defaultImage;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CartProductDetails that = (CartProductDetails) o;
        return Objects.equals(productId, that.productId) &&
                Objects.equals(name, that.name)&&
                Objects.equals(type, that.type) &&
                Objects.equals(brand, that.brand)&&
                Objects.equals(color, that.color) &&
                Objects.equals(attribute, that.attribute)&&
                Objects.equals(price, that.price);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId);
    }

}
