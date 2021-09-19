package com.github.nicolasyanncouturier.svelte3.ssr

import java.nio.ByteBuffer
import java.nio.channels.ReadableByteChannel
import java.nio.channels.SeekableByteChannel

class DelegatingSeekableByteChannel(private val delegate:ReadableByteChannel): SeekableByteChannel {

    override fun close() = delegate.close()

    override fun isOpen(): Boolean = delegate.isOpen

    override fun read(dst: ByteBuffer?): Int = delegate.read(dst)

    override fun write(src: ByteBuffer?): Int {
        throw java.lang.UnsupportedOperationException()
    }

    override fun position(): Long {
        throw java.lang.UnsupportedOperationException()
    }

    override fun position(newPosition: Long): SeekableByteChannel {
        throw java.lang.UnsupportedOperationException()
    }

    override fun size(): Long {
        throw java.lang.UnsupportedOperationException()
    }

    override fun truncate(size: Long): SeekableByteChannel {
        throw java.lang.UnsupportedOperationException()
    }

}