# WARP.md

This file provides guidance to WARP (warp.dev) when working with code in this repository.

## Project Overview

Cayman Document API is a Spring Boot 3.5.6 REST API that integrates with Newgen OmniDocs for document management and provides cryptographic operations (AES/RSA encryption, digital signatures, key generation).

**Technology Stack:**
- Java 17
- Spring Boot 3.5.6
- Maven 3.9.11
- Newgen OmniDocs integration (via custom JARs in `libs/`)

## Common Commands

### Build & Package

```bash
# Clean and compile
./mvnw clean compile

# Run tests
./mvnw test

# Package as JAR (standalone with embedded Tomcat)
./mvnw clean package

# Package as WAR (for deployment to JBoss/WildFly/Tomcat)
./mvnw clean package -Dpackaging=war

# Generate Javadoc
./mvnw javadoc:javadoc
```

### Running the Application

```bash
# Run locally (starts on port 8081)
./mvnw spring-boot:run

# Run the packaged JAR
java -jar target/caymen-document-api-1.jar
```

### Testing & Verification

```bash
# Run a specific test class
./mvnw test -Dtest=CaymenDocumentApiApplicationTests

# Skip tests during build
./mvnw clean package -DskipTests

# Run with specific Spring profile
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

## Architecture & Code Structure

### Layered Architecture

The application follows a standard layered architecture:

```
controller → service → implementation → external services (OmniDocs)
                ↓
              model (dto/dao/enums)
```

**Key packages:**
- `controller/` - REST endpoints (`DocumentController`, `SecurityController`)
- `service/` - Business logic interfaces
- `implementation/` - Service implementations (`DocumentImpl`, `EncryptionServiceImpl`, etc.)
- `model/dto/` - Request/Response DTOs (Data Transfer Objects)
- `model/dao/` - Data Access Objects for OmniDocs integration
- `model/enums/` - Enumerations (e.g., `DocumentType` for content-type mapping)
- `interfaces/` - Service interface definitions
- `config/` - Spring configuration and filters
- `exception/` - Custom exception hierarchy
- `utils/` - Utility classes for encryption/decryption

### Critical Components

#### 1. Property Encryption System
- `PropertyDecryptionInitializer` runs at startup before Spring context initialization
- Decrypts properties with pattern `ENC(...)` in `application.yml`
- Uses AES-256 encryption with key from `my.security.aes-secret`
- Only properties under `newgen.cayman.connect.cabinet.*` are decrypted
- Use `PropertyEncryptionUtil` to encrypt sensitive values before adding to config

#### 2. Rate Limiting
- `RateLimitFilter` implements Token Bucket algorithm (via bucket4j)
- Per-IP address limiting with Caffeine cache (10-minute expiration, max 10K IPs)
- Configuration: `rate.limit.capacity` and `rate.limit.duration.minutes` in `application.yml`
- Throws `TooManyRequestsException` (HTTP 429) when limit exceeded

#### 3. OmniDocs Integration
- `DocumentImpl` manages session and document retrieval via REST
- `ConnectCabinet` establishes cabinet connection (uses custom `odweb.jar`, `jtssessionbean.jar`, `ejbclient.jar`)
- `GlobalSessionService` manages shared session ID
- Session must be obtained before document operations
- Document retrieval endpoint: `/OmniDocsRestWS/rest/services/getDocumentJSON`

### API Endpoints

**Document Management** (`/api/v1`)
- `GET /api/v1` - Health check
- `GET /api/v1/sessionId` - Get OmniDocs session ID
- `GET /api/v1/download/{docIndex}` - Download document as attachment
- `GET /api/v1/fetchDoc/{format}/{docIndex}` - Fetch document (`format` = `base64` or `bytes`)

**Security/Cryptography** (`/api/security`)
- `GET /api/security/keys/rsa` - Generate RSA key pair (2048-bit)
- `GET /api/security/keys/ec` - Generate EC key pair
- `POST /api/security/aes/encrypt` - AES-256 encrypt (uses global key from config)
- `POST /api/security/aes/decrypt` - AES-256 decrypt
- `POST /api/security/rsa/encrypt` - RSA encrypt with public key
- `POST /api/security/rsa/decrypt` - RSA decrypt with private key
- `POST /api/security/sign` - Sign data (SHA256withRSA)
- `POST /api/security/verify` - Verify signature

### Exception Handling

All exceptions extend a base hierarchy and are handled by `GlobalExceptionHandler`:
- `MissingParameterException` → 400 Bad Request
- `InvalidParameterException` → 400 Bad Request
- `SessionExpiredException` → 401 Unauthorized
- `DocumentNotFoundException` → 404 Not Found
- `TooManyRequestsException` → 429 Too Many Requests
- `CabinetConnectionException` → 502 Bad Gateway
- `ExternalServiceException` → 502 Bad Gateway

## Configuration

### application.yml Structure

```yaml
server:
  port: 8081

