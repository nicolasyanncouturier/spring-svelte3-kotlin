package com.github.nicolasyanncouturier.svelte3.ssr

interface Renderer {

    fun render(location: String, props: Map<String, Any>? = emptyMap()): Rendering

}