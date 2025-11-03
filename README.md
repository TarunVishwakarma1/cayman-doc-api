# Cayman Document API

<div align="center">

![Java](https://img.shields.io/badge/Java-17-orange?style=for-the-badge&logo=java)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.6-brightgreen?style=for-the-badge&logo=spring)
![Maven](https://img.shields.io/badge/Maven-3.9.11-C71A36?style=for-the-badge&logo=apache-maven)

**A Production-Ready Spring Boot REST API for Document Management and Cryptographic Operations**

[Features](#-features) • [Quick Start](#-quick-start) • [API Documentation](#-api-documentation) • [Deployment](#-deployment) • [Configuration](#%EF%B8%8F-configuration)

</div>

---

## 📑 Table of Contents

- [Overview](#-overview)
- [Features](#-features)
- [Technology Stack](#-technology-stack)
- [Architecture](#-architecture)
    - [System Architecture](#system-architecture)
    - [Project Structure](#project-structure)
    - [Layered Design](#layered-design)
    - [Key Components](#key-components)
- [Prerequisites](#-prerequisites)
- [Quick Start](#-quick-start)
- [Installation](#-installation)
    - [Build from Source](#1-build-from-source)
    - [JAR Deployment](#2-jar-deployment-standalone)
    - [WAR Deployment](#3-war-deployment-for-application-servers)
- [Configuration](#%EF%B8%8F-configuration)
    - [Application Properties](#application-properties)
    - [Property Encryption](#property-encryption)
    - [OmniDocs Connection](#omnidocs-connection-settings)
    - [Rate Limiting](#rate-limiting-configuration)
- [API Documentation](#-api-documentation)
    - [Document Management APIs](#1-document-management-apis)
    - [Security & Cryptography APIs](#2-security--cryptography-apis)
    - [Response Formats](#response-formats)
    - [Error Handling](#error-handling)
- [Deployment](#-deployment)
    - [JBoss/WildFly Deployment](#1-jbosswildfly-deployment)
    - [Tomcat Deployment](#2-tomcat-deployment)
    - [Docker Deployment](#3-docker-deployment)
- [CI/CD with Jenkins](#-cicd-with-jenkins)
    - [Jenkins Pipeline Setup](#jenkins-pipeline-setup)
    - [Example Jenkinsfile](#example-jenkinsfile)
    - [Environment Configuration](#environment-configuration)
- [Testing](#-testing)
- [Monitoring & Health Checks](#-monitoring--health-checks)
- [Security Considerations](#-security-considerations)
- [Troubleshooting](#-troubleshooting)

---

## 🌟 Overview

**Cayman Document API** is a robust, enterprise-grade Spring Boot application that provides RESTful APIs for:

1. **Document Management**: Seamless integration with Newgen OmniDocs for document retrieval, download, and metadata management.
2. **Cryptographic Operations**: Comprehensive security features including AES-256 encryption/decryption, RSA public-key cryptography, digital signatures, and key pair generation.
3. **API Protection**: Built-in rate limiting using Token Bucket algorithm to prevent abuse and ensure fair resource allocation.

The application is designed for high availability, scalability, and security, making it suitable for production environments in financial services, healthcare, government, and enterprise document management systems.

---

## ✨ Features

### Document Management
- ✅ **Document Retrieval**: Fetch documents from Newgen OmniDocs cabinet by document index
- ✅ **Multiple Formats**: Support for Base64 encoding and raw bytes
- ✅ **File Download**: Download documents as file attachments with proper MIME types
- ✅ **Session Management**: Centralized OmniDocs session handling
- ✅ **Metadata Support**: Automatic content-type detection and filename handling

### Cryptographic Operations
- 🔐 **AES-256 Encryption/Decryption**: Symmetric encryption using global or custom keys
- 🔑 **RSA Encryption/Decryption**: Asymmetric encryption with 2048-bit keys
- ✍️ **Digital Signatures**: Sign and verify data using SHA256withRSA
- 🔧 **Key Generation**: RSA and Elliptic Curve (EC) key pair generation
- 🔒 **Property Encryption**: Secure configuration properties with ENC() pattern

### Security & Performance
- 🛡️ **Rate Limiting**: Token Bucket algorithm with per-IP tracking
- 📊 **Request Throttling**: Configurable capacity and time windows
- 🚨 **Exception Handling**: Comprehensive error handling with meaningful messages
- 📝 **Audit Logging**: Structured logging with SLF4J and Logback
- 🔍 **API Documentation**: Interactive Swagger/OpenAPI documentation

---

## 🛠 Technology Stack

| Component | Technology | Version |
|-----------|-----------|---------|
| **Language** | Java | 17 |
| **Framework** | Spring Boot | 3.5.6 |
| **Build Tool** | Maven | 3.9.11+ |
| **Documentation** | SpringDoc OpenAPI | 3.0.0-M1 |
| **Rate Limiting** | Bucket4j | 8.10.1 |
| **Caching** | Caffeine | (Spring Boot Managed) |
| **Logging** | SLF4J + Logback | (Spring Boot Managed) |
| **External Integration** | Newgen OmniDocs | Custom JARs |

---

## 🏗 Architecture

### System Architecture

```
┌─────────────────┐
│   API Clients   │
│  (REST/HTTP)    │
└────────┬────────┘
         │
         ▼
┌─────────────────────────────────────────────┐
│         Rate Limit Filter (Token Bucket)    │
│         (Per-IP, Configurable Capacity)     │
└────────┬────────────────────────────────────┘
         │
         ▼
┌─────────────────────────────────────────────┐
│           Spring MVC Controllers            │
│  ┌──────────────────┐  ┌─────────────────┐ │
│  │ DocumentController│  │SecurityController│ │
│  └──────────────────┘  └─────────────────┘ │
└────────┬────────────────────────┬───────────┘
         │                        │
         ▼                        ▼
┌─────────────────────┐  ┌──────────────────┐
│   Document Service  │  │ Encryption Service│
│   Implementation    │  │ Decryption Service│
│                     │  │ KeyPair Service   │
└────────┬────────────┘  └──────────────────┘
         │
         ▼
┌─────────────────────────────────────────────┐
│       Newgen OmniDocs Integration           │
│   (Cabinet Connection, Session Management)  │
└─────────────────────────────────────────────┘
         │
         ▼
┌─────────────────────────────────────────────┐
│          OmniDocs REST Services             │
│       (Document Storage & Retrieval)        │
└─────────────────────────────────────────────┘
```

### Project Structure

```
cayman-doc-api/
├── src/
│   ├── main/
│   │   ├── java/com/newgen/cig/cayman/document/
│   │   │   ├── Application.java                    # Main application entry point
│   │   │   ├── config/                             # Configuration classes
│   │   │   │   ├── AppConfig.java                  # Bean definitions
│   │   │   │   ├── PropertyDecryptionInitializer.java  # Property encryption support
│   │   │   │   ├── RateLimitFilter.java            # Rate limiting filter
│   │   │   │   └── SwaggerConfig.java              # OpenAPI documentation config
│   │   │   ├── controller/                         # REST endpoints
│   │   │   │   ├── DocumentController.java         # Document management APIs
│   │   │   │   └── SecurityController.java         # Cryptography APIs
│   │   │   ├── service/                            # Business logic
│   │   │   │   └── DocumentService.java
│   │   │   ├── implementation/                     # Service implementations
│   │   │   │   ├── DocumentImpl.java               # OmniDocs integration
│   │   │   │   ├── EncryptionServiceImpl.java
│   │   │   │   ├── DecryptionServiceImpl.java
│   │   │   │   └── KeyPairServiceImpl.java
│   │   │   ├── interfaces/                         # Service interfaces
│   │   │   ├── model/
│   │   │   │   ├── dto/                            # Data Transfer Objects
│   │   │   │   ├── dao/                            # Data Access Objects
│   │   │   │   └── enums/                          # Enumerations
│   │   │   ├── exception/                          # Exception hierarchy
│   │   │   │   ├── GlobalExceptionHandler.java     # Centralized error handling
│   │   │   │   ├── BaseException.java
│   │   │   │   └── [specific exceptions...]
│   │   │   └── utils/                              # Utility classes
│   │   │       ├── Encryption.java
│   │   │       ├── Decryption.java
│   │   │       ├── KeyPair.java
│   │   │       └── PropertyEncryptionUtil.java
│   │   └── resources/
│   │       ├── application.yml                     # Main configuration
│   │       └── logback-spring.xml                  # Logging configuration
│   └── test/
│       └── java/                                   # Unit and integration tests
├── libs/                                           # External JAR dependencies
│   ├── odweb.jar                                   # OmniDocs web services
│   ├── jtssessionbean.jar                          # Session management
│   ├── ejbclient.jar                               # EJB client
│   └── log4j.jar                                   # Legacy logging
├── files/                                          # Configuration templates
│   ├── jboss-deployment-structure.xml
│   └── ngdbini/
├── pom.xml                                         # Maven build configuration
└── README.md                                       # This file
```

### Layered Design

The application follows a clean layered architecture with clear separation of concerns:

```
┌─────────────────────────────────────────┐
│         Presentation Layer              │
│    (Controllers - REST Endpoints)       │
└────────────────┬────────────────────────┘
                 │
┌────────────────▼────────────────────────┐
│          Service Layer                  │
│    (Business Logic Interfaces)          │
└────────────────┬────────────────────────┘
                 │
┌────────────────▼────────────────────────┐
│       Implementation Layer              │
│  (Service Implementations, Utils)       │
└────────────────┬────────────────────────┘
                 │
┌────────────────▼────────────────────────┐
│      Integration Layer                  │
│  (OmniDocs Connectors, External APIs)   │
└─────────────────────────────────────────┘
```

**Benefits:**
- **Maintainability**: Each layer has a single responsibility
- **Testability**: Easy to mock dependencies and write unit tests
- **Scalability**: Can replace implementations without affecting other layers
- **Flexibility**: Support for multiple data sources or external services

### Key Components

#### 1. **Property Decryption System**
Automatically decrypts encrypted configuration properties at startup:
- **Pattern**: `ENC(encrypted_value)` in `application.yml`
- **Algorithm**: AES-256 with key from `my.security.aes-secret`
- **Scope**: Only properties under `newgen.cayman.connect.cabinet.*`
- **Utility**: `PropertyEncryptionUtil` for encrypting sensitive values

#### 2. **Rate Limiting Filter**
Protects APIs from abuse using Token Bucket algorithm:
- **Implementation**: Bucket4j library with Caffeine cache backend
- **Granularity**: Per-IP address tracking
- **Cache Expiration**: 10 minutes of inactivity
- **Configuration**: Capacity and duration configurable via `application.yml`
- **Response**: HTTP 429 (Too Many Requests) when limit exceeded

#### 3. **OmniDocs Integration**
Manages document retrieval from Newgen OmniDocs:
- **Session Management**: Centralized session ID handling via `GlobalSessionService`
- **Connection**: Custom `ConnectCabinet` class using OmniDocs JARs
- **REST API**: `/OmniDocsRestWS/rest/services/getDocumentJSON` endpoint
- **Error Handling**: Automatic retry and session refresh on expiration

#### 4. **Exception Handling**
Comprehensive error management with `GlobalExceptionHandler`:
- **Custom Exceptions**: Domain-specific exception hierarchy
- **HTTP Status Mapping**: Automatic status code assignment
- **Structured Responses**: Consistent error format with error codes
- **Logging**: Detailed error logging with context

---

## 📋 Prerequisites

Before installing and running the Cayman Document API, ensure you have:

### Required Software

| Software | Minimum Version | Purpose | Download |
|----------|----------------|---------|----------|
| **Java JDK** | 17+ | Runtime environment | [Oracle](https://www.oracle.com/java/technologies/downloads/) / [OpenJDK](https://adoptium.net/) |
| **Apache Maven** | 3.9.11+ | Build tool | [Maven](https://maven.apache.org/download.cgi) |

### Optional (for Deployment)

| Software | Version | Purpose |
|----------|---------|---------|
| **JBoss/WildFly** | 26+ | Application server deployment |
| **Apache Tomcat** | 10+ | Servlet container deployment |
| **Docker** | 20+ | Containerized deployment |
| **Jenkins** | 2.400+ | CI/CD automation |

### External Dependencies

- **Newgen OmniDocs JARs**: Place in `libs/` directory
    - `odweb.jar`
    - `jtssessionbean.jar`
    - `ejbclient.jar`
    - `log4j.jar` (v1.2.14)

### Network Requirements

- Access to Newgen OmniDocs server (configurable in `application.yml`)
- Ports: 8081 (default API), 8080 (OmniDocs site), 3333 (JTS)

---

## 🚀 Quick Start

Get up and running in less than 5 minutes:

```bash
# 1. Navigate to code
cd path/to/cayman-doc-api

# 2. Ensure external JARs are in libs/ directory
ls -l libs/
# Should show: odweb.jar, jtssessionbean.jar, ejbclient.jar, log4j.jar

# 3. Build the project
./mvnw clean package

# 4. Run the application
./mvnw spring-boot:run

# 5. Verify the API is running
curl http://localhost:8081/api/v1
# Expected: <h1>Hello, World!<h1>

# 6. Access Swagger UI
open http://localhost:8081/swagger-ui.html
```

---

## 📥 Installation

### 1. Build from Source

#### Step 1: Clean and Compile

```bash
# Remove previous builds and compile
./mvnw clean compile

# Output verification
# [INFO] BUILD SUCCESS
# [INFO] Total time: XX s
```

#### Step 2: Run Tests

```bash
# Execute all unit and integration tests
./mvnw test

# Run specific test class
./mvnw test -Dtest=CaymenDocumentApiApplicationTests

# Skip tests (not recommended)
./mvnw clean package -DskipTests
```

#### Step 3: Package the Application

```bash
# Default: Creates standalone JAR with embedded Tomcat
./mvnw clean package

# Generated artifact: target/caymen-document-api-1.jar
```

### 2. JAR Deployment (Standalone)

Best for development, testing, and containerized deployments.

#### Build JAR

```bash
# Package as executable JAR
./mvnw clean package

# Verify artifact
ls -lh target/caymen-document-api-1.jar
```

#### Run JAR

```bash
# Run with default configuration
java -jar target/caymen-document-api-1.jar

# Run with specific Spring profile
java -jar -Dspring.profiles.active=prod target/caymen-document-api-1.jar

# Run with custom port
java -jar -Dserver.port=9090 target/caymen-document-api-1.jar

# Run with custom application.yml
java -jar -Dspring.config.location=file:/path/to/application.yml target/caymen-document-api-1.jar

# Run with JVM tuning (production example)
java -Xms512m -Xmx2048m -XX:+UseG1GC \
  -Dspring.profiles.active=prod \
  -jar target/caymen-document-api-1.jar
```

#### Run as Background Service (Linux)

```bash
# Using nohup
nohup java -jar target/caymen-document-api-1.jar > app.log 2>&1 &

# Using systemd (recommended for production)
# Create file: /etc/systemd/system/cayman-doc-api.service
```

**systemd service file:**
```ini
[Unit]
Description=Cayman Document API
After=network.target

[Service]
Type=simple
User=appuser
WorkingDirectory=/opt/cayman-doc-api
ExecStart=/usr/bin/java -jar /opt/cayman-doc-api/caymen-document-api-1.jar
Restart=on-failure
RestartSec=10
StandardOutput=journal
StandardError=journal

[Install]
WantedBy=multi-user.target
```

**Enable and start:**
```bash
sudo systemctl daemon-reload
sudo systemctl enable cayman-doc-api
sudo systemctl start cayman-doc-api
sudo systemctl status cayman-doc-api
```

### 3. WAR Deployment (for Application Servers)

Best for enterprise environments with existing JBoss/WildFly/Tomcat infrastructure.

#### Step 1: Modify POM for WAR Packaging

Edit `pom.xml`:
```xml
<!-- Change packaging type -->
<packaging>war</packaging>

<!-- Add dependency to mark embedded Tomcat as provided -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-tomcat</artifactId>
    <scope>provided</scope>
</dependency>
```

#### Step 2: Extend SpringBootServletInitializer

Update `Application.java`:
```java
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class Application extends SpringBootServletInitializer {
    
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(Application.class);
    }
    
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(Application.class);
        app.addInitializers(new PropertyDecryptionInitializer());
        app.run(args);
    }
}
```

#### Step 3: Build WAR

```bash
# Package as WAR
./mvnw clean package

# Generated artifact: target/caymen-document-api-1.war
ls -lh target/caymen-document-api-1.war
```

#### Step 4: Deploy (See [Deployment Section](#-deployment))

---

## ⚙️ Configuration

### Application Properties

The main configuration file is `src/main/resources/application.yml`:

```yaml
spring:
  application:
    name: caymen-document-api
  profile:
    active: default

server:
  port: 8081

logging:
  config: classpath:logback-spring.xml
  level:
    root: INFO
    com.newgen.cig.cayman: INFO
    org.springframework: INFO
    org.springframework.web: INFO

newgen:
  cayman:
    connect:
      cabinet:
        username: ENC(...)
        password: ENC(...)
        cabinetName: worcuat
        siteHeader: http://
        siteIP: 127.0.0.1
        sitePort: 8080
        siteURI: /OmniDocsRestWS/rest/services
        site:
          document:
            request: /getDocumentJSON
          api:
            request:
              json: /executeAPIJSON
        jtsIP: 127.0.0.1
        jtsPort: 3333
        mainGroupIndex: 0
        userExists: N
        siteId: 1
        volumeId: 1

rate:
  limit:
    capacity: 100
    duration:
      minutes: 1

my:
  security:
    aes-secret: b229ad1a61e8a680a31a646cd634dbb1
```

### Property Encryption

Sensitive properties (like passwords) can be encrypted using the `ENC()` pattern.

#### Encrypt a Property

Run the `PropertyEncryptionUtil` to encrypt values:

```bash
# Compile the utility class
./mvnw compile

# Run encryption utility
./mvnw exec:java -Dexec.mainClass="com.newgen.cig.cayman.document.utils.PropertyEncryptionUtil"

# Follow prompts to enter:
# 1. AES secret key (32 characters)
# 2. Plain text value to encrypt

# Example output:
# Enter AES secret key (32 chars): b229ad1a61e8a680a31a646cd634dbb1
# Enter value to encrypt: mySecretPassword
# Encrypted value: ENC(abc123def456...)
```

#### Add Encrypted Value to Configuration

```yaml
newgen:
  cayman:
    connect:
      cabinet:
        password: ENC(abc123def456...)  # Replace with your encrypted value
```

The `PropertyDecryptionInitializer` automatically decrypts `ENC()` values at startup.

### OmniDocs Connection Settings

Configure your Newgen OmniDocs connection:

| Property | Description | Example |
|----------|-------------|---------|
| `username` | OmniDocs cabinet username (encrypted) | `ENC(...)` |
| `password` | OmniDocs cabinet password (encrypted) | `ENC(...)` |
| `cabinetName` | Target cabinet name | `worcuat` |
| `siteIP` | OmniDocs web server IP | `127.0.0.1` |
| `sitePort` | OmniDocs web server port | `8080` |
| `jtsIP` | JTS (JBoss Transaction Service) IP | `127.0.0.1` |
| `jtsPort` | JTS port | `3333` |
| `siteId` | Site identifier | `1` |
| `volumeId` | Volume identifier | `1` |

### Rate Limiting Configuration

Adjust rate limiting to match your traffic patterns:

```yaml
rate:
  limit:
    capacity: 100           # Maximum requests per time window
    duration:
      minutes: 1           # Time window duration
```

**Examples:**
- **Low Traffic**: `capacity: 50, minutes: 1` (50 requests/minute)
- **Medium Traffic**: `capacity: 100, minutes: 1` (100 requests/minute)
- **High Traffic**: `capacity: 500, minutes: 1` (500 requests/minute)

**Rate Limit Response:**
```json
{
  "timestamp": "2025-01-03T06:00:00",
  "status": 429,
  "message": "Too many requests",
  "details": "Rate limit exceeded. Please retry after some time.",
  "path": "/api/v1/fetchDoc/base64/12345"
}
```

---

## 📖 API Documentation

### Interactive Documentation

Access the Swagger UI for interactive API exploration:

```
http://localhost:8081/swagger-ui.html
```

### 1. Document Management APIs

#### Health Check

**Endpoint:** `GET /api/v1`

**Description:** Simple health check to verify API availability.

**Request:**
```bash
curl -X GET http://localhost:8081/api/v1
```

**Response:**
```html
<h1>Hello, World!<h1>
```

**Status Codes:**
- `200 OK`: Service is running

---

#### Get Session ID

**Endpoint:** `GET /api/v1/sessionId`

**Description:** Retrieve a new OmniDocs session ID for subsequent document operations.

**Request:**
```bash
curl -X GET http://localhost:8081/api/v1/sessionId
```

**Response:**
```
ABC123DEF456GHI789
```

**Status Codes:**
- `200 OK`: Session ID retrieved successfully
- `502 Bad Gateway`: OmniDocs connection failed
- `429 Too Many Requests`: Rate limit exceeded

**Notes:**
- Session is managed globally by `GlobalSessionService`
- Session ID is required for document retrieval operations
- Sessions expire after a period of inactivity

---

#### Download Document

**Endpoint:** `GET /api/v1/download/{docIndex}`

**Description:** Download a document as a file attachment with appropriate content-type and filename.

**Parameters:**

| Name | Type | Location | Required | Description |
|------|------|----------|----------|-------------|
| `docIndex` | String | Path | Yes | Unique document identifier in OmniDocs |

**Request:**
```bash
curl -X GET http://localhost:8081/api/v1/download/12345 \
  -H "Accept: application/octet-stream" \
  -o downloaded-document.pdf
```

**Response Headers:**
```
Content-Type: application/pdf
Content-Disposition: attachment; filename="InvoiceDoc.pdf"
```

**Response Body:** Binary document data

**Status Codes:**
- `200 OK`: Document downloaded successfully
- `400 Bad Request`: Invalid docIndex parameter
- `404 Not Found`: Document not found in OmniDocs
- `401 Unauthorized`: Session expired
- `502 Bad Gateway`: OmniDocs service error
- `429 Too Many Requests`: Rate limit exceeded

**Supported Document Types:**

| Extension      | MIME Type |
|----------------|-----------|
| `.pdf`         | `application/pdf` |
| `.tiff` `.tif` | `image/tiff` |
| `.jpg` `.jpeg` | `image/jpeg` |
| `.png`         | `image/png` |
| `.doc`         | `application/msword` |
| `.docx`        | `application/vnd.openxmlformats-officedocument.wordprocessingml.document` |
| `.xls`         | `application/vnd.ms-excel` |
| `.xlsx`        | `application/vnd.openxmlformats-officedocument.spreadsheetml.sheet` |

---

#### Fetch Document

**Endpoint:** `GET /api/v1/fetchDoc/{format}/{docIndex}`

**Description:** Fetch document content as Base64 string or raw bytes for inline display.

**Parameters:**

| Name | Type | Location | Required | Description |
|------|------|----------|----------|-------------|
| `format` | String | Path | Yes | Response format: `base64` or `bytes` |
| `docIndex` | String | Path | Yes | Unique document identifier |

**Request (Base64):**
```bash
curl -X GET http://localhost:8081/api/v1/fetchDoc/base64/12345
```

**Response (Base64):**
```json
{
  "timestamp": "2025-01-03T06:00:00",
  "status": 200,
  "message": "OK",
  "data": "JVBERi0xLjQKJeLjz9MKMy..."
}
```

**Request (Bytes):**
```bash
curl -X GET http://localhost:8081/api/v1/fetchDoc/bytes/12345 \
  -H "Accept: application/pdf"
```

**Response (Bytes):** Binary document data with headers:
```
Content-Type: application/pdf
Content-Disposition: inline; filename="InvoiceDoc.pdf"
```

**Status Codes:**
- `200 OK`: Document fetched successfully
- `400 Bad Request`: Invalid format or docIndex
- `404 Not Found`: Document not found
- `401 Unauthorized`: Session expired
- `502 Bad Gateway`: OmniDocs service error

---

### 2. Security & Cryptography APIs

#### Generate RSA Key Pair

**Endpoint:** `GET /api/security/keys/rsa`

**Description:** Generate a new 2048-bit RSA key pair for encryption/decryption operations.

**Request:**
```bash
curl -X GET http://localhost:8081/api/security/keys/rsa
```

**Response:**
```json
{
  "publicKey": "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQ...",
  "privateKey": "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIB..."
}
```

**Status Codes:**
- `200 OK`: Keys generated successfully
- `500 Internal Server Error`: Key generation failed

---

#### Generate EC Key Pair

**Endpoint:** `GET /api/security/keys/ec`

**Description:** Generate a new Elliptic Curve (EC) key pair for cryptographic operations.

**Request:**
```bash
curl -X GET http://localhost:8081/api/security/keys/ec
```

**Response:**
```json
{
  "publicKey": "MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAE...",
  "privateKey": "MEECAQAwEwYHKoZIzj0CAQYIKoZIzj0DAQcEJzAl..."
}
```

**Status Codes:**
- `200 OK`: Keys generated successfully
- `500 Internal Server Error`: Key generation failed

---

#### AES Encrypt

**Endpoint:** `POST /api/security/aes/encrypt`

**Description:** Encrypt plain text using AES-256 with the global AES secret key.

**Request Body:**
```json
{
  "text": "Sensitive data to encrypt"
}
```

**Request:**
```bash
curl -X POST http://localhost:8081/api/security/aes/encrypt \
  -H "Content-Type: application/json" \
  -d '{"text": "Sensitive data to encrypt"}'
```

**Response:**
```json
{
  "text": "U2FsdGVkX1+abc123def456ghi789..."
}
```

**Status Codes:**
- `200 OK`: Text encrypted successfully
- `400 Bad Request`: Invalid input or missing text
- `500 Internal Server Error`: Encryption failed

---

#### AES Decrypt

**Endpoint:** `POST /api/security/aes/decrypt`

**Description:** Decrypt cipher text using AES-256 with the global AES secret key.

**Request Body:**
```json
{
  "text": "U2FsdGVkX1+abc123def456ghi789..."
}
```

**Request:**
```bash
curl -X POST http://localhost:8081/api/security/aes/decrypt \
  -H "Content-Type: application/json" \
  -d '{"text": "U2FsdGVkX1+abc123def456ghi789..."}'
```

**Response:**
```json
{
  "text": "Sensitive data to encrypt"
}
```

**Status Codes:**
- `200 OK`: Text decrypted successfully
- `400 Bad Request`: Invalid input or malformed cipher text
- `500 Internal Server Error`: Decryption failed

---

#### RSA Encrypt

**Endpoint:** `POST /api/security/rsa/encrypt`

**Description:** Encrypt plain text using RSA public key encryption.

**Request Body:**
```json
{
  "plainText": "Secret message",
  "base64PublicKey": "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQ..."
}
```

**Request:**
```bash
curl -X POST http://localhost:8081/api/security/rsa/encrypt \
  -H "Content-Type: application/json" \
  -d '{
    "plainText": "Secret message",
    "base64PublicKey": "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQ..."
  }'
```

**Response:**
```json
{
  "text": "abc123def456ghi789jkl012..."
}
```

**Status Codes:**
- `200 OK`: Text encrypted successfully
- `400 Bad Request`: Invalid public key or plain text
- `500 Internal Server Error`: Encryption failed

---

#### RSA Decrypt

**Endpoint:** `POST /api/security/rsa/decrypt`

**Description:** Decrypt cipher text using RSA private key decryption.

**Request Body:**
```json
{
  "cipherText": "abc123def456ghi789jkl012...",
  "base64PrivateKey": "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIB..."
}
```

**Request:**
```bash
curl -X POST http://localhost:8081/api/security/rsa/decrypt \
  -H "Content-Type: application/json" \
  -d '{
    "cipherText": "abc123def456ghi789jkl012...",
    "base64PrivateKey": "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIB..."
  }'
```

**Response:**
```json
{
  "text": "Secret message"
}
```

**Status Codes:**
- `200 OK`: Text decrypted successfully
- `400 Bad Request`: Invalid private key or cipher text
- `500 Internal Server Error`: Decryption failed

---

#### Sign Data

**Endpoint:** `POST /api/security/sign`

**Description:** Generate a digital signature for data using SHA256withRSA algorithm.

**Request Body:**
```json
{
  "data": "Data to be signed",
  "base64PrivateKey": "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIB..."
}
```

**Request:**
```bash
curl -X POST http://localhost:8081/api/security/sign \
  -H "Content-Type: application/json" \
  -d '{
    "data": "Data to be signed",
    "base64PrivateKey": "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIB..."
  }'
```

**Response:**
```json
{
  "data": "Data to be signed",
  "signature": "xyz789abc123def456..."
}
```

**Status Codes:**
- `200 OK`: Data signed successfully
- `400 Bad Request`: Invalid private key or data
- `500 Internal Server Error`: Signing failed

---

#### Verify Signature

**Endpoint:** `POST /api/security/verify`

**Description:** Verify a digital signature using the original data and public key.

**Request Body:**
```json
{
  "data": "Data to be signed",
  "signature": "xyz789abc123def456...",
  "base64PublicKey": "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQ..."
}
```

**Request:**
```bash
curl -X POST http://localhost:8081/api/security/verify \
  -H "Content-Type: application/json" \
  -d '{
    "data": "Data to be signed",
    "signature": "xyz789abc123def456...",
    "base64PublicKey": "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQ..."
  }'
```

**Response:**
```json
{
  "isVerified": true
}
```

**Status Codes:**
- `200 OK`: Verification completed (check `isVerified` field)
- `400 Bad Request`: Invalid public key, data, or signature
- `500 Internal Server Error`: Verification failed

---

### Response Formats

#### Success Response

```json
{
  "timestamp": "2025-01-03T06:00:00.123",
  "status": 200,
  "message": "OK",
  "data": {
    // Response data object
  }
}
```

#### Error Response

```json
{
  "timestamp": "2025-01-03T06:00:00.123",
  "status": 400,
  "errorCode": "ERR_400",
  "message": "Invalid parameter",
  "details": "Document index cannot be null or empty",
  "path": "/api/v1/download/",
  "validationErrors": []  // Present only for validation errors
}
```

### Error Handling

The API uses a comprehensive exception handling system with standardized error codes:

| HTTP Status | Error Code | Message | When It Occurs |
|-------------|------------|---------|----------------|
| `400` | `ERR_400` | Bad Request | Invalid or missing parameters |
| `401` | `ERR_401` | Unauthorized | Session expired or invalid credentials |
| `404` | `ERR_404` | Not Found | Document or resource not found |
| `429` | `ERR_429` | Too Many Requests | Rate limit exceeded |
| `500` | `ERR_500` | Internal Server Error | Unexpected server error |
| `502` | `ERR_502` | Bad Gateway | OmniDocs or external service error |
| `503` | `ERR_503` | Service Unavailable | External service timeout or unavailable |

---

## 🚀 Deployment

### 1. JBoss/WildFly Deployment

#### Prerequisites
- JBoss/WildFly 26+ installed
- Server running and management console accessible

#### Step 1: Prepare WAR File

```bash
# Build WAR (see WAR Deployment section above)
./mvnw clean package

# Verify WAR artifact
ls -lh target/caymen-document-api-1.war
```

#### Step 2: Configure JBoss

Create `jboss-deployment-structure.xml` in `src/main/webapp/WEB-INF/`:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<jboss-deployment-structure>
    <deployment>
        <exclusions>
            <!-- Exclude conflicting JBoss modules -->
            <module name="org.apache.log4j"/>
            <module name="org.jboss.logging"/>
        </exclusions>
        <dependencies>
            <!-- Add custom dependencies if needed -->
            <module name="javax.api"/>
        </dependencies>
    </deployment>
</jboss-deployment-structure>
```

#### Step 3: Deploy via CLI

```bash
# Connect to JBoss CLI
$JBOSS_HOME/bin/jboss-cli.sh --connect

# Deploy WAR
deploy /path/to/target/caymen-document-api-1.war

# Verify deployment
deployment-info --name=caymen-document-api-1.war

# Undeploy (if needed)
undeploy caymen-document-api-1.war
```

#### Step 4: Deploy via Management Console

1. Open JBoss Admin Console: `http://localhost:9990`
2. Navigate to **Deployments** → **Add**
3. Upload `caymen-document-api-1.war`
4. Click **Enable** to activate deployment

#### Step 5: Deploy via File System

```bash
# Copy WAR to deployments directory
cp target/caymen-document-api-1.war $JBOSS_HOME/standalone/deployments/

# Create deployment marker (if auto-deploy is disabled)
touch $JBOSS_HOME/standalone/deployments/caymen-document-api-1.war.dodeploy

# Check deployment status
ls -l $JBOSS_HOME/standalone/deployments/
# Look for: caymen-document-api-1.war.deployed
```

#### Verification

```bash
# Check API availability
curl http://localhost:8080/caymen-document-api-1/api/v1

# Expected: <h1>Hello, World!<h1>
```

**Note:** Context path is typically `/caymen-document-api-1` unless configured otherwise.

---

### 2. Tomcat Deployment

#### Prerequisites
- Apache Tomcat 10+ installed
- Catalina running

#### Step 1: Prepare WAR File

```bash
# Build WAR
./mvnw clean package

# Verify artifact
ls -lh target/caymen-document-api-1.war
```

#### Step 2: Deploy via Manager GUI

1. Open Tomcat Manager: `http://localhost:8080/manager/html`
2. Login with credentials from `tomcat-users.xml`
3. Scroll to **WAR file to deploy**
4. Choose file: `caymen-document-api-1.war`
5. Click **Deploy**

#### Step 3: Deploy via File System

```bash
# Copy WAR to webapps directory
cp target/caymen-document-api-1.war $CATALINA_HOME/webapps/

# Tomcat will auto-deploy and extract the WAR

# Verify deployment
ls -l $CATALINA_HOME/webapps/
# Look for: caymen-document-api-1/ directory
```

#### Step 4: Deploy via Manager API

```bash
# Using curl
curl -u admin:password -T target/caymen-document-api-1.war \
  "http://localhost:8080/manager/text/deploy?path=/caymen-document-api-1&update=true"

# Expected response: OK - Deployed application at context path /caymen-document-api-1
```

#### Verification

```bash
# Check API availability
curl http://localhost:8080/caymen-document-api-1/api/v1

# Check via Swagger
open http://localhost:8080/caymen-document-api-1/swagger-ui.html
```

---

### 3. Docker Deployment

Create a `Dockerfile` in the project root:

```dockerfile
FROM eclipse-temurin:17-jre-alpine

# Set working directory
WORKDIR /app

# Copy JAR and dependencies
COPY target/caymen-document-api-1.jar app.jar
COPY libs/ /app/libs/

# Expose port
EXPOSE 8081

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8081/actuator/health || exit 1

# Run application
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]
```

#### Build and Run

```bash
# Build Docker image
docker build -t cayman-doc-api:1.0 .

# Run container
docker run -d \
  --name cayman-doc-api \
  -p 8081:8081 \
  -v /path/to/application.yml:/app/config/application.yml \
  -e SPRING_CONFIG_LOCATION=/app/config/application.yml \
  cayman-doc-api:1.0

# Check logs
docker logs -f cayman-doc-api

# Verify health
curl http://localhost:8081/actuator/health
```

#### Docker Compose

Create `docker-compose.yml`:

```yaml
version: '3.8'

services:
  cayman-doc-api:
    image: cayman-doc-api:1.0
    container_name: cayman-doc-api
    ports:
      - "8081:8081"
    volumes:
      - ./config/application.yml:/app/config/application.yml:ro
      - ./logs:/app/logs
    environment:
      - SPRING_CONFIG_LOCATION=/app/config/application.yml
      - JAVA_OPTS=-Xms512m -Xmx1024m
    restart: unless-stopped
    healthcheck:
      test: ["CMD", "wget", "--spider", "-q", "http://localhost:8081/actuator/health"]
      interval: 30s
      timeout: 10s
      retries: 3
      start_period: 60s
    networks:
      - cayman-network

networks:
  cayman-network:
    driver: bridge
```

**Run with Docker Compose:**
```bash
docker-compose up -d
docker-compose ps
docker-compose logs -f cayman-doc-api
```

---

## 🔄 CI/CD with Jenkins

### Jenkins Pipeline Setup

#### Prerequisites
- Jenkins 2.400+ with Pipeline plugin
- Maven configured as Global Tool
- JDK 17 configured as Global Tool
- Git credentials configured

#### Create Jenkins Pipeline Job

1. **New Item** → **Pipeline** → Name: `cayman-doc-api-pipeline`
2. **Pipeline** section → **Definition**: Pipeline script from SCM
3. **SCM**: Git
4. **Repository URL**: Your Git repository URL
5. **Script Path**: `Jenkinsfile`

---

### Example Jenkinsfile

Create `Jenkinsfile` in project root:

```groovy
pipeline {
    agent any
    
    tools {
        maven 'Maven-3.9.11'
        jdk 'JDK-17'
    }
    
    environment {
        APP_NAME = 'caymen-document-api'
        VERSION = '1.0'
        DEPLOY_SERVER = 'production-server.example.com'
        DEPLOY_USER = 'deploy'
        DEPLOY_PATH = '/opt/cayman-doc-api'
    }
    
    stages {
        stage('Checkout') {
            steps {
                echo 'Checking out source code...'
                checkout scm
                sh 'git log -1 --pretty=format:"%h - %an: %s"'
            }
        }
        
        stage('Build') {
            steps {
                echo 'Building application...'
                sh './mvnw clean compile'
            }
        }
        
        stage('Test') {
            steps {
                echo 'Running tests...'
                sh './mvnw test'
            }
            post {
                always {
                    junit '**/target/surefire-reports/*.xml'
                    jacoco execPattern: '**/target/jacoco.exec'
                }
            }
        }
        
        stage('Package JAR') {
            when {
                expression { params.PACKAGE_TYPE == 'JAR' }
            }
            steps {
                echo 'Packaging as JAR...'
                sh './mvnw clean package -DskipTests'
            }
        }
        
        stage('Package WAR') {
            when {
                expression { params.PACKAGE_TYPE == 'WAR' }
            }
            steps {
                echo 'Packaging as WAR...'
                sh './mvnw clean package -DskipTests -Dpackaging=war'
            }
        }
        
        stage('Code Quality Analysis') {
            steps {
                echo 'Running SonarQube analysis...'
                withSonarQubeEnv('SonarQube') {
                    sh './mvnw sonar:sonar'
                }
            }
        }
        
        stage('Quality Gate') {
            steps {
                timeout(time: 5, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }
        
        stage('Build Docker Image') {
            when {
                expression { params.DEPLOY_TYPE == 'DOCKER' }
            }
            steps {
                echo 'Building Docker image...'
                sh "docker build -t ${APP_NAME}:${VERSION} ."
                sh "docker tag ${APP_NAME}:${VERSION} ${APP_NAME}:latest"
            }
        }
        
        stage('Push to Registry') {
            when {
                expression { params.DEPLOY_TYPE == 'DOCKER' }
            }
            steps {
                echo 'Pushing to Docker registry...'
                withCredentials([usernamePassword(credentialsId: 'docker-registry', usernameVariable: 'USER', passwordVariable: 'PASS')]) {
                    sh "echo $PASS | docker login -u $USER --password-stdin"
                    sh "docker push ${APP_NAME}:${VERSION}"
                    sh "docker push ${APP_NAME}:latest"
                }
            }
        }
        
        stage('Deploy to JBoss') {
            when {
                expression { params.DEPLOY_TYPE == 'JBOSS' }
            }
            steps {
                echo 'Deploying to JBoss...'
                script {
                    def artifactPath = params.PACKAGE_TYPE == 'WAR' ? 
                        "target/${APP_NAME}-1.war" : "target/${APP_NAME}-1.jar"
                    
                    sshagent(['deployment-ssh-key']) {
                        sh """
                            scp ${artifactPath} ${DEPLOY_USER}@${DEPLOY_SERVER}:/tmp/
                            ssh ${DEPLOY_USER}@${DEPLOY_SERVER} '
                                \$JBOSS_HOME/bin/jboss-cli.sh --connect "deploy /tmp/${APP_NAME}-1.war --force"
                            '
                        """
                    }
                }
            }
        }
        
        stage('Deploy to Tomcat') {
            when {
                expression { params.DEPLOY_TYPE == 'TOMCAT' }
            }
            steps {
                echo 'Deploying to Tomcat...'
                script {
                    def warPath = "target/${APP_NAME}-1.war"
                    
                    sshagent(['deployment-ssh-key']) {
                        sh """
                            scp ${warPath} ${DEPLOY_USER}@${DEPLOY_SERVER}:${DEPLOY_PATH}/webapps/
                        """
                    }
                }
            }
        }
        
        stage('Deploy JAR') {
            when {
                expression { params.DEPLOY_TYPE == 'JAR' }
            }
            steps {
                echo 'Deploying JAR to server...'
                script {
                    def jarPath = "target/${APP_NAME}-1.jar"
                    
                    sshagent(['deployment-ssh-key']) {
                        sh """
                            scp ${jarPath} ${DEPLOY_USER}@${DEPLOY_SERVER}:${DEPLOY_PATH}/
                            ssh ${DEPLOY_USER}@${DEPLOY_SERVER} '
                                sudo systemctl stop cayman-doc-api
                                sudo systemctl start cayman-doc-api
                            '
                        """
                    }
                }
            }
        }
        
        stage('Health Check') {
            steps {
                echo 'Performing health check...'
                script {
                    sleep(time: 30, unit: 'SECONDS')
                    
                    def response = sh(
                        script: "curl -s -o /dev/null -w '%{http_code}' http://${DEPLOY_SERVER}:8081/actuator/health",
                        returnStdout: true
                    ).trim()
                    
                    if (response != '200') {
                        error("Health check failed with status: ${response}")
                    }
                    echo "Health check passed!"
                }
            }
        }
        
        stage('Smoke Tests') {
            steps {
                echo 'Running smoke tests...'
                sh """
                    curl -f http://${DEPLOY_SERVER}:8081/api/v1 || exit 1
                    curl -f http://${DEPLOY_SERVER}:8081/api/security || exit 1
                """
            }
        }
    }
    
    post {
        success {
            echo 'Pipeline succeeded!'
            emailext(
                subject: "✅ SUCCESS: ${env.JOB_NAME} [${env.BUILD_NUMBER}]",
                body: """
                    <p>Build succeeded!</p>
                    <p><b>Job:</b> ${env.JOB_NAME}</p>
                    <p><b>Build Number:</b> ${env.BUILD_NUMBER}</p>
                    <p><b>Build URL:</b> <a href='${env.BUILD_URL}'>${env.BUILD_URL}</a></p>
                """,
                to: 'team@example.com',
                mimeType: 'text/html'
            )
        }
        failure {
            echo 'Pipeline failed!'
            emailext(
                subject: "❌ FAILURE: ${env.JOB_NAME} [${env.BUILD_NUMBER}]",
                body: """
                    <p>Build failed!</p>
                    <p><b>Job:</b> ${env.JOB_NAME}</p>
                    <p><b>Build Number:</b> ${env.BUILD_NUMBER}</p>
                    <p><b>Build URL:</b> <a href='${env.BUILD_URL}'>${env.BUILD_URL}</a></p>
                    <p><b>Console:</b> <a href='${env.BUILD_URL}console'>${env.BUILD_URL}console</a></p>
                """,
                to: 'team@example.com',
                mimeType: 'text/html'
            )
        }
        always {
            cleanWs()
        }
    }
    
    parameters {
        choice(
            name: 'PACKAGE_TYPE',
            choices: ['JAR', 'WAR'],
            description: 'Select package type'
        )
        choice(
            name: 'DEPLOY_TYPE',
            choices: ['JAR', 'JBOSS', 'TOMCAT', 'DOCKER'],
            description: 'Select deployment type'
        )
    }
}
```

---

### Environment Configuration

#### Configure Jenkins Credentials

1. **Manage Jenkins** → **Credentials** → **Add Credentials**
2. Add the following credentials:

| ID | Type | Description |
|----|------|-------------|
| `docker-registry` | Username/Password | Docker registry credentials |
| `deployment-ssh-key` | SSH Username with private key | SSH key for deployment servers |
| `sonarqube-token` | Secret text | SonarQube authentication token |

#### Configure Global Tools

1. **Manage Jenkins** → **Global Tool Configuration**
2. Add:
    - **Maven**: Name: `Maven-3.9.11`, Version: 3.9.11
    - **JDK**: Name: `JDK-17`, JAVA_HOME: Path to JDK 17

---

## 🧪 Testing

### Run Unit Tests

```bash
# Run all tests
./mvnw test

# Run specific test class
./mvnw test -Dtest=CaymenDocumentApiApplicationTests

# Run with coverage
./mvnw test jacoco:report

# View coverage report
open target/site/jacoco/index.html
```

### Run Integration Tests

```bash
# Run integration tests
./mvnw verify

# Run with specific profile
./mvnw verify -Pintegration-tests
```

### Manual API Testing

#### Using cURL

```bash
# Health check
curl http://localhost:8081/api/v1

# Get session ID
curl http://localhost:8081/api/v1/sessionId

# Download document
curl -O -J http://localhost:8081/api/v1/download/12345

# Fetch as Base64
curl http://localhost:8081/api/v1/fetchDoc/base64/12345

# Generate RSA keys
curl http://localhost:8081/api/security/keys/rsa

# AES encryption
curl -X POST http://localhost:8081/api/security/aes/encrypt \
  -H "Content-Type: application/json" \
  -d '{"text": "Hello World"}'
```

#### Using Postman

Import the Swagger/OpenAPI specification into Postman:
1. Open Postman
2. Import → Link: `http://localhost:8081/v3/api-docs`
3. Create a new collection from the imported spec

---

## 📊 Monitoring & Health Checks

### Spring Boot Actuator Endpoints

The application includes Spring Boot Actuator for monitoring:

```bash
# Health check
curl http://localhost:8081/actuator/health

# Application info
curl http://localhost:8081/actuator/info

# Metrics
curl http://localhost:8081/actuator/metrics

# Specific metric (e.g., JVM memory)
curl http://localhost:8081/actuator/metrics/jvm.memory.used
```

### Custom Health Indicators

Add custom health checks in `AppConfig.java` or create a separate health indicator:

```java
@Component
public class OmniDocsHealthIndicator implements HealthIndicator {
    
    @Override
    public Health health() {
        // Check OmniDocs connectivity
        try {
            // Perform health check
            return Health.up().withDetail("omnidocs", "available").build();
        } catch (Exception e) {
            return Health.down().withDetail("error", e.getMessage()).build();
        }
    }
}
```

### Logging Configuration

Logs are configured in `src/main/resources/logback-spring.xml`:

- **Console**: `TRACE` level for development
- **File**: Rolling file appender with daily rotation
- **Path**: `logs/cayman-doc-api.log`

**View logs:**
```bash
# Tail logs in real-time
tail -f logs/cayman-doc-api.log

# Search for errors
grep "ERROR" logs/cayman-doc-api.log

# View last 100 lines
tail -n 100 logs/cayman-doc-api.log
```

---

## 🔒 Security Considerations

### 1. Property Encryption
- ✅ Always encrypt sensitive properties (passwords, API keys)
- ✅ Use strong AES-256 keys (32 characters)
- ✅ Rotate keys periodically
- ❌ Never commit unencrypted secrets to version control

### 2. Rate Limiting
- ✅ Adjust capacity based on expected traffic
- ✅ Monitor rate limit logs for abuse patterns
- ✅ Consider IP whitelisting for trusted clients
- ✅ Implement authentication for sensitive endpoints

### 3. HTTPS/TLS
- ✅ Use HTTPS in production (configure SSL in `application.yml`)
- ✅ Use valid SSL certificates (Let's Encrypt, commercial CA)
- ❌ Never use self-signed certificates in production

### 4. Authentication & Authorization
- ✅ Implement JWT or OAuth2 for API authentication
- ✅ Use role-based access control (RBAC)
- ✅ Secure OmniDocs credentials with property encryption

### 5. Input Validation
- ✅ Validate all user inputs
- ✅ Use Bean Validation (JSR-380) annotations
- ✅ Sanitize data before processing

---

## 🔧 Troubleshooting

### Problem: Session Expired Error

**Symptoms:**
```json
{
  "status": 401,
  "errorCode": "ERR_401",
  "message": "Session expired"
}
```

**Solutions:**
1. Call `/api/v1/sessionId` to get a new session
2. Check OmniDocs JTS connectivity (jtsIP:jtsPort)
3. Verify cabinet credentials are correct
4. Check OmniDocs server status

---

### Problem: Document Not Found

**Symptoms:**
```json
{
  "status": 404,
  "errorCode": "ERR_404",
  "message": "Document not found"
}
```

**Solutions:**
1. Verify `docIndex` is correct and exists in OmniDocs
2. Check cabinet name configuration
3. Ensure document is not archived or deleted
4. Verify session is valid

---

### Problem: Property Decryption Failure

**Symptoms:**
- Application fails to start
- Log message: "Failed to decrypt property"

**Solutions:**
1. Verify `my.security.aes-secret` matches encryption key
2. Check `ENC()` pattern is correct in `application.yml`
3. Re-encrypt properties using `PropertyEncryptionUtil`
4. Ensure AES secret is exactly 32 characters

---

### Problem: Rate Limit Exceeded

**Symptoms:**
```json
{
  "status": 429,
  "errorCode": "ERR_429",
  "message": "Too many requests"
}
```

**Solutions:**
1. Increase `rate.limit.capacity` in configuration
2. Implement request batching in client
3. Use exponential backoff retry strategy
4. Check for DDoS or abuse patterns

---

### Problem: OmniDocs Connection Failed

**Symptoms:**
```json
{
  "status": 502,
  "errorCode": "ERR_502",
  "message": "External service error"
}
```

**Solutions:**
1. Verify OmniDocs server is running
2. Check network connectivity (ping siteIP)
3. Verify ports are open (sitePort, jtsPort)
4. Review OmniDocs server logs
5. Validate cabinet credentials


---

### Developer
**Tarun Vishwakarma**  
📧 Email: [tarun.vishwakarma@newgensoft.com](mailto:tarun.vishwakarma@newgensoft.com)  
🏢 Organization: Newgen Software Technologies Ltd.

### Resources
- **Swagger UI**: `http://localhost:8081/swagger-ui.html`
- **API Docs**: `http://localhost:8081/v3/api-docs`
- **Health Check**: `http://localhost:8081/actuator/health`
---

<div align="center">

**Built using Spring Boot**

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.6-brightgreen?style=flat-square&logo=spring)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-17-orange?style=flat-square&logo=java)](https://www.oracle.com/java/)
[![Maven](https://img.shields.io/badge/Maven-3.9.11-C71A36?style=flat-square&logo=apache-maven)](https://maven.apache.org/)

</div>
