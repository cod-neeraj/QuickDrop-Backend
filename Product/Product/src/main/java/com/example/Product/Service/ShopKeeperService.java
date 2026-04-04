package com.example.Product.Service;

import com.example.Product.DTO.LowStockDTO;
import com.example.Product.Repository.ProductRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShopKeeperService {

    @Autowired
    ProductRepo productRepo;

    public List<LowStockDTO> getLowStockList(String phoneNumber) {
        List<LowStockDTO> productCards = productRepo.findLowStockProduct(phoneNumber);
        return productCards;
    }
}


//    }

