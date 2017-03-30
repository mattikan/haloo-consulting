package fi.halooconsulting.ohturef.web

import spark.Spark.*

class Server() {
    init {
        println("Created Ohturef server.")
    }

    fun start() {
        port(8000)
        get("/:id", { req, res -> "Hello, ${req.params("id")}!"})
        println("Started Ohturef server in port ${port()}")
    }
}
