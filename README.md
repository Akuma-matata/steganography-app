# Simple Steganography App

A minimal steganography application that allows users to hide passwords in images and later decode them. The app uses the LSB (Least Significant Bit) steganography technique to encode text within image pixels without visibly altering the image.

## Features

- Upload PNG or JPEG images
- Encode passwords into images using LSB steganography
- Decode passwords from steganographically modified images
- Simple and intuitive user interface

## Technology Stack

- **Backend**: Java with Spring Boot
- **Frontend**: Vue.js with Tailwind CSS
- **Steganography**: LSB (Least Significant Bit) algorithm

## Project Structure

```
stego-app/
├── src/
│   └── main/
│       ├── java/com/stegoapp/
│       │   ├── controller/
│       │   │   └── SteganoController.java
│       │   ├── service/
│       │   │   └── SteganoService.java
│       │   └── SteganoApp.java
│       └── resources/
│           ├── static/
│           │   └── index.html
│           └── application.properties
├── pom.xml
└── README.md
```

## How It Works

### LSB Steganography

The app employs the Least Significant Bit (LSB) technique for steganography:

1. **Encoding**:
   - The password text is converted to binary form
   - The first byte encodes the length of the password
   - Each bit of the password replaces the least significant bit of the blue color component of pixels in the image
   - This causes minimal visual change to the image while embedding the data

2. **Decoding**:
   - The app reads the first byte to determine the password length
   - It then extracts the appropriate number of least significant bits from subsequent pixel values
   - These bits are combined to reconstruct the original password

## How to Run

### Prerequisites
- Java 11 or higher
- Maven

### Running the Application

1. Clone this repository
2. Navigate to the project directory
3. Build the application:
   ```
   mvn clean package
   ```
4. Run the application:
   ```
   java -jar target/stego-app-1.0-SNAPSHOT.jar
   ```
5. Open your browser and navigate to:
   ```
   http://localhost:8080
   ```

## Security Considerations

This is a basic implementation for educational purposes. For a more secure version:

- Add encryption to the password before encoding
- Implement user authentication
- Use a more sophisticated steganography algorithm
- Add integrity verification to detect tampering

## License

This project is open source and available under the MIT License.