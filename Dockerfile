####
# This Dockerfile is used in order to build a container that runs the Quarkus application in native (no JVM) mode.
#
# Before building the container image run:
#
# ./mvnw package -Pnative
#
# Then, build the image with:
#
# docker build -f src/main/docker/Dockerfile.native -t quarkus/birthday-service .
#
# Then run the container using:
#
# docker run -i --rm -p 8080:8080 quarkus/birthday-service
#
###
FROM registry.access.redhat.com/ubi8/ubi-minimal:8.10
WORKDIR /work/

ENV TZ=America/Sao_Paulo
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

RUN chown 1001 /work \
    && chmod "g+rwX" /work \
    && chown 1001:root /work
COPY --chown=1001:root target/*-runner /work/application

# HealthCheck
COPY --chown=1001:root healthcheck.sh /usr/local/bin/healthcheck.sh
HEALTHCHECK CMD /usr/local/bin/healthcheck.sh

EXPOSE 8080
USER 1001

ENTRYPOINT ["./application", "-Dquarkus.http.host=0.0.0.0"]