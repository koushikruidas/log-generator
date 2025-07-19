# Sending Application Logs to Logpulse.io

This guide explains how to configure your Spring Boot application to send logs to the log aggregator service [logpulse.io](https://logpulse.io) using Log4j2 and the `log-analyzer-client` dependency.

## Prerequisites
- Java 17 or later
- Spring Boot application
- Access to [logpulse.io](https://logpulse.io)

## 1. Add the Log Analyzer Client Dependency

### Maven
Add the following dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>com.poinciana</groupId>
    <artifactId>log-analyzer-client</artifactId>
    <version>0.0.9-SNAPSHOT</version>
</dependency>
```

Make sure your `pom.xml` also includes the GitHub repository for the dependency:

```xml
<repositories>
    <repository>
        <id>github</id>
        <name>GitHub Packages</name>
        <url>https://maven.pkg.github.com/koushikruidas/log-analyzer-client</url>
        <snapshots>
            <enabled>true</enabled>
        </snapshots>
    </repository>
</repositories>
```

#### Maven Authentication (settings.xml)
To provide your GitHub username and personal access token for the repository, update your Maven `settings.xml` (usually located in `~/.m2/settings.xml`) as follows:

```xml
<settings>
  <!-- ...existing settings... -->
  <servers>
    <server>
      <id>github</id>
      <username>YOUR_GITHUB_USERNAME</username>
      <password>YOUR_GITHUB_PERSONAL_ACCESS_TOKEN</password>
    </server>
    <!-- ...other servers... -->
  </servers>
  <!-- ...existing settings... -->
</settings>
```

Replace `YOUR_GITHUB_USERNAME` and `YOUR_GITHUB_PERSONAL_ACCESS_TOKEN` with your actual GitHub credentials.

### Gradle
Add the following to your `build.gradle`:

```groovy
dependencies {
    implementation 'com.poinciana:log-analyzer-client:0.0.9-SNAPSHOT'
}

repositories {
    maven {
        url 'https://maven.pkg.github.com/koushikruidas/log-analyzer-client'
        credentials {
            username = project.findProperty("gpr.user") ?: System.getenv("USERNAME")
            password = project.findProperty("gpr.key") ?: System.getenv("TOKEN")
        }
    }
}
```

## 2. Configure Log4j2

Create or update the `log4j2.xml` file in your `src/main/resources` directory. Example configuration:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<Configuration status="info" name="spring-boot-kafka-log">
    <Properties>
        <Property name="bootstrap.servers">${env:KAFKA_SERVERS}</Property>
        <Property name="security.protocol">SASL_SSL</Property>
        <Property name="sasl.mechanism">SCRAM-SHA-512</Property>
        <Property name="sasl.jaas.config">
            org.apache.kafka.common.security.scram.ScramLoginModule required
            username="${env:KAFKA_USERNAME}"
            password="${env:KAFKA_PASSWORD}";
        </Property>
        <Property name="ssl.keystore.location">${env:SSL_KEYSTORE_PATH}</Property>
        <Property name="ssl.keystore.password">${env:SSL_KEYSTORE_PASSWORD}</Property>
        <Property name="ssl.key.password">${env:SSL_KEY_PASSWORD}</Property>
        <Property name="log.analyzer.admin.api.url">${env:LOGANALYZER_ADMIN_URL}/api/admin/application/details</Property>
        <Property name="application.name">${env:APPLICATION_NAME}</Property>
        <Property name="organization.name">${env:ORGANIZATION_NAME}</Property>
        <Property name="kafka.topic">${env:KAFKA_TOPIC}</Property>
        <Property name="apiKey">${env:LOG_ANALYZER_TOKEN}</Property>
    </Properties>
    <Appenders>
        <Kafka name="Kafka" topic="${kafka.topic}">
            <PatternLayout pattern="%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"/>
            <Property name="bootstrap.servers">${bootstrap.servers}</Property>
            <Property name="security.protocol">${security.protocol}</Property>
            <Property name="sasl.mechanism">${sasl.mechanism}</Property>
            <Property name="sasl.jaas.config">${sasl.jaas.config}</Property>
            <!-- Add SSL properties if required -->
            <Property name="ssl.keystore.location">${ssl.keystore.location}</Property>
            <Property name="ssl.keystore.password">${ssl.keystore.password}</Property>
            <Property name="ssl.key.password">${ssl.key.password}</Property>
        </Kafka>
        <Console name="Console" target="SYSTEM_OUT">
            <PatternLayout pattern="%d{yyyy-MM-dd HH:mm:ss} %-5p %c{1}:%L - %m%n"/>
        </Console>
    </Appenders>
    <Loggers>
        <Root level="info">
            <AppenderRef ref="Kafka"/>
            <AppenderRef ref="Console"/>
        </Root>
    </Loggers>
</Configuration>
```

- Replace environment variables with your actual values or set them in your environment.

## 3. Enable Component Scanning for the Log Analyzer Client

In your main application class (e.g., `GenerateLogsApplication.java`), add the `@ComponentScan` annotation to include the log analyzer client package:

```java
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.poinciana.loganalyzerClient"})
public class GenerateLogsApplication {
    public static void main(String[] args) {
        SpringApplication.run(GenerateLogsApplication.class, args);
    }
}
```

## 4. Get Kafka and Logpulse Credentials

To obtain Kafka details (topic name, hosts, certificates, username, password, and token), please contact: **koushikruidas@gmail.com**

## 5. Run Your Application

Build and run your application as usual. Your logs will now be sent to logpulse.io via the configured Kafka appender.

## 6. Troubleshooting
- Ensure all dependencies are downloaded and the repository is accessible.
- Double-check your Kafka and topic configuration in `log4j2.xml`.
- Review logpulse.io documentation for additional setup or troubleshooting steps.

---
For more information, visit [logpulse.io](https://logpulse.io) or contact your system administrator.
