#!/usr/bin/env bash

set -e

if [[ $TRAVIS_BRANCH == "master" ]]; then
  echo "[INFO] Build executable JAR"
  mill server.assembly

  echo "[INFO] Publish JAR to Heroku"
  heroku plugins:install java
  heroku deploy:jar "out/server/assembly/dest/out.jar" --app "cowsay-online" --jdk 11
else
  echo "[INFO] No deployment to perform"
fi


