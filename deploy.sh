#!/bin/bash
set -euxo pipefail

echo ">>> Build start"
cd ~/sunsak
./gradlew clean build -x test

echo ">>> Kill existing Java process"
pkill -f 'java -jar' || true

# (옵션) 혹시 남은 파일 락이 있을 수 있으니 잠깐 대기
for i in {1..10}; do
  if sudo lsof /home/ubuntu/sunsak-db.mv.db >/dev/null 2>&1; then
    echo ">>> Waiting H2 lock release..."
    sleep 1
  else
    break
  fi
done

export SPRING_PROFILES_ACTIVE=prod

echo ">>> Start new jar"
JAR_NAME=$(ls build/libs/*SNAPSHOT.jar | grep -v plain | head -n 1)

# 환경변수 확인 (로그만)
echo ">>> Environment variables"
echo "MYSQL_USER=${MYSQL_USER:-<unset>}"
echo "MYSQL_PASSWORD=${MYSQL_PASSWORD:-<unset>}"
echo "REDIS_PASSWORD=${REDIS_PASSWORD:-<unset>}"
echo "JWT_SECRET=${JWT_SECRET:-<unset>}"
echo "OCR_SECRET_KEY=${OCR_SECRET_KEY:-<unset>}"
echo "OCR_API_INVOKE_URL=${OCR_API_INVOKE_URL:-<unset>}"

# 실행: 프로필 인자로도 강제
nohup java -jar "$JAR_NAME" --spring.profiles.active=prod > ../log.out 2>&1 &

echo ">>> Wai
