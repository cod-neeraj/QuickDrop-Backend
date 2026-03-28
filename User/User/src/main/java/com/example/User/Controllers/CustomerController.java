package com.example.User.Controllers;

import com.example.User.DTO.AddProductToCart;
import com.example.User.DTO.DataForMongoDb;
import com.example.User.DTO.Payment;
import com.example.User.DTO.ShowOrdersInDashBoard;
import com.example.User.DataToShow.OrderDetailsInRedis;
import com.example.User.DataToShow.WishlistAddData;
import com.example.User.Models.Customer.*;
import com.example.User.Models.OrdersData.MainOrder;
import com.example.User.Repository.CustomerRepositorys.Customer_visited_product_Repo;
import com.example.User.RequestBodies.Frontend.CreateCustomerAddress;
import com.example.User.RequestBodies.Frontend.PersonalInfo;
import com.example.User.Service.Customer.CustomerService;
import com.example.User.Service.Order.DeliveryBoyActiveDelivery;
import com.example.User.Service.Order.DeliveryBoyActiveDeliveryRepoData;
import com.example.User.Service.Order.OrderService;
import com.example.User.Service.RecommendationService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.razorpay.RazorpayException;
import jakarta.validation.Valid;
import org.apache.kafka.shaded.com.google.protobuf.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import com.example.User.DataToShow.UserDashboard;
import com.example.User.Response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;


@RestController
@RequestMapping("/customer")
public class CustomerController {
    @Autowired
    CustomerService customerService;

    @Autowired
    Customer_visited_product_Repo customerVisitedProductRepo;

    @Autowired
    RecommendationService recommendationService;

    @Autowired
    OrderService orderService;


