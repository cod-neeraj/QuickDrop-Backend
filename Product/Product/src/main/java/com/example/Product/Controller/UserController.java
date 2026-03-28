package com.example.Product.Controller;

import com.example.Product.DTO.SalesDataToFrontend;
import com.example.Product.Model.ProductData.SalesData;
import com.example.Product.Repository.ProductStatsRepo;
import com.example.Product.Response.ApiResponse;
import com.example.Product.Service.ProductService;
import jakarta.ws.rs.Path;
import org.apache.kafka.common.protocol.types.Field;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    ProductService productService;

    @GetMapping("/api/sales/nearby")
    public ResponseEntity<ApiResponse<List<SalesDataToFrontend>>> getNearbySales(
            @RequestParam Double lat,
            @RequestParam Double lon
    ) {
        List<SalesDataToFrontend> list = productService.findSales(lon, lat);

        if (list.isEmpty()) {
            return ResponseEntity.ok(
                    ApiResponse.<List<SalesDataToFrontend>>builder()
                            .success(true)
                            .message("No sales found near this location")
                            .data(List.of())
                            .build()
            );
        }

        return ResponseEntity.ok(
                ApiResponse.<List<SalesDataToFrontend>>builder()
                        .success(true)
                        .message("Sales fetched successfully")
                        .data(list)
                        .build()
        );
    }



}
