package com.example.User.Service.Seller;

import ch.hsr.geohash.GeoHash;
import com.example.User.DTO.*;
import com.example.User.DataToShow.*;
import com.example.User.DataToShow.ProductCreateDto.ProductDTO;
import com.example.User.DataToShow.ProductCreateDto.ProductVariantsDTO;
import com.example.User.DataToShow.SellerDashBoard.BasicProductDetails;
import com.example.User.DataToShow.SellerDashBoard.MainDashboardData;
import com.example.User.DataToShow.SellerDashBoard.PersonalInfo;
import com.example.User.DataToShow.SellerDashBoard.RecentOrder;
import com.example.User.Exceptions.InternalServiceException;
import com.example.User.Exceptions.MessagePublishException;
import com.example.User.Exceptions.UserNotFoundException;
import com.example.User.FeignInterface.addProductInterface;
import com.example.User.Models.OrdersData.ProductDetailsInfo;
import com.example.User.Models.Seller.*;
import com.example.User.Repository.OrderRepositories.SellerInfo;
import com.example.User.Repository.SellerRepositories.*;
import com.example.User.Service.KafkaService;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.PageRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.aggregation.SelectionOperators;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.Duration;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;


@Service
public class SellerService {
    @Autowired
    private SellerRepo sellerRepo;

    @Autowired
    addProductInterface addProductInterface;

    @Autowired
    KafkaService kafkaService;

    @Autowired
    SellerInfo sellerInfoRepo;

    @Autowired
    private Seller_product_Repo sellerProductRepo;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private Seller_monthly_revenueRepo sellerMonthlyRevenueRepo;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private Seller_daily_revenueRepo sellerDailyRevenueRepo;

    @Autowired
    private RedisTemplate<String,String> redisTemplate;


    public Boolean updatePersonalInfo(SellerPersonalInfo personalInfo, String phoneNumber){
        Optional<Seller_Info> sellerInfo = sellerRepo.findByPhoneNumber(phoneNumber);
        if(sellerInfo.isEmpty()){
            return false;
        }
        Seller_Info sellerInfo1 = sellerInfo.get();
        sellerInfo1.setSellerId(sellerInfo.get().getSellerId());
        sellerInfo1.setPhoneNumber(personalInfo.getPhoneNumber());
        sellerInfo1.setImageUrls(new HashSet<>());
        sellerInfo1.setImageUrls(personalInfo.getImageUrls());
        sellerInfo1.setName(personalInfo.getName());
        sellerInfo1.setBio(personalInfo.getBio());
        sellerInfo1.setShopName(personalInfo.getShopName());
        sellerInfo1.setGstNumber(personalInfo.getGstNumber());
        sellerInfo1.setTimings(personalInfo.getTimings());
        sellerInfo1.setStreet(personalInfo.getStreet());
        sellerInfo1.setCity(personalInfo.getCity());
        sellerInfo1.setState(personalInfo.getState());
        sellerInfo1.setCountry(personalInfo.getCountry());
        sellerInfo1.setZipCode(personalInfo.getZipCode());
        sellerInfo1.setThumbnailImage(
                personalInfo.getImageUrls()
                        .stream()
                        .findFirst()
                        .orElse(null)
        );


        sellerRepo.save(sellerInfo1);
        return true;


    }
    public SellerPersonalInfo getSellerInfo(String phoneNumber) {
        Optional<Seller_Info> sellerInfo =  sellerRepo.findByPhoneNumber(phoneNumber);
        Seller_Info s = sellerInfo.get();

                SellerPersonalInfo sellerPersonalInfo= SellerPersonalInfo.builder()
                        .shopName(s.getShopName())
                        .name(s.getName())
                        .phoneNumber(s.getPhoneNumber())
                        .gstNumber(s.getGstNumber())
                        .timings(s.getTimings())
                        .street(s.getStreet())
                        .city(s.getCity())
                        .country(s.getCountry())
                        .state(s.getState())
                        .zipCode(s.getZipCode())
                        .bio(s.getBio())
                        .imageUrls(s.getImageUrls())
                        .build();
                return sellerPersonalInfo;
    }

