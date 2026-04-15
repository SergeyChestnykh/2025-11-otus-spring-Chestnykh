FROM bellsoft/liberica-openjdk-alpine-musl:17.0.10
COPY /target/docker-hw.jar /app/app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
