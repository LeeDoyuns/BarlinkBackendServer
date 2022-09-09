FROM openjdk:11-jre-slim 
ARG WAR_FILE=target/*.war
COPY ${WAR_FILE} app.war
VOLUME [ "/logs" ]
ENTRYPOINT ["java","-Dspring.profiles.active=prod","-jar","-Dserver.port=8080","/app.war"]
USER root