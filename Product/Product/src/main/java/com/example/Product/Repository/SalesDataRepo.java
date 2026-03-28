package com.example.Product.Repository;

import com.example.Product.DTO.SalesDataToFrontend;
import com.example.Product.Model.ProductData.SalesData;
import com.example.Product.Model.ProductData.Status;
import com.example.Product.Model.ProductSearchAbleObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SalesDataRepo extends MongoRepository<SalesData,String> {
    List<SalesData> findTop5ByGeoHashCode(String geoHashCode);

    @Query("""
    SELECT s FROM SalesData s
    WHERE s.geohash IN :geohashes
      AND s.status = :status
""")
    List<SalesData> findByAllGeoHashes(
            @Param("geohashes") List<String> geohashes,
            @Param("status") Status status
    );


    @Query(value = "{ 'endDate': { $lte: ?0 }, 'status': 'ACTIVE' }")
    @Update("{ '$set': { 'status': 'EXPIRED' } }")
    long expireSales(LocalDateTime now);

}
