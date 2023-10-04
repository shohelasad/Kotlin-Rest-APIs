# News Article APIs with JWT based Auth

## Used Technologies

* Java 17
* Kotlin
* Spring Boot 3.0.2
* Postgresql (for production level readiness)
* H2 database for test scope
* Spring Boot JPA
* Flyway database migration
* Docker
* Open API for API definition and API testing

## Strategy

* Restful APi design  with Implementing JWT token-based authentication for user registration and authentication endpoints.
* Enhance Article APIs' security by enforcing JWT bearer token authentication for authorized access.
* When an editor creates an article, the editor's identity is automatically linked as the author, and this information is obtained through the JwtToken.
* When an editor updates an article, the editor is included in the list of authors for that article.
* Implement Spring's ControllerAdvice to centralize exception handling, streamlining the management and upkeep of error-handling logic in a single location.

# How to run
### Run only test cases

```sh
./gradlew test -Dspring.profiles.active=test
```

### Package the application as a JAR file

```sh
./gradlew clean build -x test
```

### Run the Spring Boot application and PostgreSQL with Docker Compose
(for docker build change the database config postgresql in application.properties)

```sh
docker-compose up -d --build
```

For clean up Docker, if required

```sh
docker system prune
```

## Production ready

* Database PostgresSQL is configured for dockerige
* Flyway in implement for data migration

## API Definition

OpenAPI implemented for API definition
* http://localhost:9090/api-docs
* http://localhost:9090/swagger-ui/index.html