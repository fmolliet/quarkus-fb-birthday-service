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

#######################
## STAGE 1 - BUILD
#######################
FROM maven:3.8.1-openjdk-17-slim AS BUILDER
WORKDIR /app
COPY pom.xml ./
COPY src ./src
RUN mvn clean package

#######################
## STAGE 2 - RUN
#######################
FROM registry.access.redhat.com/ubi8/openjdk-17:1.14
WORKDIR /app
ENV LANGUAGE='en_US:en'

# We make four distinct layers so if there are application changes the library layers can be re-used
COPY --from=BUILDER /app/target/quarkus-app/lib/ /deployments/lib/
COPY --from=BUILDER /app/target/quarkus-app/*.jar /deployments/app.jar
COPY --from=BUILDER /app/target/quarkus-app/app/ /deployments/app/
COPY --from=BUILDER /app/target/quarkus-app/quarkus/ /deployments/quarkus/

EXPOSE 8080
ENV JAVA_OPTS="-Dquarkus.http.host=0.0.0.0 -Djava.util.logging.manager=org.jboss.logmanager.LogManager"
ENV JAVA_APP_JAR="/deployments/app.jar"
