package fi.halooconsulting.ohturef.web

import fi.halooconsulting.ohturef.model.RefType
import fi.halooconsulting.ohturef.model.Reference
import fi.halooconsulting.ohturef.model.ReferenceEntity
import io.requery.kotlin.eq
import io.requery.sql.KotlinEntityDataStore
import spark.ModelAndView
import spark.Spark.*
import spark.template.jade.JadeTemplateEngine

class Server(val data: KotlinEntityDataStore<Any>){
    init {
        println("Created Ohturef server.")
    }

    fun start() {
        val templateEngine = JadeTemplateEngine()

        if (System.getenv("PORT") != null) {
            port(System.getenv("PORT").toInt())
        } else {
            port(8000)
        }

        externalStaticFileLocation("${System.getProperty("user.dir")}/public")

        get("/", { req, res ->
            val refs = data {
                select(Reference::class) limit 10
            }.get()
            val vars = hashMapOf("lines" to refs)
            ModelAndView(vars, "index.jade")
        }, templateEngine)

        get("/new", { req, res ->
            val vars = null
            ModelAndView(emptyMap<String, Any>(), "new.jade")
        }, templateEngine)

        post("/new", { req, res ->
            var id = req.queryParams("id")
            var types = req.queryParams("type").orEmpty()
            var type: RefType = RefType.BOOK
            if (types.trim().toLowerCase() == "article") {
                type = RefType.ARTICLE
            } else if (types.trim().toLowerCase() == "book") {
                type = RefType.BOOK
            } else if (types.trim().toLowerCase() == "inproceedings") {
                type = RefType.INPROCEEDINGS
            }
            var author = req.queryParams("author")
            var title = req.queryParams("title")
            var year = req.queryParams("year")
            var publisher = req.queryParams("publisher").orEmpty()
            var address = req.queryParams("address").orEmpty()
            var pages = req.queryParams("pages").orEmpty()
            var ref: Reference = ReferenceEntity()
            ref.id = id
            ref.type = type
            ref.author = author
            ref.title = title
            ref.year = year.toInt()
            ref.publisher = publisher
            ref.address = address
            ref.pages = pages
            data.insert(ref)
            res.redirect("/")
            val vars = null
            ModelAndView(vars, "index.jade")
        }, templateEngine)

        get("/:id", { req, res ->
            val ref = data {
                select(Reference::class) where (Reference::id eq req.params("id"))
            }.get().first()
            val vars = hashMapOf("reference" to ref)
            ModelAndView(vars, "reference.jade")
        }, templateEngine)

        println("Started Ohturef server in port ${port()}")
    }
}
