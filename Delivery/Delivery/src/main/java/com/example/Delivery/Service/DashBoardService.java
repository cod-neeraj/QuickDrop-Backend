package com.example.Delivery.Service;


import com.example.Delivery.DataToShow.ActiveOrder;
import com.example.Delivery.DataToShow.BasicDashBoard;
import com.example.Delivery.DataToShow.ProfileDetails;
import com.example.Delivery.Models.ActiveDelivery;
import com.example.Delivery.Models.User;
import com.example.Delivery.Repository.ActiveDeliveryRepo;
import com.example.Delivery.Repository.OrdersRepo;
import com.example.Delivery.Repository.UserRepo;
import jakarta.persistence.Basic;
import org.apache.kafka.common.quota.ClientQuotaAlteration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authorization.method.AuthorizeReturnObject;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class DashBoardService {

    @Autowired
    OrdersRepo ordersRepo;

    @Autowired
    UserRepo userRepo;

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    ActiveDeliveryRepo activeDeliveryRepo;

    public BasicDashBoard getDashboard(String deliveryBoyId) {

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfToday = LocalDate.now().atStartOfDay();
        LocalDateTime startOfWeek =
                LocalDate.now().with(DayOfWeek.MONDAY).atStartOfDay();
        String id = null;

        String name     = null;
        Object[] basicDetails = userRepo.findBasicDetails(deliveryBoyId);

        if (basicDetails != null && basicDetails.length > 0) {

            Object[] stats = (Object[]) basicDetails[0];
            id = stats[0] !=null ? stats[0].toString() : null;
            name = stats[1] !=null ? stats[1].toString() : null;
        }

        Object[] todayStats = ordersRepo.getTodayStats(
                deliveryBoyId, startOfToday, now
        );

        double todayEarnings = 0.0;
        long todayOrders = 0L;

        if (todayStats != null && todayStats.length > 0) {

            Object[] stats = (Object[]) todayStats[0];

            todayEarnings = stats[0] != null ? ((Number) stats[0]).doubleValue() : 0.0;
            todayOrders   = stats[1] != null ? ((Number) stats[1]).longValue()   : 0L;
        }


        Double weekEarnings = ordersRepo.getWeekEarnings(
                deliveryBoyId, startOfWeek, now
        );
        if (weekEarnings == null) {
            weekEarnings = 0.0;
        }


        String dropLocation = null;
        String username     = null;

        ActiveOrder activeOrder = null;

     String statsKey = "deliveryBoy:stats:" + deliveryBoyId;
        Object statusObj =
                redisTemplate.opsForHash().get(statsKey, "status");

        if (statusObj == null) {
            System.out.println("No status found for " + deliveryBoyId);
            BasicDashBoard basicDashBoard = BasicDashBoard.builder()
                    .deliveryBoyId(id)
                    .name(name)
                    .phoneNumber(deliveryBoyId)
                    .thisWeekEarnings(weekEarnings)
                    .status("OFFLINE")
                    .todayDeliveries(Math.toIntExact(todayOrders))
                    .todayEarnings(todayEarnings)
                    .build();
            return basicDashBoard;

        }
        String status = statusObj.toString();
        System.out.println(status+"🥳🥳");


        BasicDashBoard basicDashBoard = BasicDashBoard.builder()
                .deliveryBoyId(id)
                .name(name)
                .phoneNumber(deliveryBoyId)
                .thisWeekEarnings(weekEarnings)
                .status(status)
                .todayDeliveries(Math.toIntExact(todayOrders))
                .todayEarnings(todayEarnings)
                .build();

        return basicDashBoard;
    }

    public ProfileDetails getProfileDetails(String phoneNumber){
        Optional<User> user = userRepo.findByPhoneNumber(phoneNumber);
        ProfileDetails profileDetails = ProfileDetails.builder()
                .name(user.get().getName())
                .pic(user.get().getPic())
                .phoneNumber(user.get().getPhoneNumber())
                .gender(user.get().getGender())
                .dob(user.get().getDob())
                .aadharCardNumber(user.get().getAadharCardNumber())
                .aadharCardurl(user.get().getAadharCardurl())
                .panCardNumber(user.get().getPanCardNumber())
                .panCardurl(user.get().getPanCardurl())
                .homeAddress(user.get().getHomeAddress())
                .vehicleNumber(user.get().getVehicleNumber())
                .vehicleType(user.get().getVehicleType())
                .licenseNumber(user.get().getLicenseNumber())
                .isVerified(user.get().getIsVerified())
                .joiningDate(user.get().getJoiningDate())
          .build();
        return profileDetails;
    }





}
