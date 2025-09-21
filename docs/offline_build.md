# Offline Build Support

The Gradle wrapper JAR is excluded from the repository to keep binaries out of version control.
To allow fully offline builds, a Base64-encoded copy of `gradle-wrapper-8.7.jar` is stored at
`gradle/wrapper/gradle-wrapper.jar.base64`.

Both `gradlew` and `gradlew.bat` decode this embedded payload automatically whenever the wrapper
JAR is missing. This works without network access as long as Python 3, the POSIX `base64` utility,
OpenSSL, or PowerShell are available.

If none of the decoders are present, place the Gradle wrapper JAR at
`gradle/wrapper/gradle-wrapper.jar` manually before invoking the wrapper scripts.
