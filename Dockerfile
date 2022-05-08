FROM openjdk:8-jre-alpine
RUN addgroup -S java && adduser -S java -G java
USER java
ARG JAVA_OPTS="-noverify -XX:+UnlockExperimentalVMOptions -XX:+UseCGroupMemoryLimitForHeap -Djava.security.egd=file:/dev/./urandom"
COPY ./build/libs/*.jar app.jar
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar /app.jar"]