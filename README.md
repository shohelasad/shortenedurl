# URL shortened


## Prerequisites

* Docker 19.03.x (for production level readiness)
* Docker Compose 1.25.x

## Used Technologies
* Java 11
* Spring Boot 2.7.3
* Postgresql (for production level readiness)
* Spring Boot Validation
* Spring Boot Jpa
* Spring Boot Actuator
* Lombok


# How to run

### Run only test cases 

```sh
mvn test
```

### Package the application as a JAR file

```sh
mvn clean install -DskipTests
```

### Run the Spring Boot application and PostgreSQL with Docker Compose
(for docker build change the database config postgresql in application.properties)

```sh
docker-compose up -d --build
```

## Note
* Database PostgresSQL is configured for docker container
* Sample Unit tests are implemented for controller, service and repository
* I mostly cover success test cases with some corner cases for the time limits

## TODO
* Implement caching and load balancer
* Improve test coverage
* Open api documentation



