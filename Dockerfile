FROM openjdk:17
WORKDIR /app
COPY . .

RUN chmod +x ./gradlew
RUN gradle bootJar

ENTRYPOINT ["java", "-jar", "/app/build/libs/tl1p-0.0.1-SNAPSHOT.jar"]
EXPOSE 8081