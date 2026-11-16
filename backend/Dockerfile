FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn -B -DskipTests package

FROM eclipse-temurin:17-jre

WORKDIR /app

COPY --from=build /app/target/employee-management-system-0.0.1-SNAPSHOT.jar app.jar

ENV SPRING_PROFILES_ACTIVE=prod
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=60.0"

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]