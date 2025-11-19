@ECHO OFF
SETLOCAL
SET APP_HOME=%~dp0
SET WRAPPER_JAR=%APP_HOME%gradle\wrapper\gradle-wrapper.jar
IF EXIST "%WRAPPER_JAR%" (
  "%JAVA_HOME%\bin\java.exe" -jar "%WRAPPER_JAR%" %*
  EXIT /B %ERRORLEVEL%
)
WHERE gradle >NUL 2>&1
IF %ERRORLEVEL% EQU 0 (
  gradle %*
  EXIT /B %ERRORLEVEL%
)
ECHO Gradle wrapper JAR not found and 'gradle' command unavailable. Install Gradle or generate the wrapper jar.
EXIT /B 1
