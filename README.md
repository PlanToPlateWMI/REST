# Plan To Plate

## General Info 
Spring Boot Rest API for application to manage diet.
Deployed on Google Cloud App Engine. Swagger UI documentation:
https://plantoplate.lm.r.appspot.com/swagger-ui/index.html#/


## Technologies 
<img width="36" src="https://user-images.githubusercontent.com/25181517/117201156-9a724800-adec-11eb-9a9d-3cd0f67da4bc.png" alt="Java" title="Java"/> 
<img width="32" src="https://user-images.githubusercontent.com/25181517/183891303-41f257f8-6b3d-487c-aa56-c497b880d0fb.png" alt="Spring Boot" title="Spring Boot"/> 
<img width="34" src="https://user-images.githubusercontent.com/25181517/117207493-49665200-adf4-11eb-808e-a9c0fcc2a0a0.png" alt="Hibernate" title="Hibernate"/> 
<img width="33" src="https://user-images.githubusercontent.com/25181517/117208740-bfb78400-adf5-11eb-97bb-09072b6bedfc.png" alt="PostgreSQL" title="PostgreSQL"/>

Project is created with:
* Spring Boot: 2.7
* Hibernate/JPA
* DataBase: Postgres, H2
* Test: Mockito, JUnit


## SetUp

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