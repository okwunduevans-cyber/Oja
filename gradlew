#!/bin/sh

# Simplified Gradle wrapper script that keeps the repository free of binary wrapper jars.
# The script reads gradle/wrapper/gradle-wrapper.properties, downloads the declared
# distribution on demand, caches it inside .gradle-dist/, and then executes the
# distribution's gradle binary with the provided arguments.

set -e

APP_HOME=$(cd "$(dirname "$0")" >/dev/null 2>&1 && pwd)
WRAPPER_PROPS="$APP_HOME/gradle/wrapper/gradle-wrapper.properties"

if [ ! -f "$WRAPPER_PROPS" ]; then
    echo "ERROR: gradle-wrapper.properties not found." >&2
    exit 1
fi

distributionUrl=$(grep '^distributionUrl=' "$WRAPPER_PROPS" | tail -n 1 | cut -d'=' -f2-)
distributionUrl=$(printf '%s' "$distributionUrl" | sed 's#\\##g')
if [ -z "$distributionUrl" ]; then
    echo "ERROR: distributionUrl not defined in gradle-wrapper.properties." >&2
    exit 1
fi

version=$(printf '%s' "$distributionUrl" | sed -n 's/.*gradle-\([0-9][0-9A-Za-z\.-]*\)-.*/\1/p')
if [ -z "$version" ]; then
    echo "ERROR: Unable to determine Gradle version from $distributionUrl" >&2
    exit 1
fi

DIST_DIR="$APP_HOME/.gradle-dist"
INSTALL_DIR="$DIST_DIR/gradle-$version"
GRADLE_BIN="$INSTALL_DIR/bin/gradle"

if [ ! -x "$GRADLE_BIN" ]; then
    tmpDist=$(mktemp "${TMPDIR:-/tmp}/gradle-dist-XXXXXX.zip") || {
        echo "ERROR: Unable to create temporary file for Gradle download." >&2
        exit 1
    }
    echo "Downloading Gradle $version from $distributionUrl" >&2
    if command -v curl >/dev/null 2>&1; then
        if ! curl -fL "$distributionUrl" -o "$tmpDist"; then
            rm -f "$tmpDist"
            echo "ERROR: Failed to download Gradle using curl." >&2
            exit 1
        fi
    elif command -v wget >/dev/null 2>&1; then
        if ! wget -O "$tmpDist" "$distributionUrl"; then
            rm -f "$tmpDist"
            echo "ERROR: Failed to download Gradle using wget." >&2
            exit 1
        fi
    else
        rm -f "$tmpDist"
        echo "ERROR: curl or wget is required to download the Gradle distribution." >&2
        exit 1
    fi

    if ! command -v unzip >/dev/null 2>&1; then
        rm -f "$tmpDist"
        echo "ERROR: unzip is required to extract the Gradle distribution." >&2
        exit 1
    fi

    mkdir -p "$DIST_DIR"
    rm -rf "$INSTALL_DIR"
    if ! unzip -q "$tmpDist" -d "$DIST_DIR"; then
        rm -f "$tmpDist"
        echo "ERROR: Failed to extract Gradle distribution." >&2
        exit 1
    fi
    rm -f "$tmpDist"
fi

exec "$GRADLE_BIN" "$@"