    public Boolean goLiveOrOffline(String phoneNumber,Double lng,Double lat,String status){

        if(status.equalsIgnoreCase("OFFLINE")){
            String statsKey = "deliveryBoy:stats:" + phoneNumber;
            redisTemplate.opsForHash().put(statsKey, "status", "OFFLINE");
            return true;
        }
        GeoHash hash = GeoHash.withCharacterPrecision(lat, lng, 5);
        String hashCode = hash.toBase32();

        String geoKey = "deliveryBoy:location:" + hashCode;

        redisTemplate.opsForGeo().add(
                geoKey,
                new Point(lng, lat),
                phoneNumber
        );

        String statsKey = "deliveryBoy:stats:" + phoneNumber;

        redisTemplate.opsForHash().put(statsKey, "status", "ONLINE");
        redisTemplate.opsForHash().put(statsKey, "points", "0");
        return true;



    }
    public Boolean createProduct(ProductDTO productDTO, String phoneNumber) {
        try {
            String key = "sellerLocation:" + phoneNumber;
            String value = redisTemplate.opsForValue().get(key);

            ObjectMapper mapper = new ObjectMapper();
            SellerLocation sl;

            if (value == null) {
                SellerLocationRepo entity = sellerRepo.findLocation(phoneNumber)
                        .orElseThrow(() -> new RuntimeException("Seller location not found"));

                sl = new SellerLocation();
                sl.setSellerId(entity.getSellerId());
                sl.setLatitude(entity.getLocation().getY());
                sl.setLongitude(entity.getLocation().getX());

                value = mapper.writeValueAsString(sl);
                redisTemplate.opsForValue().set(key, value, Duration.ofHours(6));
            } else {
                sl = mapper.readValue(value, SellerLocation.class);
            }

            ResponseEntity<String> response = addProductInterface.addProduct(productDTO, phoneNumber);
            Double defaultprice = (double) Integer.MAX_VALUE;
            List<ProductVariantsDTO> list = productDTO.getProductVariantsDTOList();
            for(ProductVariantsDTO productVariantsDTO:list){
                if(productVariantsDTO.getPrice()<defaultprice){
                    defaultprice = productVariantsDTO.getPrice();
                }
            }
            Integer quan = Integer.MAX_VALUE;
            for(ProductVariantsDTO productVariantsDTO:list){
                if(productVariantsDTO.getQuantity()<quan){
                    quan = productVariantsDTO.getQuantity();
                }
            }


            if (response.getStatusCode().is2xxSuccessful()) {
                Seller_Info sellerInfo = sellerRepo.findBySellerId(sl.getSellerId()).orElseThrow(()-> new RuntimeException("aa"));
                Seller_product sellerProduct = Seller_product.builder()
                        .productId(response.getBody())
                        .productName(productDTO.getName())
                        .price(defaultprice)
                        .quantity(quan)
                        .totalOrders(0L)
                        .ratings(0.0)
                        .productCategory(ProductCategory.valueOf(productDTO.getProductCategory()))
                        .seller(sellerInfo)
                        .build();

                sellerProductRepo.save(sellerProduct);

            }

            return true;

        } catch (Exception e) {
            throw new RuntimeException("Failed to process seller location", e);
        }
    }

