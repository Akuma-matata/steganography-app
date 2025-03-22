package com.stegoapp.service;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class SteganoService {

    private final Path uploadDir = Paths.get("uploads");

    public SteganoService() {
        try {
            Files.createDirectories(uploadDir);
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload directory", e);
        }
    }

    public String saveImage(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IOException("File is empty");
        }

        if (!isImageFile(file)) {
            throw new IOException("Only image files (JPG, PNG) are allowed");
        }

        String fileId = UUID.randomUUID().toString();
        String extension = getFileExtension(file.getOriginalFilename());
        String filename = fileId + extension;
        
        Path targetPath = uploadDir.resolve(filename);
        Files.copy(file.getInputStream(), targetPath);
        
        return fileId;
    }

    public String encodePassword(String imageId, String password) throws IOException {
        if (password == null || password.isEmpty()) {
            throw new IOException("Password cannot be empty");
        }
        
        Path sourcePath = findImageById(imageId);
        if (sourcePath == null) {
            throw new IOException("Image not found");
        }
        
        String encodedImageId = UUID.randomUUID().toString();
        // Always save encoded images as PNG to prevent data loss
        String extension = ".png";
        Path targetPath = uploadDir.resolve(encodedImageId + extension);
        
        BufferedImage sourceImage = ImageIO.read(sourcePath.toFile());
        BufferedImage encodedImage = encodePasswordInImage(sourceImage, password);
        
        // Use PNG format for saving the encoded image
        ImageIO.write(encodedImage, "png", targetPath.toFile());
        
        return encodedImageId;
    }

    public String decodePassword(String imageId) throws IOException {
        Path imagePath = findImageById(imageId);
        if (imagePath == null) {
            throw new IOException("Image not found");
        }
        
        BufferedImage image = ImageIO.read(imagePath.toFile());
        return decodePasswordFromImage(image);
    }

    public ResponseEntity<byte[]> getImageResponse(String imageId) throws IOException {
        Path imagePath = findImageById(imageId);
        if (imagePath == null) {
            throw new IOException("Image not found");
        }
        
        BufferedImage image = ImageIO.read(imagePath.toFile());
        String extension = getFileExtension(imagePath.getFileName().toString());
        String contentType = extension.equals(".png") ? "image/png" : "image/jpeg";
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, extension.substring(1), baos);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(contentType));
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(baos.toByteArray());
    }

    // LSB Steganography implementation
    private BufferedImage encodePasswordInImage(BufferedImage image, String password) {
        // Convert password to binary
        byte[] passwordBytes = password.getBytes(StandardCharsets.UTF_8);
        StringBuilder binaryPassword = new StringBuilder();
        
        // First byte is the length of the password
        String lengthBinary = String.format("%8s", Integer.toBinaryString(passwordBytes.length))
                .replace(' ', '0');
        binaryPassword.append(lengthBinary);
        
        // Add each byte of the password
        for (byte b : passwordBytes) {
            String byteBinary = String.format("%8s", Integer.toBinaryString(b & 0xFF))
                    .replace(' ', '0');
            binaryPassword.append(byteBinary);
        }
        
        int width = image.getWidth();
        int height = image.getHeight();
        
        // Check if the image is large enough
        if (width * height < binaryPassword.length()) {
            throw new IllegalArgumentException("Image is too small to encode the password");
        }
        
        // Create a copy of the image to work with
        // Explicitly use RGB type to ensure consistent color handling
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        result.getGraphics().drawImage(image, 0, 0, null);
        
        // Encode the binary data into the image
        int x = 0;
        int y = 0;
        int bitIndex = 0;
        
        while (bitIndex < binaryPassword.length()) {
            // Get the pixel's RGB value
            int pixel = result.getRGB(x, y);
            
            // Modify the least significant bit of the blue color component
            int blue = pixel & 0xFF;
            blue = (blue & 0xFE) | (binaryPassword.charAt(bitIndex) == '1' ? 1 : 0);
            
            // Replace the modified blue component
            pixel = (pixel & 0xFFFFFF00) | blue;
            result.setRGB(x, y, pixel);
            
            // Move to the next pixel
            x++;
            if (x >= width) {
                x = 0;
                y++;
            }
            
            bitIndex++;
        }
        
        return result;
    }

    private String decodePasswordFromImage(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        
        // Extract the length of the password (first byte)
        StringBuilder lengthBinary = new StringBuilder();
        int x = 0;
        int y = 0;
        
        for (int i = 0; i < 8; i++) {
            int pixel = image.getRGB(x, y);
            int blue = pixel & 0xFF;
            lengthBinary.append((blue & 1) == 1 ? '1' : '0');
            
            // Move to the next pixel
            x++;
            if (x >= width) {
                x = 0;
                y++;
            }
        }
        
        // Convert binary length to integer
        int passwordLength = Integer.parseInt(lengthBinary.toString(), 2);
        
        // Sanity check for password length
        if (passwordLength <= 0 || passwordLength > 1000) {
            throw new IllegalArgumentException("Invalid password length detected: " + passwordLength);
        }
        
        // Extract the password bits
        byte[] passwordBytes = new byte[passwordLength];
        for (int byteIndex = 0; byteIndex < passwordLength; byteIndex++) {
            StringBuilder byteBinary = new StringBuilder();
            
            for (int bitIndex = 0; bitIndex < 8; bitIndex++) {
                int pixel = image.getRGB(x, y);
                int blue = pixel & 0xFF;
                byteBinary.append((blue & 1) == 1 ? '1' : '0');
                
                // Move to the next pixel
                x++;
                if (x >= width) {
                    x = 0;
                    y++;
                }
            }
            
            // Convert binary byte to actual byte
            passwordBytes[byteIndex] = (byte) Integer.parseInt(byteBinary.toString(), 2);
        }
        
        // Convert byte array to string
        return new String(passwordBytes, StandardCharsets.UTF_8);
    }

    // Utility methods
    private Path findImageById(String imageId) throws IOException {
        try {
            return Files.list(uploadDir)
                    .filter(path -> path.getFileName().toString().startsWith(imageId + "."))
                    .findFirst()
                    .orElse(null);
        } catch (IOException e) {
            throw new IOException("Error accessing upload directory", e);
        }
    }

    private boolean isImageFile(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null && (
                contentType.equals("image/jpeg") || 
                contentType.equals("image/jpg") || 
                contentType.equals("image/png"));
    }

    private String getFileExtension(String filename) {
        if (filename == null) return ".jpg";
        int lastIndexOf = filename.lastIndexOf(".");
        if (lastIndexOf == -1) {
            return ".jpg";
        }
        return filename.substring(lastIndexOf);
    }
}