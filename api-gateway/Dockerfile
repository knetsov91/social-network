FROM amazoncorretto:21-alpine-jdk as builder

WORKDIR /app

COPY . .
RUN chmod +x ./gradlew
RUN ./gradlew bootJar

FROM amazoncorretto:21-alpine-jdk
WORKDIR /app

RUN addgroup -S spring && adduser -S spring -G spring
COPY --from=builder --chown=spring:spring /app/build/libs/*.jar app.jar
USER spring:spring

ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]