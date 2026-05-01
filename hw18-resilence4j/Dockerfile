FROM maven:3.9.6-eclipse-temurin-17 AS build

ENV PROJECT_DIR=/opt/project
WORKDIR $PROJECT_DIR

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests


FROM bellsoft/liberica-openjdk-alpine-musl:17.0.10

WORKDIR /app

COPY --from=build /opt/project/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]