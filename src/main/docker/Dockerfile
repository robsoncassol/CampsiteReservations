FROM openjdk:11
ARG APP_VERSION
ENV APP_VERSION=${APP_VERSION}
ENV SPRING_PROFILES_ACTIVE=docker
EXPOSE 8080
ADD maven/*.jar /opt/campsite-reservations.jar
RUN sh -c 'touch /opt/campsite-reservations.jar'
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/opt/campsite-reservations.jar"]
