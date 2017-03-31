package fi.halooconsulting.ohturef.web

import spark.Spark.*
import spark.ModelAndView
import spark.template.handlebars.HandlebarsTemplateEngine

class Server {
    init {
        println("Created Ohturef server.")
    }

    fun start() {
        val templateEngine = HandlebarsTemplateEngine()

        port(8000)

        get("/:id", { req, res ->
            val vars = hashMapOf("name" to req.params("id"))
            ModelAndView(vars, "index.hbs")
        }, templateEngine)

        println("Started Ohturef server in port ${port()}")
    }
}
