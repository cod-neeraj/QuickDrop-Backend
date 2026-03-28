package com.example.Delivery.Controller;

import com.example.Delivery.Config.WebSocketHandler;
import com.example.Delivery.DataToShow.BasicDashBoard;
import com.example.Delivery.DataToShow.MiniOrderDetails;
import com.example.Delivery.DataToShow.ProfileDetails;
import com.example.Delivery.Service.DashBoardService;
import com.example.Delivery.Service.RankedCandidate;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.discovery.converters.Auto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/deliveryBoy/dashBoard")
@CrossOrigin
public class DashBoardController {

    @Autowired
    ObjectMapper objectMapper;


    @Autowired
    private DashBoardService dashBoardService;


    @GetMapping("/basicDetails")
    public ResponseEntity<?> getBasicDashBoardDetails(@AuthenticationPrincipal UserDetails userDetails){
        BasicDashBoard basicDashBoard = dashBoardService.getDashboard(userDetails.getUsername());
        return ResponseEntity.ok(basicDashBoard);



    }

    @GetMapping("/getProfile")
    public ResponseEntity<?> getProfileDetails(@AuthenticationPrincipal UserDetails userDetails){
        ProfileDetails profileDetails = dashBoardService.getProfileDetails(userDetails.getUsername());
        return ResponseEntity.ok(profileDetails);

    }


    @PostMapping("/checkWebsocketConnection")
    public Boolean checkWebsocket() throws JsonProcessingException {
        objectMapper = new ObjectMapper();
        MiniOrderDetails miniOrderDetails = MiniOrderDetails.builder()
                .orderEarnings((double) 25.0)
                .droplocation("teri maa ki chut")
                .orderId("8264684459")
                .build();

            String id = "8264684459";
            String json = objectMapper.writeValueAsString(miniOrderDetails);

            WebSocketHandler.sendToDeliveryBoy(id, json);

        return true;
    }
    @GetMapping("/verifyDocuments")
    public ResponseEntity<?> verifyDocuments(){
        return ResponseEntity.ok(null);

    }
}
