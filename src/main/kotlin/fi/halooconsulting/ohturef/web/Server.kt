package fi.halooconsulting.ohturef.web

import fi.halooconsulting.ohturef.conversion.BibTexConverter
import fi.halooconsulting.ohturef.database.Database
import fi.halooconsulting.ohturef.model.RefType
import fi.halooconsulting.ohturef.model.Reference
import fi.halooconsulting.ohturef.model.ReferenceEntity
import io.requery.kotlin.eq
import spark.ModelAndView
import spark.Spark.*
import spark.route.RouteOverview
import spark.template.jade.JadeTemplateEngine

class Server(val db: Database){
    init {
        println("Created Ohturef server.")
    }

    fun start() {
        val templateEngine = JadeTemplateEngine()

        port(Server.getPort())

        externalStaticFileLocation("${System.getProperty("user.dir")}/public")

        delete("/:id", { req, res ->
            val ref = db.store {
                select(Reference::class) where (Reference::id eq req.params("id"))
            }.get().first()
            db.store.delete(ref)
            "haloo"
        })

        get("/", { req, res ->
            val refs = db.store {
                select(Reference::class)
            }.get().groupBy { k -> k.type }.mapKeys { k -> k.key.name.toLowerCase() }.toMutableMap()

            refs["book"] = refs.getOrDefault("book", emptyList())
            refs["article"] = refs.getOrDefault("article", emptyList())
            refs["inproceedings"] = refs.getOrDefault("inproceedings", emptyList())

            val vars = hashMapOf("references" to refs)
            ModelAndView(vars, "index.jade")
        }, templateEngine)

        get("/new", { req, res ->
            val vars = null
            ModelAndView(emptyMap<String, Any>(), "new.jade")
        }, templateEngine)

        post("/new", { req, res ->
            var id = req.queryParams("id")
            var types = req.queryParams("type")
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
            var journal = req.queryParams("journal").orEmpty()
            var volume = req.queryParams("volume").orEmpty()
            var number = req.queryParams("number").orEmpty()
            var booktitle = req.queryParams("booktitle").orEmpty()
            var ref: Reference = ReferenceEntity()
            ref.id = id
            ref.type = type
            ref.author = author
            ref.title = title
            ref.year = year.toInt()
            ref.publisher = publisher
            ref.address = address
            ref.pages = pages
            ref.journal = journal
            ref.volume = volume.toIntOrNull()
            ref.number = number.toIntOrNull()
            ref.booktitle = booktitle
            db.store.insert(ref)
            res.redirect("/")
            val vars = null
            ModelAndView(vars, "index.jade")
        }, templateEngine)

        get("/bibtex", { req, res ->
            val refs = db.store { select(Reference::class) }.get().toList()
    
            var converted = refs.map { BibTexConverter.toBibTex(it) }.joinToString("\n\n")
            res.header("Content-Type", "text/plain")
            converted
        })

        get("/:id/bibtex", { req, res ->
            val ref = db.store {
                select(Reference::class) where (Reference::id eq req.params("id"))
            }.get().first()

            val converted = BibTexConverter.toBibTex(ref)
            res.header("Content-Type", "text/plain")
            converted
        })

        get("/:id", { req, res ->
            val ref = db.store {
                select(Reference::class) where (Reference::id eq req.params("id"))
            }.get().first()
            val vars = hashMapOf("reference" to ref)
            ModelAndView(vars, "reference.jade")
        }, templateEngine)
        RouteOverview.enableRouteOverview();
        println("Started Ohturef server in port ${port()}")
    }

    companion object Server {
        fun getPort(): Int {
            if (System.getenv("PORT") != null) {
                return System.getenv("PORT").toInt()
            } else {
                return 8000
            }
        }
    }
}