#!/bin/bash

echo ">>> Build start"
./gradlew clean build -x test

echo ">>> Kill existing Java process"
pkill -f 'java -jar' || true

echo ">>> Start new jar"
JAR_NAME=$(ls build/libs/*SNAPSHOT.jar | grep -v plain | head -n 1)

# 환경변수 확인(로그 찍기, 테스트용)
echo "MYSQL_USER is $MYSQL_USER"
echo "MYSQL_PASSWORD is $MYSQL_PASSWORD"
echo "REDIS_PASSWORD is $REDIS_PASSWORD"
echo "JWT_SECRET is $JWT_SECRET"
echo "OCR_SECRET_KEY is $OCR_SECRET_KEY"
echo "OCR_API_INVOKE_URL is $OCR_API_INVOKE_URL"

nohup java -jar "$JAR_NAME" > ../log.out 2>&1 &
