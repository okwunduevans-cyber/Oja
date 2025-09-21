# Offline Build Support

The Gradle wrapper JAR is excluded from the repository to keep binaries out of version control.

When `gradlew` or `gradlew.bat` detect that `gradle/wrapper/gradle-wrapper.jar` is missing they:

1. Determine the desired Gradle version from `gradle/wrapper/gradle-wrapper.properties`.
2. Attempt to download the wrapper JAR directly from `repo.gradle.org`.
3. Fall back to downloading the configured Gradle distribution and extracting the wrapper JAR from it.
4. Delegate to a system-wide `gradle` installation if both downloads fail.

In offline environments you can still run builds by copying a compatible
`gradle-wrapper.jar` into `gradle/wrapper/` ahead of time (for example, from an
internal artifact cache). Once the file is present the wrapper scripts will use
it without reaching for the network.
