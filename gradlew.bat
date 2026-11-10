@echo off
setlocal
set "APP_HOME=%~dp0"
if defined JAVA_HOME (
    set "JAVA_EXE=%JAVA_HOME%\bin\java.exe"
) else (
    set "JAVA_EXE=java.exe"
)
if not exist "%APP_HOME%gradle\wrapper\gradle-wrapper.jar" (
    echo Gradle wrapper JAR is missing from gradle\wrapper. 1>&2
    exit /b 1
)
"%JAVA_EXE%" -Xmx64m -Xms64m %JAVA_OPTS% %GRADLE_OPTS% "-Dorg.gradle.appname=gradlew" -classpath "%APP_HOME%gradle\wrapper\gradle-wrapper.jar" org.gradle.wrapper.GradleWrapperMain %*
exit /b %ERRORLEVEL%

