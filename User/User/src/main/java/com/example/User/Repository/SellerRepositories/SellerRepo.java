package com.example.User.Repository.SellerRepositories;

import com.example.User.DTO.BasicSellerInfo;
import com.example.User.DataToShow.SellerLocationRepo;
import com.example.User.DataToShow.SellerMiniDetails;
import com.example.User.Models.Seller.Seller_Info;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SellerRepo extends JpaRepository<Seller_Info,String> {

//    Optional<Seller_Info> findByPhoneNumber(String phoneNumber);

//    @Query("SELECT new com.example.User.DataToShow.SellerDashBoard.PersonalInfo" +
//            "(s.sellerId,s.phoneNumber,s.name,s.imageUrls,s.bio,s.shopName,s.gstumber,s.timings,s.street,s.city,s.state,s.country,s.zipCode,s.status)" +
//            "FROM Seller_Info s WHERE s.phoneNumber=:phoneNumber")
//    Optional<PersonalInfo> findPersonalInfo(@Param("phoneNumber") String phoneNumber);
//
//    @Query("SELECT s FROM SellerInfo s WHERE s.phoneNumber = :phoneNumber")
//    Optional<Seller_Info> findPersonalInfo(@Param("phoneNumber") String phoneNumber);
//
    @Query("SELECT new com.example.User.DataToShow.SellerMiniDetails(s.sellerId,s.name,s.shopName,s.street,s.city,s.state)FROM SellerInfo s WHERE s.sellerId = :sellerId")
    Optional<SellerMiniDetails> findSeller(@Param("sellerId") String sellerId);

    @Query("SELECT s FROM SellerInfo s WHERE s.phoneNumber = :phoneNumber")
   Optional<Seller_Info> findByPhoneNumber(@Param("phoneNumber") String phoneNumber);

    @Query("SELECT s.id,s.name FROM SellerInfo s WHERE s.phoneNumber = :phoneNumber")
    Object[] findNameByPhoneNumber(@Param("phoneNumber") String phoneNumber);


    @Query("SELECT s FROM SellerInfo s WHERE s.sellerId = :sellerId")
    Optional<Seller_Info> findBySellerId(@Param("sellerId") String sellerId);


    @Query("SELECT new com.example.User.DTO.BasicSellerInfo(s.sellerId,s.name,s.shopName,s.street,s.city,s.geohash) FROM SellerInfo s WHERE s.phoneNumber=:phoneNumber")
    Optional<BasicSellerInfo> findSellerBasic(@Param("phoneNumber") String phoneNumber);


    @Query("SELECT new com.example.User.DataToShow.SellerLocationRepo(s.sellerId,s.location) FROM SellerInfo s WHERE s.phoneNumber =:phoneNumber")
    Optional<SellerLocationRepo> findLocation(@Param("phoneNumber") String phoneNumber);

    @Query("""
   SELECT new com.example.User.DataToShow.SellerLocationRepo(
       s.sellerId,
       s.location
   )
   FROM SellerInfo s
   WHERE s.sellerId IN :sellerId
""")
    List<SellerLocationRepo> findLocationsByPhoneNumbers(
            @Param("sellerId") List<String> sellerId
    );

    @Query("SELECT s.sellerId FROM SellerInfo s WHERE s.phoneNumber = :phoneNumber")
    String findSellerIdByPhoneNumber(@Param("phoneNumber") String phoneNumber);

}

