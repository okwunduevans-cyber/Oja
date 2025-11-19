#!/usr/bin/env sh

APP_HOME="$(cd "$(dirname "$0")" && pwd)"
WRAPPER_JAR="$APP_HOME/gradle/wrapper/gradle-wrapper.jar"

if [ -f "$WRAPPER_JAR" ]; then
  exec "${JAVA_HOME:-$(command -v java)}" -jar "$WRAPPER_JAR" "$@"
fi

if command -v gradle >/dev/null 2>&1; then
  exec gradle "$@"
fi

echo "Gradle wrapper JAR not found and 'gradle' command unavailable. Install Gradle or generate the wrapper jar." >&2
exit 1
