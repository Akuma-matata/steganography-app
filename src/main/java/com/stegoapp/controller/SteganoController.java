package com.stegoapp.controller;

import com.stegoapp.service.SteganoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class SteganoController {

    @Autowired
    private SteganoService steganoService;

    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> uploadImage(@RequestParam("image") MultipartFile file) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String imageId = steganoService.saveImage(file);
            response.put("success", true);
            response.put("imageId", imageId);
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/encode")
    public ResponseEntity<Map<String, Object>> encodePassword(
            @RequestParam("imageId") String imageId,
            @RequestParam("password") String password) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            String encodedImageId = steganoService.encodePassword(imageId, password);
            response.put("success", true);
            response.put("encodedImageId", encodedImageId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/decode/{imageId}")
    public ResponseEntity<Map<String, Object>> decodePassword(@PathVariable String imageId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String password = steganoService.decodePassword(imageId);
            response.put("success", true);
            response.put("password", password);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/image/{imageId}")
    public ResponseEntity<byte[]> getImage(@PathVariable String imageId) {
        try {
            return steganoService.getImageResponse(imageId);
        } catch (IOException e) {
            return ResponseEntity.notFound().build();
        }
    }
}