plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
    maven {
        // Retrofutura Gradle
        name = "GTNH Maven"
        url = uri("https://nexus.gtnewhorizons.com/repository/public/")
        mavenContent {
            includeGroup("com.gtnewhorizons")
            includeGroup("com.gtnewhorizons.retrofuturagradle")
        }
    }
}

dependencies {
    implementation(libs.retrofuturaGradle)
    implementation(libs.jvmDowngrader)
    implementation(libs.ideaExt)
}
