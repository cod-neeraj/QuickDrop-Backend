package com.example.User.Service.Order;
import com.example.User.Configuration.WebSocketHandler;
import com.example.User.DTO.DeliveryBoyOrderDetails;
import com.example.User.DTO.SellerDashBoardOrderList;
import com.example.User.DTO.SellerDeliveryDetails;
import com.example.User.DataToShow.SellerLocationRepo;
import com.example.User.FeignInterface.deliveryAllocationInterface;
import com.example.User.Models.OrdersData.DeliveryBoyStatus;
import com.example.User.Models.OrdersData.MainOrder;
import com.example.User.Models.OrdersData.ProductDetailsInfo;
import com.example.User.Models.OrdersData.SellerInfo;
import com.example.User.Models.PaymentStatus;
import com.example.User.Models.Seller.OrderStatus;
import com.example.User.Models.Seller.SellerStatus;
import com.example.User.Models.Seller.Seller_Info;
import com.example.User.Models.Seller.Seller_daily_revenue;
import com.example.User.Repository.OrderRepositories.MainOrderRepo;
import com.example.User.Repository.SellerRepositories.SellerRepo;
import com.example.User.Repository.SellerRepositories.Seller_daily_revenueRepo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
public class OrderService {

    @Autowired
    MainOrderRepo mainOrderRepo;

    @Autowired
    deliveryAllocationInterface deliveryAllocationInterface1;




    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private SellerRepo sellerRepo;

    @Autowired
    private Seller_daily_revenueRepo sellerDailyRevenueRepo;

    @Autowired
    private com.example.User.Repository.OrderRepositories.SellerInfo sellerInfoRepo;

    @KafkaListener(topics = "order-success", groupId = "order-consume")
    public void settleOrders(String values) throws JsonProcessingException {

            String[] parts = values.split(",");
            String paymentId = parts[0];
            String orderId = parts[1];
            String userId = parts[2];
            String success = parts[3];

            MainOrder mainOrder = mainOrderRepo.findByOrderId(orderId);


            if(success.equalsIgnoreCase("true")){
                mainOrder.setPaymentStatus(PaymentStatus.PAID);
                mainOrder.setOrderStatus(OrderStatus.PLACED);
                mainOrderRepo.save(mainOrder);
                List<String> productIds = new ArrayList<>();

                List<SellerInfo> list = mainOrder.getSellerInfo();
                for(SellerInfo sl:list){
                    List<ProductDetailsInfo> list2 = sl.getProductDetailsInfo();
                    for(ProductDetailsInfo productDetailsInfo : list2){
                        productIds.add(productDetailsInfo.getProductId());
                    }
                }
                String ids = productIds.get(0);
                for(String productId : productIds){
                    ids = ids+","+productId;
                }

                kafkaTemplate.send("product-add",ids);
//            }else{
                mainOrder.setPaymentStatus(PaymentStatus.FAILED);
                mainOrder.setOrderStatus(OrderStatus.CANCELLED);
                mainOrderRepo.save(mainOrder);

            }
        System.out.println(mainOrder.getUserAddress());

        if(mainOrder.getUserAddress().equalsIgnoreCase("ownPickUp")){
            return ;

        }
            Set<SellerDeliveryDetails> sellerDeliveryDetails = new HashSet<>();
            List<String> list = new ArrayList<>();
           DeliveryBoyOrderDetails deliveryBoyOrderDetails = DeliveryBoyOrderDetails.builder()
                .orderId(orderId)
                .customerName(mainOrder.getUsername())
                .customerPhoneNumber(mainOrder.getUserId())
                .deliveryAddress(mainOrder.getUserAddress())
                .sellerDeliveryDetailsSet(sellerDeliveryDetails)
                .build();

        for(SellerInfo sellerInfo :mainOrder.getSellerInfo()){
            SellerDeliveryDetails sellerDeliveryDetails1 = SellerDeliveryDetails.builder()
                    .sellerName(sellerInfo.getName())
                    .sellerAddress(sellerInfo.getStreet()+" "+sellerInfo.getCity()+" "+sellerInfo.getState())
                    .sellerShopName(sellerInfo.getShopName())
                    .orderState(sellerInfo.getOrderStatus())
                    .build();
            list.add(sellerInfo.getSellerId());
            System.out.println(sellerInfo.getSellerId());
            sellerDeliveryDetails.add(sellerDeliveryDetails1);

        }

        List<SellerLocationRepo> list1 = sellerRepo.findLocationsByPhoneNumbers(list);
        int i=0;
        for(SellerDeliveryDetails sellerDeliveryDetails1:sellerDeliveryDetails){
            sellerDeliveryDetails1.setLatitude(list1.get(i).getLocation().getX());
            sellerDeliveryDetails1.setLongitude(list1.get(i).getLocation().getY());
            i++;
        }

        deliveryBoyOrderDetails.setSellerDeliveryDetailsSet(sellerDeliveryDetails);

        objectMapper = new ObjectMapper();
        String details = objectMapper.writeValueAsString(deliveryBoyOrderDetails);
        kafkaTemplate.send("delivery-boy-allocation",details);

    }


