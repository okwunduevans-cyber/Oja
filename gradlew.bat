@echo off
setlocal

set DIRNAME=%~dp0
if "%DIRNAME%"=="" set DIRNAME=.
for %%i in ("%DIRNAME%") do set APP_HOME=%%~fi

set WRAPPER_PROPS=%APP_HOME%\gradle\wrapper\gradle-wrapper.properties
if not exist "%WRAPPER_PROPS%" (
    echo ERROR: gradle-wrapper.properties not found. 1>&2
    goto fail
)

for /f "tokens=1,* delims==" %%A in ('findstr /R "^distributionUrl=" "%WRAPPER_PROPS%"') do set DIST_URL=%%B
if "%DIST_URL%"=="" (
    echo ERROR: distributionUrl not defined in gradle-wrapper.properties. 1>&2
    goto fail
)

set DIST_URL=%DIST_URL:\=%
for /f %%V in ('powershell -NoProfile -ExecutionPolicy Bypass -Command "param($$url) if ($$url -match 'gradle-([0-9A-Za-z\.-]+)-') { Write-Output $$matches[1] }" "%DIST_URL%"') do set GRADLE_VERSION=%%V
if "%GRADLE_VERSION%"=="" (
    echo ERROR: Unable to determine Gradle version from %DIST_URL%. 1>&2
    goto fail
)

set DIST_DIR=%APP_HOME%\.gradle-dist
set INSTALL_DIR=%DIST_DIR%\gradle-%GRADLE_VERSION%
set GRADLE_CMD=%INSTALL_DIR%\bin\gradle.bat

if not exist "%GRADLE_CMD%" (
    powershell -NoProfile -ExecutionPolicy Bypass -Command "param($$url,$$installDir) $ProgressPreference='SilentlyContinue'; $distDir = Split-Path -Parent $$installDir; if (!(Test-Path $distDir)) { New-Item -ItemType Directory -Path $distDir -Force | Out-Null; } if (Test-Path $$installDir) { Remove-Item $$installDir -Recurse -Force; } $zipPath = [System.IO.Path]::GetTempFileName(); Invoke-WebRequest -Uri $$url -OutFile $zipPath -UseBasicParsing; Add-Type -AssemblyName System.IO.Compression.FileSystem; [System.IO.Compression.ZipFile]::ExtractToDirectory($zipPath, $distDir); Remove-Item $zipPath" "%DIST_URL%" "%INSTALL_DIR%"
    if not exist "%GRADLE_CMD%" goto fail
)

call "%GRADLE_CMD%" %*
endlocal
exit /b %ERRORLEVEL%

:fail
set EXIT_CODE=%ERRORLEVEL%
if %EXIT_CODE% equ 0 set EXIT_CODE=1
endlocal
exit /b %EXIT_CODE%
