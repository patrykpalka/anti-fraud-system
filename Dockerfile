# Use an official JDK as the base image
FROM eclipse-temurin:17-jdk-alpine

# Set the working directory inside the container
WORKDIR /app

# Copy the JAR file to the container
COPY build/libs/anti-fraud-system-0.0.1-SNAPSHOT.jar app.jar

# Expose the port your application runs on
EXPOSE 28852

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
