ARG DEBEZIUM_VERSION=3.5.2.Final

FROM maven:3.9-eclipse-temurin-21-alpine AS avro-jars
ARG CONFLUENT_VERSION=8.0.3
WORKDIR /build
COPY pom.xml .
RUN mvn -q -Dconfluent.version=${CONFLUENT_VERSION} \
      dependency:copy-dependencies \
      -DoutputDirectory=/jars \
      -DexcludeTypes=pom

FROM quay.io/debezium/connect:${DEBEZIUM_VERSION}
COPY --from=avro-jars --chown=kafka:kafka /jars/ /kafka/connect/confluent-avro/
