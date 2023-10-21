# Plan To Plate

### General Info 
Spring Boot Rest API for application to manage diet.
Deployed on Google Cloud App Engine. Swagger UI documentation:
https://plantoplate.lm.r.appspot.com/swagger-ui/index.html#/


### Technologies
Project is created with:
* Spring Boot: 2.7
* Hibernate/JPA
* DataBase: Postgres, H2
* Test: Mockito, JUnit


### SetUp

Requirement to run application locally:
* Postgres DB instance (you can use Docker - https://hub.docker.com/_/postgres)
* Java 11
* Configured email address to send message during registration/reset password 
  (HOW TO configure gmail - https://stackoverflow.com/questions/26594097/javamail-exception-javax-mail-authenticationfailedexception-534-5-7-9-applicatio/72592946#72592946)


To run application locally:

* change configuration properties to your own in file src/main/resources/application.properties

```
    # Databa configuration
    spring.datasource.url=
    spring.datasource.username=
    spring.datasource.password=
    spring.datasource.driverClassName=org.postgresql.Driver
    spring.h2.console.enabled=true

    spring.jpa.show-sql=true
    spring.jpa.hibernate.ddl-auto=validate


    #flyway configuration
    spring.flyway.user=
    spring.flyway.password=
    spring.flyway.schemas=
    spring.flyway.url=
    spring.flyway.driver-class-name=org.postgresql.Driver
    spring.flyway.locations=filesystem:src/main/resources/db/migration


    # secret to the JWT token
    jwt.secret = 
    jwt.expirationMills = 

    logging.level.root = info


    #mail params
    spring.mail.host=
    spring.mail.port=
    spring.mail.protocol=smtps
    spring.mail.username=
    spring.mail.password=

```

* run Spring Boot application

```
mvn clean install -U
mvn spring-boot:run
```