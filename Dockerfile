FROM eclipse-temurin:19-jdk

WORKDIR /app

COPY target/hotel-booking-system-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
