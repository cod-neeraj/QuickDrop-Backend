package com.example.Delivery.Service;

public class GeoCandidate {
    private String deliveryBoyId;
    private double distance; // km

    public GeoCandidate(String deliveryBoyId, double distance) {
        this.deliveryBoyId = deliveryBoyId;
        this.distance = distance;
    }

    public String getDeliveryBoyId() { return deliveryBoyId; }
    public double getDistance() { return distance; }
}
