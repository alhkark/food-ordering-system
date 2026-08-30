#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
CONNECTORS_DIR="${SCRIPT_DIR}/connectors"
CONNECT_URL="${CONNECT_URL:-http://localhost:8083}"

shopt -s nullglob
CONNECTOR_FILES=("${CONNECTORS_DIR}"/*.json)
if (( ${#CONNECTOR_FILES[@]} == 0 )); then
  echo "No connector JSON files in ${CONNECTORS_DIR}" >&2
  exit 1
fi

wait_for_connect() {
  local max_attempts=60
  local attempt=1

  echo "Waiting for Debezium Connect at ${CONNECT_URL}..."
  until curl -sf "${CONNECT_URL}/connectors" >/dev/null; do
    if (( attempt >= max_attempts )); then
      echo "Connect did not become ready in time." >&2
      exit 1
    fi
    sleep 2
    ((attempt++))
  done
  echo "Connect is ready."
}

wait_for_schema_registry() {
  local max_attempts=60
  local attempt=1
  local schema_registry_url="${SCHEMA_REGISTRY_URL:-http://localhost:8081}"

  echo "Waiting for Schema Registry at ${schema_registry_url}..."
  until curl -sf "${schema_registry_url}/subjects" >/dev/null; do
    if (( attempt >= max_attempts )); then
      echo "Schema Registry did not become ready in time." >&2
      exit 1
    fi
    sleep 2
    ((attempt++))
  done
  echo "Schema Registry is ready."
}

config_json() {
  python3 -c 'import json, sys; print(json.dumps(json.load(sys.stdin)["config"]))' < "$1"
}

connector_name() {
  python3 -c 'import json, sys; print(json.load(sys.stdin)["name"])' < "$1"
}

put_connector() {
  local name="$1"
  local file="$2"
  local http_code
  local body

  body="$(mktemp)"
  http_code="$(curl -sS -o "${body}" -w '%{http_code}' \
    --max-time 60 \
    -X PUT \
    -H "Content-Type: application/json" \
    -H "Expect:" \
    --data "$(config_json "${file}")" \
    "${CONNECT_URL}/connectors/${name}/config")" || true

  if [[ "${http_code}" != 200 && "${http_code}" != 201 ]]; then
    echo "Failed to register '${name}' (HTTP ${http_code}):" >&2
    cat "${body}" >&2
    rm -f "${body}"
    exit 1
  fi
  rm -f "${body}"
}

wait_for_connect
wait_for_schema_registry

for connector_file in "${CONNECTOR_FILES[@]}"; do
  name="$(connector_name "${connector_file}")"
  echo "Registering connector '${name}'..."
  put_connector "${name}" "${connector_file}"
  echo "Registered '${name}'."
done

echo
echo "Connectors registered. GET ${CONNECT_URL}/connectors"
echo
echo "CDC topics (not the saga request/response topics):"
echo "  debezium_order_payment_outbox.order.payment_outbox"
echo "  debezium_order_restaurant_approval_outbox.order.restaurant_approval_outbox"
echo "  debezium_payment_order_outbox.payment.order_outbox"
echo "  debezium_restaurant_order_outbox.restaurant.order_outbox"
