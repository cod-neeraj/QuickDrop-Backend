package com.example.Product.Model;

import lombok.*;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class FilterFetchModel implements Serializable {
    private Map<String, Set<String>> attributes;
    private List<String> colors;
    private List<String> brands;

    public FilterFetchModel(Object attributes,Object colors,Object brands) {
        this.attributes = (Map<String, Set<String>>) attributes;
        this.colors =  (List<String>)colors;
        this.brands = ( List<String>)brands;
    }
}
