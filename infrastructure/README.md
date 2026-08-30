# Infrastructure

Local dependencies and shared libraries for the food-ordering services.

- **docker-compose/** — Postgres, Kafka, Schema Registry, Debezium Connect, pgAdmin, and Kafdrop. Started with `compose.sh`. See [docker-compose/README.md](docker-compose/README.md).
- **helm/** — Kubernetes charts for the four application services. See [helm/food-ordering-system/README.md](helm/README.md).

Ollama is **not** part of Compose. Run it on the host (see below). Order-service uses it at `http://localhost:11434` locally, or `http://host.minikube.internal:11434` from Minikube.

## Ollama with mistral:7b

Order-service is configured for `mistral:7b` (`spring.ai.ollama.chat.options.model`).

Install and start the Homebrew service (listens on `127.0.0.1:11434`):

```bash
brew install ollama
brew services start ollama
```

Pull and load the model:

```bash
ollama pull mistral:7b
ollama run mistral:7b
```

`run` opens a chat prompt. For a one-shot check:

```bash
ollama run mistral:7b "hello"
```

The app does not need `run` after the model is pulled. Spring AI calls Ollama, which loads `mistral:7b` on demand.

```bash
brew services list
curl http://127.0.0.1:11434/api/tags
ollama list
```
