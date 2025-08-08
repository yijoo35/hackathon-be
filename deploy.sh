#!/bin/bash
set -euo pipefail

echo ">>> Build start"
./gradlew clean build -x test

echo ">>> Kill existing Java process"
pkill -f 'java -jar' || true

# 환경변수 필수 체크
required=(MYSQL_USER MYSQL_PASSWORD REDIS_PASSWORD JWT_SECRET OCR_SECRET_KEY OCR_API_INVOKE_URL)
for k in "${required[@]}"; do
  if [ -z "${!k:-}" ]; then
    echo "ENV MISSING: $k"
    exit 1
  fi
done

echo ">>> Start new jar"
JAR_NAME=$(ls build/libs/*SNAPSHOT.jar | grep -v plain | head -n 1)

# **수정된 부분: `nohup` 명령어에 `-D` 옵션으로 환경 변수 직접 전달**
nohup java \
  -Dspring.datasource.username="$MYSQL_USER" \
  -Dspring.datasource.password="$MYSQL_PASSWORD" \
  -Dspring.data.redis.password="$REDIS_PASSWORD" \
  -Djwt.secret="$JWT_SECRET" \
  -Docr.secret.key="$OCR_SECRET_KEY" \
  -Docr.api.invoke.url="$OCR_API_INVOKE_URL" \
  -jar "$JAR_NAME" > ../log.out 2>&1 &

echo "Launched PID=$!"