FROM amazoncorretto:21-alpine-jdk as build

WORKDIR /app

COPY . .

RUN chmod +x ./gradlew
RUN ./gradlew bootJar

FROM amazoncorretto:21-alpine-jdk
WORKDIR /app

RUN addgroup -S spring && adduser -S spring -G spring
COPY --from=build --chown=spring:spring /app/build/libs/*.jar app.jar
USER spring:spring

ENTRYPOINT ["java", "-jar", "app.jar"]