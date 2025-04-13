package com.steganography.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 * Utility class for steganography operations.
 * This class provides methods to encode and decode messages in images.
 */
public class SteganographyUtil {
    
    // Terminator sequence to mark the end of the message
    private static final String MESSAGE_TERMINATOR = "11111111";
    
    /**
     * Encodes a message into an image using the least significant bit technique.
     * 
     * @param originalImage The original image to hide the message in
     * @param message The message to hide
     * @return The image with the hidden message
     */
    public static BufferedImage encodeMessage(BufferedImage originalImage, String message) {
        // Convert message to binary string
        String binaryMessage = convertStringToBinary(message);
        // Add terminator to mark the end of the message
        binaryMessage += MESSAGE_TERMINATOR;
        
        // Make a copy of the original image
        BufferedImage encodedImage = deepCopy(originalImage);
        
        int width = encodedImage.getWidth();
        int height = encodedImage.getHeight();
        
        // Check if the image is large enough to hold the message
        if (width * height < binaryMessage.length()) {
            throw new IllegalArgumentException("Image is too small to hide the message");
        }
        
        // Embed the message in the image
        int messageIndex = 0;
        
        outerloop:
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (messageIndex < binaryMessage.length()) {
                    int pixel = encodedImage.getRGB(x, y);
                    
                    // Get RGB components
                    int alpha = (pixel >> 24) & 0xff;
                    int red = (pixel >> 16) & 0xff;
                    int green = (pixel >> 8) & 0xff;
                    int blue = pixel & 0xff;
                    
                    // Replace the least significant bit with the message bit
                    if (binaryMessage.charAt(messageIndex) == '1') {
                        blue = blue | 1;  // Set LSB to 1
                    } else {
                        blue = blue & ~1; // Set LSB to 0
                    }
                    
                    // Combine components back into a pixel
                    pixel = (alpha << 24) | (red << 16) | (green << 8) | blue;
                    encodedImage.setRGB(x, y, pixel);
                    messageIndex++;
                } else {
                    break outerloop;
                }
            }
        }
        
        return encodedImage;
    }
    
    /**
     * Decodes a message from an image.
     * 
     * @param encodedImage The image with the hidden message
     * @return The hidden message
     */
    public static String decodeMessage(BufferedImage encodedImage) {
        StringBuilder binaryMessage = new StringBuilder();
        
        int width = encodedImage.getWidth();
        int height = encodedImage.getHeight();
        
        // Extract the binary message from the image
        outerloop:
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixel = encodedImage.getRGB(x, y);
                
                // Extract the least significant bit of blue component
                int blue = pixel & 0xff;
                int lsb = blue & 1;
                binaryMessage.append(lsb);
                
                // Check if we've reached the terminator
                if (binaryMessage.length() >= 8 && 
                    binaryMessage.substring(binaryMessage.length() - 8).equals(MESSAGE_TERMINATOR)) {
                    binaryMessage.delete(binaryMessage.length() - 8, binaryMessage.length());
                    break outerloop;
                }
                
                // Avoid infinite loop for corrupted images
                if (binaryMessage.length() > width * height) {
                    throw new IllegalArgumentException("Message terminator not found. The image may be corrupted or not contain a valid hidden message.");
                }
            }
        }
        
        // Convert binary string to text
        try {
            return convertBinaryToString(binaryMessage.toString());
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to decode message. The image may be corrupted or compressed in a lossy format like JPEG.", e);
        }
    }
    
    /**
     * Converts a string to a binary string.
     * 
     * @param message The message to convert
     * @return The binary representation of the message
     */
    private static String convertStringToBinary(String message) {
        StringBuilder binaryString = new StringBuilder();
        for (char character : message.toCharArray()) {
            String binary = Integer.toBinaryString(character);
            // Ensure each character is represented by 8 bits
            while (binary.length() < 8) {
                binary = "0" + binary;
            }
            binaryString.append(binary);
        }
        return binaryString.toString();
    }
    
    /**
     * Converts a binary string to a normal string.
     * 
     * @param binaryString The binary string to convert
     * @return The decoded message
     */
    private static String convertBinaryToString(String binaryString) {
        StringBuilder message = new StringBuilder();
        for (int i = 0; i < binaryString.length(); i += 8) {
            if (i + 8 <= binaryString.length()) {
                String byte_str = binaryString.substring(i, i + 8);
                try {
                    int decimal = Integer.parseInt(byte_str, 2);
                    message.append((char) decimal);
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Invalid binary data encountered during decoding");
                }
            }
        }
        return message.toString();
    }
    
    /**
     * Creates a deep copy of a BufferedImage.
     * 
     * @param source The source image
     * @return A copy of the source image
     */
    private static BufferedImage deepCopy(BufferedImage source) {
        BufferedImage copy = new BufferedImage(
            source.getWidth(), source.getHeight(), source.getType());
        
        for (int x = 0; x < source.getWidth(); x++) {
            for (int y = 0; y < source.getHeight(); y++) {
                copy.setRGB(x, y, source.getRGB(x, y));
            }
        }
        
        return copy;
    }
    
    /**
     * Checks if the image format is suitable for steganography.
     * 
     * @param imageFormat The image format/extension
     * @return true if the format is suitable, false otherwise
     */
    public static boolean isSuitableImageFormat(String imageFormat) {
        if (imageFormat == null) {
            return false;
        }
        
        String format = imageFormat.toLowerCase();
        return format.equals("png") || format.equals("bmp");
    }
}