    public Boolean addSale(DiscountDTO discountDTO, String phoneNumber) {

        if (phoneNumber == null || phoneNumber.isBlank()) {
            throw new IllegalArgumentException("Login Again");
        }

        if (discountDTO == null) {
            throw new IllegalArgumentException("Discount data must not be null");
        }

        BasicSellerInfo basicSellerInfo = sellerRepo.findSellerBasic(phoneNumber)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        KafkaDiscountDTO kafkaDiscountDTO = KafkaDiscountDTO.builder()
                .imageUrl(discountDTO.getImageUrl())
                .endDate(discountDTO.getEndDate())
                .address(basicSellerInfo.getStreet() + ", " + basicSellerInfo.getCity())
                .description(discountDTO.getDescription())
                .heading(discountDTO.getHeading())
                .startDate(discountDTO.getStartDate())
                .name(basicSellerInfo.getName())
                .shopName(basicSellerInfo.getShopName())
                .sellerId(basicSellerInfo.getSellerId())
                .geohash(basicSellerInfo.getGeohash())
                .build();

        try {
            String payload = objectMapper.writeValueAsString(kafkaDiscountDTO);
            kafkaTemplate.send("add-sales", payload);
        } catch (JsonProcessingException ex) {
            throw new InternalServiceException("Failed to process sale data");
        } catch (Exception ex) {
            throw new MessagePublishException("Failed to publish sale event");
        }

        return true;
    }


    public Set<BasicProductDetails> getBasicProductSet(String phoneNumber,int page, int size){
        Pageable pageable = PageRequest.of(page, size);
        return sellerProductRepo.findProductSet(phoneNumber, pageable).toSet();

    }

    public Map<String,Object> OrderDetailsSellerDashBoard(String sellerPhoneNumber){
        String sellerId = sellerRepo.findSellerIdByPhoneNumber(sellerPhoneNumber);

        List<SellerDashBoardOrderList> list = sellerInfoRepo.findOrderListBySellerPhoneNumber(sellerId);


        String key = "SellerStatus:" + sellerPhoneNumber;
        String status = redisTemplate.opsForValue().get(key);

        if (status == null) {
            status = "OFFLINE";
        }

        Map<String, Object> response = new HashMap<>();
        response.put("status", status);
        response.put("orders",list);

        return response;

    }
    public List<ProductDetailsInfo> getDetailsOfOrder(String orderId, String phoneNumber) {
        String sellerId = sellerRepo.findSellerIdByPhoneNumber(phoneNumber);

        List<ProductDetailsInfo> list1 = sellerInfoRepo.findDetailorder(orderId,sellerId);

        return list1;
    }

    public List<SellerProductList> getSellerAllProduct(String phoneNumber,Integer page,Integer size){
        ResponseEntity<?> response = addProductInterface.getAllProductSeller(phoneNumber,page,size);
        if(response.getStatusCode().is2xxSuccessful()){
            List<SellerProductList> list = (List<SellerProductList>) response.getBody();
            return list;
        }
        return null;
    }

    @Transactional
    public Boolean  updateOrderStatus(String status,String orderId,String phoneNumber){
        SellerStatus status1 = SellerStatus.PROCESSING;
        if(status.equalsIgnoreCase("PROCESSING")){
            status1 = SellerStatus.PROCESSING;
        } else if (status.equalsIgnoreCase("ACCEPTED")) {
            status1 = SellerStatus.ACCEPTED;
        } else if (status.equalsIgnoreCase("REJECTED")) {
            status1 = SellerStatus.REJECTED;
        }else if (status.equalsIgnoreCase("PACKED")) {
            status1 = SellerStatus.PACKED;
        } else{
            status1 = SellerStatus.SHIPPED;
        }

        int count = sellerInfoRepo.updateStatus(orderId,phoneNumber,status1);
        System.out.println(count);
        Integer in = sellerInfoRepo.findOrderStatus(orderId);
        String status2 = "";
        if(in == 1) { status2 = "PROCESSING";}
        else if (in ==2) { status2 = "ACCEPTED";}
        else if (in ==3) { status2 = "PACKED";}
        else { status2 = "SHIPPED";}


        System.out.println(in+"❌❌❌❌");
        notifyOrderUpdate(orderId,status2);
        return count==1;
    }
    private Map<String, List<SseEmitter>> emitters = new ConcurrentHashMap<>();

    public void addEmitter(String orderId, SseEmitter emitter) {
        emitters.computeIfAbsent(orderId, k -> new CopyOnWriteArrayList()).add(emitter);
    }

