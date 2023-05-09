#!/bin/bash

echo "Starting SonarQube analysis..."

mvn clean verify sonar:sonar \
  -Dsonar.projectKey=GCP-DevOps-Demo-Prj-Java-App \
  -Dsonar.projectName='GCP-DevOps-Demo-Prj-Java-App' \
  -Dsonar.host.url=http://localhost:9000 \
  -Dsonar.token=<CHANGE_TO_SONAR_TOKEN>

echo "SonarQube analysis finished..."
