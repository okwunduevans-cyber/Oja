@ECHO OFF

REM Copyright 2015 the original authors.

REM

REM Licensed under the Apache License, Version 2.0 (the "License");

REM you may not use this file except in compliance with the License.

REM You may obtain a copy of the License at

REM

REM      https://www.apache.org/licenses/LICENSE-2.0

REM

REM Unless required by applicable law or agreed to in writing, software

REM distributed under the License is distributed on an "AS IS" BASIS,

REM WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.

REM See the License for the specific language governing permissions and

REM limitations under the License.



SETLOCAL



set DIRNAME=%~dp0

if "%DIRNAME%" == "" set DIRNAME=.

set APP_BASE_NAME=%~n0

set APP_HOME=%DIRNAME%



set DEFAULT_JVM_OPTS=



rem Find java.exe

if defined JAVA_HOME goto findJavaFromJavaHome



set JAVA_EXE=java.exe

%JAVA_EXE% -version >NUL 2>&1

if %ERRORLEVEL% equ 0 goto execute



echo.

echo ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH.

echo.

echo Please set the JAVA_HOME variable in your environment to match the

echo location of your Java installation.



goto fail



:findJavaFromJavaHome

set JAVA_HOME=%JAVA_HOME:"=%

set JAVA_EXE=%JAVA_HOME%in\java.exe



if exist "%JAVA_EXE%" goto execute



echo.

echo ERROR: JAVA_HOME is set to an invalid directory: %JAVA_HOME%

echo.

echo Please set the JAVA_HOME variable in your environment to match the

echo location of your Java installation.



goto fail



:execute

set CLASSPATH=%APP_HOME%\gradle\wrapper\gradle-wrapper.jar



rem Collect all arguments for the java command line

set CMD_LINE_ARGS=

set FIRST_ARG=1



:argLoop

if "%1" == "" goto argsDone



set ARG=%1

set CMD_LINE_ARGS=%CMD_LINE_ARGS% %ARG%

set FIRST_ARG=0

shift

goto argLoop



:argsDone



"%JAVA_EXE%" %DEFAULT_JVM_OPTS% %JAVA_OPTS% %GRADLE_OPTS% -classpath "%CLASSPATH%" org.gradle.wrapper.GradleWrapperMain %CMD_LINE_ARGS%



ENDLOCAL



:omega

