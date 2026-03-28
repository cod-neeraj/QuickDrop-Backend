package com.example.User.Controllers;

import com.example.User.DTO.DiscountDTO;
import com.example.User.DTO.SellerDashBoardOrderList;
import com.example.User.DataToShow.ProductCreateDto.ProductDTO;
import com.example.User.DataToShow.SellerDashBoard.BasicProductDetails;
import com.example.User.DataToShow.SellerDashBoard.MainDashboardData;
import com.example.User.DataToShow.SellerDashBoard.PersonalInfo;
import com.example.User.DataToShow.SellerMiniDetails;
import com.example.User.DataToShow.SellerPersonalInfo;
import com.example.User.Models.OrdersData.ProductDetailsInfo;
import com.example.User.Models.User;
import com.example.User.Response.ApiResponse;
import com.example.User.Service.BestSellerInfo;
import com.example.User.Service.KafkaService;
import com.example.User.Service.RecommendationService;
import com.example.User.Service.Seller.SellerProductList;
import com.example.User.Service.Seller.SellerService;
import com.example.User.Service.Seller.Top5orderDetails;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.netflix.discovery.converters.Auto;
import jakarta.persistence.Basic;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import org.apache.commons.pool2.BaseObject;
import org.apache.coyote.Response;
import org.apache.kafka.common.network.Selectable;
import org.apache.kafka.common.protocol.types.Field;
import org.apache.kafka.shaded.com.google.protobuf.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.config.RepositoryConfigurationSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.method.AuthorizeReturnObject;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/seller")
public class SellerController {

    @Autowired
    private SellerService sellerService;

    @Autowired
    private KafkaService kafkaService;

    @Autowired
    private RecommendationService recommendationService;

    @PostMapping("/personal-Info")    //working
    public ResponseEntity<ApiResponse<Boolean>> updatePersonalInfo(@RequestBody SellerPersonalInfo sellerPersonalInfo) {
        Authentication authentication = (Authentication) SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() &&
                authentication.getPrincipal() instanceof UserDetails) {

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String phoneNumber = userDetails.getUsername();
            Boolean bool = sellerService.updatePersonalInfo(sellerPersonalInfo, phoneNumber);
            ApiResponse<Boolean> response = ApiResponse.<Boolean>builder()
                    .success(true)
                    .message("updated successfully")
                    .data(true)
                    .build();
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } else {
            ApiResponse<Boolean> response = ApiResponse.<Boolean>builder()
                    .success(true)
                    .message(" not updated successfully")
                    .data(null)
                    .build();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);

        }
    }

    @GetMapping("/get-personal-Info")    //working
    public ResponseEntity<ApiResponse<SellerPersonalInfo>> getPersonalInfo() {
        Authentication authentication = (Authentication) SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() &&
                authentication.getPrincipal() instanceof UserDetails) {

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String phoneNumber = userDetails.getUsername();
            SellerPersonalInfo sellerPersonalInfo = sellerService.getSellerInfo(phoneNumber);
            ApiResponse<SellerPersonalInfo> response = ApiResponse.<SellerPersonalInfo>builder()
                    .success(true)
                    .message("updated successfully")
                    .data(sellerPersonalInfo)
                    .build();
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } else {
            ApiResponse<SellerPersonalInfo> response = ApiResponse.<SellerPersonalInfo>builder()
                    .success(true)
                    .message(" not updated successfully")
                    .data(null)
                    .build();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);

        }
    }


    @PostMapping("/add-product")
    public ResponseEntity<?> addProduct(@RequestBody ProductDTO productDTO,
                                        @AuthenticationPrincipal UserDetails userDetails) {

            String phoneNumber = userDetails.getUsername();
            Boolean bool = sellerService.createProduct(productDTO, phoneNumber);
            if (bool) {
                ApiResponse<Boolean> response = ApiResponse.<Boolean>builder()
                        .success(true)
                        .message("added successfully")
                        .data(bool)
                        .build();
                return ResponseEntity.status(HttpStatus.OK).body(response);
            }else{
                ApiResponse<Boolean> response = ApiResponse.<Boolean>builder()
                        .success(false)
                        .message("not added  successfully")
                        .data(bool)
                        .build();
                return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(response);

            }



    }
