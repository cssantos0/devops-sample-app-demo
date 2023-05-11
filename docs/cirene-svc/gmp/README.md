# Google Managed Prometheus

* [Documentation](https://cloud.google.com/stackdriver/docs/managed-prometheus)
* [Pricing](https://cloud.google.com/managed-prometheus#section-10)

## Setup 

Follow the instructions below.

### Enable GMP

* [Set up managed collection](https://cloud.google.com/stackdriver/docs/managed-prometheus/setup-managed)

### Configure permissions

Create a namespace to place the application (if not exists):

```
kubectl create ns backend-app
```

Create a service account:

The service account will be tighed with the k8s service account to provide permissions to access monitoring API:

```
gcloud iam service-accounts create gmp-test-wi-sa
```

Grant permissions to the service account

```
gcloud projects add-iam-policy-binding <PROJECT_ID> --member=serviceAccount:gmp-test-wi-sa@<PROJECT_ID>.iam.gserviceaccount.com --role=roles/monitoring.metricWriter
```

> These steps needs to be done only once. 

Bind the Google service account with the GKE workload identity pointing to the proper namespace

```
gcloud iam service-accounts add-iam-policy-binding --role roles/iam.workloadIdentityUser --member "serviceAccount:<PROJECT_ID>.svc.id.goog[backend-app/default]" gmp-test-wi-sa@<PROJECT_ID>.iam.gserviceaccount.com
```

> `backend-app`is the namespace name for this example

Finally, annotate the k8s service account with the Google service account

```
kubectl annotate serviceaccount --namespace backend-app default iam.gke.io/gcp-service-account=gmp-test-wi-sa@<PROJECT_ID>.iam.gserviceaccount.com
```

(Optional) Generate a key to the Google service account:

```
gcloud iam service-accounts keys create gmp-test-wi-sa-key.json --iam-account=gmp-test-wi-sa@<PROJECT_ID>.iam.gserviceaccount.com
```

```
kubectl -n backend-app create secret generic gmp-test-wi-sa --from-file=key.json=gmp-test-wi-sa-key1.json
```

### Deploy Prometheus UI

Deploy the UI (check the metric scope if needed):

```
curl https://raw.githubusercontent.com/GoogleCloudPlatform/prometheus-engine/v0.4.1/examples/frontend.yaml |
			sed 's/\$PROJECT_ID/<PROJECT_ID>/' |
			kubectl apply -n backend-app -f -			
```

Port-forward the frontend service to access on port 9090:

```
kubectl -n backend-app port-forward svc/frontend 9090            
```

### Deploy Grafana

Deploy the UI:
		
```        
kubectl -n backend-app apply -f https://raw.githubusercontent.com/GoogleCloudPlatform/prometheus-engine/v0.4.1/examples/grafana.yaml	
```

Port-forward Grafana to access on port 3001:

```
kubectl -n backend-app port-forward svc/grafana 3001:3000
```

> Default credential is `admin/admin`

### Test setup

Open Console and go to Monitoring > Metrics Explorer > PromQL tab and execute:

```
up
```

Run the query and check if there are results, if yes, everything is properly set.

## Deploy a PodMonitoring resource

First, deploy the application with a Prometheus export URL (in this app `/actuator/prometheus`).

Then, deploy the PodMonitoring resource, in the proper namespace (where the app is deployed):

```
kubectl apply -f prometheus/pod-monitors.yaml -n backend-app
```

After deploying the pod monitor, its possible to manage it:

To get:

```
kubectl get podmonitoring -A
```

To describe:

```
kubectl describe podmonitoring -n backend-app
```

To delete:

```
kubectl delete podmonitoring -n backend-app --all
```

Back to the Console go to Monitoring > Metrics Explorer > PromQL tab and execute:

```
jvm_buffer_memory_used_bytes
```

Run the query and check if there are results, if yes, everything is properly set.

> This is a example of metric exported by SpringBoot actuator.

## References

* [GitHub - Sample Custom Metrics](https://github.com/msathe-tech/custom-metrics-prometheus)
* [Spring + Prometheus + Grafana](https://medium.com/@luanrubensf/monitoring-spring-applications-with-prometheus-and-grafana-ae50bbdd1920)