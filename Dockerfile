FROM eclipse-temurin:17-jdk-alpine
EXPOSE 8080
ADD target/spring-boot-docker.jar spring-boot-docker.jar
ENTRYPOINT ["java","-jar","/spring-boot-docker.jar"]

#FROM openjdk:17-jdk-alpine
#MAINTAINER prakhar
#COPY target/spring-boot-docker.jar spring-boot-docker.jar
#ENTRYPOINT ["java","-jar","/spring-boot-docker.jar"]