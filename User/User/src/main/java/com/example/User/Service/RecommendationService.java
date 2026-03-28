package com.example.User.Service;

import ch.hsr.geohash.GeoHash;
import com.example.User.DTO.DataForMongoDb;
import com.example.User.Models.Customer.Customer_Visited_product;
import com.example.User.Repository.CustomerRepositorys.Customer_visited_product_Repo;
import com.example.User.Repository.SellerRepositories.Seller_Stat_Repo;
import com.netflix.discovery.converters.Auto;
import io.lettuce.core.dynamic.support.ClassTypeInformation;
import jakarta.annotation.PostConstruct;
import org.modelmapper.internal.bytebuddy.asm.Advice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.sql.SQLOutput;
import java.util.*;

@Service
public class RecommendationService {

    @Autowired
    Customer_visited_product_Repo customerVisitedProductRepo;

    @Autowired
    Seller_Stat_Repo sellerStatRepo;

    public int recommendationScore(Customer_Visited_product customerVisitedProduct, long visitTime) {
        long totalTime = visitTime + customerVisitedProduct.getVisitedAt();
        int points = 0;

        if (totalTime > 300 && customerVisitedProduct.getVisitedAt() <= 300) {
            points = 3;
        } else if (totalTime > 200 && customerVisitedProduct.getVisitedAt() <= 200) {
            points = 2;
        } else if (totalTime > 100 && customerVisitedProduct.getVisitedAt() <= 100) {
            points = 1;
        }

        return points;
    }

   public List<Customer_Visited_product> getUserData(){

        List<Customer_Visited_product> list = customerVisitedProductRepo.findByCustomerId("nfhrbr");
        return list;
   }

   public List<BestSellerInfo> findBestSeller(Double Longitude,Double Latitude){

      GeoHash geoHash = GeoHash.withCharacterPrecision(Latitude,Longitude,5);
      String hash = geoHash.toBase32();
       GeoHash[] neighbors = geoHash.getAdjacent();

       List<String> searchGeohashes = new ArrayList<>();
       searchGeohashes.add(neighbors.toString());

       for (GeoHash neighbor : neighbors) {
           searchGeohashes.add(neighbor.toBase32());
       }

       List<BestSellerInfo> list = sellerStatRepo.findBestSeller(searchGeohashes);
       return list;
   }

}
