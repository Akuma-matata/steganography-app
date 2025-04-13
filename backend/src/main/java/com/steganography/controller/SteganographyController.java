package com.steganography.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.steganography.model.SteganographyImage;
import com.steganography.service.SteganographyService;

/**
 * REST controller for steganography operations.
 */
@RestController
@RequestMapping("/api/steganography")
@CrossOrigin(origins = "*")
public class SteganographyController {
    
    @Autowired
    private SteganographyService steganographyService;
    
    /**
     * Endpoint for encoding a message into an image.
     * 
     * @param file The image file
     * @param message The message to encode
     * @return Response with information about the operation
     */
    @PostMapping("/encode")
    public ResponseEntity<Map<String, Object>> encodeMessage(
            @RequestParam("file") MultipartFile file,
            @RequestParam("message") String message) {
        
        try {
            SteganographyImage steganographyImage = steganographyService.encodeMessage(file, message);
            
            // Generate download URL for the encoded image
            String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/steganography/download/")
                .path(steganographyImage.getId().toString())
                .toUriString();
            
            Map<String, Object> response = new HashMap<>();
            response.put("id", steganographyImage.getId());
            response.put("fileName", steganographyImage.getFileName());
            response.put("message", steganographyImage.getMessage());
            response.put("downloadUrl", fileDownloadUri);
            
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("error", "Failed to encode message: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * Endpoint for decoding a message from an image.
     * 
     * @param file The image file with a hidden message
     * @return Response with the decoded message
     */
    @PostMapping("/decode")
    public ResponseEntity<Map<String, Object>> decodeMessage(
            @RequestParam("file") MultipartFile file) {
        
        try {
            String decodedMessage = steganographyService.decodeMessage(file);
            
            Map<String, Object> response = new HashMap<>();
            response.put("fileName", file.getOriginalFilename());
            response.put("message", decodedMessage);
            
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("error", "Failed to decode message: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * Endpoint for downloading an encoded image.
     * 
     * @param id The ID of the steganography image
     * @return The encoded image file
     */
    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long id) {
        try {
            SteganographyImage steganographyImage = steganographyService.getSteganographyImageById(id);
            
            Path path = Paths.get(steganographyImage.getEncodedImagePath());
            ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));
            
            // Always set content type to PNG image for steganography
            String contentType = "image/png";
            
            return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + 
                        steganographyImage.getFileName() + "\"")
                .body(resource);
        } catch (IOException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Endpoint for getting a list of all steganography operations.
     * 
     * @return A list of all steganography images
     */
    @GetMapping("/history")
    public ResponseEntity<List<SteganographyImage>> getAllSteganographyImages() {
        List<SteganographyImage> steganographyImages = steganographyService.getAllSteganographyImages();
        return ResponseEntity.ok(steganographyImages);
    }
}