package com.example.User.DataToShow;

import com.example.User.DTO.ShowOrdersInDashBoard;
import com.example.User.Models.Orders;
import lombok.*;

import java.util.List;
//import org.hibernate.query.Order;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDashboard {
    private String name;
    private String phoneNumber;
    private List<ShowOrdersInDashBoard> recentOrder;

}
