package com.steganography;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * Main application class for the Steganography service.
 */
@SpringBootApplication
public class SteganographyApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(SteganographyApplication.class, args);
    }
}