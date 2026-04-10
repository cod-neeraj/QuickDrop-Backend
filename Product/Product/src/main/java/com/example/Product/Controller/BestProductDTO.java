package com.example.Product.Controller;

import jdk.jfr.Name;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BestProductDTO {
    List<String> geohashes;
}
