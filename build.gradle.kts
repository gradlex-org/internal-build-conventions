plugins {
    id("java-gradle-plugin")
    id("org.gradlex.build-parameters") version "1.4.4"
    id("org.gradlex.internal.plugin-publish-conventions") version "0.6"
}

version = "0.7"

dependencies {
    implementation("com.diffplug.spotless:spotless-plugin-gradle:8.0.0") {
        // Exclude transitive dependencies of JGit as we do not need git functionality.
        // We can't exclude JGit itself as types are referenced in SpotlessTask.class.
        exclude("com.googlecode.javaewah", "JavaEWAH")
        exclude("commons-codec", "commons-codec")
        exclude("org.slf4j", "slf4j-api")
    }
    implementation("com.gradle.publish:plugin-publish-plugin:2.0.0")
    implementation("com.gradle:common-custom-user-data-gradle-plugin:2.4.0")
    implementation("com.gradle:develocity-gradle-plugin:4.2.2")
    implementation("com.gradleup.nmcp:nmcp:1.2.0")
    implementation("org.asciidoctor:asciidoctor-gradle-jvm:4.0.5")
    implementation("org.gradlex:jvm-dependency-conflict-resolution:2.4")
    implementation("org.gradlex:reproducible-builds:1.1")
}

dependencies.constraints {
    implementation("org.jetbrains:annotations:13.0!!") {
        because("This version is enforced by Gradle through the Kotlin plugin")
    }
}

// ==== the following can be remove once we update the onventions to '0.7'
group = "org.gradlex"
java { toolchain.languageVersion = JavaLanguageVersion.of(17) }
tasks.checkstyleMain { exclude("buildparameters/**") }
// ====

buildParameters {
    pluginId("org.gradlex.internal.gradlex-build-parameters")
    bool("ci") {
        description = "Whether or not the build is running in a CI environment"
        fromEnvironment()
        defaultValue = false
    }
    group("signing") {
        // allow to disable signing for locat testing
        bool("disable") {
            defaultValue = false
        }
        // key and passphrase need default values because SigningExtension.useInMemoryPgpKeys does not accept providers
        description = "Details about artifact signing"
        string("key") {
            description = "The ID of the PGP key to use for signing artifacts"
            fromEnvironment()
            defaultValue = "UNSET"
        }
        string("passphrase") {
            description = "The passphrase for the PGP key specified by signing.key"
            fromEnvironment()
            defaultValue = "UNSET"
        }
    }
    group("pluginPortal") {
        // The publish-plugin reads these values directly from System.env. We model them here
        // for completeness and documentation purposes.
        description = "Credentials for publishing to the plugin portal"
        string("key") {
            description = "The Plugin portal key for publishing the plugin"
            fromEnvironment("GRADLE_PUBLISH_KEY")
        }
        string("secret") {
            description = "The Plugin portal secret for publishing the plugin"
            fromEnvironment("GRADLE_PUBLISH_SECRET")
        }
    }

    group("mavenCentral") {
        description = "Credentials for publishing to Maven Central"
        string("username") {
            description = "The Maven Central username for publishing"
            fromEnvironment()
        }
        string("password") {
            description = "The Maven Central password for publishing"
            fromEnvironment()
        }
    }
}

pluginPublishConventions {
    id("${project.group}.${project.name}")
    implementationClass("org.gradlex.conventions.plugin.GradleXPluginConventionsPlugin")
    displayName("Conventions for building Gradle plugins")
    description("Conventions for building Gradle plugins used by all projects in the GradleX organisation.")
    tags("gradlex", "conventions", "publish", "plugins")
    gitHub("https://github.com/gradlex-org/plugin-publish-conventions")
    developer {
        id = "britter"
        name = "Benedikt Ritter"
        email = "benedikt@gradlex.org"
    }
    developer {
        id = "jjohannes"
        name = "Jendrik Johannes"
        email = "jendrik@gradlex.org"
    }
    developer {
        id = "ljacomet"
        name = "Louis Jacomet"
        email = "louis@gradlex.org"
    }
}

// Do not publish a marker for 'build-parameters'
gradlePlugin.plugins.removeAll { it.name == "build-parameters" }
