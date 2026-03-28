package com.example.Product.Service;

import ch.hsr.geohash.GeoHash;
import com.example.Product.DTO.*;
import com.example.Product.DTO.ProductCreateDTO.ProductColorVarientDTO;
import com.example.Product.DTO.ProductCreateDTO.ProductDTO;
import com.example.Product.DTO.ProductCreateDTO.ProductVariantsDTO;

import com.example.Product.Exceptions.DataNotFoundException;
import com.example.Product.FeignInterface.SellerInterface;
import com.example.Product.Model.*;
import com.example.Product.Model.ProductData.*;
import com.example.Product.Repository.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Response;
import org.apache.hc.client5.http.auth.StandardAuthScheme;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.web.client.RestTemplate;


import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ProductService {
    @Autowired
    ProductRepo productRepo;

    @Autowired
    ProductVariantsRepo productVariantsRepo;

    @Autowired
    ProductStatsRepo productStatsRepo;



    @Autowired
    SalesDataRepo salesDataRepo;


    @Autowired
    SellerInterface sellerInterface;

    @Autowired
    ApplicationEventPublisher eventPublisher;

    @Autowired
    private RedisTemplate<String,ProductStatRedis> redisTemplate12;

    @Autowired
    private final RedisTemplate<String,String> redisTemplate;
    @Autowired
    private RedisTemplate<String,FilterFetchModel> redisTemplateObject;

    @Autowired
    ProductCategoryTypeRepo productCategoryTypeRepo;

    @Autowired
    private ProductColorVariantRepo productColorVariantRepo;

    @Autowired
    private KafkaService kafkaService;


    private static final long CACHE_TTL_MINUTES = 60;






    GeometryFactory geometryFactory = new GeometryFactory();

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public ProductService(RedisTemplate redisTemplate, RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public void addTypesAttributes(){

    }
    public String addProduct(ProductDTO productDTO,String phoneNumber){
        String key = "sellerLocation:" + phoneNumber;
        String value = redisTemplate.opsForValue().get(key);
        SellerLocation sl = null;
        try {
            sl = objectMapper.readValue(value, SellerLocation.class);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Error");
        }
        String productId = UUID.randomUUID().toString();
        Point point = geometryFactory.createPoint(new Coordinate(sl.getLongitude(), sl.getLatitude()));

        List<ProductVariantsDTO> list = productDTO.getProductVariantsDTOList();
        Double default_price = (double) Integer.MAX_VALUE;
        for(ProductVariantsDTO productVariantsDTO:list){
            if(default_price>productVariantsDTO.getPrice()){
                default_price = productVariantsDTO.getPrice();
            }
        }


        point.setSRID(4326);
        Product product = Product.builder()
                .productId(productId)
                .name(productDTO.getName())
                .description(productDTO.getDescription())
                .type(productDTO.getType())
                .location(point)
                .defaultPrice(default_price)
                .brand(productDTO.getBrand())
                .productCategory(ProductCategory.valueOf(productDTO.getProductCategory()))
                .createdAt(LocalDate.now())
                .updatedAt(LocalDate.now())
                .phoneNumber(phoneNumber)
                .geohash(point.toString())
                .shopkeeperId(sl.getSellerId())
                .productVariants(new HashSet<>())
                .defaultImageUrl(productDTO.getDefaultImageUrl())
                .productColorVariants(new HashSet<>())
                .build();

        ProductCategoryType productCategoryType1 = productCategoryTypeRepo.findByType(product.getType());
        ProductCategoryType productCategoryType3 = null;
        if(productCategoryType1 == null){

            productCategoryType3 = ProductCategoryType.builder()
                    .category(product.getProductCategory())
                    .type(product.getType())
                    .colors(new ArrayList<>())
                    .brands(new ArrayList<>())
                    .build();

        }else{
            productCategoryType3 = productCategoryType1;
        }
        if(productCategoryType3.getColors() == null){
            productCategoryType3.setColors(new ArrayList<>());
        }
        if(productCategoryType3.getBrands() == null){
            productCategoryType3.setBrands(new ArrayList<>());
        }

        productCategoryType3.getBrands().add(product.getBrand());
        for(ProductVariantsDTO productVariantsDTO:productDTO.getProductVariantsDTOList()){
            String id = UUID.randomUUID().toString();
            Product_Variants productVariants = Product_Variants.builder()
                    .variant_id(id)
                    .attribute(productVariantsDTO.getAttribute())
                    .price(productVariantsDTO.getPrice())
                    .quantity(productVariantsDTO.getQuantity())
                    .products(product)
                    .build();


            product.getProductVariants().add(productVariants);

        }

        for(ProductColorVariant productColorVariant :productDTO.getColors()){
            String id = UUID.randomUUID().toString();
            if(productCategoryType3.getColors() != null){
                productCategoryType3.getColors().add(productColorVariant.getColor());
            }
            ProductColorVariant productColorVariant1 = ProductColorVariant.builder()
                    .id(id)
                    .color(productColorVariant.getColor())
                    .urls(productColorVariant.getUrls())
                    .products(product)
                    .build();
            product.getProductColorVariants().add(productColorVariant1);
        }
        productRepo.save(product);
        productCategoryTypeRepo.save(productCategoryType3);

        double lon = sl.getLongitude();
        double lat = sl.getLatitude();

        String geohash = GeoHash.withCharacterPrecision(lat,lon,5).toBase32();
        ProductStats productStats = ProductStats.builder()
                .productId(productId)
                .score(0.00)
                .add_to_cart_count(0L)
                .add_to_wishlist_count(0L)
                .ratings(0.0)
                .totalVisits(0L)
                .totalOrders(0L)
                .geohashCode(geohash)
                .build();
        productStatsRepo.save(productStats);
        return productId;
    }

    public ProductSearchReturnModel search(
            SearchProduct searchProduct,
            Integer pageNumber,
            Double longitude,
            Double latitude
    ) {

        if (pageNumber == null || pageNumber < 0) {
            throw new IllegalArgumentException("Invalid page number");
        }

        if (longitude == null || latitude == null) {
            throw new IllegalArgumentException("Longitude and latitude are required");
        }


        String query = Optional.ofNullable(searchProduct.getQuery())
                .map(String::trim)
                .filter(q -> !q.isEmpty())
                .map(q -> Arrays.stream(q.toLowerCase().split("\\s+"))
                        .map(word -> word + ":*")
                        .collect(Collectors.joining(" | ")))
                .orElse("");

        Pageable pageable = PageRequest.of(pageNumber, 3);


        Page<ProductSearchAbleObject> page = productRepo.searchProducts(
                query,
                longitude,
                latitude,
                searchProduct.getMinPrice(),
                searchProduct.getMaxPrice(),
                searchProduct.getDistance(),
                pageable
        );

        if (page.isEmpty()) {
            return ProductSearchReturnModel.builder()
                    .content(page)
                    .attributes((Map<String, Set<String>>) List.of())
                    .colors(List.of())
                    .brands(List.of())
                    .build();
        }

        String type = page.getContent().getFirst().getType();
        String redisKey = "product:filters:" + type.toLowerCase();

        FilterFetchModel filters = redisTemplateObject.opsForValue().get(redisKey);

        if (filters == null) {
            Object[] row = productCategoryTypeRepo.findByTypeFilters(type);

            if (row == null || row.length == 0) {
                throw new IllegalStateException(
                        "Filter configuration missing for product type: " + type
                );
            }

            Object[] data = (Object[]) row[0];
            filters = new FilterFetchModel(data[0], data[1], data[2]);

            redisTemplateObject.opsForValue()
                    .set(redisKey, filters, 6, TimeUnit.HOURS);
        }


        return ProductSearchReturnModel.builder()
                .content(page)
                .attributes(filters.getAttributes())
                .colors(filters.getColors())
                .brands(filters.getBrands())
                .build();
    }


    public boolean isBrand(String token) {
        return Boolean.TRUE.equals(
                redisTemplate.opsForSet().isMember("products:brand", token)
        );
    }

    public boolean isColor(String token) {
        return Boolean.TRUE.equals(
                redisTemplate.opsForSet().isMember("products:colors", token)
        );
    }

    public List<ProductCategory> getAllCategories() throws JsonProcessingException {
        ObjectMapper obj = new ObjectMapper();
        String key = "categories";
        String value = redisTemplate.opsForValue().get(key);
        if(value != null){
            List<ProductCategory> newList = obj.readValue(value, new TypeReference<List<ProductCategory>>() {});
            return newList;

        }
        List<ProductCategory> list = productCategoryTypeRepo.findAllCategories();
        String val = obj.writeValueAsString(list);
        redisTemplate.opsForValue().set(key,val, Duration.ofHours(1));
        return list;


    }

    public List<Map<String,Set<String>>> fetchAttributes(ProductCategory category,String type){

        return productCategoryTypeRepo.findByCategoryAndType(category,type);

    }

    public List<String> fetchTypes(ProductCategory productCategory){
       return productCategoryTypeRepo.findByCategory(productCategory);
    }


public List<SellerProductList> getAllProductsForSeller(String phoneNumber,Integer page,Integer size){
        Pageable pageable = PageRequest.of(page, size);
        Page<SellerProductList> list = productRepo.findAllProductByPhoneNumber(phoneNumber,pageable);
        return list.getContent();
}

    public ProductPageDetails fetchProduct(String productId) throws JsonProcessingException {
        final String productKey = "product:" + productId;
        final String statKey = "product:stats:" + productId;

        ObjectMapper objectMapper = new ObjectMapper();
        ValueOperations<String, String> productOps = redisTemplate.opsForValue();
        String cachedJson = productOps.get(productKey);
        if (cachedJson != null && !cachedJson.isEmpty()) {
            try {
                return objectMapper.readValue(cachedJson, ProductPageDetails.class);
            } catch (JsonProcessingException e) {
                log.warn("Failed to parse cached product JSON for key {}. Recomputing.", productKey, e);
            }
        }

        // Not in cache — load from DB
        Product product = productRepo.findProduct(productId);
        if (product == null) {
            return null;
        }

        // Load color variants (guard against null)
        Set<ProductColorVariant> colorVariants = product.getProductColorVariants();
        if (colorVariants == null) colorVariants = Collections.emptySet();

        Set<ProductColorVarientDTO> colorDtoSet = new HashSet<>();
        for (ProductColorVariant pv : colorVariants) {
            if (pv == null) continue;
            ProductColorVarientDTO dto = ProductColorVarientDTO.builder()
                    .color(pv.getColor())
                    .urls(pv.getUrls())
                    .build();

            colorDtoSet.add(dto);
        }

        // Product variants (guard)
        Set<Product_Variants> variants = product.getProductVariants();
        if (variants == null) variants = Collections.emptySet();


        SellerMiniDetails sellerMiniDetails;
        try {
            SellerMiniDetails resp = sellerInterface.getSeller(product.getShopkeeperId());
            if (resp == null) {
                throw new IllegalStateException("Seller service returned null for shopkeeperId=" + product.getShopkeeperId());
            }
            sellerMiniDetails = resp;
        } catch (Exception ex) {
            log.error("Failed to fetch seller info for shopkeeperId={}", product.getShopkeeperId(), ex);
            throw new RuntimeException("Failed to fetch seller data", ex);
        }

        ProductPageDetails productPageDetails = ProductPageDetails.builder()
                .id(product.getProductId())
                .shopkeeperId(product.getShopkeeperId())
                .name(product.getName())
                .description(product.getDescription())
                .type(product.getType())
                .defaultImage(product.getDefaultImageUrl())
                .brand(product.getBrand())
                .sellerMiniDetails(sellerMiniDetails)
                .productCategory(product.getProductCategory())
                .colors(new HashSet<>(colorDtoSet))
                .productVariants(new HashSet<>(variants))
                .build();

        // Cache the product JSON (optionally with TTL)
        String jsonValue = objectMapper.writeValueAsString(productPageDetails);
        productOps.set(productKey, jsonValue); // or productOps.set(productKey, jsonValue, 1, TimeUnit.HOURS);

        // Update stats in a separate template (use correct statKey)
        try {
            ValueOperations<String, ProductStatRedis> statOps = redisTemplate12.opsForValue();
            ProductStatRedis stats = statOps.get(statKey);
            if (stats == null) {
                stats = ProductStatRedis.builder()
                        .user_clicks(1L)
                        .cart_count(0L)
                        .wishlist_count(0L)
                        .order(0L)
                        .build();
            } else {
                stats.setUser_clicks(stats.getUser_clicks() + 1);
            }
            statOps.set(statKey, stats); // optionally with TTL
        } catch (Exception ex) {
            // Logging only — stats updates should not block normal flow
            log.warn("Failed to update product stats for {}, continuing. Error: {}", productId, ex.toString());
        }

        return productPageDetails;
    }

public int updateQuantity(String productId,Integer quantity){
        int c =0;
        if(quantity <0){
            c=productRepo.subtractIfAvailable(productId,quantity*-1);
        }else{
           c= productRepo.addProductAgain(productId);
        }
        return c;

}

public Long getProductCount(String phoneNumber){
        return productRepo.findProductCount(phoneNumber);
}
    public Page<RecommendationDataDTO> getRecommendations(Double longitude,Double latitude){
//        String geohash = GeoHash.withCharacterPrecision(latitude,longitude, 5).toBase32();
        String geohash = "tttdx";
        Page<RecommendationDataDTO> list = productStatsRepo.findTopProductsByGeohashCode(geohash,PageRequest.of(0,10));
        return list;


    }

//    public boolean discountAdd(DiscountDataDTO discountDataDTO){
//
//        SalesData salesData = SalesData.builder()
//                .shopkeeperId(discountDataDTO.getShopkeeperId())
//                .imageUrl(discountDataDTO.getImageUrl())
//                .shopName(discountDataDTO.getShopName())
//                .shopAddress(discountDataDTO.getShopAddress())
//                .heading(discountDataDTO.getHeading())
//                .description(discountDataDTO.getDescription())
//                .timeLimit(discountDataDTO.getTimeLimit())
//                .geoHashCode(discountDataDTO.getGeoHashCode())
//                .build();
//
//        salesDataRepo.save(salesData);
//        return true;
//
//    }

    public List<SalesDataToFrontend> findSales(Double longitude, Double latitude) {

        if (longitude == null || latitude == null) {
            throw new IllegalArgumentException("Longitude and latitude must not be null");
        }

        if (longitude < -180 || longitude > 180 || latitude < -90 || latitude > 90) {
            throw new IllegalArgumentException("Invalid longitude or latitude value");
        }

        GeoHash geoHash = GeoHash.withCharacterPrecision(longitude, latitude, 5);

        List<String> neighbourGeohashes = new ArrayList<>();
        neighbourGeohashes.add(geoHash.toBase32());

        for (GeoHash neighbor : geoHash.getAdjacent()) {
            neighbourGeohashes.add(neighbor.toBase32());
        }

        Set<String> offerIds = new HashSet<>();

        for (String hash : neighbourGeohashes) {
            String geoKey = "geo:" + hash;

            Set<String> ids = redisTemplate.opsForSet().members(geoKey);
            if (ids != null) {
                offerIds.addAll(ids);
            }
        }

        List<SalesDataToFrontend> result = new ArrayList<>();

        for (String offerId : offerIds) {

            Map<Object, Object> data = redisTemplate.opsForHash().entries(offerId);

            if (data == null || data.isEmpty()) {
                continue;
            }

            result.add(SalesDataToFrontend.builder()
                    .imageUrl((String) data.get("imageUrl"))
                    .startDate(LocalDate.parse((String) data.get("startDate")))
                    .endDate(LocalDate.parse((String)data.get("endDate")))
                    .shopAddress((String) data.get("shopAddress"))
                    .description((String) data.get("description"))
                    .shopName((String) data.get("shopName"))
                    .heading((String) data.get("title"))
                    .build());
        }
        if (result.isEmpty()) {

            List<SalesData> salesList =
                    salesDataRepo.findByAllGeoHashes(neighbourGeohashes, Status.ACTIVE);

            if (salesList.isEmpty()) {
                throw new DataNotFoundException("No sales found near your location");
            }

            return salesList.stream()
                    .map(sd -> SalesDataToFrontend.builder()
                            .imageUrl(sd.getImageUrl())
                            .startDate(sd.getStartDate())
                            .endDate(sd.getEndDate())
                            .shopAddress(sd.getShopAddress())
                            .description(sd.getDescription())
                            .shopName(sd.getShopName())
                            .heading(sd.getHeading())
                            .build())
                    .toList();
        }

        return result;
    }



}
