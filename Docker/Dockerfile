# Stage 1: Build the application
FROM amazoncorretto:17 AS build

WORKDIR /App

COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .
COPY src src

# Fix Windows-style line endings using sed. dos2unix not available in the base image. Apt missing too.
RUN sed -i 's/\r$//' gradlew

RUN chmod +x gradlew
RUN ./gradlew clean bootJar


# Stage 2: Run the application
FROM amazoncorretto:17

WORKDIR /App

COPY --from=build /App/build/libs/*.jar app.jar

COPY Docker/entrypoint.sh /App/entrypoint.sh

RUN chmod +x /App/entrypoint.sh

EXPOSE 8082

ENTRYPOINT ["/App/entrypoint.sh"]
