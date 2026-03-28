package com.example.Delivery.Config;


import com.example.Delivery.Filter.JwtFilter;
import com.example.Delivery.Service.UserService;
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

    private final UserService userBasicService;
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
                        .requestMatchers("/deliver/login",
                                "/deliver/partialSignUp",
                                "/deliveryBoy/order/allocateDelivery",
                                "/deliveryBoy/dashBoard/checkWebsocketConnection").permitAll()
                        .requestMatchers("/deliver/statusChange","/deliver/deliveryBoy/*",
                                "/deliver/me",
                                "/deliver/signUp",
                                "/deliveryBoy/dashBoard/basicDetails",
                                "/deliveryBoy/dashBoard/getProfile",
                                "/deliveryBoy/order/acceptOrder/{orderId}",
                                "/deliver/goLiveOrOffline/{status}/{longitude}/{latitude}",
                                "/deliver/updateOrderStatus/{status}/{orderId}/{sellerPhoneNumber}",
                                "/deliveryBoy/order/acceptOrder/{orderId}").hasAuthority("DELIVER")
                        .anyRequest().authenticated()
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
