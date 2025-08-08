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

# 디버그 (마스킹 없이 서버 로그에 남으니, 문제 해결 후엔 지워도 됨)
env | grep -E 'MYSQL|REDIS|JWT|OCR' || true

nohup java -jar "$JAR_NAME" > ../log.out 2>&1 &
echo "Launched PID=$!"
