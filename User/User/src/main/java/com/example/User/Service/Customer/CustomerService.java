package com.example.User.Service.Customer;

import com.example.User.DTO.AddProductToCart;
import com.example.User.DTO.ProductStatRedis;
import com.example.User.DTO.Payment;
import com.example.User.DTO.ShowOrdersInDashBoard;
import com.example.User.DataToShow.*;
import com.example.User.Exceptions.UserNotFoundException;
import com.example.User.FeignInterface.OrderPlacingInterface;
import com.example.User.Models.Customer.*;
import com.example.User.Models.OrdersData.MainOrder;
import com.example.User.Models.OrdersData.ProductDetailsInfo;
import com.example.User.Models.OrdersData.SellerInfo;
import com.example.User.Models.PaymentMode;
import com.example.User.Models.PaymentStatus;
import com.example.User.Models.Seller.OrderStatus;
import com.example.User.Models.Seller.SellerStatus;
import com.example.User.Models.User;
import com.example.User.Repository.CustomerAddressRepository;
import com.example.User.Repository.CustomerRepositorys.*;
import com.example.User.Repository.OrderRepositories.MainOrderRepo;
import com.example.User.Repository.OrderRepositories.ProductInfoRepo;
import com.example.User.RequestBodies.Frontend.CreateCustomerAddress;
import  com.example.User.RequestBodies.Frontend.PersonalInfo;
import com.example.User.Repository.UserRepository;
import com.example.User.Service.KafkaService;
import com.example.User.Service.RecommendationService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.razorpay.Card;
import com.razorpay.RazorpayException;
import jakarta.transaction.Transactional;
import org.locationtech.jts.geom.GeometryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import javax.smartcardio.CardPermission;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class CustomerService {

    @Autowired
    UserRepository userRepository;

  @Autowired
    MainOrderRepo mainOrderRepo;

  @Autowired
    ProductInfoRepo productInfoRepo;

  @Autowired
    com.example.User.Repository.OrderRepositories.SellerInfo sellerInfoRepo;

    @Autowired
    SearchHistoryRepo searchHistoryRepo;

    @Autowired
    CustomerAddressRepository customerAddressRepository;

    @Autowired
    private RedisTemplate<String,String> redisTemplateString;

    @Autowired
    private RedisTemplate<String,ProductStatRedis> redisTemplate12;
//
//    @Autowired
//    private RedisTemplate<String,OrderDetailsTosend> redisTemplateOrderSave;

    @Autowired
    CustomerRepo customerRepo;

    @Autowired
    CustomerCartRepo customerCartRepo;

    @Autowired
    CustomerWishlIstRepo customerWishlIstRepo;

@Autowired
    RedisTemplate<String, Integer> redisTemplateInteger;

//    @Autowired
//    CustomerOrderListRepo customerOrderListRepo;

    @Autowired
    RecommendationService recommendationService;



//    @Autowired
//    private SellerUserOrderRepo sellerUserOrderRepo;


    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private KafkaService kafkaService;

//    @Autowired
//    private ProductOrderRepo productOrderRepo;

    @Autowired
    private OrderPlacingInterface orderPlacingInterface;

    @Autowired
    Customer_visited_product_Repo customerVisitedProductRepo;
//


    GeometryFactory geometryFactory = new GeometryFactory();

//    @PostConstruct
//    public void testRedisConnection() {
//        try {
//            String pong =stringRedisTemplate.getConnectionFactory().getConnection().ping();
//            System.out.println("Redis ping: " + pong); // Should print "PONG"
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    public PersonalInfo getPersonalInfo(String phoneNumber) throws JsonProcessingException {
        String redisKey = "user:personalInfo:" + phoneNumber;

        String cachedData = redisTemplateString.opsForValue().get(redisKey);
        if (cachedData != null) {
            return objectMapper.readValue(cachedData, PersonalInfo.class);
        }

        PersonalInfo personalInfo = userRepository.findByPersonalInfo(phoneNumber);

        if (personalInfo == null) {
            throw new UserNotFoundException("User with phone number " + phoneNumber + " not found");
        }

        String json = objectMapper.writeValueAsString(personalInfo);
       redisTemplateString.opsForValue().set(redisKey, json, 10, TimeUnit.MINUTES);

        return personalInfo;
    }
    public Boolean updatePersonalInfo(PersonalInfo personalInfo,String phoneNumber){

        User user = userRepository.findByPhoneNumber(phoneNumber).orElseThrow(()->new RuntimeException("User Not Found"));
        user.setName(personalInfo.getName());
        user.setDob(personalInfo.getDob());
        user.setPhoneNumber(personalInfo.getPhoneNumber());
        user.setEmailId(personalInfo.getEmailId());
        user.setGender(personalInfo.getGender());

        userRepository.save(user);
        return true;
    }
    public Set<Customer_Address> getAddress(String phoneNumber){
        Customers customers = customerRepo.findByPhoneNumber(phoneNumber).orElseThrow(()->new RuntimeException("hh"));
        return  customers.getAddresses();
    }
    public Boolean createAddress(CreateCustomerAddress createCustomerAddress,String phoneNumber){
        User user = userRepository.findByPhoneNumber(phoneNumber).orElseThrow(()->new RuntimeException("user not found"));
        Customers customers = customerRepo.findByPhoneNumber(phoneNumber).orElseThrow(()-> new RuntimeException("jj"));
        Customer_Address customerAddress = Customer_Address.builder()
                .id(UUID.randomUUID().toString())
                .street(createCustomerAddress.getStreet())
                .city(createCustomerAddress.getCity())
                .state(createCustomerAddress.getState())
                .country(createCustomerAddress.getCountry())
                .postalCode(createCustomerAddress.getZipCode())
                .addressType(createCustomerAddress.getIsDefault())
                .phoneNumber(phoneNumber)
                .customer(customers)

                .build();

        customerAddressRepository.save(customerAddress);
        return true;

    }

    @Transactional
    public Boolean deleteAddress(String id){
        Customer_Address customerAddress = customerAddressRepository.findById(id).orElseThrow(()-> new RuntimeException("no address Found"));
        customerAddress.getCustomer().getAddresses().remove(customerAddress);
        return true;

    }
    public Boolean updateAddress(CreateCustomerAddress createCustomerAddress,String id){
        Customer_Address customerAddress = customerAddressRepository.findById(id).orElseThrow(()-> new RuntimeException("no address Found"));
          customerAddress.setStreet(createCustomerAddress.getStreet());
          customerAddress.setCity(createCustomerAddress.getCity());
          customerAddress.setState(createCustomerAddress.getState());
          customerAddress.setCountry(createCustomerAddress.getCountry());
          customerAddress.setPostalCode(createCustomerAddress.getZipCode());
          customerAddress.setPhoneNumber(createCustomerAddress.getPhoneNumber());
          customerAddress.setAddressType(createCustomerAddress.getIsDefault());

          customerAddressRepository.save(customerAddress);
          return true;

    }
//    public Boolean addProductToCart(String productId,String phoneNumber){
//        Customers customers = customerRepo.findByPhoneNumber(phoneNumber).orElseThrow(()-> new RuntimeException("user not found"));
//        boolean alreadyExists = customers.getCart().stream()
//                .anyMatch(w -> w.getProductId().equals(productId));
//
//        if (alreadyExists) {
//            return false;
//        }
//
//        Customer_Cart customerCart = Customer_Cart.builder()
//                .productId(productId)
//                .customer(customers)
//                .build();
//        if(customers.getCart().contains(customerCart)){
//            return false;
//        }
//
//        customerCartRepo.save(customerCart);
//        customers.getCart().add(customerCart);
//        customerRepo.save(customers);
//
//        Optional<Customer_Visited_product> visitedOpt = customerVisitedProductRepo
//                .findByCustomerIdAndProductId(customers.getUserId(), productId);
//
//        Customer_Visited_product visited=null;
//        if (visitedOpt.isEmpty()) {
//            visited = Customer_Visited_product.builder()
//                    .customerId(customers.getUserId())
//                    .productId(productId)
//                    .inWishList(false)
//                    .inCart(true)
//                    .score(2)
//                    .build();
//        } else {
//           visited = visitedOpt.get();
//            visited.setInCart(true);
//            visited.setScore(visited.getScore()+2);
//        }
//        visited.setScore(visited.getScore()+recommendationService.recommendationScore(visited,32444));
//
//        customerVisitedProductRepo.save(visited);
//
//        String key = "product:stats:" + productId;
//
//        // 1️⃣ Check if key exists
//        ProductStatRedis stats = redisTemplate12.opsForValue().get(key);
//
//        if (stats == null) {
//            stats = ProductStatRedis.builder()
//                    .user_clicks(0L)
//                    .cart_count(1L)
//                    .wishlist_count(0L)
//                    .order(0L)
//                    .build();
//        } else {
//            stats.setCart_count(stats.getCart_count() + 1);
//        }
//
//        // 2️⃣ Save back to Redis
//        redisTemplate12.opsForValue().set(key, stats);
//
//        return true;
//
//    }
    public Boolean deleteProductFromCart(Long id,String phoneNumber){
        Customers customers = customerRepo.findByPhoneNumber(phoneNumber).orElseThrow(()-> new RuntimeException("user not found"));
        Customer_Cart customerCart = customerCartRepo.findById(id).orElseThrow(()->new RuntimeException("not found"));
        customerCartRepo.delete(customerCart);
        customers.getCart().remove(customerCart);
        customerRepo.save(customers);
        return true;

    }
    public Boolean addProductToWishList(WishlistAddData wishlistAddData, String phoneNumber) {

        Customers customer = customerRepo.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new RuntimeException("User not found"));

        boolean alreadyExists = customer.getWishLists().stream()
                .anyMatch(w -> w.getProductId().equals(wishlistAddData.getProductId()));

        if (alreadyExists) {
            return false;
        }

        Customer_WishList customerWishList = Customer_WishList.builder()
                .productId(wishlistAddData.getProductId())
                .name(wishlistAddData.getName())
                .type(wishlistAddData.getType())
                .brand(wishlistAddData.getBrand())
                .date(LocalDate.now())
                .defaultImage(wishlistAddData.getDefaultImage())
                .customer(customer)
                .build();

        customerWishlIstRepo.save(customerWishList);
        customer.getWishLists().add(customerWishList);
        customerRepo.save(customer);

        Optional<Customer_Visited_product> visitedOpt = customerVisitedProductRepo
                .findByCustomerIdAndProductId(customer.getUserId(), wishlistAddData.getProductId());


        Customer_Visited_product visited = null;
        if (visitedOpt.isEmpty()) {
             visited = Customer_Visited_product.builder()
                    .customerId(customer.getUserId())
                    .productId(wishlistAddData.getProductId())
                    .inWishList(true)
                    .inCart(false)
                    .score(1)
                    .build();
        } else {
           visited = visitedOpt.get();
            visited.setInWishList(true);
            visited.setScore(visited.getScore()+1);
        }

        visited.setScore(visited.getScore()+recommendationService.recommendationScore(visited,32444));

        customerVisitedProductRepo.save(visited);

        String key = "product:stats:" + wishlistAddData.getProductId();

        // 1️⃣ Check if key exists
        ProductStatRedis stats = redisTemplate12.opsForValue().get(key);

        if (stats == null) {
            stats = ProductStatRedis.builder()
                    .user_clicks(0L)
                    .cart_count(0L)
                    .wishlist_count(1L)
                    .order(0L)
                    .build();
        } else {
            stats.setWishlist_count(stats.getWishlist_count() + 1);
        }

        // 2️⃣ Save back to Redis
        redisTemplate12.opsForValue().set(key, stats);

        return true;
    }

    public List<WishlistAddData> getWishlistData(String phoneNumber){
        List<WishlistAddData> list = customerWishlIstRepo.findByPhoneNumber(phoneNumber);
        return list;
    }
    public Boolean deleteWishListFromCart(Long id,String phoneNumber){
        Customers customers = customerRepo.findByPhoneNumber(phoneNumber).orElseThrow(()-> new RuntimeException("user not found"));
        Customer_WishList customerWishList = customerWishlIstRepo.findById(id).orElseThrow(()->new RuntimeException("not found"));
        customerWishlIstRepo.delete(customerWishList);
        customers.getWishLists().remove(customerWishList);
        customerRepo.save(customers);
        return true;

    }
    public UserDashboard getDashBoaredData(String phoneNumber) {
        Optional<Customers> user = customerRepo.findByPhoneNumber(phoneNumber);

        Pageable top3 = PageRequest.of(0, 3);

        List<ShowOrdersInDashBoard> latestOrders =
                mainOrderRepo.findUserOrdersSorted(phoneNumber, top3);

        return UserDashboard.builder()
                .name(user.get().getName())
                .phoneNumber(user.get().getPhoneNumber())
                .recentOrder(latestOrders)
                .build();
    }
    public List<Search_History> searchHistory(String phoneNumber) {
        Customers customer = customerRepo.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        String userId = customer.getUserId();
        return searchHistoryRepo.findLatestFiveSearches(userId);
    }

    public Payment placeOrder(String phoneNumber, OrderDetailsInRedis orderDetailsInRedis) throws JsonProcessingException, RazorpayException {
        ObjectMapper mapper = new ObjectMapper();
        Boolean deliveryBoy = true;
        String address = null;
        if(orderDetailsInRedis.getDeliveryAddressId() == null){
            deliveryBoy = false;

        }else {
            Optional<Customer_Address> customerAddress = customerAddressRepository.findById(orderDetailsInRedis.getDeliveryAddressId());
            if (customerAddress.isEmpty()) {
                return null;
            }
             address = customerAddress.get().getStreet()+","+customerAddress.get().getCity()+","+customerAddress.get().getState()+","+customerAddress.get().getCountry();
        }
        String name = customerRepo.findByPhone(phoneNumber);
        String order_id = UUID.randomUUID().toString();
        if(!deliveryBoy){
            address="ownPickUp";

        }


        MainOrder mainOrder = MainOrder.builder()
                .order_id(order_id)
                .orderStatus(OrderStatus.PROCESSING)
                .paymentMethod(PaymentMode.UPI)
                .paymentStatus(PaymentStatus.PENDING)
                .userAddress(address)
                .userId(orderDetailsInRedis.getUserId())
                .username(orderDetailsInRedis.getUserName())
                .totalAmount(orderDetailsInRedis.getTotalAmount())
                .orderDate(LocalDate.now())
                .build();

        List<SellerInfo> sellerInfos = new ArrayList<>();
        for (Map.Entry<String, SellerAndProduct> entry : orderDetailsInRedis.getSellers().entrySet()) {
            SellerAndProduct sellerAndProduct = entry.getValue();
            SellerInfo sellerInfo = SellerInfo.builder()
                    .sellerId(sellerAndProduct.getSeller().getSellerId())
                    .name(sellerAndProduct.getSeller().getName())
                    .shopName(sellerAndProduct.getSeller().getShopName())
                    .street(sellerAndProduct.getSeller().getStreet())
                    .city(sellerAndProduct.getSeller().getCity())
                    .state(sellerAndProduct.getSeller().getState())
                    .orderStatus(SellerStatus.PROCESSING)
                    .mainOrder(mainOrder)
                    .build();

            List<ProductDetailsInfo> productInfos = new ArrayList<>();
            List<ProductDetailsForRedis> list = sellerAndProduct.getProducts();
            for(ProductDetailsForRedis productDetailsForRedis:list) {
                ProductDetailsInfo productDetailsInfo = ProductDetailsInfo.builder()
                        .productId(productDetailsForRedis.getProductId())
                        .name(productDetailsForRedis.getName())
                        .type(productDetailsForRedis.getType())
                        .brand(productDetailsForRedis.getBrand())
                        .color(productDetailsForRedis.getColor())
                        .attribute(productDetailsForRedis.getAttribute())
                        .price(productDetailsForRedis.getPrice())
                        .quantity(productDetailsForRedis.getQuantity())
                        .sellerInfo(sellerInfo)
                        .build();
                productInfos.add(productDetailsInfo);

            }

            sellerInfo.setProductDetailsInfo(productInfos);
            sellerInfos.add(sellerInfo);
        }
        mainOrder.setSellerInfo(sellerInfos);

        ResponseEntity<Payment> response = orderPlacingInterface.makePayment(order_id, orderDetailsInRedis.getTotalAmount(),orderDetailsInRedis.getUserName());
        if(response.getStatusCode().is2xxSuccessful()){
            String paymentId = response.getBody().getRazorpayId();
            mainOrder.setPayment_id(paymentId);
            mainOrderRepo.save(mainOrder);
            return response.getBody();
        }

        return null;


    }

    public void getRecommendedProduct(String phoneNumber){
      Customers customer = customerRepo.findByPhoneNumber(phoneNumber).orElseThrow(()-> new RuntimeException("maa chuda"));



    }

    public List<ShowOrdersInDashBoard> getOrderList(String phoneNumber){
       List<ShowOrdersInDashBoard> list  = mainOrderRepo.findUserOrdersData(phoneNumber);
       return list;

    }
    public MainOrder getOrderDetails(String orderId){
        MainOrder mainOrder = mainOrderRepo.findByOrderId(orderId);
        return mainOrder;
    }

    public List<MainOrder> getOrderListSeller(String phoneNumber){
        List<MainOrder> list  = mainOrderRepo.findOrdersBySellerId(phoneNumber);
        return list;

    }

    @Transactional
    public void addProductToCart(AddProductToCart dto, String phoneNumber) {

//        if (dto == null || dto.getQuantity() == null || dto.getQuantity() <= 0) {
//            throw new IllegalArgumentException("Invalid cart data");
//        }

        Customers user = customerRepo.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (user.getCart() == null) {
            user.setCart(new HashSet<>());
        }

        for (Customer_Cart cartItem : user.getCart()) {

            if (cartItem.getSellerId().equals(dto.getSellerId())) {

                List<CartProductDetails> products = cartItem.getProductDetails();
                CartProductDetails incoming = dto.getCartProductDetailsList();

                boolean merged = false;

                for (CartProductDetails existing : products) {
                    if (existing.equals(incoming)) {
                        existing.setQuantity(existing.getQuantity() + incoming.getQuantity());
                        merged = true;
                        break;
                    }
                }

                if (!merged) {
                    products.add(incoming);
                }

                updateVisitedProduct(user.getUserId(), dto.getCartProductDetailsList().getProductId());
                customerRepo.save(user);
                return;
            }
        }

        Customer_Cart newSellerCart = Customer_Cart.builder()
                .sellerId(dto.getSellerId())
                .name(dto.getName())
                .shopName(dto.getShopName())
                .street(dto.getStreet())
                .city(dto.getCity())
                .state(dto.getState())
                .date(LocalDate.now())
                .productDetails(new ArrayList<>())
                .customer(user)
                .build();

        newSellerCart.getProductDetails().add(dto.getCartProductDetailsList());
        user.getCart().add(newSellerCart);

        updateVisitedProduct(user.getUserId(), dto.getCartProductDetailsList().getProductId());
        customerRepo.save(user);
    }
    private void updateVisitedProduct(String userId, String productId) {

        Customer_Visited_product visited =
                customerVisitedProductRepo
                        .findByCustomerIdAndProductId(userId, productId)
                        .orElseGet(() -> Customer_Visited_product.builder()
                                .customerId(userId)
                                .productId(productId)
                                .inWishList(false)
                                .inCart(true)
                                .score(2)
                                .build());

        visited.setInCart(true);
        visited.setScore(
                visited.getScore() +
                        recommendationService.recommendationScore(visited, 32444)
        );

        customerVisitedProductRepo.save(visited);
    }

    public Set<Customer_Cart> getCartProductList(String phoneNumber){
        Customers customers = customerRepo.findByPhoneNumber(phoneNumber)
                .orElseThrow(()-> new UserNotFoundException("User Not found"));
        Set<Customer_Cart> customerCarts = customers.getCart();

        if (customerCarts.isEmpty()){
            return new HashSet<>();
        }
        return customerCarts;

    }




