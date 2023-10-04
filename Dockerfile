# Use an official OpenJDK runtime as a parent image
FROM openjdk:17-alpine
ADD build/libs/news-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar","app.jar"]