package com.steganography.service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.steganography.model.SteganographyImage;
import com.steganography.repository.SteganographyImageRepository;
import com.steganography.util.SteganographyUtil;

/**
 * Service for steganography operations.
 */
@Service
public class SteganographyService {
    
    @Autowired
    private SteganographyImageRepository steganographyImageRepository;
    
    @Autowired
    private FileStorageService fileStorageService;
    
    /**
     * Encodes a message into an image.
     * 
     * @param file The image file
     * @param message The message to encode
     * @return The steganography image entity with information about the operation
     * @throws IOException If an I/O error occurs
     */
    public SteganographyImage encodeMessage(MultipartFile file, String message) throws IOException {
        // Get file extension
        String fileExtension = getFileExtension(file.getOriginalFilename());
        String originalFileName = file.getOriginalFilename();
        
        // Determine if we need to convert the image format
        boolean isJpeg = "jpg".equalsIgnoreCase(fileExtension) || "jpeg".equalsIgnoreCase(fileExtension);
        
        // Always use PNG as output format - this ensures lossless storage of the steganography data
        String outputFormat = "png";
        
        // Store the original image - this is just for reference
        String originalImagePath = fileStorageService.storeFile(file);
        
        // Read the image
        BufferedImage originalImage = ImageIO.read(new File(originalImagePath));
        
        // Log conversion if needed
        if (isJpeg) {
            System.out.println("Converting JPEG to PNG for steganography");
        }
        
        // Encode the message
        BufferedImage encodedImage = SteganographyUtil.encodeMessage(originalImage, message);
        
        // Convert the encoded image to bytes - ALWAYS using PNG format
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        boolean success = ImageIO.write(encodedImage, outputFormat, outputStream);
        
        if (!success) {
            throw new IOException("Failed to write image as PNG. No appropriate writer found.");
        }
        
        byte[] encodedImageBytes = outputStream.toByteArray();
        
        // Store the encoded image
        String encodedImagePath = fileStorageService.storeEncodedImage(originalImagePath, encodedImageBytes);
        
        // Create a proper filename for the result, always using .png extension
        String resultFileName = originalFileName.substring(0, originalFileName.lastIndexOf(".")) + ".png";
        
        // Create and save a record in the database
        SteganographyImage steganographyImage = new SteganographyImage(
            resultFileName, // This is now always a .png filename
            originalImagePath,
            encodedImagePath,
            message
        );
        
        return steganographyImageRepository.save(steganographyImage);
    }
    
    /**
     * Decodes a message from an image.
     * 
     * @param file The image file with a hidden message
     * @return The decoded message
     * @throws IOException If an I/O error occurs
     */
    public String decodeMessage(MultipartFile file) throws IOException {
        // Get file extension
        String fileExtension = getFileExtension(file.getOriginalFilename());
        
        // Check if the image format is suitable for steganography
        if (!SteganographyUtil.isSuitableImageFormat(fileExtension)) {
            System.out.println("Warning: " + fileExtension + 
                " is not an ideal format for steganography. " +
                "The message might be corrupted due to lossy compression.");
        }
        
        // Store the image temporarily
        String imagePath = fileStorageService.storeFile(file);
        
        // Read the image
        BufferedImage encodedImage = ImageIO.read(new File(imagePath));
        
        try {
            // Decode the message
            return SteganographyUtil.decodeMessage(encodedImage);
        } catch (Exception e) {
            throw new IllegalArgumentException(
                "Failed to decode message. This could be because: \n" +
                "1. The image does not contain a hidden message\n" +
                "2. The image was saved in a lossy format like JPEG/JPG which corrupts hidden data\n" +
                "3. The image was modified after the message was hidden\n\n" +
                "Technical details: " + e.getMessage());
        }
    }
    
    /**
     * Gets a list of all steganography operations.
     * 
     * @return A list of all steganography images
     */
    public List<SteganographyImage> getAllSteganographyImages() {
        return steganographyImageRepository.findAllByOrderByCreatedAtDesc();
    }
    
    /**
     * Gets a steganography image by ID.
     * 
     * @param id The ID of the steganography image
     * @return The steganography image
     */
    public SteganographyImage getSteganographyImageById(Long id) {
        return steganographyImageRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Steganography image not found with id: " + id));
    }
    
    /**
     * Extracts the file extension from a file name.
     * 
     * @param fileName The name of the file
     * @return The file extension
     */
    private String getFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return null;
        }
        return fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
    }
}