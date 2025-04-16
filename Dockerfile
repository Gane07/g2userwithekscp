FROM openjdk:17-jdk-slim
RUN apt-get update && apt-get install -y maven
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests
EXPOSE ${USERPROFILE_SERVICE_PORT}
ENTRYPOINT ["java", "-jar", "target/userprofile-service.jar"]