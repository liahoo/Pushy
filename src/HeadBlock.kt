package com.fascode

import io.ktor.html.Placeholder
import io.ktor.html.Template
import io.ktor.html.insert
import kotlinx.html.HEAD
import kotlinx.html.link
import kotlinx.html.meta
import kotlinx.html.script

class HeadBlock: Template<HEAD> {
    override fun HEAD.apply() {
        meta(charset = "charset")
        meta {
            httpEquiv = "X-UA-Compatible"
        }
        meta(name = "viewport", content = "width=device-width, initial-scale=1")
        meta(name = "description", content = "meta description")
        link(rel = "shortcut icon", href = "/static/img/favicon.ico", type = "image/x-icon")
        link(rel = "stylesheet", href = "https://fonts.googleapis.com/css?family=Poppins:300,400,400i,500,600,700")
        link(rel = "stylesheet", href = "https://stackpath.bootstrapcdn.com/bootstrap/4.4.1/css/bootstrap.min.css") {
            integrity="sha384-Vkoo8x4CGsO3+Hhxv8T/Q5PaXtkKtu6ug5TOeNV6gBiFeWPGFN9MuhOf23Q9Ifjh"
            attributes["crossorigin"] = "anonymous"
        }
        link(rel="stylesheet", href="https://use.fontawesome.com/releases/v5.9.0/css/all.css") {
            attributes["crossorigin"] = "anonymous"
        }
        link(rel = "stylesheet", href = "/static/css/styles.css")
        script(src="https://code.jquery.com/jquery-3.5.1.min.js"){
            integrity="sha256-9/aliU8dGd2tb6OSsuzixeV4y/faTqgFtohetphbbj0="
            attributes["crossorigin"] = "anonymous"
        }
        script(src = "https://cdn.jsdelivr.net/npm/popper.js@1.16.0/dist/umd/popper.min.js"){
            integrity="sha384-Q6E9RHvbIyZFJoft+2mJbHaEWldlvI9IOYy5n3zV9zzTtmI3UksdQRVvoxMfooAo"
            attributes["crossorigin"] = "anonymous"
        }
        script(src = "https://stackpath.bootstrapcdn.com/bootstrap/4.4.1/js/bootstrap.min.js"){
            integrity="sha384-wfSDF2E50Y2D1uUdj0O3uMBJnjuUD4Ih7YwaYd1iqfktj0Uod8GCExl3Og8ifwB6"
            attributes["crossorigin"] = "anonymous"
        }
    }
}