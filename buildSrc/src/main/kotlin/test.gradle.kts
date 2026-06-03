import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import xyz.wagyourtail.jvmdg.gradle.task.files.DowngradeFiles

plugins {
    id("java")
}

// Make test can access source code of Minecraft.
sourceSets {
    test {
        java {
            val patchedMc = named("patchedMc").get().output
            val mcLauncher = named("mcLauncher").get().output
            val main = main.get().output
            val downgradeJar = files(tasks.named("downgradeJar"))
            compileClasspath += patchedMc + mcLauncher
            runtimeClasspath += patchedMc + mcLauncher + downgradeJar
            runtimeClasspath -= main
        }
    }
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
    testLogging {
        events(TestLogEvent.STARTED, TestLogEvent.PASSED, TestLogEvent.FAILED)
        exceptionFormat = TestExceptionFormat.FULL
        showExceptions = true
        showStackTraces = true
        showCauses = true
        showStandardStreams = true
    }
    javaLauncher = javaToolchains.launcherFor {
        languageVersion.set(JavaLanguageVersion.of(8))
    }
}

val downgradeTestClasses by tasks.registering(DowngradeFiles::class) {
    dependsOn("testClasses")
    val test = sourceSets.test.get()
    inputCollection = test.output.classesDirs
    classpath = test.runtimeClasspath

    // DowngradeFiles registers outputs by probing inputCollection with isDirectory/isFile during configuration.
    // On a clean build, test.output.classesDirs paths exist logically but may not exist on disk yet, so its outputs can end up empty.
    // Declare the mapped output dirs here so Gradle can wire testClassesDirs/classpath correctly on the first run too.
    outputs.dirs(*inputCollection.files.map { temporaryDir.resolve(it.name) }.toTypedArray())
}

tasks.test {
    val downgradeTestClasses = downgradeTestClasses.get().outputCollection
    val test = sourceSets.test.get().output
    testClassesDirs = downgradeTestClasses
    classpath += downgradeTestClasses
    classpath -= test
}

dependencies {
    // Allow jdk.unsupported classes like sun.misc.Unsafe, workaround for JDK-8206937 and fixes Forge crashes in tests.
    testImplementation("me.eigenraven.java8unsupported:java-8-unsupported-shim:1.0.0")
    // Use prebuilt API JAR to speed up.
    testImplementation("xyz.wagyourtail.jvmdowngrader:jvmdowngrader-java-api:1.3.6:downgraded-8") {
        isTransitive = false
    }
    testImplementation("org.hamcrest:hamcrest:2.2")
    testImplementation("org.junit.jupiter:junit-jupiter:5.9.2")
    testImplementation("org.junit.platform:junit-platform-launcher")
}
