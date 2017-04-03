package fi.halooconsulting.ohturef.web

import fi.halooconsulting.ohturef.model.RefType
import fi.halooconsulting.ohturef.model.Reference
import fi.halooconsulting.ohturef.model.ReferenceEntity
import io.requery.kotlin.eq
import io.requery.sql.KotlinEntityDataStore
import spark.ModelAndView
import spark.Spark.*
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
            val lines: List<String> = refs.map{ it: Reference -> "[${it.id}] ${it.author}: ${it.title}, ${it.year}" }
            val vars = hashMapOf("lines" to lines)
            ModelAndView(vars, "index.hbs")
        }, templateEngine)

        get("/new", { req, res ->
            val vars = null
            ModelAndView(vars, "new.hbs")
        }, templateEngine)

        post("/new", { req, res ->
            var id = req.queryParams("id")
            var types = req.queryParams("type")
            var type: RefType = RefType.BOOK
            if (types == "ARTICLE") {
                type = RefType.ARTICLE
            } else if (types == "BOOK") {
                type = RefType.BOOK
            } else if (types == "INPROCEEDINGS") {
                type = RefType.INPROCEEDINGS
            }
            var author = req.queryParams("author")
            var title = req.queryParams("title")
            var year = req.queryParams("year")
            var ref: Reference = ReferenceEntity()
            ref.id = id
            ref.type = type
            ref.author = author
            ref.title = title
            ref.year = year.toInt()
            data.insert(ref)
            res.redirect("/")
            val vars = null
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