logging:
  config: classpath:logback-spring.xml
  level:
    root: INFO
    com.newgen.cig.cayman: INFO

newgen.cayman.connect.cabinet:
  username: ENC(...)  # Encrypted with AES
  password: ENC(...)  # Encrypted with AES
  cabinetName: worcuat
  siteIP: 127.0.0.1
  sitePort: 8080
  jtsIP: 127.0.0.1
  jtsPort: 3333
  # ... other OmniDocs connection properties

rate.limit:
  capacity: 100
  duration.minutes: 1

my.security:
  aes-secret: [32-char-secret]  # Used for property encryption
```

### Adding Encrypted Properties

1. Use `PropertyEncryptionUtil.main()` to encrypt sensitive values
2. Add encrypted value to `application.yml` with `ENC(...)` pattern
3. Property will be decrypted at startup by `PropertyDecryptionInitializer`

### External Dependencies

The project requires custom Newgen JARs in the `libs/` directory:
- `odweb.jar` - OmniDocs web services client
- `jtssessionbean.jar` - JTS session management
- `ejbclient.jar` - EJB client for OmniDocs
- `log4j.jar` (v1.2.14) - Logging (legacy)

These are referenced as system-scoped dependencies in `pom.xml` with `<systemPath>${lib.path}/filename.jar</systemPath>`.

## Development Guidelines

### Logging Standards
- Use structured logging with SLF4J
- Log levels configured in `logback-spring.xml`
- TRACE: Method entry/exit
- DEBUG: Detailed diagnostic info (lengths, sizes, intermediate values)
- INFO: Key operations and milestones
- WARN: Recoverable issues (rate limit, validation failures)
- ERROR: Exceptions and errors with stack traces

### Adding New Document Types
- Update `DocumentType` enum in `model/enums/` with MIME type mapping
- Follows pattern: `PDF("application/pdf"), TIFF("image/tiff"), ...`
- Used by `DocumentController` to set `Content-Type` header

### Adding New Cryptographic Operations
- Implement in `utils/` package (e.g., `Encryption`, `Decryption`, `KeyPair`)
- Create service interface in `interfaces/`
- Create service implementation in `implementation/`
- Add controller endpoint in `SecurityController`
- Follow existing pattern for request/response DTOs

### Error Handling
- Always throw domain-specific exceptions (not generic `RuntimeException`)
- Use appropriate HTTP status codes via exception mapping
- Include descriptive error messages for API consumers
- Log errors with context (docIndex, sessionId, etc.)

## Swagger/OpenAPI

- API documentation available at: `http://localhost:8081/swagger-ui.html`
- OpenAPI spec configuration in `SwaggerConfig`
- All endpoints are documented with `@Operation`, `@Parameter` annotations

## Deployment Options

### Standalone JAR (Embedded Tomcat)
```bash
./mvnw clean package
java -jar target/caymen-document-api-1.jar
```

### WAR to Application Server
1. Modify `pom.xml` packaging to `<packaging>war</packaging>`
2. Build: `./mvnw clean package`
3. Deploy `target/caymen-document-api-1.war` to:
   - JBoss/WildFly: Copy to `standalone/deployments/`
   - Tomcat: Copy to `webapps/`

### Important Deployment Notes
- Ensure custom JARs in `libs/` directory are accessible
- Configure `application.yml` with correct OmniDocs connection details
- Set secure `my.security.aes-secret` for production
- Verify rate limit settings appropriate for environment
- Health check endpoint: `/actuator/health`

## Troubleshooting

**Session Expired Errors:**
- Call `/api/v1/sessionId` first to establish session
- Session managed by `GlobalSessionService` (in-memory, shared across requests)
- If persistent issues, check OmniDocs JTS connectivity (jtsIP:jtsPort)

**Document Not Found:**
- Verify `docIndex` is correct
- Check cabinet connection and credentials
- Validate document exists in OmniDocs cabinet

**Property Decryption Failures:**
- Verify `my.security.aes-secret` is set correctly
- Check encrypted values use correct `ENC(...)` pattern
- Review startup logs for decryption errors (logged during initialization)

**Rate Limit Issues:**
- Adjust `rate.limit.capacity` and `rate.limit.duration.minutes`
- Check IP detection with proxies (uses `X-Forwarded-For` header)
- Caffeine cache auto-expires after 10 minutes of inactivity
