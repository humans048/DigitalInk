#!/bin/sh
APP_HOME=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd) || exit 1
if [ -n "$JAVA_HOME" ]; then
    JAVA_EXE="$JAVA_HOME/bin/java"
else
    JAVA_EXE=java
fi
if [ ! -f "$APP_HOME/gradle/wrapper/gradle-wrapper.jar" ]; then
    printf '%s\n' 'Gradle wrapper JAR is missing from gradle/wrapper.' >&2
    exit 1
fi
exec "$JAVA_EXE" -Xmx64m -Xms64m ${JAVA_OPTS:-} ${GRADLE_OPTS:-} \
    -Dorg.gradle.appname=gradlew \
    -classpath "$APP_HOME/gradle/wrapper/gradle-wrapper.jar" \
    org.gradle.wrapper.GradleWrapperMain "$@"

