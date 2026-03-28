package com.example.Product.CronJobs;


import com.example.Product.Repository.SalesDataRepo;
import com.netflix.discovery.converters.Auto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class SalesStatusChange {

    @Autowired
    SalesDataRepo salesDataRepo;

    @Scheduled(cron = "0 0 0 * * *")
    public void expireSales() {
        LocalDateTime now = LocalDateTime.now();

        long updatedCount = salesDataRepo.expireSales(now);

        log.info("Expired {} sales offers", updatedCount);
    }
}
