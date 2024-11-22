package net.darkmeow.loader

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.DuplicatesStrategy
import org.gradle.jvm.tasks.Jar

class ModLoaderPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val runtimeConfiguration = project.configurations.create("modLoaderRuntime")
        val extension = project.extensions.create("modLoader", ModLoaderExtension::class.java)

        val platforms = project.configurations.register("modLoaderPlatforms")
        val platformJarFiles = platforms.map { project.files(it.files, it.allArtifacts.files.files) }

        project.dependencies.add(runtimeConfiguration.name, "net.darkmeow:mod-loader-runtime:$version")

        project.afterEvaluate {
            project.dependencies.add(
                runtimeConfiguration.name,
                "net.darkmeow:mod-loader-runtime:$version:legacy-forge"
            )
        }

        val generateConstants = project.tasks.create("generateConstants", GenerateConstantsTask::class.java) { generateConstants ->
            generateConstants.dependsOn(platforms)
            generateConstants.dependsOn(platforms.get().artifacts)

            generateConstants.directClass.set(extension.directClass)
            generateConstants.modName.set(extension.modName)
            generateConstants.forgeModClass.set(extension.forgeModClass)
            generateConstants.platformJars.set(platformJarFiles)
        }

        val modPackaging = project.tasks.create("modPackaging", ModPackagingTask::class.java) { modPackaging ->
            modPackaging.dependsOn(platforms)
            modPackaging.dependsOn(platforms.get().artifacts)

            modPackaging.modName.set(extension.modName)
            modPackaging.forgeModClass.set(extension.forgeModClass)

            modPackaging.splitLibs.add(extension.mcVersion.map {
                if ((it.split('.')[1].toIntOrNull() ?: 0) >= 18) "forge" else ""
            })
            modPackaging.platformJars.set(platformJarFiles)
        }

        val remapRuntimeTask = project.tasks.create("remapRuntime", RemapRuntimeTask::class.java) { remapRuntime ->
            remapRuntime.runtimeJars.setFrom(runtimeConfiguration)
        }

        val modLoaderJar = project.tasks.create("modLoaderJar", Jar::class.java) { modLoaderJar ->
            modLoaderJar.duplicatesStrategy = DuplicatesStrategy.EXCLUDE
            modLoaderJar.dependsOn(generateConstants)
            modLoaderJar.from(remapRuntimeTask.outputs)
            modLoaderJar.from(generateConstants.resourcesDir) {
                it.exclude("MANIFEST.MF")
            }
            modLoaderJar.from(modPackaging.outputs)

            modLoaderJar.manifest.from(generateConstants.manifestFile)
        }

        project.artifacts {
            it.add("archives", modLoaderJar)
        }
    }

    companion object {
        val version = ModLoaderPlugin::class.java.getResource("/mod-loader-plugin.version")!!.readText()
    }
}