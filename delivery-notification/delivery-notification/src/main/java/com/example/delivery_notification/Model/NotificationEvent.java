package com.example.delivery_notification.Model;

import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NotificationEvent {
        private String deliveryBoyId;
        private String orderId;
        private String shopkeeperLocation;


}
