pluginManagement {
    repositories {
        gradlePluginPortal()
    }
}

plugins {
    // Automatic toolchain provisioning.
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

rootProject.name = "ExtendedAE"
