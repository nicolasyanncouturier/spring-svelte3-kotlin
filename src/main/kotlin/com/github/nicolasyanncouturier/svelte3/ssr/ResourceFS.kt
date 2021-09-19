package com.github.nicolasyanncouturier.svelte3.ssr

import org.graalvm.polyglot.io.FileSystem
import java.io.IOException
import java.net.URI
import java.nio.channels.Channels
import java.nio.channels.SeekableByteChannel
import java.nio.file.AccessMode
import java.nio.file.DirectoryStream
import java.nio.file.LinkOption
import java.nio.file.OpenOption
import java.nio.file.Path
import java.nio.file.attribute.FileAttribute

class ResourceFS:FileSystem {

    companion object {
        private val regularFileAttributes = mapOf("isRegularFile" to true)
    }

    override fun parsePath(uri: URI): Path {
        throw UnsupportedOperationException()
    }

    override fun parsePath(path: String): Path? = Path.of(path.ifBlank{""})

    override fun checkAccess(path: Path, modes: Set<AccessMode?>, vararg linkOptions: LinkOption) {
        if (!exists(path)) throw IOException()
    }

    override fun createDirectory(dir: Path, vararg attrs: FileAttribute<*>?) {
        throw UnsupportedOperationException()
    }

    override fun delete(path: Path) {
        throw UnsupportedOperationException()
    }

    override fun newByteChannel(path: Path, options: Set<OpenOption?>, vararg attrs: FileAttribute<*>?): SeekableByteChannel =
        DelegatingSeekableByteChannel(Channels.newChannel(this::class.java.classLoader.getResourceAsStream(path.toString()) ?: throw IOException("Resource $path not found")))

    override fun newDirectoryStream(dir: Path, filter: DirectoryStream.Filter<in Path>): DirectoryStream<Path> {
        throw UnsupportedOperationException()
    }

    override fun toAbsolutePath(path: Path): Path = path.toAbsolutePath()

    override fun toRealPath(path: Path, vararg linkOptions: LinkOption): Path = path

    override fun readAttributes(path: Path, attributes: String, vararg options: LinkOption): Map<String, Any> = regularFileAttributes

    private fun exists(path: Path): Boolean = ResourceFS::class.java.classLoader.getResource(path.toString()) != null

}