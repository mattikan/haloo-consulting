package fi.halooconsulting.ohturef.web

import fi.halooconsulting.ohturef.model.Reference
import io.requery.kotlin.eq
import io.requery.sql.KotlinEntityDataStore
import spark.Spark.*
import spark.ModelAndView
import spark.template.handlebars.HandlebarsTemplateEngine

class Server(val data: KotlinEntityDataStore<Any>){
    init {
        println("Created Ohturef server.")
    }

    fun start() {
        val templateEngine = HandlebarsTemplateEngine()

        port(8000)

        get("/", { req, res ->
            val refs = data {
                select(Reference::class) limit 10
            }.get()
            val lines: List<String> = refs.map{ it: Reference -> "[${it.id}] ${it.author}: ${it.title}" }
            val vars = hashMapOf("lines" to lines)
            ModelAndView(vars, "index.hbs")
        }, templateEngine)

        get("/:id", { req, res ->
            val refs = data {
                select(Reference::class) where (Reference::id eq req.params("id"))
            }.get()
            val lines: List<String> = refs.map{ it: Reference -> "[${it.id}] ${it.author}: ${it.title}" }
            val vars = hashMapOf("lines" to lines)
            ModelAndView(vars, "index.hbs")
        }, templateEngine)

        println("Started Ohturef server in port ${port()}")
    }
}
