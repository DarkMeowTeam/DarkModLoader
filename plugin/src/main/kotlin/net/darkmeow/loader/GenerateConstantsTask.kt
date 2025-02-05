package net.darkmeow.loader

import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.FileCollection
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.*
import java.io.File
import java.util.jar.Attributes
import java.util.jar.Manifest
import javax.inject.Inject

@Suppress("LeakingThis")
abstract class GenerateConstantsTask : DefaultTask() {

    @get:Optional
    @get:Input
    internal abstract val directClass: Property<String>

    @get:Optional
    @get:Input
    internal abstract val forgeModClass: Property<String>

    @get:Optional
    @get:Input
    internal abstract val verifyClass: Property<String>

    @get:InputFiles
    internal abstract val platformJars: Property<FileCollection>

    @get:Inject
    internal abstract val project: Project

    @get:OutputDirectory
    internal abstract val sourcesDir: DirectoryProperty

    @get:OutputDirectory
    internal abstract val resourcesDir: DirectoryProperty

    @get:OutputFile
    internal abstract val manifestFile: RegularFileProperty

    init {
        sourcesDir.set(project.layout.buildDirectory.dir("mod-loader/sources"))
        resourcesDir.set(project.layout.buildDirectory.dir("mod-loader/resources"))
        manifestFile.set(resourcesDir.file("META-INF/MANIFEST.MF"))
    }

    @TaskAction
    fun run() {
        val sourceDir = sourcesDir.get().asFile
        sourceDir.deleteRecursively()
        sourceDir.mkdirs()

        val resourceDir = resourcesDir.get().asFile
        resourceDir.deleteRecursively()
        resourceDir.mkdirs()

        File(resourceDir, "META-INF").mkdirs()

        val manifestFile = manifestFile.get().asFile
        manifestFile.parentFile.mkdirs()
        manifestFile.createNewFile()

        generateResources()
        generateClasses()
    }

    private fun generateResources() {
        val resourcesDir = resourcesDir.asFile.get()
        resourcesDir.mkdirs()

        val file = platformJars.get().singleFile
        forge(file, resourcesDir)
    }

    private fun forge(file: File, resourcesDir: File) {
        val zipTree = project.zipTree(file)
        val newManifest = Manifest()
        newManifest.mainAttributes[Attributes.Name.MANIFEST_VERSION] = "1.0"
        newManifest.mainAttributes[Attributes.Name("FMLCorePlugin")] = "net.darkmeow.loader.loaders.LegacyForgeLoader"

        // 模组信息
        @Suppress("SpellCheckingInspection")
        zipTree.findByName("mcmod.info")?.copyTo(File(resourcesDir, "mcmod.info"), true)
        // 模组内置材质包信息
        zipTree.findByName("pack.mcmeta")?.copyTo(File(resourcesDir, "pack.mcmeta"), true)

        forgeModClass.orNull?.let { forgeModClass ->
            newManifest.mainAttributes[Attributes.Name("FMLCorePluginContainsFMLMod")] = "true"
            val forgeModClassRegex = "$forgeModClass.*\\.class".toRegex()
            val forgeModPackage = forgeModClass.substringBeforeLast('.').replace('.', '/')
            zipTree
                .filter { it.path.contains(forgeModClassRegex) }
                .forEach {
                    it.copyTo(File(resourcesDir, "$forgeModPackage/${it.name}"), true)
                }
        }

        // 直接运行 jar 执行
        newManifest.mainAttributes[Attributes.Name("Main-Class")] = "net.darkmeow.loader.loaders.DirectLoader"

        manifestFile.get().asFile.outputStream().use { newManifest.write(it) }
    }

    private fun Iterable<File>.findByName(name: String): File? {
        return find { it.name == name }
    }

    private val constantsSrc = """
        package net.darkmeow.loader.core;

        public final class Constants {
            public static String DIRECT_CLASS = "%s";
            public static String VERIFY_CLASS = "%s";
        }
    """.trimIndent()

    private fun generateClasses() {
        val dir = File(sourcesDir.asFile.get(), "net/darkmeow/loader/core")
        dir.mkdirs()
        File(dir, "Constants.java").writeText(
            constantsSrc.format(
                directClass.orNull ?: "",
                verifyClass.orNull ?: ""
            )
        )
    }
}