package com.example.Product.Service;

import ch.hsr.geohash.GeoHash;
import com.example.Product.DTO.RecommendedProductCard;
import com.example.Product.Model.ProductData.ProductStats;
import com.example.Product.Repository.ProductRepo;
import com.example.Product.Repository.ProductStatsRepo;
import com.netflix.discovery.converters.Auto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RecommendationsService {

    @Autowired
    ProductStatsRepo productStatsRepo;

    @Autowired
    ProductRepo productRepo;

    public List<RecommendedProductCard> bestProductNearBy(Double longitude,Double latitude){
        GeoHash geoHash = GeoHash.withCharacterPrecision(latitude,longitude,5);
        String hash = geoHash.toBase32();
        GeoHash[] neighbors = geoHash.getAdjacent();

        List<String> searchGeohashes = new ArrayList<>();
        searchGeohashes.add(neighbors.toString());

        for (GeoHash neighbor : neighbors) {
            searchGeohashes.add(neighbor.toBase32());
        }

        List<RecommendedProductCard> list = productRepo.findBestProduct(searchGeohashes);
        return list;
    }

}
