FROM maven:3.8.5-jdk-11-slim

WORKDIR /app
COPY pom.xml ./
COPY src ./src/
COPY execute-functional-tests.sh ./

RUN chmod +x execute-functional-tests.sh

ARG CIRENE_URL_HOST
ENV CIRENE_URL_HOST ${CIRENE_URL_HOST:-0.0.0.0}

CMD ["./execute-functional-tests.sh"]
