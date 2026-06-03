package utils

import org.gradle.api.Project
import kotlin.reflect.KProperty

// region Mod Info

val Project.modGroup: String by StringDelegate()
val Project.modId: String by StringDelegate()
val Project.modName: String by StringDelegate()
val Project.modVersion: String by StringDelegate()

// endregion

// region Minecraft Info

val Project.mcVersion: String by StringDelegate()
val Project.userName: String by StringDelegate()

val Project.coreModPluginPath: String by StringDelegate()
val Project.generateTokenPath: String by StringDelegate()

// endregion

class StringDelegate {

    operator fun getValue(thisRef: Project, property: KProperty<*>): String =
            thisRef.findProperty(property.name)?.toString()
                    ?: error("Property '${property.name}' not found in gradle.properties")
}