//    @PostMapping("/updateStatus/{status}")
//    public ResponseEntity<?> updateStatus(@PathVariable String status,@AuthenticationPrincipal UserDetails userDetails){
//        String phoneNumber = userDetails.getUsername();
//
//    }

    @PostMapping("/addSale")
    public ResponseEntity<ApiResponse<String>> addSalesData(
            @RequestBody DiscountDTO discountDTO,
            @AuthenticationPrincipal UserDetails userDetails
    ) {

        String phoneNumber = userDetails.getUsername();

        sellerService.addSale(discountDTO, phoneNumber);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.<String>builder()
                        .success(true)
                        .message("Sale saved successfully")
                        .build());
    }

    @GetMapping("/getBasicProductDetails")
    public ResponseEntity<ApiResponse<List<SellerProductList>>> getProductList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal UserDetails userDetails){
            String phoneNumber = userDetails.getUsername();
            List<SellerProductList> set =  sellerService.getSellerAllProduct(phoneNumber,page,size);
            ApiResponse<List<SellerProductList>> response = ApiResponse.<List<SellerProductList>>builder()
                    .success(true)
                    .message("updated successfully")
                    .data(set)
                    .build();
            return ResponseEntity.status(HttpStatus.OK).body(response);


    }


    // Frontpage dashBoard detaisl okk

    @GetMapping("/getMainSellerDashBoardPageData")
    public ResponseEntity<?> getMainSellerDashBoardData(@AuthenticationPrincipal UserDetails userDetails){
        String phoneNumber = userDetails.getUsername();
        MainDashboardData mainDashboardData = sellerService.getDashboardData(phoneNumber);
        return ResponseEntity.ok(mainDashboardData);

    }

    @GetMapping("/getRecentOrders")
    public ResponseEntity<?> getRecentOrders(@AuthenticationPrincipal UserDetails userDetails){
        String phoneNumber  = userDetails.getUsername();
List<Top5orderDetails> list = sellerService.getTop5orders(phoneNumber);
        return ResponseEntity.ok(list);

    }





