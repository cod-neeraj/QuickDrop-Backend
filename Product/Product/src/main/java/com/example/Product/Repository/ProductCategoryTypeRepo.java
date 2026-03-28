package com.example.Product.Repository;

import com.example.Product.Model.FilterFetchModel;
import com.example.Product.Model.ProductData.ProductCategory;
import com.example.Product.Model.ProductData.ProductCategoryType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Repository
public interface ProductCategoryTypeRepo extends JpaRepository<ProductCategoryType,Long> {

    @Query("SELECT DISTINCT p.category FROM ProductCategoryType p")
    List<ProductCategory> findAllCategories();

    @Query("SELECT  p.type FROM ProductCategoryType p WHERE p.category = :category")
    List<String> findByCategory(@Param("category") ProductCategory category);

    @Query("SELECT p.attributes FROM ProductCategoryType p WHERE p.category = :category AND p.type =:type")
    List<Map<String, Set<String>>> findByCategoryAndType(@Param("category") ProductCategory category,
                                                         @Param("type") String type);

    @Query("SELECT p FROM ProductCategoryType p WHERE p.type =:type")
    ProductCategoryType findByType(@Param("type") String type);

    @Query("""
    SELECT p.attributes, p.colors, p.brands
    FROM ProductCategoryType p
    WHERE p.type = :type
""")
    Object[] findByTypeFilters( @Param("type") String type);


}