    @GetMapping("/getDashBoard")
    public ResponseEntity<?> getDashBoardData(@AuthenticationPrincipal UserDetails userDetails){

        String phoneNumber = userDetails.getUsername();
        UserDashboard userDashboard = customerService.getDashBoaredData(phoneNumber);
            if(userDashboard != null){
                ApiResponse<UserDashboard> response = ApiResponse.<UserDashboard>builder()
                        .success(true)
                        .message("fetched successfully")
                        .data(userDashboard)
                        .build();
                return ResponseEntity.status(HttpStatus.FOUND).body(response);
            }
            ApiResponse<PersonalInfo> response = ApiResponse.<PersonalInfo>builder()
                    .success(false)
                    .message("not fetched successfully")
                    .data(null)
                    .build();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);


    }

    @GetMapping("/getPersonalinfo")
    public ResponseEntity<ApiResponse<PersonalInfo>> getPersonalInfo(@AuthenticationPrincipal UserDetails userDetails) throws JsonProcessingException {

            String phoneNumber = userDetails.getUsername();
            PersonalInfo personalInfo = customerService.getPersonalInfo(phoneNumber);
            if(personalInfo != null){
                ApiResponse<PersonalInfo> response = ApiResponse.<PersonalInfo>builder()
                        .success(true)
                        .message("fetched successfully")
                        .data(personalInfo)
                        .build();
                return ResponseEntity.status(HttpStatus.OK).body(response);
            }
           ApiResponse<PersonalInfo> response = ApiResponse.<PersonalInfo>builder()
                   .success(false)
                   .message("not fetched successfully")
                   .data(null)
                   .build();
           return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }



    @PutMapping("/update-PersonalInfo")
    public ResponseEntity<ApiResponse<Boolean>> updatePersonalInfo(@RequestBody PersonalInfo personalInfo,
                                                                   @AuthenticationPrincipal UserDetails userDetails){
            String phoneNumber = userDetails.getUsername();
            Boolean bool = customerService.updatePersonalInfo(personalInfo,phoneNumber);
            if(bool){
                ApiResponse<Boolean> response = ApiResponse.<Boolean>builder()
                        .success(true)
                        .message("updated successfully")
                        .data(null)
                        .build();
                return ResponseEntity.status(HttpStatus.OK).body(response);
            }
            ApiResponse<Boolean> response = ApiResponse.<Boolean>builder()
                    .success(false)
                    .message("not updated successfully")
                    .data(null)
                    .build();
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }

    @GetMapping("/getAddressList")
    public ResponseEntity<?> getAddressList(){
        Authentication authentication = (Authentication) SecurityContextHolder.getContext().getAuthentication();
        if(authentication != null && authentication.isAuthenticated() &&
                authentication.getPrincipal() instanceof UserDetails){

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String phoneNumber = userDetails.getUsername();
            Set<Customer_Address> customerAddressSet = customerService.getAddress(phoneNumber);
            if(!customerAddressSet.isEmpty()){
                ApiResponse<Set<Customer_Address>> response = ApiResponse.<Set<Customer_Address>>builder()
                        .success(true)
                        .message("fetched successfully")
                        .data(customerAddressSet)
                        .build();
                return ResponseEntity.status(HttpStatus.OK).body(response);
            }
            ApiResponse<Set<Customer_Address>> response = ApiResponse.<Set<Customer_Address>>builder()
                    .success(true)
                    .message("not fetched successfully")
                    .data(null)
                    .build();
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }else{
            ApiResponse<Boolean> response = ApiResponse.<Boolean>builder()
                    .success(false)
                    .message("Unauthorized")
                    .data(null)
                    .build();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

    }

    @PostMapping("/create-address")
    public ResponseEntity<ApiResponse<Boolean>> createAddress(@RequestBody CreateCustomerAddress createCustomerAddress){
        Authentication authentication = (Authentication) SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() &&
                authentication.getPrincipal() instanceof UserDetails) {

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String phoneNumber = userDetails.getUsername();
            Boolean bool = customerService.createAddress(createCustomerAddress,phoneNumber);
            if(bool){
                ApiResponse<Boolean> response = ApiResponse.<Boolean>builder()
                        .success(true)
                        .message("created successfully")
                        .data(null).build();

                return ResponseEntity.status(HttpStatus.CREATED).body(response);
            }
            ApiResponse<Boolean> response = ApiResponse.<Boolean>builder()
                    .success(false)
                    .message("not created successfully")
                    .data(null).build();

            return ResponseEntity.status(HttpStatus.NOT_MODIFIED).body(response);
        }else{
            ApiResponse<Boolean> response = ApiResponse.<Boolean>builder()
                    .success(false)
                    .message("UnAuthorized")
                    .data(null).build();

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);

        }

    }

    @DeleteMapping("/delete-address/{id}")
    public ResponseEntity<ApiResponse<Boolean>> deleteAddress(@PathVariable String id){
        Authentication authentication = (Authentication) SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() &&
                authentication.getPrincipal() instanceof UserDetails) {

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            Boolean bool = customerService.deleteAddress(id);
            if(bool){
                ApiResponse<Boolean> response = ApiResponse.<Boolean>builder()
                        .success(true)
                        .message("deleted successfully")
                        .data(null).build();

                return ResponseEntity.status(HttpStatus.OK).body(response);
            }
            ApiResponse<Boolean> response = ApiResponse.<Boolean>builder()
                    .success(false)
                    .message("not deleted successfully")
                    .data(null).build();

            return ResponseEntity.status(HttpStatus.NOT_MODIFIED).body(response);
        }else{
            ApiResponse<Boolean> response = ApiResponse.<Boolean>builder()
                    .success(false)
                    .message("UnAuthorized")
                    .data(null).build();

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);

        }


    }

    @PutMapping("/update-address/{id}")
    public ResponseEntity<ApiResponse<Boolean>> updateAddress(@PathVariable String id,@RequestBody CreateCustomerAddress createCustomerAddress){
        Authentication authentication = (Authentication) SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() &&
                authentication.getPrincipal() instanceof UserDetails) {

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String phoneNumber = userDetails.getUsername();
            Boolean bool = customerService.updateAddress(createCustomerAddress,id);
            if(bool){
                ApiResponse<Boolean> response = ApiResponse.<Boolean>builder()
                        .success(true)
                        .message("updates successfully")
                        .data(null).build();

                return ResponseEntity.status(HttpStatus.OK).body(response);
            }
            ApiResponse<Boolean> response = ApiResponse.<Boolean>builder()
                    .success(false)
                    .message("not updated successfully")
                    .data(null).build();

            return ResponseEntity.status(HttpStatus.NOT_MODIFIED).body(response);
        }else{
            ApiResponse<Boolean> response = ApiResponse.<Boolean>builder()
                    .success(false)
                    .message("UnAuthorized")
                    .data(null).build();

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);

        }

    }

    @DeleteMapping("/delete-cart-product/{id}")
    public ResponseEntity<ApiResponse<Boolean>> deleteCartProduct(@PathVariable Long id){
        Authentication authentication = (Authentication) SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() &&
                authentication.getPrincipal() instanceof UserDetails) {

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String phoneNumber = userDetails.getUsername();
            Boolean bool = customerService.deleteProductFromCart(id,phoneNumber);
            if(bool){
                ApiResponse<Boolean> response = ApiResponse.<Boolean>builder()
                        .success(true)
                        .message("deleted successfully")
                        .data(null).build();

                return ResponseEntity.status(HttpStatus.OK).body(response);
            }
            ApiResponse<Boolean> response = ApiResponse.<Boolean>builder()
                    .success(false)
                    .message("not deleted successfully")
                    .data(null).build();

            return ResponseEntity.status(HttpStatus.NOT_MODIFIED).body(response);
        }else{
            ApiResponse<Boolean> response = ApiResponse.<Boolean>builder()
                    .success(false)
                    .message("UnAuthorized")
                    .data(null).build();

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);

        }


    }

    @PostMapping("/add-product-to-wishList")
    public ResponseEntity<ApiResponse<Boolean>> addProductToWishList(@RequestBody @Valid WishlistAddData wishlistAddData,
                                                                     @AuthenticationPrincipal UserDetails userDetails){

            String phoneNumber = userDetails.getUsername();
            Boolean bool = customerService.addProductToWishList(wishlistAddData,phoneNumber);
            if(bool){
                ApiResponse<Boolean> response = ApiResponse.<Boolean>builder()
                        .success(true)
                        .message("added successfully")
                        .data(null).build();

                return ResponseEntity.status(HttpStatus.OK).body(response);
            }
            ApiResponse<Boolean> response = ApiResponse.<Boolean>builder()
                    .success(false)
                    .message("not added successfully")
                    .data(null).build();

            return ResponseEntity.status(HttpStatus.NOT_MODIFIED).body(response);
        }


