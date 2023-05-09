#!/bin/bash

echo "Starting SonarQube analysis..."

mvn clean verify sonar:sonar \
  -Dsonar.projectKey=GCP-DevOps-Demo-Java-App \
  -Dsonar.projectName='GCP DevOps Demo Java App' \
  -Dsonar.host.url=http://localhost:9000 \
  -Dsonar.token=sqp_30f6ded810e81873c3d568ab6240b852bb1b5ad9

echo "SonarQube analysis finished..."
