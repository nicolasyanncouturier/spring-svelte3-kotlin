package com.github.nicolasyanncouturier.svelte3.spring.web

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.nicolasyanncouturier.svelte3.ssr.Renderer
import com.github.nicolasyanncouturier.svelte3.ssr.Rendering
import org.springframework.web.servlet.view.AbstractView
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import java.util.Locale


internal class SvelteView(private val renderer: Renderer, private val htmlDocument: HtmlDocument, private val name: String, private val charset: String, locale: Locale, private val ssrOnly: Boolean, private val jsonMapper: ObjectMapper) : AbstractView() {

    private val lang: String = locale.language

    companion object {

        fun exists(name: String): Boolean {
            val filename = filename(name)
            val exists = SvelteView::class.java.classLoader.getResource(filename) != null
            if (!exists && (name.startsWith("svelte:") || name.endsWith(".svelte"))) {
                throw RuntimeException("View '$name' resource ('$filename') does not exist; check your rollup config that the view is included.")
            }
            return exists
        }

        private fun filename(name: String): String = name.removePrefix("svelte:").removeSuffix(".svelte").addSuffix(".js")

        private fun String.addSuffix(suffix:String) = if(this.endsWith(suffix)) this else "$this$suffix"

    }

    private fun filename(): String = Companion.filename(name)

    @Throws(Exception::class)
    override fun renderMergedOutputModel(model: Map<String, Any>, request: HttpServletRequest, response: HttpServletResponse) {
        val filename = filename()
        val rendering: Rendering = renderer.render(filename, model)
        val (bundle, props) = if (ssrOnly) Pair(null, null) else Pair(filename, jsonMapper.writeValueAsString(model))
        val doc = htmlDocument.render(lang, rendering.head, rendering.css, rendering.html, bundle, props)
        response.contentType = contentType
        response.outputStream.write(doc.toByteArray(charset(charset)))
    }

    override fun getContentType(): String = "text/html"

}