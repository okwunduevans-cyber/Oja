@rem
@rem Copyright 2015 the original author or authors.
@rem
@rem Licensed under the Apache License, Version 2.0 (the "License");
@rem you may not use this file except in compliance with the License.
@rem You may obtain a copy of the License at
@rem
@rem      https://www.apache.org/licenses/LICENSE-2.0
@rem
@rem Unless required by applicable law or agreed to in writing, software
@rem distributed under the License is distributed on an "AS IS" BASIS,
@rem WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
@rem See the License for the specific language governing permissions and
@rem limitations under the License.
@rem

@if "%DEBUG%"=="" @echo off
@rem ##########################################################################
@rem
@rem  Gradle startup script for Windows
@rem
@rem ##########################################################################

@rem Set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" setlocal

set DIRNAME=%~dp0
if "%DIRNAME%"=="" set DIRNAME=.
@rem This is normally unused
set APP_BASE_NAME=%~n0
set APP_HOME=%DIRNAME%

@rem Resolve any "." and ".." in APP_HOME to make it shorter.
for %%i in ("%APP_HOME%") do set APP_HOME=%%~fi

set WRAPPER_JAR=%APP_HOME%\gradle\wrapper\gradle-wrapper.jar
if not exist "%WRAPPER_JAR%" (
    set WRAPPER_PROPERTIES=%APP_HOME%\gradle\wrapper\gradle-wrapper.properties
    if not exist "%WRAPPER_PROPERTIES%" (
        echo Missing gradle-wrapper.jar and gradle-wrapper.properties. 1>&2
        goto fail
    )
    set DOWNLOAD_RESULT=
    for /f "usebackq tokens=* delims=" %%i in (`powershell -NoLogo -NoProfile -Command ^
        "$props = Get-Content -Raw -Path '%WRAPPER_PROPERTIES%';" ^
        "if ($props -match 'distributionUrl=(.+)') {" ^
        "  $url = $Matches[1].Replace('\\:',':');" ^
        "  $distFile = [System.IO.Path]::GetFileName($url);" ^
        "  $base = [System.IO.Path]::GetFileNameWithoutExtension($distFile);" ^
        "  $version = $base.Replace('gradle-','').Replace('-bin','').Replace('-all','');" ^
        "  $tmpDir = [System.IO.Path]::Combine([System.IO.Path]::GetTempPath(), 'gradle-wrapper-' + [System.Guid]::NewGuid().ToString());" ^
        "  [System.IO.Directory]::CreateDirectory($tmpDir) | Out-Null;" ^
        "  $zipPath = [System.IO.Path]::Combine($tmpDir, 'distribution.zip');" ^
        "  try {" ^
        "    Invoke-WebRequest -Uri $url -OutFile $zipPath -UseBasicParsing | Out-Null;" ^
        "    Add-Type -AssemblyName System.IO.Compression.FileSystem;" ^
        "    $pluginEntry = 'gradle-' + $version + '/lib/plugins/gradle-wrapper-' + $version + '.jar';" ^
        "    $sharedEntry = 'gradle-' + $version + '/lib/gradle-wrapper-shared-' + $version + '.jar';" ^
        "    $cliEntry = 'gradle-' + $version + '/lib/gradle-cli-' + $version + '.jar';" ^
        "    $filesEntry = 'gradle-' + $version + '/lib/gradle-files-' + $version + '.jar';" ^
        "    $pluginJar = [System.IO.Path]::Combine($tmpDir, 'plugin.jar');" ^
        "    $sharedJar = [System.IO.Path]::Combine($tmpDir, 'shared.jar');" ^
        "    $cliJar = [System.IO.Path]::Combine($tmpDir, 'cli.jar');" ^
        "    $filesJar = [System.IO.Path]::Combine($tmpDir, 'files.jar');" ^
        "    $zip = [System.IO.Compression.ZipFile]::OpenRead($zipPath);" ^
        "    $plugin = $zip.GetEntry($pluginEntry);" ^
        "    $shared = $zip.GetEntry($sharedEntry);" ^
        "    $cli = $zip.GetEntry($cliEntry);" ^
        "    $files = $zip.GetEntry($filesEntry);" ^
        "    if (-not $plugin -or -not $shared -or -not $cli -or -not $files) { throw 'Required Gradle wrapper entries not found.' }" ^
        "    $plugin.ExtractToFile($pluginJar, $true);" ^
        "    $shared.ExtractToFile($sharedJar, $true);" ^
        "    $cli.ExtractToFile($cliJar, $true);" ^
        "    $files.ExtractToFile($filesJar, $true);" ^
        "    $zip.Dispose();" ^
        "    $extractDir = [System.IO.Path]::Combine($tmpDir, 'extracted');" ^
        "    [System.IO.Directory]::CreateDirectory($extractDir) | Out-Null;" ^
        "    [System.IO.Compression.ZipFile]::ExtractToDirectory($pluginJar, $extractDir, $true);" ^
        "    [System.IO.Compression.ZipFile]::ExtractToDirectory($sharedJar, $extractDir, $true);" ^
        "    [System.IO.Compression.ZipFile]::ExtractToDirectory($cliJar, $extractDir, $true);" ^
        "    [System.IO.Compression.ZipFile]::ExtractToDirectory($filesJar, $extractDir, $true);" ^
        "    $wrapperJar = '%WRAPPER_JAR%';" ^
        "    if (Test-Path $wrapperJar) { Remove-Item $wrapperJar -Force }" ^
        "    [System.IO.Compression.ZipFile]::CreateFromDirectory($extractDir, $wrapperJar, [System.IO.Compression.CompressionLevel]::Optimal, $false);" ^
        "    Remove-Item $tmpDir -Recurse -Force;" ^
        "    'OK'" ^
        "  } catch {" ^
        "    if (Test-Path $tmpDir) { Remove-Item $tmpDir -Recurse -Force -ErrorAction SilentlyContinue }" ^
        "    Write-Error $_;" ^
        "    'ERROR'" ^
        "  }" ^
        "} else {" ^
        "  Write-Error 'distributionUrl missing from gradle-wrapper.properties';" ^
        "  'ERROR'" ^
        "}"`) do set DOWNLOAD_RESULT=%%i
    if /I not "%DOWNLOAD_RESULT%"=="OK" goto fail
)