//
//    @PostMapping("/authenticate")
//    public ResponseEntity<Boolean> authenticateShopkeeper(){
//        Authentication authentication = (Authentication) SecurityContextHolder.getContext().getAuthentication();
//        if (authentication != null && authentication.isAuthenticated() &&
//                authentication.getPrincipal() instanceof UserDetails){
//            return ResponseEntity.ok(true);
//        }
//        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false);
//    }
//
//    @GetMapping("/get-Main-DashBoard")
//    public ResponseEntity<?> getMainDashBoard(){
//        Authentication authentication = (Authentication) SecurityContextHolder.getContext().getAuthentication();
//        if (authentication != null && authentication.isAuthenticated() &&
//                authentication.getPrincipal() instanceof UserDetails) {
//
//            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
//            String phoneNumber = userDetails.getUsername();
//              MainDashboardData mainDashboardData =  sellerService.getDashboardData(phoneNumber);
//              return ResponseEntity.ok(mainDashboardData);
//
//        }else{
//            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
//
//        }
//
//
//    }
//
//    @GetMapping("/get-personal-Info")   //working
//    public ResponseEntity<ApiResponse<PersonalInfo>>  getPersonalInfo(){
//        Authentication authentication = (Authentication) SecurityContextHolder.getContext().getAuthentication();
//        if (authentication != null && authentication.isAuthenticated() &&
//                authentication.getPrincipal() instanceof UserDetails) {
//
//            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
//            String phoneNumber = userDetails.getUsername();
//          PersonalInfo personalInfo =  sellerService.getPersonalInfo(phoneNumber);
//            ApiResponse<PersonalInfo> response = ApiResponse.<PersonalInfo>builder()
//                    .success(true)
//                    .message("fetched successfully")
//                    .data(personalInfo)
//                    .build();
//            return ResponseEntity.status(HttpStatus.OK).body(response);
//        }
//        else{
//            ApiResponse<PersonalInfo> response = ApiResponse.<PersonalInfo>builder()
//                    .success(true)
//                    .message(" not fetched successfully")
//                    .data(null)
//                    .build();
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
//
//        }
//
//
//    }
//
//    @PutMapping("/update-personal-Info")    //working
//    public ResponseEntity<ApiResponse<Boolean>> updatePersonalInfo(@RequestBody PersonalInfo personalInfo){
//        Authentication authentication = (Authentication) SecurityContextHolder.getContext().getAuthentication();
//        if (authentication != null && authentication.isAuthenticated() &&
//                authentication.getPrincipal() instanceof UserDetails) {
//
//            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
//            String phoneNumber = userDetails.getUsername();
//            Boolean bool =  sellerService.updatePersonalInfo(personalInfo,phoneNumber);
//            ApiResponse<Boolean> response = ApiResponse.<Boolean>builder()
//                    .success(true)
//                    .message("updated successfully")
//                    .data(true)
//                    .build();
//            return ResponseEntity.status(HttpStatus.OK).body(response);
//        }
//        else{
//            ApiResponse<Boolean> response = ApiResponse.<Boolean>builder()
//                    .success(true)
//                    .message(" not updated successfully")
//                    .data(null)
//                    .build();
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
//
//        }
//    }
//
//    @GetMapping("/getBasicProductDetails")
//    public ResponseEntity<ApiResponse<Set<BasicProductDetails>>> getProductList(
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "10") int size){
//        Authentication authentication = (Authentication) SecurityContextHolder.getContext().getAuthentication();
//        if (authentication != null && authentication.isAuthenticated() &&
//                authentication.getPrincipal() instanceof UserDetails) {
//
//            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
//            String phoneNumber = userDetails.getUsername();
//            Set<BasicProductDetails> set =  sellerService.getBasicProductSet(phoneNumber,page,size);
//            ApiResponse<Set<BasicProductDetails>> response = ApiResponse.<Set<BasicProductDetails>>builder()
//                    .success(true)
//                    .message("updated successfully")
//                    .data(set)
//                    .build();
//            return ResponseEntity.status(HttpStatus.OK).body(response);
//        }
//        else{
//            ApiResponse<Set<BasicProductDetails>> response = ApiResponse.<Set<BasicProductDetails>>builder()
//                    .success(true)
//                    .message(" not updated successfully")
//                    .data(null)
//                    .build();
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
//
//        }
//
//    }
//
//    @PostMapping("/addSale")
//    public ResponseEntity<?> addSalesData(@RequestBody DiscountDTO discountDTO){
////
////        Authentication authentication = (Authentication) SecurityContextHolder.getContext().getAuthentication();
////        if (authentication != null && authentication.isAuthenticated() &&
////                authentication.getPrincipal() instanceof UserDetails) {
////
////            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
////            String phoneNumber = userDetails.getUsername();
//            kafkaService.addSales(discountDTO);
//            return ResponseEntity.ok("ll");
//
////        }else{
////            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
////
////        }
//
//
//    }
//
//
    @GetMapping("/bestSeller/{longitude}/{latitude}")
    public ResponseEntity<?> findBestSeller(@PathVariable Double longitude,@PathVariable Double latitude){
        List<BestSellerInfo> list = recommendationService.findBestSeller(longitude,latitude);
        return ResponseEntity.ok(list);
    }
//
    @GetMapping("/sellerMiniDetails")
    public ResponseEntity<SellerMiniDetails> sellerMiniDetails(
            @RequestParam String id
    ) {
        return ResponseEntity.ok(sellerService.sellerMiniDetails(id));
    }

    @GetMapping("/getOrderList")
    public ResponseEntity<?> getorderList(@AuthenticationPrincipal UserDetails userDetails){
        Map<String,Object> lsit1 = sellerService.OrderDetailsSellerDashBoard(userDetails.getUsername());
        return ResponseEntity.ok(lsit1);

    }

    @GetMapping("/getDetailOrderList/{orderId}")
    public ResponseEntity<?> getOrderDetail(@PathVariable String orderId,
                              @AuthenticationPrincipal UserDetails userDetails){
    String phoneNumber = userDetails.getUsername();
        List<ProductDetailsInfo> list = sellerService.getDetailsOfOrder(orderId,phoneNumber);

        return ResponseEntity.ok(list);
    }

    @PutMapping("/updateOrderStatus/{orderId}/{status}")
    public ResponseEntity<?> updateOrderStatus(@PathVariable String orderId,
                                               @PathVariable String status,
                                               @AuthenticationPrincipal UserDetails userDetails){
    Boolean bool = sellerService.updateOrderStatus(status,orderId, userDetails.getUsername());
    return ResponseEntity.ok(bool);

    }

    @PostMapping("/updateStatus/{status}")
    public ResponseEntity<?> updateStatus (@PathVariable String status,@AuthenticationPrincipal UserDetails userDetails){
    String phoneNumber = userDetails.getUsername();
    String id = sellerService.updateStatus(status,phoneNumber);
    return ResponseEntity.ok(id);

    }



}



