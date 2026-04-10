package com.example.User.Service;

import ch.hsr.geohash.GeoHash;
import com.example.User.DTO.DataForMongoDb;
import com.example.User.FeignInterface.addProductInterface;
import com.example.User.FeignInterface.BestProductDTO;
import com.example.User.FeignInterface.BestProductInfo;
import com.example.User.Models.Customer.Customer_Visited_product;
import com.example.User.Repository.CustomerRepositorys.CustomerRepo;
import com.example.User.Repository.CustomerRepositorys.Customer_visited_product_Repo;
import com.example.User.Repository.SellerRepositories.Seller_Stat_Repo;
import com.netflix.discovery.converters.Auto;
import io.lettuce.core.dynamic.support.ClassTypeInformation;
import jakarta.annotation.PostConstruct;
import org.modelmapper.internal.bytebuddy.asm.Advice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
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

    @Autowired
    addProductInterface AddproductInterface;

    @Autowired
    CustomerRepo customerRepo;

    @Autowired
    RedisTemplate<String, Object> redisTemplate;

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

   public RecommendationResponse getBestRecommendations(String phoneNumber,Double Longitude,Double Latitude){
        Boolean status = false;
        String customerId = customerRepo.findByPhoneUserId(phoneNumber);
       GeoHash geoHash = GeoHash.withCharacterPrecision(Latitude,Longitude,5);
       String hash = geoHash.toBase32();
       String key  = "recommendations:"+hash;
       List<Customer_Visited_product> list1= customerVisitedProductRepo.findByCustomerId(customerId);
       if(!list1.isEmpty()){
           status = true;
       }
       if(!status){
           RecommendationResponse cached =
                   (RecommendationResponse) redisTemplate.opsForValue().get(key);

           if(cached !=  null){
               return cached;
           }

       }
       System.out.println("👍👍 part-01");
       Map<String, Customer_Visited_product> visitedMap = new HashMap<>();

       for (Customer_Visited_product v : list1) {
           visitedMap.put(v.getProductId(), v);
       }
       GeoHash[] neighbors = geoHash.getAdjacent();

       List<String> searchGeohashes = new ArrayList<>();
       searchGeohashes.add(neighbors.toString());

       for (GeoHash neighbor : neighbors) {
           searchGeohashes.add(neighbor.toBase32());
       }
       System.out.println("👍👍 part-02");
       List<BestSellerInfo> bestSellerInfoList = findBestSeller(searchGeohashes);
       List<BestProductInfo> bestProductInfoList = findBestProduct(searchGeohashes);

       System.out.println("👍👍 part-03");

       RecommendationResponse recommendationResponse = RecommendationResponse.builder()
               .bestSellerInfoList(bestSellerInfoList)
               .bestProductInfoList(bestProductInfoList)
               .build();
 redisTemplate.opsForValue().set(key,recommendationResponse);
       for (BestProductInfo product : bestProductInfoList) {

           double finalScore = product.getScore();

           Customer_Visited_product visited = visitedMap.get(product.getProductId());

           if (visited != null) {
               double userScore =
                       visited.getScore();

               finalScore += userScore;
           }

           product.setScore(finalScore);
       }
       System.out.println("👍👍 part-04");
      bestProductInfoList.sort(
               (a, b) -> Double.compare(b.getScore(), a.getScore())
       );

       recommendationResponse.setBestProductInfoList(bestProductInfoList);
       System.out.println("👍👍 part-05");
       return recommendationResponse;



   }
   public List<BestSellerInfo> findBestSeller(List<String> searchGeohashes){
        List<BestSellerInfo> list = sellerStatRepo.findBestSeller(searchGeohashes);
       return list;
   }
   public List<BestProductInfo> findBestProduct(List<String> searchGeohashes){
       BestProductDTO bestProductDTO =  BestProductDTO.builder()
               .geohashes(searchGeohashes)
               .build();

       try {

           ResponseEntity<List<BestProductInfo>> response = AddproductInterface.findBestProducts(bestProductDTO);
           if (response.getStatusCode().is2xxSuccessful()) {
               return response.getBody();
           }
       }catch (Exception e){
           e.printStackTrace();
       }
        return null;
    }

}
