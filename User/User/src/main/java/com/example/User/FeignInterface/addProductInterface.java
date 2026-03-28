package com.example.User.FeignInterface;

import com.example.User.DataToShow.ProductCreateDto.ProductDTO;
import com.example.User.Service.Seller.SellerProductList;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name="product",url="http://localhost:9091")
public interface addProductInterface {

    @GetMapping("/product/addProduct")
     public ResponseEntity<String> addProduct(@RequestBody ProductDTO productDTO,
                                      @RequestParam("phoneNumber") String phoneNumber)throws JsonProcessingException;

    @GetMapping("/product/getAllSellerProducts/{phoneNumber}/{page}/{size}")
    public ResponseEntity<List<SellerProductList>> getAllProductSeller(@PathVariable String phoneNumber,
                                                                       @PathVariable Integer page,
                                                                       @PathVariable Integer size);

    @GetMapping("/product-shopkeeper/sellerProductCount/{phoneNumber}")
    public ResponseEntity<Long> getProductCount(@PathVariable String phoneNumber);
}

