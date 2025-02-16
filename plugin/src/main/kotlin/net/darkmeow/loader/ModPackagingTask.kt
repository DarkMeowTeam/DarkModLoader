@file:Suppress("LeakingThis")

package net.darkmeow.loader

import net.darkmeow.loader.xz.ParallelXZOutputStream
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import net.darkmeow.loader.xz.LZMA2Options
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.FileCollection
import org.gradle.api.provider.Property
import org.gradle.api.tasks.*
import java.io.File
import java.io.OutputStream
import java.util.zip.CRC32

abstract class ModPackagingTask : DefaultTask() {

    @get:Optional
    @get:Input
    internal abstract val forgeModClass: Property<String>

    @get:Input
    abstract val dictSize: Property<Int>

    @get:InputFiles
    internal abstract val platformJars: Property<FileCollection>

    @get:OutputDirectory
    internal abstract val outputFile: DirectoryProperty

    init {
        outputFile.set(project.layout.buildDirectory.dir("mod-loader/packed"))
        dictSize.convention(4 * 1024 * 1024)
    }

    @TaskAction
    fun packageMod() {
        val outputFile = File(File(outputFile.get().asFile, "META-INF"), "NATIVE")
        outputFile.delete()
        outputFile.parentFile.mkdirs()

        val rawStream = outputFile.outputStream().buffered(16 * 1024)
        val lzma2Options = LZMA2Options(LZMA2Options.PRESET_MAX)
        lzma2Options.dictSize = dictSize.get()
        lzma2Options.niceLen = 273
        ParallelXZOutputStream(Dispatchers.Default, rawStream, lzma2Options).use {
            readToZip(it)
        }
    }

    private fun readToZip(output: OutputStream) {
        runBlocking {
            val zipOut = ZipArchiveOutputStream(output)
            zipOut.setMethod(ZipArchiveOutputStream.STORED)
            val channel = Channel<Pair<ZipArchiveEntry, ByteArray>>(Channel.BUFFERED)

            launch(Dispatchers.IO) {
                for (entry in channel) {
                    zipOut.putArchiveEntry(entry.first)
                    zipOut.write(entry.second)
                    zipOut.closeArchiveEntry()
                }
            }

            coroutineScope {
                pack(platformJars.get().singleFile, channel)
            }

            channel.close()
        }
    }

    private fun CoroutineScope.pack(
        input: File,
        channel: Channel<Pair<ZipArchiveEntry, ByteArray>>
    ) {
        launch(Dispatchers.IO) {
            val filterName = forgeModClass.map { "${it.replace('.', '/')}.*\\.class".toRegex() }.orNull
            val crc32 = CRC32()
            ZipArchiveInputStream(input.inputStream().buffered(16 * 1024)).use {
                while (true) {
                    val entryIn = it.nextEntry ?: break
                    if (filterName != null && filterName.matches(entryIn.name)) continue

                    val dir = "package"

                    val entryOut = ZipArchiveEntry("$dir/${entryIn.name}")
                    if (!entryIn.isDirectory) {
                        val bytes = it.readBytes()
                        crc32.reset()
                        crc32.update(bytes)
                        entryOut.crc = crc32.value
                        entryOut.size = bytes.size.toLong()
                        channel.send(entryOut to bytes)
                    }
                }
            }
        }
    }
}