package fi.halooconsulting.ohturef.web

import fi.halooconsulting.ohturef.conversion.BibTexConverter
import fi.halooconsulting.ohturef.database.IdGenerator
import fi.halooconsulting.ohturef.database.SqlDatabase
import fi.halooconsulting.ohturef.model.*
import io.requery.kotlin.eq
import spark.ModelAndView
import spark.Spark
import spark.Spark.*
import spark.route.RouteOverview
import spark.template.jade.JadeTemplateEngine

class Server(val db: SqlDatabase){
    init {
        println("Created Ohturef server.")
    }

    fun start() {
        val templateEngine = JadeTemplateEngine()

        port(Server.getPort())

        externalStaticFileLocation("${System.getProperty("user.dir")}/public")

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
            ModelAndView(null, "new.jade")
        }, templateEngine)

        post("/new", { req, res ->
            val id = req.queryParams("id")
            val type = RefType.fromString(req.queryParams("type"))!!

            val ref = ReferenceEntity()
            ref.id = id
            ref.type = type
            ref.author = req.queryParams("author")
            ref.title = req.queryParams("title")
            ref.year = req.queryParams("year").toInt()
            ref.publisher = req.queryParams("publisher").orEmpty()
            ref.address = req.queryParams("address").orEmpty()
            ref.pages = req.queryParams("pages").orEmpty()
            ref.journal = req.queryParams("journal").orEmpty()
            ref.volume = req.queryParams("volume").orEmpty().toIntOrNull()
            ref.number = req.queryParams("number").orEmpty().toIntOrNull()
            ref.booktitle = req.queryParams("booktitle").orEmpty()

            db.store.insert(ref)
            res.redirect("/")
        })

        get("/bibtex", { _, res ->
            val refs = db.store { select(Reference::class) }.get().toList()
    
            var converted = refs.map { BibTexConverter.toBibTex(it) }.joinToString("\n\n")
            res.header("Content-Type", "text/plain")
            converted
        })

        post("/generate_id", { req, res ->
            val generationRequest = IdGenerationRequest.fromJson(req.body())
            val generator = IdGenerator(db)
            val id = generator.generateUniqueId(generationRequest)
            res.header("Content-Type", "text/plain")
            id
        })

        before("/:id", { req, _ ->
            val ref = db.store {
                select(Reference::class) where (Reference::id eq req.params("id"))
            }.get().firstOrNull()

            if (ref == null) {
                halt(404)
            } else {
                req.attribute("reference", ref)
            }
        })

        get("/:id/bibtex", { req, res ->
            val ref = req.attribute<Reference>("reference")
            val converted = BibTexConverter.toBibTex(ref)
            res.header("Content-Type", "text/plain")
            converted
        })

        get("/:id", { req, _ ->
            val ref = req.attribute<Reference>("reference")
            val vars = hashMapOf("reference" to ref)
            ModelAndView(vars, "reference.jade")
        }, templateEngine)

        delete("/:id", { req, _ ->
            val ref = req.attribute<Reference>("reference")
            db.store.delete(ref)
        })

        post("/:id/tag", { req, res ->
            val id = req.params("id")
            val ref = req.attribute<Reference>("reference")
            val name = req.queryParams("name")

            val tag = TagEntity()
            tag.name = name

            val reftag = ReferenceTagEntity()
            reftag.ref = ref
            reftag.tag = tag

            db.store.insert(tag)
            db.store.insert(reftag)

            res.redirect("/$id")
        })

        RouteOverview.enableRouteOverview()
        println("Started Ohturef server in port ${port()}")
    }

    fun stop() {
        Spark.stop()
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