# food-ordering-system Helm chart

Deploys order, payment, restaurant, and customer services (four instances of the `food-ordering-service` chart). Image, ports, and datasource settings are in [values.yaml](food-ordering-system/values.yaml).

## Prerequisites

- A Kubernetes cluster (`minikube`, `kind`, Docker Desktop, etc.)
- `helm` and `kubectl`

## Build images

Images must exist on the node (`imagePullPolicy: IfNotPresent`). From the repo root:

On minikube, point Docker at the cluster first so builds land in minikube’s daemon:

```bash
eval $(minikube docker-env)
```

```bash
./gradlew :order-service:order-container:bootBuildImage
./gradlew :payment-service:payment-container:bootBuildImage
./gradlew :restaurant-service:restaurant-container:bootBuildImage
./gradlew :customer-service:customer-container:bootBuildImage
```

Undo point Docker at the cluster
```bash
eval $(minikube docker-env -u)
```

## Infrastructure

This chart does **not** install Postgres, Kafka, Schema Registry, or Ollama. Default values expect Services in the same namespace:

- `postgres`
- `broker-1`, `broker-2`, `broker-3`
- `schema-registry`
- `ollama` (order-service only)

Override `datasource.url`, `kafka.bootstrapServers`, and `kafka.schemaRegistryUrl` in [values.yaml](food-ordering-system/values.yaml) if your hosts differ.

## Configure ingress

Enable the nginx Ingress controller:

```bash
minikube addons enable ingress
```

On Docker-driver minikube (typical on macOS), expose Ingress on localhost:

```bash
minikube tunnel
```

With `minikube tunnel`, use `http://127.0.0.1/orders` and `http://127.0.0.1/customers`.

Paths `/orders` and `/customers` go to order-service and customer-service. Payment and restaurant are not exposed. Disable with `ingress.enabled: false`.

## Install

```bash
cd infrastructure/helm/food-ordering-system
helm dependency update
helm upgrade --install food-ordering . \
  --namespace food-ordering \
  --create-namespace \
  --atomic \
  --timeout 10m
```

`--atomic` waits for resources to become ready and rolls back to the last successful release if the upgrade fails (or uninstalls on a failed first install). `--timeout 10m` covers Spring Boot startup probes (up to ~150s each across four services).

## Check

```bash
kubectl get pods,svc,ingress -n food-ordering
helm status food-ordering -n food-ordering
```

## Uninstall

```bash
helm uninstall food-ordering -n food-ordering
```
