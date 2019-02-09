#!/usr/bin/env bash

set -e

echo "[INFO] Build executable JAR"
mill server.assembly

echo "[INFO] Publish JAR to Heroku"
heroku deploy:jar "out/server/assembly/dest/out.jar" --app "cowsay-online" --jdk 11
