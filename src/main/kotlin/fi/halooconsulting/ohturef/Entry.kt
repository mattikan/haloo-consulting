
package fi.halooconsulting.ohturef

import fi.halooconsulting.ohturef.web.Server

fun main(args: Array<String>) {
    println("Hello, Ohtu!")
    val server = Server()
    server.start()
}

fun getOne(): Int = 1