    @Transactional
    public void addStatsToSeller(MainOrder mainOrder) {

        LocalDate date = LocalDate.now();

        List<SellerInfo> sellers = mainOrder.getSellerInfo();

        Map<String, Double> map = new HashMap<>();

        for (SellerInfo seller : sellers) {

            double totalAmount = 0.0;

            for (ProductDetailsInfo product : seller.getProductDetailsInfo()) {
                totalAmount += product.getPrice() * product.getQuantity();
            }

            map.put(seller.getSellerId(), totalAmount);
        }

        for (Map.Entry<String, Double> entry : map.entrySet()) {

            String sellerId = entry.getKey();
            Double amount = entry.getValue();

            boolean exists = sellerDailyRevenueRepo
                    .existsByDateAndSellerId(date, sellerId);

            if (exists) {

                sellerDailyRevenueRepo.updateRevenue(
                        date,
                        sellerId,
                        BigDecimal.valueOf(amount)
                );

            } else {

                Seller_Info sellerInfo = sellerRepo.findBySellerId(sellerId).orElseThrow(() -> new RuntimeException("Seller not found"));
                Seller_daily_revenue revenue = Seller_daily_revenue.builder()
                        .date(date)
                        .orderCount(1)
                        .revenue(amount)
                        .averageOrderValue(amount)
                        .build();
                sellerInfo.getSellerDailyRevenues().add(revenue);
                sellerRepo.save(sellerInfo);
            }
        }
    }

    @KafkaListener(topics = "deliveryBoy-updateOrderStatus",groupId = "order-consume")
    public void deliveryBouOrderStatusChange(String values) throws JsonProcessingException {
        objectMapper = new ObjectMapper();
        System.out.println("📈📈");
        DeliveryBoyOrderStatusChange deliveryBoyOrderStatusChange = objectMapper.readValue(values,DeliveryBoyOrderStatusChange.class);

        int update = sellerInfoRepo.updateDeliveryBoyStatus(deliveryBoyOrderStatusChange.getOrderId(),
                                                            deliveryBoyOrderStatusChange.getSellerId(),
                                                            deliveryBoyOrderStatusChange.getStatus());
        if(deliveryBoyOrderStatusChange.getStatus() == DeliveryBoyStatus.PICKED){
            mainOrderToDailyRevenue(deliveryBoyOrderStatusChange.getSellerId(), deliveryBoyOrderStatusChange.getOrderId());
        }
        return;



    }

    @KafkaListener(topics = "deliveryBoy-delivered-order",groupId = "order-consume")
    public void deliveryChangingOrderStatus(String orderId){
        mainOrderRepo.updateOrderStatus(orderId, OrderStatus.DELIVERED);
        return;

    }

    public void mainOrderToDailyRevenue(String phoneNumber,String orderId){
        Double amount = sellerInfoRepo.fetchAmountWhichOrderAreDone(phoneNumber,orderId,SellerStatus.SHIPPED,DeliveryBoyStatus.PICKED);
        if(amount == null){
            return ;
        }
        Optional<Seller_Info> sellerInfo = sellerRepo.findByPhoneNumber(phoneNumber);
        Seller_Info sellerInfo1 = sellerInfo.get();
        int update = sellerDailyRevenueRepo.upsertDailyRevenue(sellerInfo1.getId(), amount);
        System.out.println(update);
        return;
    }

