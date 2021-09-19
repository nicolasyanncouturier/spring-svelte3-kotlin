package com.github.nicolasyanncouturier.svelte3.ssr

import org.graalvm.polyglot.Context
import org.graalvm.polyglot.Source
import org.graalvm.polyglot.io.FileSystem
import org.graalvm.polyglot.proxy.ProxyObject
import java.io.InputStreamReader
import java.io.Reader
import java.io.SequenceInputStream

class SimpleRenderer(fs: () -> FileSystem = ::ResourceFS):Renderer {

    private val context: Context = Context.newBuilder("js")
        .fileSystem(fs())
        .allowIO(true)
        .build()

    override fun render(location: String, props: Map<String, Any>?): Rendering {
        val reader = InputStreamReader(SequenceInputStream(SimpleRenderer::class.java.getResourceAsStream("/$location"), "app.render".byteInputStream()))
        val execArgs = props?.let { arrayOf(ProxyObject.fromMap(it)) } ?: emptyArray()
        val value = context.eval(js(location, reader)).execute(*execArgs)
        return Rendering(
            value.getMember("html").asString(),
            value.getMember("css").getMember("code").asString(),
            value.getMember("head").asString()
        )
    }

    private fun js(name: String, reader: Reader): Source {
        return Source.newBuilder("js", reader, "$name.js").build()
    }
}

