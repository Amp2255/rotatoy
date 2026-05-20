package com.amp.rotatoy.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mongodb.client.MongoClient;

import jakarta.annotation.PostConstruct;

@Component
public class MongoConnectionChecker {
    private static Logger logger = LoggerFactory.getLogger(MongoConnectionChecker.class);
    
    @Autowired
    MongoClient mongoClient;
    
    @PostConstruct
    public void checkConnection() {
        try {
            mongoClient.getDatabase("admin").runCommand(new org.bson.Document("ping", 1));
            logger.info("✅ MongoDB is connected.");
        } catch (Exception e) {
            logger.error("❌ MongoDB connection failed: {} " , e.getMessage());
        }
    }
}
