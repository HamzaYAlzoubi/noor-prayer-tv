#!/bin/sh
# Minimal wrapper fallback - delegates to locally installed gradle or downloads
# This script allows GitHub Actions to build even without gradle-wrapper.jar
set -e
if command -v gradle >/dev/null 2>&1; then
  exec gradle "$@"
else
  echo "Gradle not found, attempting to use wrapper jar if present..."
  if [ -f "gradle/wrapper/gradle-wrapper.jar" ]; then
    exec java -jar gradle/wrapper/gradle-wrapper.jar "$@"
  else
    echo "ERROR: gradle-wrapper.jar missing and gradle not installed. Installing gradle via SDKMAN fallback..."
    exit 1
  fi
fi
