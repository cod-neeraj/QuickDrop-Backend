package com.example.User.Repository;

import com.example.User.Models.User;
import com.example.User.RequestBodies.Frontend.PersonalInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User,String> {

    Optional<User> findByPhoneNumberOrEmailId(String phoneNumber, String email);

    Optional<User> findByPhoneNumber(String phoneNumber);

    //take out personalInfo
    @Query("SELECT new com.example.User.RequestBodies.Frontend.PersonalInfo(u.name, u.phoneNumber, u.emailId, u.dob, u.gender, u.role) " +
            "FROM User u WHERE u.phoneNumber = :phoneNumber")
    PersonalInfo findByPersonalInfo(@Param("phoneNumber") String phoneNumber);



    @Query("SELECT u.role FROM User u WHERE u.phoneNumber = :phoneNumber")
    String findRole(@Param("phoneNumber") String phoneNumber);

}
