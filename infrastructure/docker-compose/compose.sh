#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

COMPOSE_FILES=(
  -f common.yaml
  -f kafka_cluster.yaml
  -f database.yaml
)

usage() {
  cat <<EOF
Usage: $(basename "$0") [command]

Commands:
  run             Start all compose files, init Kafka topics, and register Debezium connectors (default)
  up, start       Same as run
  down, stop      Delete Debezium connectors, then stop all infrastructure services
  down-all        Delete Debezium connectors, stop services, and remove volumes
  init            Init Kafka topics only (Kafka must be running)
  reset-pgadmin   Recreate pgAdmin and reload server config
  ps              List running compose services
  logs [service]  Tail logs (optional service name)
  config          Show merged compose configuration

Examples:
  $(basename "$0")
  $(basename "$0") run
  $(basename "$0") logs broker-1
EOF
}

compose() {
  docker compose "${COMPOSE_FILES[@]}" "$@"
}

wait_for_kafka() {
  local max_attempts=90
  local attempt=1
  local kafka_topics_cmd="/opt/kafka/bin/kafka-topics.sh"
  local bootstrap_server="broker-1:19092"

  echo "Waiting for Kafka (broker-1)..."

  until docker ps --format '{{.Names}}' | grep -qx 'broker-1'; do
    if (( attempt >= max_attempts )); then
      echo "broker-1 container did not start in time." >&2
      exit 1
    fi
    sleep 2
    ((attempt++))
  done

  attempt=1
  until docker exec broker-1 "${kafka_topics_cmd}" --bootstrap-server "${bootstrap_server}" --list >/dev/null 2>&1; do
    if (( attempt >= max_attempts )); then
      echo "Kafka did not become ready in time." >&2
      echo "Last broker check output:" >&2
      docker exec broker-1 "${kafka_topics_cmd}" --bootstrap-server "${bootstrap_server}" --list >&2 || true
      exit 1
    fi
    sleep 2
    ((attempt++))
  done

  echo "Kafka is ready."
}

wait_for_postgres() {
  local max_attempts=90
  local attempt=1

  echo "Waiting for Postgres..."
  until docker exec food-ordering-postgres pg_isready -U postgres >/dev/null 2>&1; do
    if (( attempt >= max_attempts )); then
      echo "Postgres did not become ready in time." >&2
      exit 1
    fi
    sleep 2
    ((attempt++))
  done
  echo "Postgres is ready."
}

init_kafka() {
  docker compose "${COMPOSE_FILES[@]}" -f init_kafka.yaml run --rm init-kafka
}

register_debezium_connectors() {
  echo "Registering Debezium connectors..."
  "${SCRIPT_DIR}/debezium/register-connectors.sh"
}

delete_debezium_connectors() {
  local connect_url="${CONNECT_URL:-http://localhost:8083}"
  local connectors_dir="${SCRIPT_DIR}/debezium/connectors"
  local name http_code

  if ! curl -sf --max-time 5 -H "Expect:" "${connect_url}/connectors" >/dev/null; then
    echo "Debezium Connect is not reachable; skip deleting connectors."
    return 0
  fi

  echo "Deleting Debezium connectors..."
  shopt -s nullglob
  local files=("${connectors_dir}"/*.json)
  if (( ${#files[@]} == 0 )); then
    echo "No connector JSON files in ${connectors_dir}"
    return 0
  fi

  for file in "${files[@]}"; do
    name="$(python3 -c 'import json, sys; print(json.load(sys.stdin)["name"])' < "${file}")"
    http_code="$(curl -sS -o /dev/null -w '%{http_code}' --max-time 15 -X DELETE -H "Expect:" \
      "${connect_url}/connectors/${name}" || true)"
    if [[ "${http_code}" == "204" || "${http_code}" == "200" ]]; then
      echo "Deleted connector '${name}'."
    elif [[ "${http_code}" == "404" ]]; then
      echo "Connector '${name}' was not registered."
    else
      echo "Could not delete '${name}' (HTTP ${http_code})." >&2
    fi
  done
}

start_all() {
  echo "Starting infrastructure..."
  compose up -d "$@"
  wait_for_kafka
  echo "Initializing Kafka topics..."
  init_kafka
  wait_for_postgres
  register_debezium_connectors
  echo
  echo "Infrastructure is up, Kafka topics are initialized, and Debezium connectors are registered."
  echo "  Postgres:        localhost:5432"
  echo "  pgAdmin:         http://localhost:5050"
  echo "  Kafdrop:         http://localhost:9000"
  echo "  Schema Registry: http://localhost:8081"
  echo "  Debezium Connect: http://localhost:8083"
}

command="${1:-run}"
shift || true

case "$command" in
  run|up|start|up-all|start-all)
    start_all "$@"
    ;;
  down|stop)
    delete_debezium_connectors
    compose down "$@"
    ;;
  down-all)
    delete_debezium_connectors
    compose down -v "$@"
    ;;
  init)
    wait_for_kafka
    init_kafka
    ;;
  reset-pgadmin)
    container_id="$(docker ps -aq -f name=^food-ordering-pgadmin$)"
    if [ -n "$container_id" ]; then
      volume_id="$(docker inspect -f '{{ range .Mounts }}{{ if eq .Destination "/var/lib/pgadmin" }}{{ .Name }}{{ end }}{{ end }}' "$container_id" 2>/dev/null || true)"
      docker rm -f food-ordering-pgadmin >/dev/null 2>&1 || true
      if [ -n "$volume_id" ]; then
        docker volume rm "$volume_id" >/dev/null 2>&1 || true
      fi
    fi
    compose up -d pgAdmin
    echo "pgAdmin recreated. Open http://localhost:5050"
    ;;
  ps)
    compose ps "$@"
    ;;
  logs)
    compose logs -f "$@"
    ;;
  config)
    compose config "$@"
    ;;
  -h|--help|help)
    usage
    ;;
  *)
    echo "Unknown command: $command" >&2
    echo
    usage
    exit 1
    ;;
esac
