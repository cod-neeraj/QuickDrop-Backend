package com.example.Delivery.Repository;

import com.example.Delivery.DTO.DeliveryBoyBasicDetails;
import com.example.Delivery.Models.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<User,String> {
    Optional<User> findByPhoneNumber(String phoneNumber);


    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("""
   UPDATE User t
   SET t.isVerified = true
   WHERE t.phoneNumber = :id
""")
    int markActiveIfFalse(@Param("id") String id);


    @Query("SELECT u.isVerified FROM User u WHERE u.phoneNumber = :phone")
    Boolean findVerifiedByPhone(@Param("phone") String phoneNumber);

    @Query("SELECT u.name,u.user_id FROM User u WHERE u.phoneNumber = :phone")
    Object[] findBasicDetails(@Param("phone") String phoneNumber);


    @Query("SELECT new com.example.Delivery.DTO.DeliveryBoyBasicDetails(u.name,u.phoneNumber) FROM User u WHERE u.phoneNumber=:id")
    DeliveryBoyBasicDetails findDetails(@Param("id") String id);


}
