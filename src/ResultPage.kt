package com.fascode

import io.ktor.html.Template
import io.ktor.html.insert
import kotlinx.html.*

class ResultPage(
    private val response: String
): Template<HTML> {
    var token: String? = null
    override fun HTML.apply() {
        head {
            insert(HeadBlock()){

            }
        }
        body {
            div("container"){
                h3 {
                    +"Sent push notification to iOS device : "
                }
                h2 {
                    +(token ?: "Token not set")
                }
                h2 {
                    +"Result:"
                }
                code {
                    +(response)
                }
            }
        }
    }

}