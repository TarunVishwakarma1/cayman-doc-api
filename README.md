# Cayman Document API

Spring Boot REST API integrating with Newgen OmniDocs for document retrieval, plus cryptography utilities (AES, RSA, ECDSA).

## Features

- Document management with OmniDocs
  - Connect/disconnect cabinet session
  - Fetch documents by index as base64 or bytes
  - Content-Type mapping by file extension
- Security and cryptography
  - AES-GCM encryption/decryption with a global key
  - RSA encryption/decryption with provided keys
  - EC key pair generation (secp256r1)
  - ECDSA signing and verification
- API resilience
  - Token-bucket rate limiting filter
  - Centralized exception handling with structured error responses
  - Property decryption for encrypted configuration values

## Endpoints

Base paths: `/api/v1` (documents), `/api/security` (crypto)

- Documents (`/api/v1`)
  - GET `/` — Health check
  - GET `/sessionId` — Get OmniDocs session id
  - GET `/download/{docIndex}` — Download document as attachment
  - GET `/fetchDoc/{base64}/{docIndex}` — Inline fetch; when `base64` equals `base64`, returns base64, otherwise bytes

- Security (`/api/security`)
  - GET `/` — Welcome/health
  - GET `/keys/rsa` — Generate RSA key pair
  - GET `/keys/ec` — Generate EC key pair (secp256r1)
  - POST `/aes/encrypt` — AES encrypt text
  - POST `/aes/decrypt` — AES decrypt text
  - POST `/rsa/encrypt` — RSA encrypt text (requires public key)
  - POST `/rsa/decrypt` — RSA decrypt text (requires private key)
  - POST `/sign` — ECDSA sign data (requires EC private key)
  - POST `/verify` — ECDSA verify signature (requires EC public key)

## Configuration

- Global AES secret: `my.security.aes-secret` (32+ chars recommended)
- Cabinet properties under `newgen.cayman.connect.cabinet.*`
- Encrypted values supported as `ENC(...)` and auto-decrypted at startup

## Generate Javadocs

- Build API docs site:

```bash
./mvnw javadoc:javadoc
open target/site/apidocs/index.html
```

- Attach javadoc jar during package:

```bash
./mvnw -DskipTests package
```

Javadoc jar will be produced via the Maven Javadoc Plugin.

## Tech Stack

- Java 17, Spring Boot 3
- Caffeine cache (rate limit buckets)
- Newgen DMS libraries (local jars)

## Author

- Name: Tarun Vishwakarma
- Email: tarun.vishwakarma@newgensoft.com
- Username: tarun.vishwakarma
