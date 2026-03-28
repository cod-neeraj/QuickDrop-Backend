package com.example.Product.FeignInterface;

import com.example.Product.DTO.SellerMiniDetails;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name="user",url="http://localhost:9090")
public interface SellerInterface {

    @GetMapping("/seller/sellerMiniDetails")
    SellerMiniDetails getSeller(@RequestParam("id") String id);

}
