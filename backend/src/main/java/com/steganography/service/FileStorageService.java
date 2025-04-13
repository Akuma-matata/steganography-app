package com.steganography.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * Service for handling file storage operations.
 */
@Service
public class FileStorageService {
    
    private final Path fileStorageLocation;
    
    /**
     * Constructor that initializes the file storage location.
     * 
     * @param uploadDir The directory where files will be stored
     */
    public FileStorageService(@Value("${file.upload-dir:./uploads}") String uploadDir) {
        this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (IOException ex) {
            throw new RuntimeException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }
    
    /**
     * Stores a file and returns the file path.
     * 
     * @param file The file to store
     * @return The path where the file is stored
     */
    public String storeFile(MultipartFile file) {
        // Generate a unique file name to avoid conflicts
        String originalFileName = file.getOriginalFilename();
        String fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
        String uniqueFileName = UUID.randomUUID().toString() + fileExtension;
        
        try {
            // Check if the file is empty
            if (file.isEmpty()) {
                throw new RuntimeException("Failed to store empty file.");
            }
            
            // Resolve the file path
            Path targetLocation = this.fileStorageLocation.resolve(uniqueFileName);
            
            // Copy the file to the target location
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, targetLocation, StandardCopyOption.REPLACE_EXISTING);
            }
            
            return targetLocation.toString();
        } catch (IOException ex) {
            throw new RuntimeException("Failed to store file " + originalFileName, ex);
        }
    }
    
    /**
     * Stores an encoded image and returns the file path.
     * 
     * @param originalImagePath The path of the original image
     * @param imageData The image data to store
     * @return The path where the encoded image is stored
     */
    public String storeEncodedImage(String originalImagePath, byte[] imageData) {
        try {
            // Always use .png extension for encoded images to ensure lossless format
            String encodedFileName = "encoded_" + UUID.randomUUID().toString() + ".png";
            
            Path encodedImagePath = this.fileStorageLocation.resolve(encodedFileName);
            Files.write(encodedImagePath, imageData);
            
            return encodedImagePath.toString();
        } catch (IOException ex) {
            throw new RuntimeException("Failed to store encoded image", ex);
        }
    }
    
    /**
     * Gets the path to a file by its name.
     * 
     * @param fileName The name of the file
     * @return The path to the file
     */
    public Path getFilePath(String fileName) {
        return this.fileStorageLocation.resolve(fileName);
    }
}