# ---- Build Stage ----
FROM gradle:8.13-jdk17 AS build

# Copy source code
COPY . /home/gradle/project
WORKDIR /home/gradle/project

ENV GRADLE_USER_HOME=/home/gradle/.gradle
RUN gradle clean build --no-daemon --no-build-cache

# ---- Run Stage ----
FROM eclipse-temurin:17-jdk
EXPOSE 8888
COPY --from=build /home/gradle/project/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]