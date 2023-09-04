FROM openjdk:17
ARG JAR_FILE=build/libs/devhive-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java", "-DSpring.profiles.active=prod", "-jar", "app.jar"]