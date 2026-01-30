# Plugin Publish Conventions Gradle plugin

[![Build Status](https://img.shields.io/endpoint.svg?url=https%3A%2F%2Factions-badge.atrox.dev%2Fgradlex-org%2Fplugin-publish-conventions%2Fbadge%3Fref%3Dmain&style=flat)](https://actions-badge.atrox.dev/gradlex-org/plugin-publish-conventions/goto?ref=main)
[![Gradle Plugin Portal](https://img.shields.io/maven-metadata/v?label=Plugin%20Portal&metadataUrl=https%3A%2F%2Fplugins.gradle.org%2Fm2%2Forg%2Fgradlex%2Finternal%2Fplugin-publish-conventions%2Forg.gradlex.internal.plugin-publish-conventions.gradle.plugin%2Fmaven-metadata.xml)](https://plugins.gradle.org/plugin/org.gradlex.internal.plugin-publish-conventions)

**Note: This plugin is currently to be used within the GradleX organization only to develop `org.gradlex` plugins**

# Usage

**settings.gradle.kts**
```kotlin
plugins {
    id("org.gradlex.internal-build-conventions") version "0.10"
}
```

**build.gradle.kts**
```kotlin
publishingConventions {
    pluginPortal("${project.group}.${project.name}") {
        implementationClass("org.gradlex.buildparameters.BuildParametersPlugin")
        displayName("Build Parameters Gradle Plugin")
        description("Compile-safe access to parameters supplied to a Gradle build.")
        tags("gradlex", "parameters", "build parameters")
    }
    gitHub("https://github.com/gradlex-org/build-parameters")
    // ...
}
testingConventions {
    testGradleVersions("6.8.3", "6.9.4", "7.0.2", "8.0.2", "8.14.3")
}
```

# Disclaimer

Gradle and the Gradle logo are trademarks of Gradle, Inc.
The GradleX project is not endorsed by, affiliated with, or associated with Gradle or Gradle, Inc. in any way.
