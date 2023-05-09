# Performance Tests

This project has the performance tests for the microservices.

## Tech stack

* [Python](https://www.python.org/)
* [Pip](https://pypi.org/project/pip/)
* [Locust](https://locust.io/)

## Setup

The setup is composed by 2 parts:

* Deploy the application
* Deploy Locust to GKE

### Deploy the application

1. In a terminal, go to `/cirene-svc` folder

2. Change the kube context to staging:

```
kubectx staging
```

3. Deploy the applications using skaffold:

```
skaffold run
```

4. Check the applications were deployed:

```
kubectl get all -n demo-app-staging
```

5. If the EXTERNAL_IP for services are still pending, wait until all of them have IPs. Check with:

```
kubectl get svc -n demo-app-staging --watch
```

### Deploy Locust to GKE

1. In a terminal, go to `/tests/performance-tests` folder

2. Change the kube context to devops

```
kubectx devops
```

3. Run `./bootstrap-perf-tests-env.sh`

4. Copy the `LOCUST WEB URL` from the output results in terminal

5. Check if locust was properly deployed:

```
kubectl get all -n locust
```

6. Open the Locust webapp in a browser tab

7. Start a test to check everything is working
