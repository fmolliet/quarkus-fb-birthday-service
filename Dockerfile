
# Estágio de compilação
FROM quay.io/quarkus/centos-quarkus-maven:21.2.0-java11 AS build
WORKDIR /app
COPY . /app
RUN mvn clean package -Pnative

# Estágio de execução
FROM registry.access.redhat.com/ubi8/ubi-minimal:8.6
WORKDIR /work/

ENV TZ=America/Sao_Paulo

RUN chown 1001 /work \
    && chmod "g+rwX" /work \
    && chown 1001:root /work
COPY --from=build --chown=1001:root /app/target/*-runner /work/application

COPY --chown=1001:root healthcheck.sh /usr/local/bin/healthcheck.sh

# HealthCheck
HEALTHCHECK CMD /usr/local/bin/healthcheck.sh

EXPOSE 8080
USER 1001

CMD ["./application", "-Dquarkus.http.host=0.0.0.0"]
