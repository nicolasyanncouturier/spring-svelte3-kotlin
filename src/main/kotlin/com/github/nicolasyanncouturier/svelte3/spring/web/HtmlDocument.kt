package com.github.nicolasyanncouturier.svelte3.spring.web

interface HtmlDocument {
    fun render(lang: String?, head: String?, css: String?, html: String?, bundle: String?=null, props: String?=null): String
}