@GetMapping("/getWishlistProducts")
public ResponseEntity<?> getWishListProducts(@AuthenticationPrincipal UserDetails userDetails){
        List<WishlistAddData> list = customerService.getWishlistData(userDetails.getUsername());
        return ResponseEntity.ok(list);
}
    @DeleteMapping("/delete-wishlist-product/{id}")
    public ResponseEntity<ApiResponse<Boolean>> deleteWishListProduct(@PathVariable Long id){
        Authentication authentication = (Authentication) SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() &&
                authentication.getPrincipal() instanceof UserDetails) {

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String phoneNumber = userDetails.getUsername();
            Boolean bool = customerService.deleteWishListFromCart(id,phoneNumber);
            if(bool){
                ApiResponse<Boolean> response = ApiResponse.<Boolean>builder()
                        .success(true)
                        .message("deleted successfully")
                        .data(null).build();

                return ResponseEntity.status(HttpStatus.OK).body(response);
            }
            ApiResponse<Boolean> response = ApiResponse.<Boolean>builder()
                    .success(false)
                    .message("not deleted successfully")
                    .data(null).build();

            return ResponseEntity.status(HttpStatus.NOT_MODIFIED).body(response);
        }else{
            ApiResponse<Boolean> response = ApiResponse.<Boolean>builder()
                    .success(false)
                    .message("UnAuthorized")
                    .data(null).build();

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);

        }


    }

    @PostMapping("/placeOrder")
    public ResponseEntity<Payment> placeOrders(@RequestBody OrderDetailsInRedis orderDetailsInRedis,
                                               @AuthenticationPrincipal UserDetails userDetails) throws JsonProcessingException, RazorpayException {

        String phoneNumber = userDetails.getUsername();
        Payment bool = customerService.placeOrder(phoneNumber,orderDetailsInRedis);
            if(bool != null){

                return ResponseEntity.status(HttpStatus.OK).body(bool);
            }

            return ResponseEntity.status(HttpStatus.NOT_MODIFIED).body(null);


    }
