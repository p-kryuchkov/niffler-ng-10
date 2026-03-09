#!/bin/bash
source ./docker.properties

export PROFILE=local

export HEAD_COMMIT_MESSAGE="local build"

COMPOSE_FILE=docker-compose-local.yml

docker compose -f "$COMPOSE_FILE" down

docker_containers=$(docker ps -a -q)

if [ ! -z "$docker_containers" ]; then
  echo "### Stop containers: $docker_containers ###"
  docker stop $docker_containers
  docker rm $docker_containers
fi

echo '### Java version ###'
java --version

bash ./gradlew clean

docker compose -f "$COMPOSE_FILE" up -d
docker ps -a