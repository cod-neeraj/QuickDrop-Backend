package com.example.User.Configuration;

import com.example.User.Filter.JwtFilter;
import com.example.User.Service.User.UserBasicService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserBasicService userBasicService;
    private final JwtFilter jwtFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userBasicService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/customer/getPersonalinfo",
                                                   "/customer/update-PersonalInfo",
                                                    "/customer/create-address",
                                                    "/customer/getAddressList",
                                                    "/customer/delete-address/{id}",
                                "/customer/addToCart",
                                "/customer/getProductListCart",
                                "/customer/getWishlistProduct",
                                "/customer/getDashBoard",
                                "/customer/placeOrder",
                                "/customer/search-history",
                                "/customer/getOrders/{phoneNumber}",
                                "/customer/orderDetails/{orderId}").hasAuthority("CUSTOMER")
                        .requestMatchers("/seller/authenticate",
                                "/seller/addSale",
                                "/seller/add-product",
                                "/seller/personal-Info",
                                "/seller/get-Main-DashBoard",
                                "/seller/get-personal-Info",
                                "/seller/update-personal-Info/*",
                                "/seller/getBasicProductDetails",
                                "/seller/getOrderList",
                                "/seller/getDetailOrderList/{orderId}",
                                "/seller/updateOrderStatus/{orderId}/{status}/{sellerId}",
                                "/seller/getMainSellerDashBoardPageData",
                                "/seller/getRecentOrders",
                                "/seller/updateStatus/{status}",
                                "/seller/getAnalytics/{id}",
                                "/seller/getGraph/{id}/{startDate}/{endDate}",
                                "/seller/getOrderGraph/{id}/{startDate}/{endDate}").hasAuthority("SELLER")
                        .requestMatchers("/user/signUp",
                                "/seller/bestSeller/{longitude}/{latitude}",
                                "/user/login",
                                "/customer/addtomongodb",
                                "/customer/getRecommendationsList",
                                "/user/getProduct",
                                "/user/seller/authcheck",
                                "/user/addProductToRedisOrder",
                                "/user/getProductsFromRedisOrder",
                                "/user/updateQuantity",
                                "/user/deleteOrder",
                                "/seller/sellerMiniDetails",
                                "/customer/getOrders/{phoneNumber}",
                                "/customer/getOrders/seller/{phoneNumber}",
                                "/customer/orderDetails/deliveryBoy/{orderId}",
                                "/ws/**"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
