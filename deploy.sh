#!/bin/bash

echo ">>> Build start"
./gradlew clean build -x test

echo ">>> Kill existing Java process"
pkill -f 'java -jar' || true

echo ">>> Start new jar"
JAR_NAME=$(ls build/libs/*SNAPSHOT.jar | grep -v plain | head -n 1)
nohup java -jar "$JAR_NAME" > ../log.out 2>&1 &