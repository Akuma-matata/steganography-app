package com.steganography.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.steganography.model.SteganographyImage;

/**
 * Repository interface for SteganographyImage entity.
 */
@Repository
public interface SteganographyImageRepository extends JpaRepository<SteganographyImage, Long> {
    
    /**
     * Find steganography operations by file name.
     * 
     * @param fileName The name of the file
     * @return A list of steganography images matching the file name
     */
    List<SteganographyImage> findByFileName(String fileName);
    
    /**
     * Find all steganography operations, ordered by creation date (newest first).
     * 
     * @return A list of all steganography images ordered by creation date
     */
    List<SteganographyImage> findAllByOrderByCreatedAtDesc();
}