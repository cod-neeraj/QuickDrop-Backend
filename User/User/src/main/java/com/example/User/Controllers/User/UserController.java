package com.example.User.Controllers.User;

import com.example.User.DTO.PaymentDTO.ProductSave;
import com.example.User.DTO.ProductStatRedis;
import com.example.User.DataToShow.ProductAdd;
import com.example.User.DataToShow.ProductIds;
import com.example.User.Exceptions.InvalidCredentialsException;
import com.example.User.Models.Customer.Customer_Visited_product;
import com.example.User.RequestBodies.Frontend.Login;
import com.example.User.RequestBodies.Frontend.SignUp;
import com.example.User.Response.ApiResponse;
import com.example.User.Service.PaymentService;
import com.example.User.Service.User.UserBasicService;
import com.example.User.Utils.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ReportAsSingleViolation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authorization.method.AuthorizeReturnObject;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.awt.print.Pageable;
import java.io.InvalidClassException;
import java.util.InvalidPropertiesFormatException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

@RestController
@RequestMapping("/user")
@CrossOrigin
public class UserController {

    @Autowired
    UserBasicService userBasicService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private PaymentService paymentService;


    @PostMapping("/signUp")
    public ResponseEntity<ApiResponse<String>> signUp(@RequestBody SignUp signUp){
        signUp.setPassword(passwordEncoder.encode(signUp.getPassword()));
        boolean result = userBasicService.signUp(signUp);
        if(result){
            ApiResponse<String> response = ApiResponse.<String>builder()
                    .success(true)
                    .message("Signup Successfully")
                    .data(signUp.getRole())
                    .build();

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }else{
            ApiResponse<String> response = ApiResponse.<String>builder()
                    .success(false)
                    .message("Already there, try to login ")
                    .data(null)
                    .build();

            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(response);

        }
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<String>> login(@RequestBody Login login, HttpServletResponse httpServletResponse){
try {
    Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(login.getPhoneNumber(), login.getPassword()));
    UserDetails userDetails = (UserDetails) authentication.getPrincipal();

    Boolean bool = userBasicService.checkRole(login.getPhoneNumber());
    String token = null;
    if(bool) {
        token = jwtUtil.generateToken(login.getPhoneNumber(), "CUSTOMER");
    }else{
        token = jwtUtil.generateToken(login.getPhoneNumber(), "SELLER");
    }
    Cookie cookie = new Cookie("jwt", token);
    cookie.setHttpOnly(true);
    cookie.setSecure(false);
    cookie.setPath("/");
    cookie.setMaxAge(10 * 60 * 60);
    httpServletResponse.addCookie(cookie);
    ApiResponse<String> apiResponse=null;
if(bool) {
    apiResponse = ApiResponse.<String>builder()
            .success(true)
            .message("Login Success")
            .data("CUSTOMER")
            .build();
}
else{
    apiResponse = ApiResponse.<String>builder()
            .success(true)
            .message("Login Success")
            .data("SELLER")
            .build();

}

    return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
}catch(BadCredentialsException e){
    throw new InvalidCredentialsException("Invalid Credentials");
}


    }

    @GetMapping("/getProduct")
    public List<Customer_Visited_product> getUserData() {
//        List<Customer_Visited_product> list = customerVisitedProductRepo.findByCustomerId("nfhrbr");
//        System.out.println("Fetched documents: " + list.size());
//        list.forEach(System.out::println);
        return null;
    }


    @GetMapping("/seller/authcheck")
    public ResponseEntity<?> getCurrentUser(
            HttpServletRequest request,
            @RequestParam("sellerPhoneNumber") String sellerPhoneNumber
    ) {

        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No cookies found");
        }

        String token = null;

        // Extract JWT
        for (Cookie c : cookies) {
            if ("jwt".equals(c.getName())) {
                token = c.getValue();
                break;
            }
        }

        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token not found");
        }

        try {
            // Validate token
            String tokenPhone = jwtUtil.extractUsername(token);
            String role = jwtUtil.extractRole(token);


            // Role check
            if (!"SELLER".equals(role)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only sellers allowed");
            }

            // Owner check
            if (!sellerPhoneNumber.equals(tokenPhone)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
            }

            return ResponseEntity.ok(true);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }
    }


    @PostMapping("/addProductToRedisOrder")
    public ResponseEntity<?> addProductToRedisOrder(@RequestBody ProductAdd productAdd){
           Boolean bool  = paymentService.addProductToRedisOrder("7973008735",productAdd);
           return ResponseEntity.ok(bool);


    }

    @GetMapping("/getProductsFromRedisOrder")
    public ResponseEntity<?> fetchRedisOrderProduct(){
      List<ProductAdd> productSave = paymentService.fetchProductFromRedisOrder("7973008735");
      return ResponseEntity.ok(productSave);
    }

    @PutMapping("/updateQuantity/{id}")
    public ResponseEntity<?> updateRedisOrder(@PathVariable String id,
                                              @AuthenticationPrincipal UserDetails userDetails) throws ExecutionException, InterruptedException, TimeoutException {
        Boolean bool = paymentService.updateProductToRedisOrder("7973008735",id);
        return ResponseEntity.ok(bool);
    }

    @DeleteMapping("/deleteOrder")
    public ResponseEntity<?> deleteOrder(@RequestBody ProductAdd productSave){
        paymentService.deleteRedisOrderProduct("7973008735",productSave);
        return ResponseEntity.ok(true);

    }

    @GetMapping("/getTopSellerAndProducts")
    public ResponseEntity<?> fetchBest(){
        return ResponseEntity.ok("");
    }


}
