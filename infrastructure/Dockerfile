# Build stage
FROM maven:3.9-eclipse-temurin-17 AS build

ARG GITHUB_USERNAME
ARG GITHUB_TOKEN

WORKDIR /log-generator
COPY pom.xml .
COPY src ./src

# Create a temporary settings.xml with GitHub credentials for Maven
RUN mkdir -p /root/.m2 && \
    echo '<settings><servers><server><id>github</id><username>'${GITHUB_USERNAME}'</username><password>'${GITHUB_TOKEN}'</password></server></servers></settings>' > /root/.m2/settings.xml

RUN mvn clean package -DskipTests

# Remove the temporary settings.xml for security
RUN rm /root/.m2/settings.xml

# Run stage
FROM eclipse-temurin:17-jre-alpine

# Copy the certificate into the runtime image
COPY infrastructure/kafka_server_ca.crt /tmp/kafka_server_ca.crt
COPY infrastructure/elastic_server.crt /tmp/elastic_server.crt

# Add certificates to JRE truststore
RUN keytool -import \
      -alias kafka-server-ca \
      -file /tmp/kafka_server_ca.crt \
      -keystore $JAVA_HOME/lib/security/cacerts \
      -storepass changeit \
      -noprompt && \
    keytool -import \
          -alias elastic-server-ca \
          -file /tmp/elastic_server.crt \
          -keystore $JAVA_HOME/lib/security/cacerts \
          -storepass changeit \
          -noprompt && \
    rm /tmp/kafka_server_ca.crt /tmp/elastic_server.crt

# Set working directory
WORKDIR /log-generator

# Copy the built JAR from the build stage
COPY --from=build /log-generator/target/*.jar log-generator.jar

# Create a non-root user
RUN adduser -D appuser && \
    chown -R appuser:appuser /log-generator
USER appuser

# Set environment variables
ENV SPRING_PROFILES_ACTIVE=default

# Expose the port the app runs on
EXPOSE 8080

# Command to run the application
ENTRYPOINT ["java", "-jar", "log-generator.jar"]