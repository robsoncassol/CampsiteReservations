FROM openjdk:8-jdk-alpine
ARG APP_VERSION
ENV APP_VERSION=${APP_VERSION}
EXPOSE 8080
ADD target/*.jar /opt/campsite-reservations.jar
RUN sh -c 'touch /opt/campsite-reservations.jar' && \
    addgroup -S appuser && \
    adduser -S -g appuser appuser && \
    apk add --update --no-cache bash coreutils curl jq && \
    rm -rf /var/cache/apk/*
USER appuser
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/opt/campsite-reservations.jar"]
