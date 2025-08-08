#!/bin/bash

echo ">>> Build start"
./gradlew clean build -x test

echo ">>> Kill existing Java process"
pkill -f 'java -jar' || true

echo ">>> Start new jar"
JAR_NAME=$(ls build/libs/*SNAPSHOT.jar | grep -v plain | head -n 1)

# 프로필 prod 설정 추가
nohup SPRING_PROFILES_ACTIVE=prod java -jar "$JAR_NAME" > ../log.out 2>&1 &