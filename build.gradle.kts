plugins {
    id("java-gradle-plugin")
    id("org.gradlex.build-parameters") version "1.4.4"
    id("org.gradlex.internal.plugin-publish-conventions") version "0.6"
}

version = "0.7"

dependencies {
    implementation("com.diffplug.spotless:spotless-plugin-gradle:8.0.0")
    implementation("com.gradle.publish:plugin-publish-plugin:2.0.0")
    implementation("com.gradle:common-custom-user-data-gradle-plugin:2.4.0")
    implementation("com.gradle:develocity-gradle-plugin:4.2.2")
    implementation("org.asciidoctor:asciidoctor-gradle-jvm:4.0.5")
    implementation("org.gradlex:jvm-dependency-conflict-resolution:2.4")
    implementation("org.gradlex:reproducible-builds:1.1")
}

// ==== the following can be remove once we update the onventions to '0.7'
group = "org.gradlex"
java { toolchain.languageVersion = JavaLanguageVersion.of(17) }
tasks.checkstyleMain { exclude("buildparameters/**") }
buildParameters {
    pluginId("gradlexbuild.build-parameters")
    bool("ci") {
        description.set("Whether or not the build is running in a CI environment")
        fromEnvironment()
        defaultValue.set(false)
    }
    group("signing") {
        // key and passphrase need default values because SigningExtension.useInMemoryPgpKeys does not accept providers
        description.set("Details about artifact signing")
        string("key") {
            description.set("The ID of the PGP key to use for signing artifacts")
            fromEnvironment()
            defaultValue.set("UNSET")
        }
        string("passphrase") {
            description.set("The passphrase for the PGP key specified by signing.key")
            fromEnvironment()
            defaultValue.set("UNSET")
        }
    }
    group("pluginPortal") {
        // The publish-plugin reads these values directly from System.env. We model them here
        // for completeness and documentation purposes.
        description.set("Credentials for publishing to the plugin portal")
        string("key") {
            description.set("The Plugin portal key for publishing the plugin")
            fromEnvironment("GRADLE_PUBLISH_KEY")
        }
        string("secret") {
            description.set("The Plugin portal secret for publishing the plugin")
            fromEnvironment("GRADLE_PUBLISH_SECRET")
        }
    }
}
// ====

pluginPublishConventions {
    id("${project.group}.internal.gradlex-build-conventions")
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
}