import com.gtnewhorizons.retrofuturagradle.mcp.DeobfuscateTask
import com.gtnewhorizons.retrofuturagradle.mcp.ReobfuscatedJar
import com.gtnewhorizons.retrofuturagradle.minecraft.RunMinecraftTask
import extensions.DeobfExtension
import utils.*
import xyz.wagyourtail.jvmdg.gradle.task.DowngradeJar
import xyz.wagyourtail.jvmdg.gradle.task.ShadeJar

plugins {
    id("com.gtnewhorizons.retrofuturagradle")
}

minecraft {
    mcVersion = project.mcVersion

    mcpMappingChannel = "stable"
    mcpMappingVersion = "39"

    username = project.userName

    val args = mutableListOf<String>()
    // Enable assertions for the mod group.
    args += "-ea:${modGroup}"
    // Add colored line support for the terminal in development environment.
    args += "-Dterminal.jline=true"
    // Enable mixin debugging.
    args += "-Dmixin.hotSwap=true"
    args += "-Dmixin.checks.interfaces=true"
    args += "-Dmixin.debug.export=true"
    extraRunJvmArguments.addAll(args)

    useDependencyAccessTransformers = true

    injectedTags.put("MOD_VERSION", modVersion)
    injectedTags.put("MOD_ID", modId)
    injectedTags.put("MOD_NAME", modName)
    injectedTags.put("DEP_VERSION_STRING", "required-after:gregtech@[${modVersion},);")
}

tasks.injectTags {
    outputClassName.set(generateTokenPath)
}

// region Resource Processing

tasks.processResources {
    val props = properties.mapValues { it.value.toString() }
    inputs.properties(props)

    filesMatching(listOf("mcmod.info", "pack.mcmeta")) {
        expand(props)
    }
}

// endregion

// region Access Transformer

val atFiles = sourceSets.main.map {
    it.resources.filter { it.name.endsWith("_at.cfg") }
}

tasks.named<DeobfuscateTask>("deobfuscateMergedJarToSrg") {
    accessTransformerFiles.from(atFiles)
}

tasks.named<DeobfuscateTask>("srgifyBinpatchedJar") {
    accessTransformerFiles.from(atFiles)
}

// endregion

// region JAR

tasks.withType<Jar> {
    archiveBaseName = modId
    manifest {
        val attrs = mutableMapOf<String, String>()
        attrs["FMLCorePlugin"] = coreModPluginPath
        attrs["FMLCorePluginContainsFMLMod"] = "true"
        attrs["ForceLoadAsMod"] = "true"
        attrs["FMLAT"] = modId + "_at.cfg"
        attributes(attrs)
    }
}

tasks.jar {
    archiveClassifier = "dev-java21"
}

tasks.reobfJar {
    archiveClassifier = "java21"
}

val downgradeJar by tasks.getting(DowngradeJar::class) {
    archiveClassifier = "shim"
}

tasks.named<ShadeJar>("shadeDowngradedApi") {
    archiveClassifier = "dev"
}

val reobfShadeDowngradedApi = tasks.named<ReobfuscatedJar>("reobfShadeDowngradedApi") {
    archiveClassifier = ""
}

tasks.assemble {
    dependsOn(reobfShadeDowngradedApi)
}

// endregion

// region Run Minecraft

tasks.withType<RunMinecraftTask> {
    if (name in arrayOf("runClient", "runServer")) {
        classpath -= files(tasks.jar)
        classpath(downgradeJar)
        // Add coremod path here to avoid breaking runObfClient/Server
        mcExtExtraRunJvmArguments.add("-Dfml.coreMods.load=${coreModPluginPath}")
    }
}

tasks.prepareObfModsFolder {
    // Prefix mixinbooter jar with '!' to load it first
    rename("^(mixinbooter.*\\.jar)\$", "!$1")
    // replace java 21 jar with downgraded jar
    exclude("**-java21.jar")
    from(reobfShadeDowngradedApi)
}

// endregion

dependencies {
    // Allow jdk.unsupported classes like sun.misc.Unsafe, workaround for JDK-8206937 and fixes Forge crashes in tests.
    patchedMinecraft("me.eigenraven.java8unsupported:java-8-unsupported-shim:1.0.0")
    // Use prebuilt API JAR to speed up.
    patchedMinecraft("xyz.wagyourtail.jvmdowngrader:jvmdowngrader-java-api:1.3.6:downgraded-8") {
        isTransitive = false
    }

    val mixinBooter = modUtils.enableMixins("zone.rong:mixinbooter:10.7", "mixins.${modId}.refmap.json") as String
    api(mixinBooter) {
        isTransitive = false
    }
    annotationProcessor("org.ow2.asm:asm-debug-all:5.2")
    annotationProcessor("com.google.guava:guava:32.1.2-jre")
    annotationProcessor("com.google.code.gson:gson:2.8.9")
    annotationProcessor(mixinBooter) {
        isTransitive = false
    }

    extensions.create("deobf", DeobfExtension::class.java, dependencies)
}
