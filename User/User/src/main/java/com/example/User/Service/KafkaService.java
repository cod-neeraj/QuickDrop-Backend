package com.example.User.Service;

import ch.hsr.geohash.GeoHash;
import com.example.User.DTO.DiscountDTO;
import com.example.User.DTO.DiscountDTOKafka;
import com.example.User.DataToShow.SellerLocation;
import com.example.User.DataToShow.SellerLocationRepo;
import com.example.User.DataToShow.SellerMiniDetails;
import com.example.User.Models.Seller.Seller_Info;
import com.example.User.Repository.SellerRepositories.SellerRepo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Service
public class KafkaService {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private ReplyingKafkaTemplate<String,String,String> replyingKafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SellerRepo sellerRepo;

    public void sendProductEvent(Object product) {
        try {
            // Convert product object to JSON string
            String productJson = objectMapper.writeValueAsString(product);

            // Send JSON to Kafka topic
            kafkaTemplate.send("place-order", productJson);


            System.out.println("✅ Sent product event: " + productJson);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to convert product to JSON", e);

        }
    }

    public void addSales( DiscountDTO discountDTO,String phoneNumber) {
        try {
            SellerLocationRepo sl = sellerRepo.findLocation(phoneNumber).orElseThrow(()-> new RuntimeException("ff"));
            Double lat = sl.getLocation().getX();
            Double longs =  sl.getLocation().getY();
            DiscountDTOKafka discountDTOKafka = DiscountDTOKafka.builder()
                    .description(discountDTO.getDescription())
                    .longitude(longs)
                    .latitude(lat)
                    .heading(discountDTO.getHeading())
                    .imageUrl(discountDTO.getImageUrl())
                    .startDate(discountDTO.getStartDate())
                    .endDate(discountDTO.getEndDate())
                    .shopkeeperId(sl.getSellerId())
                    .build();

            String productJson = objectMapper.writeValueAsString(discountDTO);

            kafkaTemplate.send("add-sales", productJson);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to convert product to JSON", e);

        }
    }

    public String checkQuantity(String productId)
            throws ExecutionException, InterruptedException, TimeoutException {

        System.out.println("❤️ Sending quantity check request...");

        ProducerRecord<String, String> record =
                new ProducerRecord<>("check-product-quantity", productId);

        // Send and wait for reply
        RequestReplyFuture<String, String, String> future =
                replyingKafkaTemplate.sendAndReceive(record);

        ConsumerRecord<String, String> response =
                future.get(3, TimeUnit.SECONDS); // increased timeout slightly

        System.out.println("❤️ Received reply: " + response.value());
        return response.value();
    }

    public void addProduct(String productId){
        kafkaTemplate.send("add-product-again",productId);
    }

}
