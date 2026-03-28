package com.example.User.Service.User;

import ch.hsr.geohash.GeoHash;
import com.example.User.Models.Customer.Customer_Visited_product;
import com.example.User.Models.Customer.Customers;
import com.example.User.Models.Seller.SellerStats;
import com.example.User.Models.Seller.Seller_Info;
import com.example.User.Models.User;
import com.example.User.Repository.CustomerRepositorys.CustomerRepo;
import com.example.User.Repository.CustomerRepositorys.Customer_visited_product_Repo;
import com.example.User.Repository.SellerRepositories.SellerRepo;
import com.example.User.Repository.SellerRepositories.Seller_Stat_Repo;
import com.example.User.Repository.UserRepository;
import com.example.User.RequestBodies.Frontend.SignUp;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.locationtech.jts.geom.GeometryFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserBasicService implements UserDetailsService {


    @Autowired
    UserRepository userRepository;

    @Autowired
    CustomerRepo customerRepo;

    @Autowired
    Seller_Stat_Repo sellerStatRepo;

    @Autowired
    Customer_visited_product_Repo customerVisitedProductRepo;


    GeometryFactory geometryFactory = new GeometryFactory();

    @Autowired
    SellerRepo sellerRepo;
    public Boolean signUp(SignUp signUp){
        Optional<User> user = userRepository.findByPhoneNumberOrEmailId(signUp.getPhoneNumber(), signUp.getEmailId());
        if(user.isPresent()){
            return false;
        }
        String id = UUID.randomUUID().toString();
        User user1 = User.builder()
                .id(id)
                .name(signUp.getName())
                .phoneNumber(signUp.getPhoneNumber())
                .emailId(signUp.getEmailId())
                .password(signUp.getPassword())
                .dob(signUp.getDob())
                .gender(signUp.getGender())
                .role(String.valueOf(signUp.getRole()))
                .build();
        userRepository.save(user1);

        String geohash = GeoHash.withCharacterPrecision(signUp.getLatitude(),signUp.getLongitude(),5).toBase32();
        Point location = geometryFactory.createPoint(new Coordinate(signUp.getLongitude(),signUp.getLatitude()));
        location.setSRID(4326);

        if(signUp.getRole().equalsIgnoreCase("CUSTOMER")) {
            Customers customers = Customers.builder()
                    .userId(user1.getId())
                    .phoneNumber(user1.getPhoneNumber())
                    .geohash(geohash)
                    .build();
            customerRepo.save(customers);
        }else{
            Seller_Info sellerInfo = Seller_Info.builder()
                    .sellerId(id)
                    .phoneNumber(user1.getPhoneNumber())
                    .name(user1.getName())
                    .location(location)
                    .geohash(geohash)
                    .build();
            sellerRepo.save(sellerInfo);

            SellerStats sellerStats = SellerStats.builder()
                    .ratings(0.0)
                    .geoHash(geohash)
                    .score(0L)
                    .shopkeeperId(id)
                    .totalOrders(0L)
                    .totalOrderReturn(0L)
                    .seller(sellerInfo)
                    .build();
            sellerStatRepo.save(sellerStats);

        }
        return true;
    }

    public Boolean checkRole(String phoneNumber){
        String role = userRepository.findRole(phoneNumber);
        return "CUSTOMER".equalsIgnoreCase(role);
    }

    @Override
    public UserDetails loadUserByUsername(String phoneNumber) throws UsernameNotFoundException {
        User user = userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return new org.springframework.security.core.userdetails.User(
                user.getPhoneNumber(),
                user.getPassword(),
                List.of(new SimpleGrantedAuthority(user.getRole()))
        );

    }
}
