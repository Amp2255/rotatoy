package com.amp.rotatoy.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.*;

@Component
public class MongoConnectionChecker {
    private static Logger logger = LoggerFactory.getLogger(MongoConnectionChecker.class);

    @Value("${spring.data.mongodb.host:localhost}")
    private String mongoHost;

    @Value("${spring.data.mongodb.port:27017}")
    private int mongoPort;

    @PostConstruct
    public void checkConnection() {
        try (MongoClient mongoClient =  MongoClients.create("mongodb://"+mongoHost+":"+ mongoPort)) {
            mongoClient.getDatabase("admin").runCommand(new org.bson.Document("ping", 1));
            logger.info("✅ MongoDB is connected.");
        } catch (Exception e) {
            logger.error("❌ MongoDB connection failed: {} " , e.getMessage());
        }
    }
}
