import utils.modGroup
import utils.modId
import utils.modVersion

plugins {
    `java-library`
    id("xyz.wagyourtail.jvmdowngrader")
}

group = modGroup
version = modVersion

base {
    archivesName = modId
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
    withSourcesJar()
}

jvmdg {
    downgradeTo = JavaVersion.VERSION_1_8
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    if (name in arrayOf("compileMcLauncherJava", "compilePatchedMcJava", "compileApiJava")) {
        sourceCompatibility = "8"
    }
}

configurations {
    val localRuntime by creating {
        isCanBeConsumed = false
    }

    runtimeClasspath {
        extendsFrom(localRuntime)
    }
}
