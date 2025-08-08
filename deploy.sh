#!/bin/bash

echo ">>> Build start"
cd ~/sunsak  # 명시적으로 sunsak 폴더로 이동 (안정성)
./gradlew clean build -x test

echo ">>> Kill existing Java process"
pkill -f 'java -jar' || true

echo ">>> Start new jar"
JAR_NAME=$(ls build/libs/*SNAPSHOT.jar | grep -v plain | head -n 1)

# 환경변수 확인
echo ">>> Environment variables"
echo "MYSQL_USER=$MYSQL_USER"
echo "MYSQL_PASSWORD=$MYSQL_PASSWORD"
echo "REDIS_PASSWORD=$REDIS_PASSWORD"
echo "JWT_SECRET=$JWT_SECRET"
echo "OCR_SECRET_KEY=$OCR_SECRET_KEY"
echo "OCR_API_INVOKE_URL=$OCR_API_INVOKE_URL"

nohup java -jar "$JAR_NAME" > ../log.out 2>&1 &

# 서버가 기동되는지 확인용 (tail 로그)
echo ">>> Waiting for server to start..."
sleep 5
tail -n 20 ../log.out
