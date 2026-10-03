#!/bin/sh
# Launcher. The real gradle-wrapper.jar could not be bundled (no network when this was generated).
# If it is missing: install Gradle 9.2+ and run `gradle wrapper` once, or just use `gradle build`.
DIR=$(cd "$(dirname "$0")" && pwd)
JAR="$DIR/gradle/wrapper/gradle-wrapper.jar"
if [ -f "$JAR" ]; then
  exec java -classpath "$JAR" org.gradle.wrapper.GradleWrapperMain "$@"
elif command -v gradle >/dev/null 2>&1; then
  exec gradle "$@"
else
  echo "gradle-wrapper.jar missing and gradle not installed. Install Gradle 9.2+ and run: gradle wrapper" >&2
  exit 1
fi
