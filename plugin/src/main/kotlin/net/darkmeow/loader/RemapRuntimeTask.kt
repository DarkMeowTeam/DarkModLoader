package net.darkmeow.loader

import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.*
import java.io.File
import java.util.zip.ZipInputStream
import javax.inject.Inject

@Suppress("LeakingThis")
abstract class RemapRuntimeTask : DefaultTask() {

    @get:InputFiles
    internal abstract val runtimeJars: ConfigurableFileCollection

    @get:OutputDirectory
    internal abstract val outputDirectory: DirectoryProperty

    @get:Inject
    internal abstract val project: Project

    init {
        outputDirectory.set(project.layout.buildDirectory.dir("mod-loader/runtime"))
    }

    @TaskAction
    fun remap() {
        val output = outputDirectory.get().asFile
        output.deleteRecursively()
        output.mkdirs()

        runtimeJars.forEach {
            ZipInputStream(it.inputStream().buffered(16 * 1024)).use { zipIn ->
                while (true) {
                    val entry = zipIn.nextEntry ?: break
                    val fileTo = File(output, entry.name)
                    when {
                        entry.isDirectory -> {
                            // Ignored
                        }
                        entry.name.startsWith("META-INF/services/") -> {
                            val text = zipIn.readBytes().toString(Charsets.UTF_8)
                            fileTo.parentFile.mkdirs()
                            fileTo.writeText(text)
                        }
                        entry.name.endsWith(".class") -> {
                            val bytes = zipIn.readBytes()
                            fileTo.parentFile.mkdirs()
                            fileTo.writeBytes(bytes)
                        }
                        else -> {
                            fileTo.parentFile.mkdirs()
                            zipIn.copyTo(fileTo.outputStream())
                        }
                    }
                }
            }
        }
    }
}