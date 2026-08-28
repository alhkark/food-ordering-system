# Food ordering system

Spring Boot microservices for placing and tracking food orders. Order, payment, restaurant, and customer services talk over Kafka (saga + outbox). Order-service can interpret order notes with Ollama (`mistral:7b`).

| Service | Port | HTTP |
|---|---|---|
| order-service | 8181 | `POST /orders`, `GET /orders/{trackingId}` |
| payment-service | 8182 | Kafka only |
| restaurant-service | 8183 | Kafka only |
| customer-service | 8184 | `POST /customers` |

## Quick start

**1. Local infrastructure** (Postgres, Kafka, Schema Registry, pgAdmin, Kafdrop):

```bash
./infrastructure/docker-compose/compose.sh
```

Details and commands: [infrastructure/docker-compose/README.md](infrastructure/docker-compose/README.md).

**2. Ollama** (order-service only). Not part of Compose:

```bash
brew install ollama
brew services start ollama
ollama pull mistral:7b
```

Details: [infrastructure/README.md](infrastructure/README.md#ollama-with-mistral7b).

**3. Run the services** locally:

```bash
./gradlew :order-service:order-container:bootRun
./gradlew :payment-service:payment-container:bootRun
./gradlew :restaurant-service:restaurant-container:bootRun
./gradlew :customer-service:customer-container:bootRun
```

Or deploy to Kubernetes: [infrastructure/helm/README.md](infrastructure/helm/README.md).

## Docs

- [infrastructure/README.md](infrastructure/README.md) — overview, Ollama
- [infrastructure/docker-compose/README.md](infrastructure/docker-compose/README.md) — `compose.sh`
- [infrastructure/helm/README.md](infrastructure/helm/README.md) — Minikube / Helm
