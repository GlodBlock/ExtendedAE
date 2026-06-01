package extensions

import com.gtnewhorizons.retrofuturagradle.modutils.ModUtils
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.internal.artifacts.dependencies.DependencyVariant
import org.gradle.api.provider.Provider

abstract class DeobfExtension(dependencies: DependencyHandler) {

    private val rfg by lazy { dependencies.extensions.getByType(ModUtils.RfgDependencyExtension::class.java) }

    /**
     * Deobfuscates a dependency via RetroFuturaGradle's rfg extension.
     * Handles [org.gradle.api.provider.Provider], [org.gradle.api.artifacts.Dependency], [org.gradle.api.internal.artifacts.dependencies.DependencyVariant], and raw notation strings.
     */
    fun of(dependencyNotation: Any): Any {
        if (dependencyNotation is Provider<*>) {
            return of(dependencyNotation.get())
        }

        when (dependencyNotation) {
            is Dependency -> return of(dependencyNotation)
            else -> return rfg.deobf(dependencyNotation)
        }
    }

    fun of(provider: Provider<out Dependency>): String {
        return of(provider.get())
    }

    fun of(dependency: Dependency): String {
        var depSpec: String = "${dependency.group}:${dependency.name}:${dependency.version}"
        if (dependency is DependencyVariant) {
            depSpec = "$depSpec:${(dependency as DependencyVariant).classifier}"
        }
        return rfg.deobf(depSpec) as String
    }
}
