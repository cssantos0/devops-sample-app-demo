#!/bin/bash

TIMEFORMAT='=> Bootstraping performance tests environment done in %0lR'

time {
  echo -e "Starting performance tests env provisioning"

  echo -e " "
  echo -e "Setting up env vars..."

  GCP_PROJECT_ID=$(gcloud config get-value project)
  GCP_REGION=$(gcloud config get-value compute/region)
  GCP_ZONE=$(gcloud config get-value compute/zone)
  LOCUST_REPO=locust
  LOCUST_IMAGE_NAME=locust-tasks
  LOCUST_IMAGE_TAG=latest

  echo -e "PROJECT is ${GCP_PROJECT_ID}"
  echo -e "REGION is ${GCP_REGION}"
  echo -e "ZONE is ${GCP_ZONE}" 
  echo -e "LOCUST REPO is ${LOCUST_REPO}" 
  echo -e "LOCUST IMAGE NAME is ${LOCUST_IMAGE_NAME}" 
  echo -e "LOCUST IMAGE TAG is ${LOCUST_IMAGE_TAG}" 

  echo -e " "
  echo -e "Building locust image and pushing it to the repository..."

  gcloud builds submit \
    --tag ${GCP_REGION}-docker.pkg.dev/${GCP_PROJECT_ID}/${LOCUST_REPO}/${LOCUST_IMAGE_NAME}:${LOCUST_IMAGE_TAG} \
    locust

  echo -e " "
  echo -e "Changing Kubernetes context to staging cluster..."
  kubectx staging

  echo -e " "
  echo -e "Getting cirene service external LB public IP..."

  CIRENE_EXT_IP=$(kubectl -n demo-app-staging get svc cirene-svc --output jsonpath='{.status.loadBalancer.ingress[0].ip}')
  APP_TARGET="${CIRENE_EXT_IP}:4000"
  echo -e "APP_TARGET is ${APP_TARGET}" 

  echo -e " "
  echo -e "Changing Kubernetes context to Devops cluster..."
  kubectx devops

  echo -e " "
  echo -e "Creating Kubernetes deployment objects..."

  sed -e " \
  s|@GCP_PROJECT_ID@|${GCP_PROJECT_ID}|g; \
  s|@GCP_REGION@|${GCP_REGION}|g; \
  s|@LOCUST_REPO@|${LOCUST_REPO}|g; \
  s|@LOCUST_IMAGE_NAME@|${LOCUST_IMAGE_NAME}|g; \
  s|@LOCUST_IMAGE_TAG@|${LOCUST_IMAGE_TAG}|g; \
  s|@APP_TARGET@|${APP_TARGET}|g;" \
  templates/locust-master-controller.yaml.template > k8s/locust-master-controller.yaml

  sed -e " \
  s|@GCP_PROJECT_ID@|${GCP_PROJECT_ID}|g; \
  s|@GCP_REGION@|${GCP_REGION}|g; \
  s|@LOCUST_REPO@|${LOCUST_REPO}|g; \
  s|@LOCUST_IMAGE_NAME@|${LOCUST_IMAGE_NAME}|g; \
  s|@LOCUST_IMAGE_TAG@|${LOCUST_IMAGE_TAG}|g; \
  s|@APP_TARGET@|${APP_TARGET}|g;" \
  templates/locust-worker-controller.yaml.template > k8s/locust-worker-controller.yaml

  echo -e " "
  echo -e "Deploying locust to Kubernetes staging cluster..."

  kubectl apply -f k8s/locust-namespace.yaml
  kubectl apply -f k8s/locust-master-controller.yaml
  kubectl apply -f k8s/locust-worker-controller.yaml
  kubectl apply -f k8s/locust-master-service.yaml

  echo -e " "
  echo -e "Getting locust web service external LB public IP..."

  sleep 60

  LOCUST_WEB_IP=$(kubectl -n locust get svc locust-master-web --output jsonpath='{.status.loadBalancer.ingress[0].ip}')  
  LOCUST_WEB_URL="http://${LOCUST_WEB_IP}:8089"

  echo -e " "
  echo -e "LOCUST WEB URL is ${LOCUST_WEB_URL}"   

  echo -e " "
  echo -e "Performance tests env provisioned successfully"
}

