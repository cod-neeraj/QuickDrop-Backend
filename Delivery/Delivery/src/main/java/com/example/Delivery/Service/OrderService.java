package com.example.Delivery.Service;

import ch.hsr.geohash.GeoHash;
import com.example.Delivery.Config.WebSocketHandler;
import com.example.Delivery.DTO.*;
import com.example.Delivery.DataToShow.MiniOrderDetails;
import com.example.Delivery.DeliveryApplication;
import com.example.Delivery.Models.*;
import com.example.Delivery.Repository.ActiveDeliveryRepo;
import com.example.Delivery.Repository.OrdersRepo;
import com.example.Delivery.Repository.UserRepo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.*;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.domain.geo.Metrics;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class OrderService {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private OrdersRepo ordersRepo;


    @Autowired
    private ActiveDeliveryRepo activeDeliveryRepo;

    @Autowired
    private KafkaTemplate kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @KafkaListener(topics = "delivery-boy-allocation",groupId = "order-consume")
    public Boolean allocateDeliveryBoy(String data) throws JsonProcessingException {

        System.out.println("checkpoint 1 ✅✅");
        System.out.println(data);
        objectMapper = new ObjectMapper();
        DeliveryBoyOrderDetails deliveryBoyOrderDetails =
                objectMapper.readValue(data, DeliveryBoyOrderDetails.class);
        Set<SellerDeliveryDetails> list = deliveryBoyOrderDetails.getSellerDeliveryDetailsSet();

        int n = list.size();
        if (n == 0) {
            System.out.println("❤️❤️");
            throw new IllegalArgumentException("No seller locations provided");
        }

        double sumLat = 0.0;
        double sumLng = 0.0;

        for (SellerDeliveryDetails sellerDeliveryDetails : list) {
            System.out.println(sellerDeliveryDetails.getLatitude());
            System.out.println("neeraj");
            System.out.println(sellerDeliveryDetails.getLongitude());
            double lat = sellerDeliveryDetails.getLongitude(); // latitude
            double lng = sellerDeliveryDetails.getLatitude(); // longitude

            sumLat += lat;
            sumLng += lng;
        }

        double centerLat = sumLat / n;
        double centerLng = sumLng / n;

        Point centroid = new Point(centerLng, centerLat);

        GeoHash centerHash = GeoHash.withCharacterPrecision(
                centroid.getY(),
                centroid.getX(),
                5
        );

        List<String> geoCells = new ArrayList<>();
        geoCells.add(centerHash.toBase32());

        for (GeoHash gh : centerHash.getAdjacent()) {
            geoCells.add(gh.toBase32());
        }


        Map<String, GeoCandidate> uniqueCandidates = new HashMap<>();

        for (String cell : geoCells) {

            String key = "deliveryBoy:location:" + cell;

            GeoResults<RedisGeoCommands.GeoLocation<String>> results =
                    redisTemplate.opsForGeo().radius(
                            key,
                            new Circle(
                                    new Point(centerLng, centerLat),
                                    new Distance(5, Metrics.KILOMETERS)
                            ),
                            RedisGeoCommands.GeoRadiusCommandArgs.newGeoRadiusArgs()
                                    .includeDistance()
                    );

            if (results == null) continue;

            for (GeoResult<RedisGeoCommands.GeoLocation<String>> r : results) {

                String deliveryBoyId = r.getContent().getName();
                double distance = r.getDistance().getValue();

                GeoCandidate existing = uniqueCandidates.get(deliveryBoyId);

                if (existing == null || distance < existing.getDistance()) {
                    uniqueCandidates.put(
                            deliveryBoyId,
                            new GeoCandidate(deliveryBoyId, distance)
                    );
                }
            }
        }

        Collection<GeoCandidate> allGeoCandidates = uniqueCandidates.values();


        int K1 = 500;

        Comparator<GeoCandidate> worstDistanceFirst =
                (a, b) -> Double.compare(b.getDistance(), a.getDistance());

        PriorityQueue<GeoCandidate> distanceHeap =
                new PriorityQueue<>(K1, worstDistanceFirst);

        for (GeoCandidate c : allGeoCandidates) {

            if (distanceHeap.size() < K1) {
                distanceHeap.offer(c);
            } else {
                GeoCandidate worst = distanceHeap.peek();

                if (c.getDistance() < worst.getDistance()) {
                    distanceHeap.poll();
                    distanceHeap.offer(c);
                }
            }
        }

        List<GeoCandidate> nearest500 = new ArrayList<>(distanceHeap);


        List<RankedCandidate> candidatesWithStats = new ArrayList<>();

        long now = System.currentTimeMillis();

        for (GeoCandidate geo : nearest500) {

            String id = geo.getDeliveryBoyId();
            String statsKey = "deliveryBoy:stats:" + id;

            Map<Object, Object> stats = redisTemplate.opsForHash().entries(statsKey);
            if (stats == null || stats.isEmpty()) continue;

            String status = (String) stats.get("status");
            String pointsStr = (String) stats.get("points");

            if (status == null || pointsStr == null) continue;

            if (!"ONLINE".equals(status)) continue;


            int points = Integer.parseInt(pointsStr);

            candidatesWithStats.add(
                    new RankedCandidate(id, geo.getDistance(), points)
            );
        }


        int K2 = 20;

        Comparator<RankedCandidate> worstFinalFirst = (a, b) -> {

            int cmp = Double.compare(b.getDistance(), a.getDistance());
            if (cmp != 0) return cmp;

            return Integer.compare(a.getPoints(), b.getPoints());
        };

        PriorityQueue<RankedCandidate> finalHeap =
                new PriorityQueue<>(K2, worstFinalFirst);

        for (RankedCandidate c : candidatesWithStats) {

            if (finalHeap.size() < K2) {
                finalHeap.offer(c);
            } else {
                RankedCandidate worst = finalHeap.peek();

                if (isBetterFinal(c, worst)) {
                    finalHeap.poll();
                    finalHeap.offer(c);
                }
            }
        }


        List<RankedCandidate> top20 = new ArrayList<>(finalHeap);

        top20.sort((a, b) -> {
            int cmp = Double.compare(a.getDistance(), b.getDistance());
            if (cmp != 0) return cmp;
            return Integer.compare(b.getPoints(), a.getPoints());
        });

        for(RankedCandidate rankedCandidate:top20){
            System.out.println("checkpoint 1 ✅✅");
            System.out.println("❤️❤️");
            System.out.println(rankedCandidate.getDeliveryBoyId());
        }
        MiniOrderDetails miniOrderDetails = MiniOrderDetails.builder()
                .orderEarnings((double) 33.0)
                .droplocation(deliveryBoyOrderDetails.getDeliveryAddress())
                .orderId(deliveryBoyOrderDetails.getOrderId())
                .orderStatus("OPEN")
                .build();

        String orderId = deliveryBoyOrderDetails.getOrderId();

        redisTemplate.opsForValue().set(
                "order:status:" + orderId,
                "OPEN",
                2, TimeUnit.MINUTES   // TTL is important
        );

        for (RankedCandidate deliveryBoyId : top20) {
            redisTemplate.opsForSet().add(
                    "order:eligible:" + orderId,
                    deliveryBoyId.getDeliveryBoyId()
            );
        }

        redisTemplate.expire("order:eligible:" + orderId, 5, TimeUnit.MINUTES);

        for (RankedCandidate c : top20) {

            String id = c.getDeliveryBoyId();
            String json = objectMapper.writeValueAsString(miniOrderDetails);

            WebSocketHandler.sendToDeliveryBoy(id, json);
        }
        return true;

    }

    private boolean isBetterFinal(RankedCandidate a, RankedCandidate b) {

        if (a.getDistance() < b.getDistance()) return true;
        if (a.getDistance() > b.getDistance()) return false;

        return a.getPoints() > b.getPoints();
    }

    @Transactional
    public void acceptOrder(String orderId, String deliveryBoyId) throws JsonProcessingException  {


        String status = (String) redisTemplate.opsForValue()
                .get("order:status:" + orderId);

        if (!"OPEN".equals(status)) {
            return ;
        }

        redisTemplate.opsForValue().set("order:status:" + orderId, "ASSIGNED");

        Set<String> eligible =
                redisTemplate.opsForSet().members("order:eligible:" + orderId);

        MiniOrderDetails miniOrderDetails = MiniOrderDetails.builder()
                .orderStatus("ASSIGNED")
                .orderEarnings((double) 25.0)
                .orderId(orderId)
                .droplocation("hh")
                .build();
        String value = objectMapper.writeValueAsString(miniOrderDetails);
        for (String otherId : eligible) {
                WebSocketHandler.sendToDeliveryBoy(otherId, value);

        }
        redisTemplate.delete("order:eligible:" + orderId);

//       kafkaTemplate.send("addOrdersInDeliveryBoy",deliveryBoyId+","+orderId);
        DeliveryBoyBasicDetails deliveryBoyBasicDetails = userRepo.findDetails(deliveryBoyId);

        DeliveryBoyDetails deliveryBoyDetails = DeliveryBoyDetails.builder()
                .deliveryBoyName(deliveryBoyBasicDetails.getName())
                .orderId(orderId)
                .phoneNumber(deliveryBoyId)
                .build();
        String value1 = objectMapper.writeValueAsString(deliveryBoyDetails);

        kafkaTemplate.send("deliveryBoy-userRepo",value1);

    }

    @KafkaListener(topics = "delivery-boy-send-order-details",groupId = "order-consume")
    public void addOrderToThisService(String value) throws JsonProcessingException {

        OrdersDetailsForDeliveryBoy orders = objectMapper.readValue(value,OrdersDetailsForDeliveryBoy.class);
        User user = userRepo.findById(orders.getDeliveryBoyId()).orElseThrow(()->new RuntimeException("maa chuda"));
        Set<Orders> userOrders = user.getOrders();
        Orders order = Orders.builder()
                .orderId(orders.getOrderId())
                .customerName(orders.getCustomerName())
                .deliveryLocation(orders.getDeliveryLocation())
                .deliveryBoyReview(orders.getDeliveryBoyReview())
                .userReview(orders.getUserReview())
                .orderStatus(orders.getOrderStatus())
                .orderEarnings(orders.getOrderEarnings())
                .deliveredAt(orders.getDeliveredAt())
                .build();
        userOrders.add(order);
        userRepo.save(user);

    }

    public void updateOrderStatus(String phoneNumber,
                                  String status,
                                  String orderId,
                                  String sellerId) throws JsonProcessingException {

        objectMapper = new ObjectMapper();
        DeliveryBoyOrderStatusChange deliveryBoyOrderStatusChange = DeliveryBoyOrderStatusChange.builder()
                .orderId(orderId)
                .phoneNumber(phoneNumber)
                .sellerId(sellerId)
                .build();
        if(status.equalsIgnoreCase("NOTPICKED")){
            deliveryBoyOrderStatusChange.setStatus(DeliveryBoyStatus.NOTpICKED);
               String s = objectMapper.writeValueAsString(deliveryBoyOrderStatusChange);

            kafkaTemplate.send("deliveryBoy-updateOrderStatus",s);

        }else{
            deliveryBoyOrderStatusChange.setStatus(DeliveryBoyStatus.PICKED);
            String s = objectMapper.writeValueAsString(deliveryBoyOrderStatusChange);

            kafkaTemplate.send("deliveryBoy-updateOrderStatus",s);
        }
        return ;

    }

    public void setDeliveredTheOrder(String phoneNumber,String orderId){
        kafkaTemplate.send("deliveryBoy-delivered-order",orderId);

    }
    public void setOutForDeliveryTheOrder(String phoneNumber,String orderId){
        kafkaTemplate.send("deliveryBoy-outForDelivery-order",orderId);

    }



}
