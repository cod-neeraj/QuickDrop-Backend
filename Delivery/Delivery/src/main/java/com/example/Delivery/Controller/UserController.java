package com.example.Delivery.Controller;

import com.example.Delivery.DTO.Location;
import com.example.Delivery.DTO.Login;
import com.example.Delivery.DTO.PartialSignUp;
import com.example.Delivery.DTO.SignUp;
import com.example.Delivery.Models.User;
import com.example.Delivery.Service.OrderService;
import com.example.Delivery.Service.UserService;
import com.example.Delivery.Utils.JwtUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.ws.rs.Path;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.config.RepositoryConfigurationSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.nio.DoubleBuffer;
import java.util.List;

@RestController
@RequestMapping("/deliver")
@CrossOrigin
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    OrderService orderService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/partialSignUp")
    public ResponseEntity<?> partialSignUp(@RequestBody @Valid PartialSignUp partialSignUp){
        partialSignUp.setPassword(passwordEncoder.encode(partialSignUp.getPassword()).toString());
        String id = userService.partialSignUp(partialSignUp);
        return ResponseEntity.ok(id);

    }

    @PostMapping("/signUp")
    public ResponseEntity<?> signUpController(@RequestBody @Valid SignUp signUp,@AuthenticationPrincipal UserDetails userDetails) {
        String phoneNumber = userDetails.getUsername();
        Boolean bool = userService.doSignUp(signUp,phoneNumber);
        if (bool) {
            return ResponseEntity.ok(bool);
        }
        return ResponseEntity.status(HttpStatus.CONFLICT).body(bool);

    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody Login login,
                                       HttpServletResponse httpServletResponse) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(login.getPhoneNumber(), login.getPassword()));
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String token = jwtUtil.generateToken(userDetails);
            Cookie cookie = new Cookie("jwt", token);
            cookie.setHttpOnly(true);
            cookie.setSecure(false);
            cookie.setPath("/");
            cookie.setMaxAge(10 * 60 * 60);
            httpServletResponse.addCookie(cookie);
            Boolean bool = userService.isVerified(login.getPhoneNumber());


            return ResponseEntity.ok(bool);
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(false);
        }

    }

    @GetMapping("/me")
    public ResponseEntity<?> checkUser(@AuthenticationPrincipal UserDetails userDetails){
        String phoneNumber = userDetails.getUsername();
        Boolean bool = userService.checkUser(phoneNumber);
        if (bool){
            return ResponseEntity.ok(true);
        }
        return ResponseEntity.ok(false);

    }

    @PostMapping("/goLiveOrOffline/{status}/{longitude}/{latitude}")
    public ResponseEntity<?> goLiveOrOffline(@PathVariable String status , @PathVariable Double longitude,@PathVariable Double latitude , @AuthenticationPrincipal UserDetails userDetails){
        Boolean bool  =userService.goLiveOrOffline(userDetails.getUsername(),longitude,latitude,status);
        return ResponseEntity.ok(bool);
    }

    @PostMapping("/statusChange")
    public ResponseEntity<?> statusChange(@RequestBody Location location){
        Authentication authentication = (Authentication) SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() &&
                authentication.getPrincipal() instanceof UserDetails) {

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String phoneNumber = userDetails.getUsername();
          Boolean bool = userService.updateStatus(location,phoneNumber);
            if(bool){

                return ResponseEntity.status(HttpStatus.OK).body("done");
            }
            return ResponseEntity.status(HttpStatus.NOT_MODIFIED).body("not");
        }else{
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("gg");

        }
    }


    @PostMapping("/updateOrderStatus/{status}/{orderId}/{sellerId}")
    public ResponseEntity<?> updateOrderStatus(@AuthenticationPrincipal UserDetails userDetails,
                                               @PathVariable String status,
                                               @PathVariable String orderId,
                                               @PathVariable String sellerId
                                               ) throws JsonProcessingException {
        String phoneNumber = userDetails.getUsername();
        orderService.updateOrderStatus(phoneNumber,status,orderId,sellerId);
        return ResponseEntity.ok(true);

    }

    @PostMapping("markDelivered/{orderId}")
    public ResponseEntity<?> deliverTheOrder(@AuthenticationPrincipal UserDetails userDetails,@PathVariable String orderId){
        String phoneNumber = userDetails.getUsername();
        orderService.setDeliveredTheOrder(phoneNumber,orderId);
        return ResponseEntity.ok("succesfully");
    }
//    @GetMapping("/deliveryBoy/{longitude}/{latitude}")
//    public ResponseEntity<List<String>> findBestDeliveryBoy(@PathVariable Double longitude, @PathVariable Double latitude){
//        Authentication authentication = (Authentication) SecurityContextHolder.getContext().getAuthentication();
//        if (authentication != null && authentication.isAuthenticated() &&
//                authentication.getPrincipal() instanceof UserDetails) {
//
//            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
//            String phoneNumber = userDetails.getUsername();
//            List<String> list = userService.findBestDeliveryBoy(longitude,latitude,5);
//            return ResponseEntity.status(HttpStatus.OK).body(list);
//        }else{
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
//
//        }
//
//    }
}

