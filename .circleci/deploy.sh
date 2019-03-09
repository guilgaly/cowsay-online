#!/usr/bin/env bash

set -e

if [[ $CIRCLE_BRANCH == "master" ]]; then
  echo "[INFO] Deploy JAR to Heroku"
  heroku deploy:jar "out/server/assembly/dest/out.jar" --app "cowsay-online" --jdk 11
else
  echo "[INFO] No deployment to perform"
fi
