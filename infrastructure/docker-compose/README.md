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

The script waits for the outbox tables, registers connector config, then **restarts** all connectors so failed tasks recreate replication slots. Re-registering config alone does not restart failed tasks.

Check with `GET http://localhost:8083/connectors`. Topics (not the saga request/response topics):

- `debezium_order_payment_outbox.order.payment_outbox`
- `debezium_order_restaurant_approval_outbox.order.restaurant_approval_outbox`
- `debezium_payment_order_outbox.payment.order_outbox`
- `debezium_restaurant_order_outbox.restaurant.order_outbox`

### Debezium startup order

Outbox tables are created by Flyway when you start the Spring services (order, payment, restaurant). Debezium connectors need those tables before they can create filtered publications and replication slots.

Recommended workflow:

1. `./compose.sh run` — start Postgres, Kafka, Schema Registry, and Debezium Connect
2. Start the application services — Flyway creates the outbox tables
3. If connectors were registered before the tables existed, run `./debezium/register-connectors.sh` again

Expected replication slots (one per connector):

| Slot | Outbox table |
|---|---|
| `dbz_order_payment_outbox` | `order.payment_outbox` |
| `dbz_order_restaurant_approval_outbox` | `order.restaurant_approval_outbox` |
| `dbz_payment_order_outbox` | `payment.order_outbox` |
| `dbz_restaurant_order_outbox` | `restaurant.order_outbox` |

Check slots in Postgres:

```bash
docker exec food-ordering-postgres psql -U postgres -d postgres -c \
  "SELECT slot_name, plugin, active FROM pg_replication_slots ORDER BY slot_name;"
```

When healthy, all four slots exist with `plugin = pgoutput` and `active = t`.

Check connector status:

```bash
curl -s 'http://localhost:8083/connectors?expand=status' | python3 -m json.tool
```

Both the connector and its task should be `RUNNING`.

### Debezium troubleshooting

| Symptom | Likely cause | Fix |
|---|---|---|
| No replication slots | Connectors failed on first start (tables missing) and were not restarted | Start services, then run `./debezium/register-connectors.sh` |
| `No table filters found for filtered publication dbz_*` | Outbox tables do not exist yet | Start order/payment/restaurant services, then re-run `./debezium/register-connectors.sh` |
| Connector `RUNNING`, task `FAILED` | Same as above, or stale failed task | Re-run `./debezium/register-connectors.sh` (it restarts connectors) |
| `role "<username>" does not exist` | Client connected with your macOS username instead of `postgres` | Use `-U postgres` / password `admin` |

Connect to Postgres as `postgres`, not your OS username:

```bash
psql -h localhost -p 5432 -U postgres -d postgres
```

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
| `down`, `stop` | Delete Debezium connectors (if Connect is up), then stop containers |
| `down-all` | Same, then **delete volumes** (Postgres data is wiped) |
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
