FROM amazoncorretto:21-alpine-jdk as builder

WORKDIR /app

COPY . .
RUN chmod +x ./gradlew
RUN ./gradlew bootJar

FROM amazoncorretto:21-alpine-jdk
WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]