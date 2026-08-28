# docker-compose

Local Postgres, Kafka, Schema Registry, pgAdmin, and Kafdrop. Started with `compose.sh`.

Ollama is not started here. See [../README.md](../README.md#ollama-with-mistral7b).

## compose.sh

Wrapper around `docker compose`. It always merges:

- `common.yaml` — shared network
- `kafka_cluster.yaml` — Kafka brokers, Schema Registry, Kafdrop
- `database.yaml` — Postgres and pgAdmin

`init_kafka.yaml` is added only when initializing topics.

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
| `run` (default), `up`, `start` | `docker compose up -d`, wait until `broker-1` accepts topic listing, then run `init-kafka` |
| `init` | Recreate Kafka topics only (brokers must already be up) |
| `down`, `stop` | Stop containers |
| `down-all` | Stop containers and **delete volumes** (Postgres data is wiped) |
| `reset-pgadmin` | Recreate pgAdmin so server config reloads |
| `ps` | List running services |
| `logs [service]` | Follow logs (`./compose.sh logs broker-1`) |
| `config` | Print the merged Compose file |

On `run`, after Kafka is ready the script starts a one-shot `init-kafka` container. That job deletes and recreates the payment, restaurant, customer, DLT, and `_schemas` topics.

Endpoints after a successful `run`:

- Postgres: `localhost:5432` (`postgres` / `admin`)
- pgAdmin: http://localhost:5050
- Kafdrop: http://localhost:9000
- Schema Registry: http://localhost:8081
- Kafka (host): `localhost:29092,localhost:39092,localhost:49092`
- Kafka (Minikube): `host.minikube.internal:29094,host.minikube.internal:39094,host.minikube.internal:49094`
