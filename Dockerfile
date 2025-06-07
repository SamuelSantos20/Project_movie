FROM maven:3.9.6 AS build
LABEL authors="Samuel"

COPY . /app
WORKDIR /app

RUN mvn clean package -DskipTests

FROM openjdk:21-jdk-slim
WORKDIR /app
COPY --from=build /app/target/Project-Movie.jar /app/Project-Movie.jar

CMD ["java", "-jar", "Project-Movie.jar"]
