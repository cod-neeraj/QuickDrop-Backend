package com.example.Delivery.Service;

import ch.hsr.geohash.GeoHash;
import com.example.Delivery.DTO.Location;
import com.example.Delivery.DTO.PartialSignUp;
import com.example.Delivery.DTO.SignUp;
import com.example.Delivery.Models.ActiveDelivery;
import com.example.Delivery.Models.SellerSnapshot;
import com.example.Delivery.Models.User;
import com.example.Delivery.Repository.ActiveDeliveryRepo;
import com.example.Delivery.Repository.UserRepo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Circle;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.GeoOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.domain.geo.GeoLocation;
import org.springframework.data.redis.domain.geo.Metrics;
import org.springframework.security.authorization.method.AuthorizeReturnObject;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {
    @Autowired
    private UserRepo userRepo;

    @Autowired
    private KafkaService kafkaService;

    @Autowired
    private ActiveDeliveryRepo activeDeliveryRepo;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RedisTemplate<String, String> redisTemplateString;

    public String partialSignUp(PartialSignUp partialSignUp) {
        String id = UUID.randomUUID().toString();
        User user = User.builder()
                .user_id(id)
                .name(partialSignUp.getName())
                .password(partialSignUp.getPassword())
                .phoneNumber(partialSignUp.getPhoneNumber())
                .role("DELIVER")
                .build();
        userRepo.save(user);
        return id;

    }

    public Boolean doSignUp(SignUp signUp,String phoneNumber){
        Optional<User> user = userRepo.findByPhoneNumber(phoneNumber);
        User user1 = user.get();

                user1.setPic(signUp.getPic());
                user1.setGender(signUp.getGender());
                user1.setDob(signUp.getDob());
                user1.setAadharCardNumber(signUp.getAadharCardNumber());
                user1.setAadharCardurl(signUp.getAadharCardurl());
                user1.setPanCardNumber(signUp.getPanCardNumber());
                user1.setPanCardurl(signUp.getPanCardurl());
                user1.setHomeAddress(signUp.getHomeAddress());
                user1.setJoiningDate(LocalDate.now());
                user1.setVehicleNumber(signUp.getVehicleNumber());
                user1.setVehicleType(signUp.getVehicleType());
                user1.setLicenseNumber(signUp.getLicenseNumber());
                user1.setIsVerified(true);

                userRepo.save(user1);
return true;

    }

    public Boolean checkUser(String phoneNumber){
        Optional<User> user = userRepo.findByPhoneNumber(phoneNumber);
        if(user.get() != null){
            return true;
        }
        return false;
    }

    public Boolean isVerified(String phoneNumber){
        return userRepo.findVerifiedByPhone(phoneNumber);

    }
    public Boolean goLiveOrOffline(String phoneNumber,Double lng,Double lat,String status){

        if(status.equalsIgnoreCase("OFFLINE")){
            String statsKey = "deliveryBoy:stats:" + phoneNumber;
            redisTemplate.opsForHash().put(statsKey, "status", "OFFLINE");
            return true;
        }
        GeoHash hash = GeoHash.withCharacterPrecision(lat, lng, 5);
        String hashCode = hash.toBase32();

        String geoKey = "deliveryBoy:location:" + hashCode;

        redisTemplate.opsForGeo().add(
                geoKey,
                new Point(lng, lat),
                phoneNumber
        );

        String statsKey = "deliveryBoy:stats:" + phoneNumber;

        redisTemplate.opsForHash().put(statsKey, "status", "ONLINE");
        redisTemplate.opsForHash().put(statsKey, "points", "0");
        return true;



    }
    public boolean updateStatus(Location location,String phoneNumber){
        User user = userRepo.findByPhoneNumber(phoneNumber).orElseThrow(()-> new RuntimeException("no user found"));
            String key = "deliveryBoy:Location:" + user.getUser_id();

            // Update status in hash
            redisTemplate.opsForHash().put(key, "status", location.getStatus());

            // Update location in GEO
            redisTemplate.opsForGeo().add("deliveryBoyLocations", new Point(location.getLongitude(), location.getLatitude()), String.valueOf(user.getUser_id()));

            return true;

    }
    public void findBestDeliveryBoy(Double longitude, Double latitude, int limit1) throws JsonProcessingException {
        GeoOperations<String, String> geoOps = redisTemplate.opsForGeo();

        // 1. Get nearest delivery boys by location
        GeoResults<RedisGeoCommands.GeoLocation<String>> geoResults = geoOps.search(
                "delivery:boys:geo",
                new Circle(new Point(longitude, latitude), new Distance(3, Metrics.KILOMETERS))
        );

        // 2. Filter only online delivery boys
        List<String> onlineBoys = geoResults.getContent().stream()
                .map(r -> r.getContent().getName()) // deliveryBoy id
                .filter(id -> {
                    String status = (String) redisTemplate.opsForHash().get("delivery:boy:" + id, "status"); // corrected key
                    return "AVAILABLE".equalsIgnoreCase(status);
                })
                .limit(limit1)
                .collect(Collectors.toList());

        kafkaService.sendOrderNotification();;
 for(String st:onlineBoys){
     System.out.println(st);

 }
    }

    @Override
    public UserDetails loadUserByUsername(String phoneNumber) throws UsernameNotFoundException {
        User user = userRepo.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return new org.springframework.security.core.userdetails.User(
                user.getPhoneNumber(),
                user.getPassword(),
                List.of(new SimpleGrantedAuthority(user.getRole()))
        );
    }
}
