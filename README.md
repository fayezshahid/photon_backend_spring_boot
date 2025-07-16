# Photon Backend
Spring Boot-based backend API for the Photon photo management platform. Provides secure RESTful endpoints for authentication, photo management, and user operations.

## 🛠️ Technologies Used
- **Spring Boot 3** - Backend framework
- **Spring Security 6** - Authentication and authorization
- **Spring Data JPA** - Database operations
- **PostgreSQL** - Database management
- **JWT (JSON Web Tokens)** - Stateless authentication

## 🚀 Getting Started

### Prerequisites
1. **Install Java 17 or higher**
   - Download from [https://adoptium.net/](https://adoptium.net/)
   - Verify installation: `java -version`

2. **Install PostgreSQL**
   - Download from [https://www.postgresql.org/download/](https://www.postgresql.org/download/)
   - Or use pgAdmin for database management

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/fayezshahid/photon_backend_spring_boot.git
   cd photon_backend_spring_boot
   ```

2. **Database Setup**
   - Create a new PostgreSQL database for your project
   - Update `src/main/resources/application.properties`:
   
   ```properties
   # Database Configuration
   spring.datasource.url=jdbc:postgresql://localhost:5432/your_db_name
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   spring.datasource.driver-class-name=org.postgresql.Driver
   
   # JPA Configuration
   spring.jpa.hibernate.ddl-auto=update
   spring.jpa.show-sql=true
   spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
   
   # JWT Configuration
   photon.app.jwtSecret=photonSecretKey
   photon.app.jwtExpirationMs=86400000
   
   # Server Configuration
   server.port=8080
   
   # CORS Configuration
   photon.app.cors.allowed-origins=http://localhost:4200
   ```

3. **Install Dependencies and Run**
   ```bash
   # Using Maven
   ./mvnw clean install
   ./mvnw spring-boot:run
   
   # Or using Gradle
   ./gradlew build
   ./gradlew bootRun
   ```

4. **Access the API**
   - Backend API: `http://localhost:8080`

## 🎯 Features
- **JWT Authentication**: Secure token-based authentication system
- **RESTful APIs**: Clean REST endpoints for all operations
- **Database Integration**: PostgreSQL with JPA/Hibernate
- **Security**: Spring Security 6 with JWT protection
- **Input Validation**: Request validation using Spring Boot Validation
- **CORS Support**: Cross-origin resource sharing configuration

## 📁 Project Structure
```
photon_backend_spring_boot/
│
├── src/main/java/
│   └── com/photon/
│       ├── controllers/    # REST controllers
│       ├── services/       # Business logic
│       ├── repositories/   # Data access layer
│       ├── entities/       # Entity classes
│       ├── dtos/          # Data transfer objects
│       ├── config/        # Configuration classes
│       └── security/      # Security configuration
├── src/main/resources/
│   ├── application.properties
│   └── static/           # Static files
├── src/test/             # Unit and integration tests
└── pom.xml              # Maven dependencies
```

## 🔐 Security Features
- **JWT Authentication**: Stateless authentication using JSON Web Tokens
- **Password Encryption**: BCrypt password hashing
- **CORS Configuration**: Cross-origin resource sharing setup
- **Input Validation**: Request validation using Spring Boot Validation
- **SQL Injection Prevention**: JPA/Hibernate protection
- **Role-based Access Control**: Spring Security role management

## 📚 API Endpoints

### Authentication
- `POST /api/auth/login` - User login
- `POST /api/auth/register` - User registration
- `POST /api/auth/refresh` - Refresh JWT token

### Photo Management
- `GET /api/photos` - Get all photos
- `POST /api/photos` - Upload new photo
- `GET /api/photos/{id}` - Get photo by ID
- `PUT /api/photos/{id}` - Update photo
- `DELETE /api/photos/{id}` - Delete photo
- `POST /api/photos/{id}/favorite` - Toggle favorite status
- `POST /api/photos/{id}/archive` - Toggle archive status

### User Management
- `GET /api/users/profile` - Get user profile
- `PUT /api/users/profile` - Update user profile

## 🧪 Testing
```bash
# Run unit tests
./mvnw test

# Run integration tests
./mvnw verify

# Run specific test class
./mvnw test -Dtest=YourTestClass
```

## 🚀 Deployment
1. **Build the application**
   ```bash
   ./mvnw clean package
   ```

2. **Run the JAR file**
   ```bash
   java -jar target/photon_backend_spring_boot-1.0.0.jar
   ```

3. **Or deploy to cloud platforms** like AWS, Heroku, or Docker

## 🐳 Docker Support
```dockerfile
FROM openjdk:17-jdk-slim
COPY target/photon_backend_spring_boot-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

## 📝 Configuration
Key configuration properties in `application.properties`:

```properties
# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/photon_db
spring.datasource.username=your_username
spring.datasource.password=your_password

# JWT
photon.app.jwtSecret=your_secret_key
photon.app.jwtExpirationMs=86400000

# File Upload
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB

# Logging
logging.level.com.photon=DEBUG
```

## 📝 Contributing
1. Fork the repository
2. Create a feature branch: `git checkout -b feature-name`
3. Commit your changes: `git commit -m 'Add feature'`
4. Push to the branch: `git push origin feature-name`
5. Submit a pull request