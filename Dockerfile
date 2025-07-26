FROM eclipse-temurin:21-jre
WORKDIR /app
COPY target/zer0balance-0.0.1-SNAPSHOT.jar zer0Balance-v1.0.jar
EXPOSE 9090
ENTRYPOINT ["java", "-jar", "zer0Balance-v1.0.jar"]