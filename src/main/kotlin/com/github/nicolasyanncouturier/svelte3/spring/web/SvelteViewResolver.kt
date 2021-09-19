package com.github.nicolasyanncouturier.svelte3.spring.web

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.nicolasyanncouturier.svelte3.ssr.Renderer
import org.springframework.core.Ordered
import org.springframework.web.servlet.View
import org.springframework.web.servlet.ViewResolver
import java.util.Locale


class SvelteViewResolver (private val renderer: Renderer, private val htmlDocument: HtmlDocument, private val ssrOnly: Boolean? = false, val order: Int? = Int.MAX_VALUE, private val jsonMapper: ObjectMapper) : ViewResolver, Ordered {

    override fun getOrder(): Int = order ?: Int.MAX_VALUE

    override fun resolveViewName(viewName: String, locale: Locale): View? =
        if (SvelteView.exists(viewName)) SvelteView(renderer, htmlDocument, viewName, "UTF-8", locale, ssrOnly ?: false, jsonMapper) else null

}