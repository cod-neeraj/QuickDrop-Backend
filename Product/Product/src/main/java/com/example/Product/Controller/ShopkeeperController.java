package com.example.Product.Controller;

import com.example.Product.DTO.ProductCard;
import com.example.Product.Service.ProductService;
import com.example.Product.Service.ShopKeeperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/product-shopkeeper")
public class ShopkeeperController {

    @Autowired
    ShopKeeperService shopKeeperService;

    @Autowired
    ProductService productService;
//    @GetMapping("/low-stock-products")
//    public List<ProductCard> getLowStockProducts(Long shopkeeperId){
//        List<ProductCard> list = shopKeeperService.getLowStockList(shopkeeperId);
//        if(list.isEmpty()){
//            return null;
//
//        }
//        return list;
//
//    }
    @GetMapping("/sellerProductCount/{phoneNumber}")
    public ResponseEntity<Long> getProductCount(@PathVariable String phoneNumber){
        Long count = productService.getProductCount(phoneNumber);
        if(count == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }else{
            return ResponseEntity.ok(count);
        }

    }
}