    public void removeEmitter(String orderId, SseEmitter emitter) {
        List<SseEmitter> list = emitters.get(orderId);
        if (list != null) list.remove(emitter);
    }
    public void notifyOrderUpdate(String orderId, String data) {


        List<SseEmitter> list = emitters.get(orderId);

        if (list != null) {
            for (SseEmitter emitter : list) {
                try {
                    emitter.send(SseEmitter.event()
                            .name("order-update")
                            .data(data));
                } catch (Exception e) {
                    emitter.complete();
                }
            }
        }
    }


    public record MonthlyRevenue(double thisMonth, double lastMonth) {}

    public MonthlyRevenue getMonthlyRevenue(Long sellerId) {

        LocalDate now = LocalDate.now();
        LocalDate startOfCurrentMonth = now.withDayOfMonth(1);
        double  thisMonthRevenue = 0.0;
        double lastMonthRevenue = 0.0;

        try {
            thisMonthRevenue =
                    sellerInfoRepo.getRevenueBetweenDates(
                            sellerId,
                            startOfCurrentMonth.atStartOfDay(),
                            now.plusDays(1).atStartOfDay()
                    );
        } catch (Exception e) {
             thisMonthRevenue = 0.0;
        }
        LocalDate lastMonthDate = now.minusMonths(1);
        int lastMonth = lastMonthDate.getMonthValue();
        int lastYear = lastMonthDate.getYear();

        try {
             lastMonthRevenue =
                    sellerMonthlyRevenueRepo.getRevenueForMonth(sellerId, lastYear, lastMonth);

//            if (lastMonthRevenue == null) {
//                LocalDate start = lastMonthDate.withDayOfMonth(1);
//                LocalDate end = start.plusMonths(1);
//
//                lastMonthRevenue =
//                        sellerInfoRepo.getRevenueBetweenDates(
//                                sellerId,
//                                start.atStartOfDay(),
//                                end.atStartOfDay()
//                        );
//            }

        } catch (Exception e) {
            lastMonthRevenue = 0.0;
        }


        return new MonthlyRevenue(thisMonthRevenue, lastMonthRevenue);
    }
    public MainDashboardData getDashboardData(String phoneNumber){
        Object[] seller = sellerRepo.findNameByPhoneNumber(phoneNumber);
        Object[] sellerAndId = (Object[]) seller[0];


        MainDashboardData mainDashboardData = MainDashboardData.builder()
                .seller_name((String) sellerAndId[1])
                .total_products(0L)
                .total_orders(0L)
                .prev_month_orders(0L)
                .pending_order(0L)
                .monthly_revenue(0.0)
                .prev_month_revenue(0.0)
                .build();
        ResponseEntity<Long> response = addProductInterface.getProductCount(phoneNumber);
        if(response.getStatusCode().is2xxSuccessful()){
            mainDashboardData.setTotal_products(response.getBody());
        }
        Object[] outer = sellerInfoRepo.findMainDashboardPageStats(phoneNumber,SellerStatus.PROCESSING);
        Object[] obj = (Object[]) outer[0];
        Long currentMonth = ((Number) obj[0]).longValue();
        Long today = ((Number) obj[1]).longValue();
        Long lastMonth = ((Number) obj[2]).longValue();
        Long processing = ((Number) obj[3]).longValue();
        mainDashboardData.setTotal_orders(today);
        mainDashboardData.setPending_order(processing);
        mainDashboardData.setPrev_month_orders(lastMonth);
        mainDashboardData.setTotal_orders(currentMonth);


        MonthlyRevenue monthlyRevenue = getMonthlyRevenue((Long) sellerAndId[0]);
        mainDashboardData.setMonthly_revenue(monthlyRevenue.thisMonth);
        mainDashboardData.setPrev_month_revenue(monthlyRevenue.lastMonth);



        return mainDashboardData;


    }

