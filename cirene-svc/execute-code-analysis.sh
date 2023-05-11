#!/bin/bash

TIMEFORMAT='=> SonarQube analysis done in %0lR'

time {
  echo "Starting SonarQube analysis..."

  SONAR_ORG=$(gcloud secrets versions access 1 --secret=SONAR_ORG)
  SONAR_PROJECT=$(gcloud secrets versions access 1 --secret=SONAR_PROJECT)
  SONAR_TOKEN=$(gcloud secrets versions access 1 --secret=SONAR_TOKEN)
  SONAR_URL=$(gcloud secrets versions access 1 --secret=SONAR_URL)

  mvn clean

  mvn verify sonar:sonar -Pcoverage \
    -Dsonar.login=${SONAR_TOKEN} \
    -Dsonar.host.url=${SONAR_URL} \
    -Dsonar.organization=${SONAR_ORG} \
    -Dsonar.projectKey=${SONAR_PROJECT}

  echo "SonarQube analysis finished..."
}