//    @KafkaListener(topics = "order-success", groupId = "order-consume")
//    public void settleOrders(String values) throws JsonProcessingException {
//
//        System.out.println("❤️❤️❤️❤️");
//        ObjectMapper obj = new ObjectMapper();
//
//        String[] parts = values.split(",");
//        String paymentId = parts[0];
//        String orderId = parts[1];
//        String userId = parts[2];
//
//        String key = "order:" + orderId + ":" + userId + ":";
//        String value = redisTemplateString.opsForValue().get(key);
//
//        OrderDetailsTosend orderDetailsTosend =
//                obj.readValue(value, OrderDetailsTosend.class);
//
//        // create main order root
//        Customer_OrderList order = Customer_OrderList.builder()
//                .orderId(orderId)
//                .orderDate(LocalDate.now())
//                .orderStatus("PENDING")
//                .sellerOrders(new ArrayList<>())
//                .build();
//
//        // loop sellers
//        for (Map.Entry<String, SellerAndProduct> entry :
//                orderDetailsTosend.getOrderDetailsInRedis().getSellers().entrySet()) {
//
//            SellerAndProduct sap = entry.getValue();
//            SellerMiniDetails seller = sap.getSeller();
//            List<ProductDetailsForRedis> products = sap.getProducts();
//
//            // 1) create SellerOrder (not saved)
//            SellerOrder sellerOrder = SellerOrder.builder()
//                    .sellerId(seller.getSellerId())
//                    .name(seller.getName())
//                    .shopName(seller.getShopName())
//                    .street(seller.getStreet())
//                    .city(seller.getCity())
//                    .state(seller.getState())
//                    .productOrders(new ArrayList<>())
//                    .build();
//
//            // IMPORTANT: set parent at creation time
//            sellerOrder.setCustomerOrderList(order);
//
//            // 2) create ProductOrder children and set reverse FK
//            List<ProductOrder> productOrders = products.stream()
//                    .map(p -> {
//                        ProductOrder po = ProductOrder.builder()
//                                .productId(p.getProductId())
//                                .attribute(p.getAttribute())
//                                .price(p.getPrice())
//                                .quantity(p.getQuantity())
//                                .color(p.getColor())
//                                .build();
//
//                        po.setSellerOrder(sellerOrder);  // CRITICAL
//                        return po;
//                    })
//                    .collect(Collectors.toList());
//
//            // 3) attach children to parent
//            sellerOrder.setProductOrders(productOrders);
//
//            // 4) attach sellerOrder to main order
//            order.getSellerOrders().add(sellerOrder);
//        }
//
//        // 5) SAVE ONLY THE ROOT — cascade will handle ALL inserts
//        customerOrderListRepo.save(order);
//
//        //order save for seller
//    }


}
