    public List<Top5orderDetails> getTop5orders(String phoneNumber){
        Pageable pageable = PageRequest.of(0, 5);

        List<Object[]> top5Orders =
                sellerInfoRepo.findTop5Orders("2d335fa0-12e2-4b2f-a510-636964b647b9", pageable);

        List<Top5orderDetails> list = new ArrayList<>();
        for(Object[] obj : top5Orders) {
            Top5orderDetails top5orderDetails = Top5orderDetails.builder()
                    .amount((Double) obj[1])
                    .username((String) obj[0])
                    .status((SellerStatus) obj[2])
                    .build();
            list.add(top5orderDetails);
        }
        return list;

    }

    public String updateStatus(String status, String phoneNumber) {

            String key = "SellerStatus:" + phoneNumber;

            if ("ONLINE".equalsIgnoreCase(status)) {
                redisTemplate.opsForValue().set(key, "ONLINE");
                String id = sellerRepo.findSellerIdByPhoneNumber(phoneNumber);
                return id;
            }
            else{
                redisTemplate.opsForValue().set(key, "OFFLINE");
                return null;
            }




    }

//

//    public record MonthlyRevenue(double thisMonth, double lastMonth) {}
//
//    public MonthlyRevenue getMonthlyRevenue(Long seller_id) {
//        LocalDate now = LocalDate.now();
//        int month = now.getMonthValue();
//        int year = now.getYear();
//        if(month == 1){
//            month=12;
//            year-=1;
//        }else{
//            month = month-1;
//        }
//
//        double thisMonth=sellerDailyRevenueRepo.getThisMonthRevenue(seller_id,now);
//        double lastMonth = sellerMonthlyRevenueRepo.getRevenueForMonth(seller_id,year,month);
//        return new MonthlyRevenue(thisMonth, lastMonth);
//    }
//
//    public MainDashboardData getDashboardData(String phoneNumber){
//        Seller_Info seller = sellerRepo.findByPhoneNumber(phoneNumber).orElseThrow(()-> new RuntimeException("user not ounf"));
//        MainDashboardData mainDashboardData = MainDashboardData.builder()
//                .seller_name(seller.getName())
//                .shop_name(seller.getShopName())
//                .total_products(sellerProductRepo.countProductBySellerId(seller.getId()))
//                .pending_order(sellerOrderRepo.countOrdersBySellerAndStatus(seller.getId(), OrderStatus.PROCESSING))
//                .build();
//
//        MonthlyRevenue monthlyRevenue = getMonthlyRevenue(seller.getId());
//        mainDashboardData.setMonthly_revenue(monthlyRevenue.thisMonth);
//        mainDashboardData.setPrev_month_revenue(monthlyRevenue.lastMonth);
//
//            List<Seller_orders> set = sellerOrderRepo.findOrdersWithProducts(seller.getId(), PageRequest.of(0, 10));
//        if(set.isEmpty()){
//            mainDashboardData.setRecentOrder(new HashSet<>());
//        }
//        for(Seller_orders r:set){
//            RecentOrder recentOrder = RecentOrder.builder()
//                    .orderId(r.getOrderId())
//                    .orderStatus(String.valueOf(r.getOrderStatus()))
//                    .price(r.getPrice())
//                    .basicProductDetailsSet(new HashSet<>())
//                    .build();
//            for(BasicProductIInfo basic:r.getBasicProductIInfo()){
//                BasicProductDetails basicProductDetails = BasicProductDetails.builder()
//                        .productId(basic.getProductId())
//                        .productName(basic.getName())
//                        .price(basic.getPrice())
//                        .build();
//
//     recentOrder.getBasicProductDetailsSet().add(basicProductDetails);
//
//            }
//            mainDashboardData.getRecentOrder().add(recentOrder);
//
//        }
//        Set<BasicProductDetails> set1 = sellerProductRepo.findLowStock(seller.getId());
//        mainDashboardData.setBasicProductDetailsSet(set1);
//
//        return mainDashboardData;
//
//
//    }
//
//    public PersonalInfo getPersonalInfo(String phoneNumber){
//        Optional<Seller_Info> personalInfo = sellerRepo.findPersonalInfo(phoneNumber);
//        if(personalInfo.isEmpty()){
//            PersonalInfo personalInfo1 = PersonalInfo.builder().build();
//            return personalInfo1;
//        }
//        PersonalInfo personalInfo1 = PersonalInfo.builder()
//                .sellerId(personalInfo.get().getSellerId())
//                .phoneNumber(personalInfo.get().getPhoneNumber())
//                .name(personalInfo.get().getName())
//                .imageUrls(personalInfo.get().getImageUrls())
//                .bio(personalInfo.get().getBio())
//                .shopName(personalInfo.get().getShopName())
//                .gstumber(personalInfo.get().getGstumber())
//                .timings(personalInfo.get().getTimings())
//                .street(personalInfo.get().getStreet())
//                .city(personalInfo.get().getCity())
//                .state(personalInfo.get().getState())
//                .country(personalInfo.get().getCountry())
//                .zipCode(personalInfo.get().getZipCode())
//                .status(personalInfo.get().getStatus())
//                .build();
//
//        return personalInfo1;
//    }
//
//    public Boolean updatePersonalInfo(PersonalInfo personalInfo,String phoneNumber){
//        Optional<Seller_Info> sellerInfo = sellerRepo.findByPhoneNumber(phoneNumber);
//        if(sellerInfo.isEmpty()){
//            return false;
//        }
//        Seller_Info sellerInfo1 = sellerInfo.get();
//        sellerInfo1.setSellerId(personalInfo.getSellerId());
//        sellerInfo1.setPhoneNumber(personalInfo.getPhoneNumber());
//        sellerInfo1.setImageUrls(new HashSet<>());
//        sellerInfo1.setImageUrls(personalInfo.getImageUrls());
//        sellerInfo1.setName(personalInfo.getName());
//        sellerInfo1.setBio(personalInfo.getBio());
//        sellerInfo1.setShopName(personalInfo.getShopName());
//        sellerInfo1.setGstumber(personalInfo.getGstumber());
//        sellerInfo1.setTimings(personalInfo.getTimings());
//        sellerInfo1.setStreet(personalInfo.getStreet());
//        sellerInfo1.setCity(personalInfo.getCity());
//        sellerInfo1.setState(personalInfo.getState());
//        sellerInfo1.setCountry(personalInfo.getCountry());
//        sellerInfo1.setZipCode(personalInfo.getZipCode());
//
//        sellerRepo.save(sellerInfo1);
//        return true;
//
//
//    }
//
//    public Set<BasicProductDetails> getBasicProductSet(String phoneNumber,int page, int size){
//        Pageable pageable = PageRequest.of(page, size);
//        return sellerProductRepo.findProductSet(phoneNumber, pageable).toSet();
//
//    }
//
    public SellerMiniDetails sellerMiniDetails(String id){
          Optional<SellerMiniDetails> sellerMiniDetails = sellerRepo.findSeller(id);
          if(sellerMiniDetails.isEmpty()){
              throw new RuntimeException("mma chuda");
          }
          return sellerMiniDetails.get();
    }

//    public void saveOrderToDB(Map<String, SellerAndProduct> map,
//                              String orderId,
//                              OrderStatus orderStatus,
//                              Double price,
//                              String username
//                              ){
//
//        for(Map.Entry<String, SellerAndProduct> data:map.entrySet()){
//            SellerAndProduct sap = data.getValue();
//            SellerMiniDetails seller = sap.getSeller();
//            List<ProductDetailsForRedis> products = sap.getProducts();
//
//
//            Optional<Seller_Info> sellerInfo = sellerRepo.findById(data.getKey());
//            Seller_orders sellerOrders = Seller_orders.builder()
//                    .orderId(orderId)
//                    .orderStatus(orderStatus)
//                    .price(price)
//                    .username(username)
//                    .build();
//
//            sellerOrderProductInfoRepo.save(products.get(0));
//
//
//        }
//
//    }
}
