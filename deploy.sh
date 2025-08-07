#!/bin/bash

echo ">>> Build start"
./gradlew clean build -x test # test 없이 build

echo ">>> Kill existing Java process"
pkill -f 'java -jar' || true

echo ">>> Start new jar"
JAR_NAME=$(ls build/libs/*.jar | head -n 1)
nohup java -jar $JAR_NAME > ../log.out 2>&1 &
