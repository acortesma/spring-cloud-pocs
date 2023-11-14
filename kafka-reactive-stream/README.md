# poc-spring-cloud-kafka-reactive-stream
This project connects to kafka to read and send events. This project contains:
- Basic configuration to connect to kafka.
- Two topics defined by configuration: "my-topic" and "my-topic-stream". And two processes that consume from the same input topic "my-topic".
- A docker-compose that raises the necessary kafka environment: kafka+zookeper+kafkaUI.
- A couple of integration test examples

## Develop Environment Configuration

First we start kafka in order to have the environment ready.
````shell script
docker-compose -f docker-compose.yml up -d
````

Then you can start the application from your IDE or with the maven command:
````shell script
mvn spring-boot:run
````

## Build image docker

To build a local docker image of the service you can run the command

````shell script
mvn clean package jib:dockerBuild
````

