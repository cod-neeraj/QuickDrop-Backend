package com.example.User.Service.Seller;

import com.example.User.DTO.SellerAnalytics;
import com.example.User.Models.OrdersData.ProductDetailsInfo;
import com.example.User.Repository.OrderRepositories.ProductInfoRepo;
import com.example.User.Repository.OrderRepositories.SellerInfo;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.aggregation.ArithmeticOperators;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class SellerAnalyticsService {

    @Autowired
    SellerInfo sellerInfo;

    @Autowired
    ProductInfoRepo productDetailsInfo;

    public SellerAnalytics getAnalytics(String phoneNumber){
        LocalDate now = LocalDate.now();
        LocalDate startOfThisMonth = now.withDayOfMonth(1);
        LocalDate startOfNextMonth = startOfThisMonth.plusMonths(1);
        LocalDate startOfLastMonth = startOfThisMonth.minusMonths(1);
        Object[] object = sellerInfo.getMonthlyStats(phoneNumber,startOfThisMonth,startOfNextMonth,startOfLastMonth);
        Object[] obj = (Object[]) object[0];
        Double thisMonthSale  = (Double)obj[0];
        Double lastMonthSale = (Double)obj[1];
        Long ordersThisMonth = (Long) obj[2];
        Long ordersLastMonth = (Long) obj[3];

        Object[] object1 = productDetailsInfo.getMonthlyProductStats(phoneNumber,startOfThisMonth,startOfNextMonth,startOfLastMonth);
        Object[] obj1 = (Object[]) object1[0];
        Long thisMonthProductOrder = (Long)obj1[0];
        Long lastMonthProductOrder = (Long)obj1[1];
        SellerAnalytics sellerAnalytics = SellerAnalytics.builder()
                .totalRevenue(thisMonthSale)
                .totalRevenueLastMonth(lastMonthSale)
                .orders(ordersThisMonth)
                .ordersLastMonth(ordersLastMonth)
                .productSell(thisMonthProductOrder)
                .productSellLastMonth(lastMonthProductOrder)
                .build();
        return sellerAnalytics;









    }

    public List<Map<String, Object>> getGraphData(
            String sellerId,
            LocalDate startDate,
            LocalDate endDate
    ) {

        long days = ChronoUnit.DAYS.between(startDate, endDate);

        String grouping = (days <= 30) ? "DAY" : "MONTH";

        List<Map<String, Object>> finalData = new ArrayList<>();

        if (grouping.equalsIgnoreCase("DAY")) {

            List<Object[]> list = sellerInfo.getDailySales(sellerId, startDate, endDate);

            Map<LocalDate,Double> dbData = new HashMap<>();

            for (Object[] row : list) {

                LocalDate date = ((java.sql.Date) row[0]).toLocalDate();
                Double sales = (Double) row[1];
                dbData.put(date, sales);
            }

            LocalDate current = startDate;

            while (!current.isAfter(endDate)) {

                Double sales = dbData.getOrDefault(current, 0.0);

                finalData.add(Map.of(
                        "label", current.toString(),
                        "sales", sales
                ));

                current = current.plusDays(1);
            }

        } else {

            List<Object[]> list = sellerInfo.getMonthlyGraphSales(sellerId, startDate, endDate);

            Map<YearMonth, BigDecimal> dbMap = new HashMap<>();

            for (Object[] row : list) {

                int month = ((Number) row[0]).intValue();
                int year = ((Number) row[1]).intValue();

                YearMonth ym = YearMonth.of(year, month);

                dbMap.put(ym, (BigDecimal) row[2]);
            }

            LocalDate current = startDate.withDayOfMonth(1);

            while (!current.isAfter(endDate)) {

                YearMonth ym = YearMonth.from(current);

                BigDecimal sales = dbMap.getOrDefault(ym, BigDecimal.ZERO);

                finalData.add(Map.of(
                        "label", ym.toString(),   // consistent key
                        "sales", sales
                ));

                current = current.plusMonths(1);
            }
        }

        return finalData;
    }

    public List<Map<String, Object>> getOrdersGraphData(
            String sellerId,
            LocalDate startDate,
            LocalDate endDate
    ) {

        long days = ChronoUnit.DAYS.between(startDate, endDate);

        String grouping = (days <= 30) ? "DAY" : "MONTH";

        List<Map<String, Object>> finalData = new ArrayList<>();

        if (grouping.equalsIgnoreCase("DAY")) {

            List<Object[]> list = sellerInfo.getOrderDaily(sellerId, startDate, endDate);

            Map<LocalDate,Long> dbData = new HashMap<>();

            for (Object[] row : list) {

                LocalDate date = ((java.sql.Date) row[0]).toLocalDate();
                Long orders = (Long) row[1];
                dbData.put(date,orders);
            }

            LocalDate current = startDate;

            while (!current.isAfter(endDate)) {

               Long orders = dbData.getOrDefault(current, 0l);

                finalData.add(Map.of(
                        "label", current.toString(),
                        "orders", orders
                ));

                current = current.plusDays(1);
            }

        } else {

            List<Object[]> list = sellerInfo.getOrderMonthlyGraph(sellerId, startDate, endDate);

            Map<YearMonth, Long> dbMap = new HashMap<>();

            for (Object[] row : list) {

                int month = ((Number) row[0]).intValue();
                int year = ((Number) row[1]).intValue();

                YearMonth ym = YearMonth.of(year, month);

                dbMap.put(ym, (Long) row[2]);
            }

            LocalDate current = startDate.withDayOfMonth(1);

            while (!current.isAfter(endDate)) {

                YearMonth ym = YearMonth.from(current);

                Long orders = dbMap.getOrDefault(ym, 0l);

                finalData.add(Map.of(
                        "label", ym.toString(),   // consistent key
                        "orders", orders
                ));

                current = current.plusMonths(1);
            }
        }

        return finalData;
    }
}
