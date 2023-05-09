# Devops Demo - Sample App

This is a backend service representing a given planet size called cirene.

## Tech stack

This service is built in Java, therefore the following technologies are needed to build and run it:

* [Java 11](https://openjdk.org/install/)
* [Maven 3](https://maven.apache.org/install.html)

> Also make sure all tech stack mentioned in the [main documentation](../../../README.md#tech-stack) is properly set.

## Development environment

There are different options to run this project. Choose one below.

**OBS 1:** Make sure to be in the correct folder. In this case, the root folder for cirene service:

```
/cirene-svc
```

**OBS 2:** The project is set to run on port `4000`. If needed, change in `application.properties` file.

### Using Maven + Spring Boot

To run locally using Maven + SpringBoot:

#### V1 with only the planet size

```
mvn spring-boot:run
```

#### V2 Planet size with ratings enabled

```
mvn spring-boot:run -Dspring-boot.run.arguments="--ENABLE_RATING=true"
```

#### V3 Planet size with info enabled

```
mvn spring-boot:run -Dspring-boot.run.arguments="--ENABLE_INFO=true"
```

#### V4 Planet size with info and ratings enabled

```
mvn spring-boot:run -Dspring-boot.run.arguments="--ENABLE_INFO=true --ENABLE_RATING=true"
```

> **Tip:** Check the [APIs Testing](#apis-testing) section below to make calls to the application.

### Using Docker

To run locally using Docker, build the image:

```
docker build -t cirene-svc .
```

```
docker build -t cirene-svc-v2 --build-arg ENABLE_INFO=true .
```

```
docker build -t cirene-svc-v3 --build-arg ENABLE_RATING=true .
```

```
docker build -t cirene-svc-v4 --build-arg ENABLE_RATING=true --build-arg ENABLE_INFO=true .
```

Finally, run the container exposing on port 4000:

```
docker run -p4000:4000 cirene-svc
```

```
docker run -p4000:4000 cirene-svc-v2
```

```
docker run -p4000:4000 cirene-svc-v3
```

```
docker run -p4000:4000 cirene-svc-v4
```

Check the [APIs Testing](#apis-testing) section below to make calls to the application.

**OBS:** Over time, there will be created several dangling Docker images, to delete them:

```
docker image prune -a
```

or

```
docker system prune -a
```

### Using Skaffold

The base of Skaffold configuration is within the `skaffold.yaml` file. There are 2 profiles:

* `minikube-profile`: To run locally using Minikube
* `staging-profile`: To run in a Kubernetes cluster (not limited to GKE)

#### on Minikube

First, start a minikube K8S cluster:

```
minikube start
```

Check with `kubectx`if `minikube` is the active kube context:

```
kubectx
```

If `minikube` is not the current active context, change it by running:

```
kubectx minikube
```

Then, check if `kubectl` is properly using the minikube cluster:

```
kubectl get nodes
```

The output should be similar to:

```
NAME       STATUS   ROLES           AGE   VERSION
minikube   Ready    control-plane   26d   v1.25.3
```

Finally, use `Skaffold`to build and deploy the application, starting the `inner development loop` on minikube:

```
skaffold dev --port-forward
```

> This will start skaffold in development mode, therefore its possible to use its features such as hot deploy, remote debugging and others.

> The `--port-forward` flag forwards the proper port so the services can receive requests. This is needed because there is no actual 
load balancer created (since its been running on Minikube).

To make sure everything was properly deployed on minikube, run:

```
kubectl get all -n mc-app
```

> where `-n mc-app` is the namespace where the application is deployed.

As output of the command above, it shoud be returned 1 pod running, 1 service of type Load Balancer, 1 deployment and 1 replica set.

Now, the application should be reachable, check the [APIs Testing](#apis-testing) section below to make calls.

As an alternative, if the `--port-forward`is not provided, meaning the developer ran the application using `skaffold dev`, the port needs to be forwarded
so it can be reachable. For this, run the following command to port forward the service in the port 4000:

```
kubectl port-forward service/cirene-svc 4000:4000 -n mc-app
```

#### on GKE

If this is the first service being deployed, first follow the instructions to setup the [Google Cloud project](../../infra/gcp/README.md) and then to 
provision resources using [Terraform](../../infra/terraform/README.md). If this is not the first service deployed, the Google Cloud project is
already created and the Terraform provisioning was already done, which means, it does not need to be created again.

**OBS:** After provisiong resources with Terraform, make sure to be in the correct folder. In thiss case, the root folder for cirene service:

```
/app-dev-demos/microservices/cirene-svc
```

Then, create the following env vars:

```
GCP_PROJECT_ID=$(gcloud config get-value project)
GCP_REGION=$(gcloud config get-value compute/region)
GCP_ZONE=$(gcloud config get-value compute/zone)
```

Now, connect to the GKE cluster and generate the kube context:

```
gcloud container clusters get-credentials gke-1-mc-cluster --zone $GCP_ZONE --project $GCP_PROJECT_ID
```

Then, rename the kubectl context to `staging` using `kubectx`:

```
kubectx staging=$(kubectx -c)
```

> `staging`is the name set in `skaffold.yaml` to find the GKE cluster configuration

Check with `kubectx`if `staging` is the active kube context:

```
kubectx
```

If `staging` is not the current active context, change it by running:

```
kubectx staging
```

Then, check if `kubectl` is properly using the GKE cluster:

```
kubectl get nodes
```

The output should be similar to:

```
NAME                                            STATUS   ROLES    AGE   VERSION
gke-gke-mc-cluster-e2-node-pool-01ee4352-nfsk   Ready    <none>   17m   v1.24.9-gke.2000
gke-gke-mc-cluster-e2-node-pool-1cd89454-05wv   Ready    <none>   17m   v1.24.9-gke.2000
gke-gke-mc-cluster-e2-node-pool-891ecc22-mq2r   Ready    <none>   17m   v1.24.9-gke.2000
```

Finally, start the `inner development loop` with Skaffold providing the `Artifact Registry` repository:

```
skaffold dev --default-repo $GCP_REGION-docker.pkg.dev/$GCP_PROJECT_ID/cirene-svc
```

With that, Skaffold with build the Docker image, then push into the Artifact Registry. Then, apply the K8S resources into the GKE cluster.

> This will start skaffold in development mode, therefore its possible to use its features such as hot deploy, remote debugging and others. The 
trick is that now this is done directly into a GKE cluster.

To make sure everything was properly deployed on GKE, run:

```
kubectl get all -n mc-app
```

> where `-n mc-app` is the namespace where the application is deployed.

As output of the command above, it shoud be returned 1 pod running, 1 service of type Load Balancer, 1 deployment and 1 replica set.

Observe in the output of the command for the `service/cirene-svc` object that a Load Balancer was provisioned. Take a look at the `EXTERNAL-IP`.

To make calls to the service, first get the LB external IP in a env var:

```
CIRENE_SVC_EXT_IP=$(kubectl -n mc-app get svc cirene-svc --output jsonpath='{.status.loadBalancer.ingress[0].ip}')
```

Validate the env var has the external IP value:

```
echo $CIRENE_SVC_EXT_IP
```

Now, the application should be reachable through the Load Balancer, check the [APIs Testing](#apis-testing) section below to make calls. Just 
make sure to change the `locahost` to `EXERNAL-IP` of the Load Balancer. For example:

```
curl http://$CIRENE_SVC_EXT_IP:4000/cirene/health | jq
```

##### Clean up

Since it was used the `skaffold dev` command to deploy on GKE, simply press `control + C` on terminal to clean up the K8S resources. The output should be:

```
Cleaning up...
 - namespace "mc-app" deleted
 - deployment.apps "cirene-svc" deleted
 - service "cirene-svc" deleted
```

Then, run `kubectl get all -n mc-app` to make sure there are no resources found in the `mc-app` namespace.

Alternatively, in case of any issues with the current terminal session, causing Skaffold to lost its state, its possible to delete all K8S resources using:

```
kubectl delete all --all -n mc-app
```

### APIs Testing

There are 2 endpoint APIs for this service:

#### Planet Size

Parameters:

* RADIUS: a float value representing the radius of a planet

```
curl http://localhost:4000/cirene/planet/size?radius=<RADIUS>
```

Response:

* PLANET SIZE: a float value representing a planet size.

For example:

```
curl http://localhost:4000/cirene/planet/size?radius=34.5

HTTP/1.1 200
Content-Type: text/plain;charset=UTF-8

3739.28
```

#### Health Check

```
curl http://localhost:4000/cirene/health | jq
```

> To use `jq`, first intall the utility on the OS.

Response:

```
HTTP/1.1 200
Content-Type: application/json

{
  "status": "up"
}
```

## Observability

* [Enable GMP](docs/cirene-svc/gmp/README.md)

## References

* [Spring Boot GCP - Samples](https://spring-gcp.saturnism.me/)
* [Spring Boot GCP - Docs](https://docs.google.com/document/d/14NRxngEhkh1gPTyTS-V51XEHrGMhJKnbzw5uSs6oFaI/edit)
* [Skaffold - YAML Reference](https://skaffold.dev/docs/references/yaml/)
* [Skaffold - Code Lab](https://codelabs.developers.google.com/skaffold-deep-dive#0)
* [Skaffold - Example Repositories](https://github.com/GoogleContainerTools/skaffold/tree/v1.39.3/examples)
