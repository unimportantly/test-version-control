FROM openjdk:11

WORKDIR: /app

COPY: testDemo-0.0.1-SNAPSHOT.jar app.jar/

CMD: ["java", "-ar", "app.jar"]