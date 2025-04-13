# Steganography Service

## Overview
This project is an online steganography service that allows users to hide secret messages within images and later retrieve them. Steganography is the practice of concealing information within other non-secret data or a physical object to avoid detection. In this case, we implement the Least Significant Bit (LSB) steganography technique to hide text messages within digital images.

## Features
- **Message Encoding**: Hide text messages within images
- **Message Decoding**: Extract hidden messages from encoded images
- **Format Conversion**: Automatic conversion of JPEG/JPG images to PNG for better steganography results
- **Operation History**: View past steganography operations and download encoded images
- **Responsive UI**: User-friendly interface compatible with various devices

## Technologies Used
### Backend
- Java 11
- Spring Boot 2.7.0
- Spring Data JPA
- H2 Database (for simplicity)
- Java AWT/ImageIO (for image processing)

### Frontend
- Vue.js 2.6
- Axios (for HTTP requests)
- Bootstrap 5 (for styling)
- Vue Router (for navigation)

## Setup Instructions

### Prerequisites
- Java 11 or higher
- Node.js and npm
- Maven

### Backend Setup
1. Clone the repository
   ```
   git clone <repository-url>
   cd steganography-service/backend
   ```

2. Build and run the Spring Boot application
   ```
   mvn clean install
   mvn spring-boot:run
   ```
   The backend server will start on http://localhost:8080

### Frontend Setup
1. Navigate to the frontend directory
   ```
   cd ../frontend
   ```

2. Install dependencies
   ```
   npm install
   ```

3. Run the development server
   ```
   npm run serve
   ```
   The frontend application will be available at http://localhost:8081

## Usage Instructions

### Encoding a Message
1. Navigate to the "Encode" page
2. Upload an image (PNG or BMP recommended; JPEGs will be automatically converted to PNG)
3. Enter your secret message in the text area
4. Click "Encode Message"
5. Once encoding is complete, download the encoded image
6. **Important**: Do not re-save the encoded image as JPEG, as it will destroy the hidden message

### Decoding a Message
1. Navigate to the "Decode" page
2. Upload an image that has a hidden message
3. Click "Decode Message"
4. The hidden message will be displayed if successful

### Viewing History
1. Navigate to the "History" page to see past steganography operations
2. Click on "Show Message" to view the hidden message
3. Click on "Download" to download the encoded image

## Design Patterns and Principles

### Architecture Pattern
- **MVC (Model-View-Controller)**: The application follows the MVC architectural pattern:
  - Model: Java entities representing data (SteganographyImage)
  - View: Vue.js components for the user interface
  - Controller: Spring REST controllers handling HTTP requests

### Design Patterns
1. **Singleton Pattern**: Spring's dependency injection creates singleton services by default
2. **Repository Pattern**: Data access through Spring Data repositories
3. **Factory Method Pattern**: ImageIO's creation of image objects
4. **Strategy Pattern**: Different steganography algorithms could be swapped out

### Design Principles
1. **Single Responsibility Principle (SRP)**: Each class has a single responsibility
2. **Open/Closed Principle (OCP)**: The system is extensible without modifying existing code
3. **Interface Segregation Principle (ISP)**: Interfaces are focused on specific client needs
4. **Dependency Inversion Principle (DIP)**: High-level modules depend on abstractions

## Technical Implementation Details

### Image Processing
- We use the Least Significant Bit (LSB) technique to hide messages in images
- Each character of the message is converted to its binary representation
- The least significant bit of each pixel's color values is modified to store message bits
- A terminator sequence is added to mark the end of the message

### Format Considerations
- PNG and BMP formats are recommended as they use lossless compression
- JPEG/JPG uses lossy compression which can destroy hidden data
- The system automatically converts JPEG images to PNG to ensure message integrity

## Team Members
- [Team Member 1]
- [Team Member 2]
- [Team Member 3]
- [Team Member 4]

## Screenshots
[Add screenshots of the application here]

## License
This project is licensed under the MIT License - see the LICENSE file for details.