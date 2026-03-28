package com.example.Product.Repository;

import com.example.Product.DTO.ProductCreateDTO.ProductColorVarientDTO;
import com.example.Product.Model.ProductData.ProductColorVariant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface ProductColorVariantRepo extends JpaRepository<ProductColorVariant,String> {

    @Query("""
    SELECT p
    FROM ProductColorVariant p
    WHERE p.products.id = :id
""")
    Set<ProductColorVariant> findColors(@Param("id") String id);

}
