FROM maven:3.8.5-jdk-11-slim as builder

WORKDIR /app
COPY pom.xml ./
COPY src ./src/

RUN mvn package -DskipTests

FROM adoptopenjdk/openjdk11:ubi

COPY --from=builder /app/target/cirene-svc-*.jar /cirene-svc.jar

ARG ENABLE_INFO
ARG ENABLE_RATING

ENV ENABLE_INFO ${ENABLE_INFO:-false}
ENV ENABLE_RATING ${ENABLE_RATING:-false}

CMD ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/cirene-svc.jar"]
