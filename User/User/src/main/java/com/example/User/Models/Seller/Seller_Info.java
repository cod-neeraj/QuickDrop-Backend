package com.example.User.Models.Seller;

import com.example.User.Models.Customer.Customer_Cart;
import com.example.User.Models.Customer.PointSerializer;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.persistence.*;
import lombok.*;
import org.locationtech.jts.geom.Point;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity (name = "SellerInfo")
@Table(name="seller_info",schema = "seller")
public class Seller_Info {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String sellerId;
    private String phoneNumber;
    private String name;

    @ElementCollection
    private Set<String> imageUrls;
    private String thumbnailImage;

    private String bio;
    private String shopName;
    private String gstNumber;
    private String timings;


    private String street;
    private String city;
    private String state;
    private String country;
    private String zipCode;

    private Boolean status;

    @JsonSerialize(using = PointSerializer.class)
    @Column(columnDefinition = "geography(Point, 4326)")
    private Point location;

    @OneToMany(mappedBy = "seller", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonManagedReference
    private Set<Seller_product> productSet;

    @OneToMany(mappedBy = "seller", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonManagedReference
    private Set<Seller_daily_revenue> sellerDailyRevenues;

    @OneToMany(mappedBy = "seller", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonManagedReference
    private Set<Seller_daily_revenue_archive> sellerDailyRevenuesArchive;

    @OneToMany(mappedBy = "seller", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonManagedReference
    private Set<Seller_monthly_revenue> sellerMonthlyRevenues;

    @OneToMany(mappedBy = "seller", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonManagedReference
    private Set<Seller_yearly_revenue> sellerYearlyRevenues;



    private String geohash;

    @OneToOne(mappedBy = "seller", cascade = CascadeType.ALL)
    private SellerStats sellerStats;

}