    @KafkaListener(topics = "deliveryBoy-userRepo", groupId = "order-consume")
    public void setDeliveryBoy(String value) throws JsonProcessingException {
        objectMapper = new ObjectMapper();
        DeliveryBoyDetails deliveryBoyDetails = objectMapper.readValue(value, DeliveryBoyDetails.class);
        MainOrder mainOrder = mainOrderRepo.findByOrderId(deliveryBoyDetails.getOrderId());
        mainOrder.setDeliveryBoyId(deliveryBoyDetails.getPhoneNumber());
        mainOrder.setDeliveryBoyName(deliveryBoyDetails.getDeliveryBoyName());
        SellerDashBoardOrderList sellerDashBoardOrderList = SellerDashBoardOrderList.builder()
                .deliveryBoyName(mainOrder.getDeliveryBoyName())
                .order_id(mainOrder.getOrder_id())
                .orderStatus(SellerStatus.ACCEPTED)
                .totalAmount(mainOrder.getTotalAmount())
                .username(mainOrder.getUsername())
                .build();
        addStatsToSeller(mainOrder);
        for(int i=0;i<mainOrder.getSellerInfo().size();i++) {
            System.out.println("👍👍");

            WebSocketHandler.sendToseller(mainOrder.getSellerInfo().get(i).getSellerId(), sellerDashBoardOrderList);
        }


       for(SellerInfo sellerInfo: mainOrder.getSellerInfo()){
           sellerInfo.setDeliveryBoyStatus(DeliveryBoyStatus.NOTpICKED);
       }

        mainOrderRepo.save(mainOrder);
       OrdersDetailsForDeliveryBoy ordersDetailsForDeliveryBoy = OrdersDetailsForDeliveryBoy.builder()
               .orderId(mainOrder.getOrder_id())
               .customerName(mainOrder.getUsername())
               .deliveryLocation(mainOrder.getUserAddress())
               .orderStatus(mainOrder.getOrderStatus().toString())
               .orderEarnings((double)mainOrder.getTotalAmount()%10)
               .deliveredAt(null)
               .deliveryBoyId(mainOrder.getDeliveryBoyId())
               .deliveryBoyReview("")
               .userReview("")
               .build();

       String value1 = objectMapper.writeValueAsString(ordersDetailsForDeliveryBoy);
       kafkaTemplate.send("delivery-boy-send-order-details",value1);

    }


//    @KafkaListener(topics = "addOrdersInDeliveryBoy", groupId = "order-consume")
//    public void sendOrderDataToDeliveryBoy(String string){
//        String orderId;
//        String deliveryBoyId;
//
//        MainOrderReepo//find by delivery boy Id and ordrId
//
//        OrdersDetailsForDeliveryBoy ordersDetailsForDeliveryBoy = OrdersDetailsForDeliveryBoy.builder()
//                .orderId
//                .build();
//
//        private String orderId;
//
//        private String customerName;
//
//        private String deliveryLocation;
//
//        private String userReview;
//
//        private String deliveryBoyReview;
//
//        private Double orderEarnings;
//
//        private String orderStatus;
//
//        private LocalDateTime deliveredAt;
//
//
//
//
//    }


    public DeliveryBoyActiveDelivery findDetails(String orderId){
        DeliveryBoyActiveDeliveryRepoData deliveryBoyActiveDeliveryRepoData = mainOrderRepo.findByOrderIdDeliveryBoy(orderId);
        List<DeliveryBoyActiveDeliverySellerInfo> list = sellerInfoRepo.findByOrderId(orderId);
        DeliveryBoyActiveDelivery deliveryBoyActiveDelivery = DeliveryBoyActiveDelivery.builder()
                .order_id(deliveryBoyActiveDeliveryRepoData.getOrder_id())
                .orderStatus(deliveryBoyActiveDeliveryRepoData.getOrderStatus())
                .userAddress(deliveryBoyActiveDeliveryRepoData.getUserAddress())
                .userId(deliveryBoyActiveDeliveryRepoData.getUserId())
                .username(deliveryBoyActiveDeliveryRepoData.getUsername())
                .orderDate(deliveryBoyActiveDeliveryRepoData.getOrderDate())
                .totalAmount(deliveryBoyActiveDeliveryRepoData.getTotalAmount())
                .sellerInfoList(list).build();
        return deliveryBoyActiveDelivery;
    }

}
