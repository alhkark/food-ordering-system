# docker-compose

Local Postgres, Kafka, Schema Registry, Debezium Connect, pgAdmin, and Kafdrop. Started with `compose.sh`.

Ollama is not started here. See [../README.md](../README.md#ollama-with-mistral7b).

## compose.sh

Wrapper around `docker compose`. It always merges:

- `common.yaml` — shared network
- `kafka_cluster.yaml` — Kafka brokers, Schema Registry, Kafdrop, Debezium Connect
- `database.yaml` — Postgres and pgAdmin on the same Compose network as Kafka/Debezium (`food-ordering-system`) so Connect can reach `food-ordering-postgres`. Postgres uses `wal_level=logical` (for Debezium) and `max_connections=200`. Those flags need a container **restart** (`./compose.sh down` then `./compose.sh run`); they do **not** require `down-all` / wiping the volume.

`init_kafka.yaml` is added only when initializing topics.

Debezium Connect is a **local** image (`food-ordering-debezium`), built from `quay.io/debezium/connect` plus Confluent Avro converter JARs so it can use Schema Registry. First `./compose.sh run` builds it; rebuild with `docker compose -f common.yaml -f kafka_cluster.yaml -f database.yaml build debezium`.

`./compose.sh run` also registers **four** outbox CDC connectors (one replication slot and `topic.prefix` per table) after Kafka and Postgres are ready. To register again without restarting the stack:

```bash
./debezium/register-connectors.sh
```

Check with `GET http://localhost:8083/connectors`. Topics (not the saga request/response topics):

- `debezium_order_payment_outbox.order.payment_outbox`
- `debezium_order_restaurant_approval_outbox.order.restaurant_approval_outbox`
- `debezium_payment_order_outbox.payment.order_outbox`
- `debezium_restaurant_order_outbox.restaurant.order_outbox`

From this directory:

```bash
./compose.sh
```

From the repo root:

```bash
./infrastructure/docker-compose/compose.sh
```

### How it works

| Command | What it does |
|---|---|
| `run` (default), `up`, `start` | `docker compose up -d`, wait until Kafka is ready, run `init-kafka`, then register Debezium connectors |
| `init` | Recreate Kafka topics only (brokers must already be up) |
| `down`, `stop` | Stop containers |
| `down-all` | Stop containers and **delete volumes** (Postgres data is wiped) |
| `reset-pgadmin` | Recreate pgAdmin so server config reloads |
| `ps` | List running services |
| `logs [service]` | Follow logs (`./compose.sh logs broker-1`) |
| `config` | Print the merged Compose file |

On `run`, after Kafka is ready the script starts a one-shot `init-kafka` container. That job deletes and recreates the payment, restaurant, customer, DLT, and `_schemas` topics. It then waits for Postgres and registers the four Debezium outbox connectors.

Endpoints after a successful `run`:

- Postgres: `localhost:5432` (`postgres` / `admin`)
- pgAdmin: http://localhost:5050
- Kafdrop: http://localhost:9000
- Schema Registry: http://localhost:8081
- Debezium Connect: http://localhost:8083 (`GET /connectors`); Avro via Schema Registry
- Kafka (host): `localhost:29092,localhost:39092,localhost:49092`
- Kafka (Minikube): `host.minikube.internal:29094,host.minikube.internal:39094,host.minikube.internal:49094`
