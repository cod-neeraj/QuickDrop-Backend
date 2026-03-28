package com.example.Product.Repository;

import com.example.Product.Model.ProductData.Product_Variants;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductVariantsRepo extends JpaRepository<Product_Variants,String> {

}

