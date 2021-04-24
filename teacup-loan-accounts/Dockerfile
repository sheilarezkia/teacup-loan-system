FROM openjdk:8-jre-alpine
WORKDIR /app
COPY build/libs/*.jar app.jar
ARG PROFILE
ENV PROFILE=${PROFILE}
ENTRYPOINT java -Dspring.profiles.active=$PROFILE -jar app.jar