//    @GetMapping("/getAllOrders")

    @GetMapping("/search-history")
    public ResponseEntity<?> getSearch_History_Products(){
        Authentication authentication = (Authentication) SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() &&
                authentication.getPrincipal() instanceof UserDetails) {

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String phoneNumber = userDetails.getUsername();
            List<Search_History> searchHistory = customerService.searchHistory(phoneNumber);
            if(searchHistory.isEmpty()){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("no search yet");
            }
            return ResponseEntity.ok(searchHistory);
        }else{

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("loda");

        }



    }

    @GetMapping("/getRecommendationsList")
    public ResponseEntity<?> getRecommendationsList(){
        List<Customer_Visited_product> list = recommendationService.getUserData();
        return ResponseEntity.ok(list);



    }

    @PostMapping("/addtomongodb")
    public Boolean toaddinmongodb(@RequestBody DataForMongoDb data){
        Customer_Visited_product customerVisitedProduct = Customer_Visited_product.builder()
                .id(data.getId())
                .customerId(data.getCustomerId())
                .productId(data.getProductId())
                .inWishList(data.getInWishList())
                .inCart(data.getInCart())
                .visitedAt(data.getVisitedAt())
                .build();
      customerVisitedProductRepo.save(customerVisitedProduct);
      return true;
    }

    @GetMapping("/getOrders/{phoneNumber}")
    public ResponseEntity<?> getOrders(@AuthenticationPrincipal UserDetails userDetails){
           String phoneNumber = userDetails.getUsername();
           List<ShowOrdersInDashBoard> orderList = customerService.getOrderList(phoneNumber);
           return ResponseEntity.ok(orderList);
    }

@GetMapping("/orderDetails/{orderId}")
public ResponseEntity<?> orderDetails(@PathVariable String orderId,@AuthenticationPrincipal UserDetails userDetails){
        return ResponseEntity.ok(customerService.getOrderDetails(orderId));
}
    @GetMapping("/getOrders/seller/{phoneNumber}")
    public ResponseEntity<?> getOrdersSeller(@PathVariable String phoneNumber){
        List<MainOrder> orderList = customerService.getOrderListSeller(phoneNumber);
        return ResponseEntity.ok(orderList);
    }

    @GetMapping("/orderDetails/deliveryBoy/{orderId}")
    public ResponseEntity<?> getOrderDetails(@PathVariable String orderId){
        DeliveryBoyActiveDelivery deliveryBoyActiveDeliveryRepoData = orderService.findDetails(orderId);
        return ResponseEntity.ok(deliveryBoyActiveDeliveryRepoData);

    }

    @PutMapping("/addToCart")
    public ResponseEntity<?> addProductToCart(@AuthenticationPrincipal UserDetails userDetails,
                                              @RequestBody @Valid AddProductToCart addProductToCart
                                              ){
        String phoneNumber = userDetails.getUsername();
        customerService.addProductToCart(addProductToCart,phoneNumber);
       ApiResponse<String> response = ApiResponse.<String>builder()
                .success(true)
                .message("Add succesfully")
                .data(null)
                .build();
        return ResponseEntity.ok(response);

    }

    @GetMapping("/getProductListCart")
    public  ResponseEntity<?> getProductList(@AuthenticationPrincipal UserDetails userDetails){
        String phoneNumber = userDetails.getUsername();
        Set<Customer_Cart> set = customerService.getCartProductList(phoneNumber);
       ApiResponse<Set<Customer_Cart>> response = ApiResponse.<Set<Customer_Cart>>builder()
               .success(true)
               .message("fetched your cart")
               .data(set)
               .build();
       return ResponseEntity.ok(response);
    }


}