@rem Add default JVM options here. You can also use JAVA_OPTS and GRADLE_OPTS to pass JVM options to this script.
set DEFAULT_JVM_OPTS="-Xmx64m" "-Xms64m"

@rem Find java.exe
if defined JAVA_HOME goto findJavaFromJavaHome

set JAVA_EXE=java.exe
%JAVA_EXE% -version >NUL 2>&1
if %ERRORLEVEL% equ 0 goto execute

echo. 1>&2
echo ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH. 1>&2
echo. 1>&2
echo Please set the JAVA_HOME variable in your environment to match the 1>&2
echo location of your Java installation. 1>&2

goto fail

:findJavaFromJavaHome
set JAVA_HOME=%JAVA_HOME:"=%
set JAVA_EXE=%JAVA_HOME%/bin/java.exe

if exist "%JAVA_EXE%" goto execute

echo. 1>&2
echo ERROR: JAVA_HOME is set to an invalid directory: %JAVA_HOME% 1>&2
echo. 1>&2
echo Please set the JAVA_HOME variable in your environment to match the 1>&2
echo location of your Java installation. 1>&2

goto fail

:execute
@rem Setup the command line

set CLASSPATH=%APP_HOME%\gradle\wrapper\gradle-wrapper.jar


@rem Execute Gradle
"%JAVA_EXE%" %DEFAULT_JVM_OPTS% %JAVA_OPTS% %GRADLE_OPTS% "-Dorg.gradle.appname=%APP_BASE_NAME%" -classpath "%CLASSPATH%" org.gradle.wrapper.GradleWrapperMain %*

:end
@rem End local scope for the variables with windows NT shell
if %ERRORLEVEL% equ 0 goto mainEnd

:fail
rem Set variable GRADLE_EXIT_CONSOLE if you need the _script_ return code instead of
rem the _cmd.exe /c_ return code!
set EXIT_CODE=%ERRORLEVEL%
if %EXIT_CODE% equ 0 set EXIT_CODE=1
if not ""=="%GRADLE_EXIT_CONSOLE%" exit %EXIT_CODE%
exit /b %EXIT_CODE%

:mainEnd
if "%OS%"=="Windows_NT" endlocal

:omega
