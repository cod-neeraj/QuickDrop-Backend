package com.example.Product.Repository;

import com.example.Product.Model.ProductData.ShopkeeperIds;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ShopkeeperIdsRepo extends JpaRepository<ShopkeeperIds,String> {
}
