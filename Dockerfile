FROM adoptopenjdk/openjdk11:alpine-jre

RUN addgroup -S ehsanyar && adduser -S ehsanyar -G ehsanyar
USER ehsanyar:ehsanyar

ARG JAR_FILE=target/iot-pipeline-0.0.1-SNAPSHOT.jar

ARG ACTIVE_PROFILES=-Dspring.profiles.active=mqtt-producer,mqtt-consumer,kafka-producer,kafka-consumer,simulation

# cd /opt/app
WORKDIR /opt/app

# cp target/iot-pipeline-0.0.1-SNAPSHOT.jar /opt/app/app.jar
COPY ${JAR_FILE} /app.jar

# java -jar /opt/app/app.jar
ENTRYPOINT ["java", "-jar", "/app.jar", "${ACTIVE_PROFILES}"]