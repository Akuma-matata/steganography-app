package com.steganography.model;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

/**
 * Entity class to store information about steganography operations.
 */
@Entity
@Table(name = "steganography_images")
public class SteganographyImage {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String fileName;
    
    private String originalImagePath;
    
    private String encodedImagePath;
    
    @Lob
    private String message;
    
    private LocalDateTime createdAt;
    
    // Default constructor
    public SteganographyImage() {
        this.createdAt = LocalDateTime.now();
    }
    
    // Constructor with fields
    public SteganographyImage(String fileName, String originalImagePath, String encodedImagePath, String message) {
        this.fileName = fileName;
        this.originalImagePath = originalImagePath;
        this.encodedImagePath = encodedImagePath;
        this.message = message;
        this.createdAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getOriginalImagePath() {
        return originalImagePath;
    }

    public void setOriginalImagePath(String originalImagePath) {
        this.originalImagePath = originalImagePath;
    }

    public String getEncodedImagePath() {
        return encodedImagePath;
    }

    public void setEncodedImagePath(String encodedImagePath) {
        this.encodedImagePath = encodedImagePath;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    @Override
    public String toString() {
        return "SteganographyImage [id=" + id + ", fileName=" + fileName + ", originalImagePath=" + originalImagePath
                + ", encodedImagePath=" + encodedImagePath + ", message=" + message + ", createdAt=" + createdAt + "]";
    }
}