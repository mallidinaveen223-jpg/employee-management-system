# Build React frontend
FROM node:22-alpine AS frontend-build

WORKDIR /app/frontend

COPY frontend/package*.json ./
RUN npm ci

COPY frontend/ ./
RUN npm run build

# Build Spring Boot backend
FROM maven:3.9-eclipse-temurin-17 AS backend-build

WORKDIR /app/backend

COPY backend/pom.xml ./
COPY backend/src ./src

COPY --from=frontend-build /app/frontend/dist/ ./src/main/resources/static/

RUN mvn -B -DskipTests package

# Run the combined application
FROM eclipse-temurin:17-jre

WORKDIR /app

COPY --from=backend-build /app/backend/target/employee-management-system-0.0.1-SNAPSHOT.jar app.jar

ENV SPRING_PROFILES_ACTIVE=prod
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=60.0"

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]