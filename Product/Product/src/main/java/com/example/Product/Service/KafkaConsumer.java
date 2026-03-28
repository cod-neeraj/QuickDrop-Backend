package com.example.Product.Service;

import ch.hsr.geohash.GeoHash;
import com.example.Product.DTO.*;
import com.example.Product.Model.ProductData.Orders;
import com.example.Product.Model.ProductData.SalesData;
import com.example.Product.Model.ProductData.ShopkeeperIds;
import com.example.Product.Model.ProductData.Status;
import com.example.Product.Repository.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.apache.kafka.common.protocol.Message;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import com.razorpay.*;

import java.time.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class KafkaConsumer {
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ShopkeeperIdsRepo shopkeeperIdsRepo;

    @Autowired
    private KafkaService kafkaService;
    @Autowired
    private OrderRepo orderRepo;

    @Autowired
    private SalesDataRepo salesDataRepo;

    @Autowired
    private ProductStatsRepo productStatsRepo;



    private final RazorpayClient razorpayClient;

    @Autowired
    private ProductRepo productRepo;

    @Autowired
    private StringRedisTemplate redisTemplate;

    public KafkaConsumer(@Value("${razorpay.key_id}") String keyId,
                         @Value("${razorpay.key_secret}") String keySecret) throws Exception {
      
        this.razorpayClient = new RazorpayClient(keyId,keySecret);
    }

//    public RazorPayOrderDetails consumeProduct(UserOrderDetails orderDetails) throws JsonProcessingException, RazorpayException {
//
//
//        Map<Long,List<ProductDetailsForRedis>> productSeries = orderDetails.getOrderDetailsInRedis().getProductDetails();
//        System.out.println(orderDetails.getOrderId());
//        Orders orders = Orders.builder()
//                .orderId(orderDetails.getOrderId())
//                .userId(orderDetails.getUserId())
//                .username(orderDetails.getUserName())
//                .amount(orderDetails.getTotalAmount())
//                .paymentStatus("PENDING")
//                .orderStatus("PENDING")
//                .orderDate(LocalDate.now())
//                .productDetails(productSeries)
//                .build();
//
//        JSONObject options = new JSONObject();
//        options.put("amount", orderDetails.getTotalAmount() * 100); //
//        options.put("currency", "INR");
//        options.put("receipt", "txn_" + System.currentTimeMillis());
//
//        Order razorpayOrder = razorpayClient.orders.create(options);
//
//        RazorPayOrderDetails razorPayOrderDetails = RazorPayOrderDetails.builder()
//                .razorpayId(razorpayOrder.get("id"))                  // order ID
//                .entity(razorpayOrder.get("entity"))
//                .amount((Integer)razorpayOrder.get("amount"))// total amount in paise
//                .amount_paid((Integer) razorpayOrder.get("amount_paid"))   // usually 0 initially
//                .amount_due((Integer) razorpayOrder.get("amount_due"))     // equals amount initially
//                .currency(razorpayOrder.get("currency"))            // "INR"
//                .receipt(razorpayOrder.get("receipt"))              // your receipt string
//                .status(razorpayOrder.get("status"))                // "created"
//                .attempts((Integer) razorpayOrder.get("attempts"))  // usually 0
//                .build();
//
//        orders.setPaymentId(razorPayOrderDetails.getRazorpayId());
//        String key = "RazorPay:" + orders.getPaymentId();
//        String value = objectMapper.writeValueAsString(orders);
//
//        System.out.println(value);
//
//        System.out.println("🥳🥳🥳🥳");
//        redisTemplate.opsForValue().set(key, value, Duration.ofMinutes(30));
//      return razorPayOrderDetails;
//    }


    @KafkaListener(topics = "add-sales", groupId = "order-consume")
    public void salesAdd(String salesData) throws JsonProcessingException {

        DiscountDataDTO orders = objectMapper.readValue(salesData, DiscountDataDTO.class);

        SalesData salesData1 = SalesData.builder()
                .sellerId(orders.getSellerId())
                .geoHashCode(orders.getGeohash())
                .heading(orders.getHeading())
                .description(orders.getDescription())
                .startDate(orders.getStartDate())
                .endDate(orders.getEndDate())
                .imageUrl(orders.getImageUrl())
                .status(Status.ACTIVE)
                .shopName(orders.getShopName())
                .shopAddress(orders.getAddress())
                .build();


        SalesData saved = salesDataRepo.save(salesData1);

        String offerId = "offer:" + saved.getId();
        String geoKey = "geo:" + orders.getGeohash();


        redisTemplate.opsForSet().add(geoKey, offerId);

        Map<String, String> offerMap = new HashMap<>();
        offerMap.put("title", orders.getHeading());
        offerMap.put("description", orders.getDescription());
        offerMap.put("shopName", orders.getShopName());
        offerMap.put("imageUrl", orders.getImageUrl());
        offerMap.put("startDate", orders.getStartDate().toString());
        offerMap.put("endDate", orders.getEndDate().toString());

        redisTemplate.opsForHash().putAll(offerId, offerMap);

        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Asia/Kolkata"));

        ZonedDateTime end = orders.getEndDate()
                .atTime(23, 59, 59)
                .atZone(ZoneId.of("Asia/Kolkata"));

        long ttlSeconds = Duration.between(now, end).getSeconds();

        if (ttlSeconds > 0) {
            redisTemplate.expire(offerId, ttlSeconds, TimeUnit.SECONDS);
        }

    }


    @KafkaListener(topics = "product-add", groupId = "order-consume")
    @Transactional
    public void addProductOrderCount(String productIds){
        String[] parts = productIds.split(",");
        List<String> list = new ArrayList<>(Arrays.asList(parts));


        productStatsRepo.incrementOrderCountBulk(list);

        System.out.println("❤️❤️");

    }


    @Transactional
    public void saveAfterSuccessOfPayment(String paymentId) throws JsonProcessingException {
        String key = "RazorPay:" + paymentId;
        String value = redisTemplate.opsForValue().get(key);

        System.out.println(value);
//        Orders orders = objectMapper.readValue(value, Orders.class);
//
////        List<ShopkeeperIds> shopkeeperIds = orders.getShopkeeperIds();
////        shopkeeperIdsRepo.saveAll(shopkeeperIds);
//        orderRepo.save(orders);
////        String id =  shopkeeperIds.get(0).getShopkeeperId();
////        String keys = "shopkeeper:" + id;
//        HashOperations<String, String, String> hashOps = redisTemplate.opsForHash();
//
//        String longitude = hashOps.get(keys, "long");
//        String latitude = hashOps.get(keys, "lat");
//
//        String send = longitude + "," + latitude;
//        kafkaService.sendDeliveryBoyRequest(send);
//
//        redisTemplate.delete(key);
//
//
//
//
    }

//    @KafkaListener(topics = "check-product-quantity", groupId = "order-consume")
//    @SendTo("product-check-response") // 👈 tells Spring to send reply automatically
//    public String checkProductQuantity(String productId) {
//        System.out.println("❤️ Received: " + productId);
//
//        int updated = productRepo.subtractIfAvailable(productId, 1L);
//        if(updated == 0){
//            return "0";
//        }
//
//        return "1"; // 👈 Spring sends this to reply topic with correlationId preserved
//    }

//    @KafkaListener(topics = "add-product-again",groupId = "order-consume")
//    public void addProductAgain(String productId){
//        int updated = productRepo.addProductAgain(productId);
//
//    }


}
