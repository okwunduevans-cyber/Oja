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
@rem SPDX-License-Identifier: Apache-2.0
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

set WRAPPER_JAR=%APP_HOME%\gradle\wrapper\gradle-wrapper.jar
set WRAPPER_PROPS=%APP_HOME%\gradle\wrapper\gradle-wrapper.properties
set WRAPPER_BASE64=%APP_HOME%\gradle\wrapper\gradle-wrapper.jar.base64
if not exist "%WRAPPER_JAR%" (
    if exist "%WRAPPER_BASE64%" (
        powershell -NoProfile -Command "try { $bytes = [Convert]::FromBase64String((Get-Content -Raw -LiteralPath '%WRAPPER_BASE64%')); [IO.Directory]::CreateDirectory([IO.Path]::GetDirectoryName('%WRAPPER_JAR%')) | Out-Null; [IO.File]::WriteAllBytes('%WRAPPER_JAR%', $bytes) } catch { exit 1 }"
    )
    if exist "%WRAPPER_JAR%" goto wrapperReady
    set WRAPPER_VERSION=8.7
    set DISTRIBUTION_URL=
    if exist "%WRAPPER_PROPS%" (
        for /f "usebackq tokens=1* delims==" %%A in ("%WRAPPER_PROPS%") do (
            if /I "%%A"=="distributionUrl" set DISTRIBUTION_URL=%%B
        )
    )
    if defined DISTRIBUTION_URL set DISTRIBUTION_URL=%DISTRIBUTION_URL:\=%
    if defined DISTRIBUTION_URL (
        for /f "tokens=2 delims=-" %%A in ("%DISTRIBUTION_URL%") do set WRAPPER_VERSION=%%A
    )
    set WRAPPER_DOWNLOAD_URL=https://repo.gradle.org/gradle/libs-releases-local/org/gradle/gradle-wrapper/%WRAPPER_VERSION%/gradle-wrapper-%WRAPPER_VERSION%.jar
    powershell -NoProfile -Command "try { (New-Object Net.WebClient).DownloadFile('%WRAPPER_DOWNLOAD_URL%', '%WRAPPER_JAR%') } catch { exit 1 }"
    if not exist "%WRAPPER_JAR%" (
        if not defined DISTRIBUTION_URL set DISTRIBUTION_URL=https://services.gradle.org/distributions/gradle-%WRAPPER_VERSION%-bin.zip
        powershell -NoProfile -Command "
            Add-Type -AssemblyName System.IO.Compression.FileSystem
            function Get-WrapperBytes([string] $ArchivePath) {
                $zip = [System.IO.Compression.ZipFile]::OpenRead($ArchivePath)
                try {
                    $entry = $zip.Entries | Where-Object { $_.FullName -like 'gradle-*/lib/plugins/gradle-wrapper-*.jar' } | Select-Object -First 1
                    if (-not $entry) {
                        $entry = $zip.Entries | Where-Object { $_.FullName -like 'gradle-*/lib/gradle-wrapper-*.jar' } | Select-Object -First 1
                    }
                    if (-not $entry) { throw 'gradle-wrapper jar not found' }
                    $pluginStream = $entry.Open()
                    try {
                        $pluginBuffer = New-Object System.IO.MemoryStream
                        $pluginStream.CopyTo($pluginBuffer)
                    } finally {
                        $pluginStream.Dispose()
                    }
                    $pluginBuffer.Position = 0
                    $pluginArchive = New-Object System.IO.Compression.ZipArchive($pluginBuffer, [System.IO.Compression.ZipArchiveMode]::Read, $false)
                    try {
                        $inner = $pluginArchive.GetEntry('gradle-wrapper.jar')
                        if ($inner) {
                            $innerStream = $inner.Open()
                            try {
                                $result = New-Object System.IO.MemoryStream
                                $innerStream.CopyTo($result)
                            } finally {
                                $innerStream.Dispose()
                            }
                            $bytes = $result.ToArray()
                        } else {
                            $bytes = $pluginBuffer.ToArray()
                        }
                    } finally {
                        $pluginArchive.Dispose()
                    }
                } finally {
                    $zip.Dispose()
                }
                return $bytes
            }

            function Ensure-WrapperManifest([string] $JarPath) {
                $fs = [System.IO.File]::Open($JarPath, [System.IO.FileMode]::Open, [System.IO.FileAccess]::ReadWrite)
                try {
                    $archive = New-Object System.IO.Compression.ZipArchive($fs, [System.IO.Compression.ZipArchiveMode]::Update)
                    $entry = $archive.GetEntry('META-INF/MANIFEST.MF')
                    if (-not $entry) { return }
                    $reader = New-Object System.IO.StreamReader($entry.Open())
                    $content = $reader.ReadToEnd()
                    $reader.Dispose()
                    $mainPresent = $content -match '(?im)^Main-Class:'
                    $nativePresent = $content -match '(?im)^Enable-Native-Access:'
                    if ($mainPresent -and $nativePresent) { return }
                    $lines = $content -split "`r?`n" | Where-Object { $_ }
                    if (-not $mainPresent) { $lines += 'Main-Class: org.gradle.wrapper.GradleWrapperMain' }
                    if (-not $nativePresent) { $lines += 'Enable-Native-Access: ALL-UNNAMED' }
                    $entry.Delete()
                    $newEntry = $archive.CreateEntry('META-INF/MANIFEST.MF', [System.IO.Compression.CompressionLevel]::NoCompression)
                    $writer = New-Object System.IO.StreamWriter($newEntry.Open())
                    $writer.Write(([string]::Join("`n", $lines) + "`n"))
                    $writer.Dispose()
                } finally {
                    $archive.Dispose()
                    $fs.Dispose()
                }
            }

            try {
                $tmp = New-Item -ItemType Directory -Path ([System.IO.Path]::Combine([System.IO.Path]::GetTempPath(),[System.Guid]::NewGuid().ToString()))
                $archive = Join-Path $tmp.FullName 'gradle-distribution.zip'
                (New-Object Net.WebClient).DownloadFile('%DISTRIBUTION_URL%', $archive)
                $bytes = Get-WrapperBytes $archive
                [System.IO.File]::WriteAllBytes('%WRAPPER_JAR%', $bytes)
                Ensure-WrapperManifest '%WRAPPER_JAR%'
            } catch {
                if (Test-Path $tmp) { Remove-Item $tmp -Recurse -Force }
                exit 1
            }
            if (Test-Path $tmp) { Remove-Item $tmp -Recurse -Force }
        "
    )
    if exist "%WRAPPER_JAR%" goto wrapperReady
    echo Gradle wrapper JAR missing; attempting to use system Gradle... 1>&2
    gradle %*
    if %ERRORLEVEL% equ 0 goto end
    echo Failed to run Gradle because the wrapper JAR is absent and could not be downloaded. 1>&2
    goto fail
)

:wrapperReady

@rem Resolve any "." and ".." in APP_HOME to make it shorter.
for %%i in ("%APP_HOME%") do set APP_HOME=%%~fi

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

set CLASSPATH=


@rem Execute Gradle
"%JAVA_EXE%" %DEFAULT_JVM_OPTS% %JAVA_OPTS% %GRADLE_OPTS% "-Dorg.gradle.appname=%APP_BASE_NAME%" -classpath "%CLASSPATH%" -jar "%WRAPPER_JAR%" %*

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
