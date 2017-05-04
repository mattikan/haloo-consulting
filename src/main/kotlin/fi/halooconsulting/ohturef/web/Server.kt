package fi.halooconsulting.ohturef.web

import fi.halooconsulting.ohturef.conversion.BibTexConverter
import fi.halooconsulting.ohturef.database.IdGenerator
import fi.halooconsulting.ohturef.database.SqlDatabase
import fi.halooconsulting.ohturef.model.*
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

        get("/", { _, _ ->
            val refs = db.getGroupedReferences().toMutableMap()
            refs["book"] = refs.getOrDefault("book", emptyList())
            refs["article"] = refs.getOrDefault("article", emptyList())
            refs["inproceedings"] = refs.getOrDefault("inproceedings", emptyList())

            val groupedTags = db.getGroupedTags()
            val reftags = refs
                    .flatMap { it.value }
                    .map { r -> Pair(r.id, groupedTags.getOrDefault(r.id, emptyList())) }
                    .toMap()
                    .mapValues { it.value.map { it.name }.joinToString(" ") }

            val tags = db.getAllTags()
            val vars = hashMapOf("references" to refs, "reftags" to reftags, "tags" to tags)
            ModelAndView(vars, "index.jade")
        }, templateEngine)

        get("/new", { _, _ -> ModelAndView(emptyMap<String, Any>(), "new.jade") }, templateEngine)

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

            db.insert(ref)
            res.redirect("/")
        })

        get("/bibtex", { _, res ->
            val refs = db.getAllReferences()
            val converted = refs.map { BibTexConverter.toBibTex(it) }.joinToString("\n\n")
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

        before("/ref/:id", { req, _ ->
            val ref = db.getReferenceById(req.params("id"))

            if (ref == null) {
                halt(404)
            } else {
                req.attribute("reference", ref)
            }
        })

        get("/ref/:id/bibtex", { req, res ->
            val ref = db.getReferenceById(req.params("id"))!!
            val converted = BibTexConverter.toBibTex(ref)
            res.header("Content-Type", "text/plain")
            converted
        })

        get("/ref/:id", { req, _ ->
            val ref = req.attribute<Reference>("reference")
            val vars = hashMapOf("reference" to ref)
            ModelAndView(vars, "reference.jade")
        }, templateEngine)

        delete("/ref/:id", { req, _ ->
            val ref = req.attribute<Reference>("reference")
            db.delete(ref)
            "Reference deleted"
        })

        post("/ref/:id/tag", { req, res ->
            val id = req.params("id")
            val ref = db.getReferenceById(id)!!
            val name = req.queryParams("tag")


            val tag = db.getOrCreateTag(name)

            val reftag = db.getOrCreateReferenceTag(ref, tag)

            res.redirect("/ref/$id")
        })

        RouteOverview.enableRouteOverview()
        println("Started Ohturef server in port ${port()}")
    }

    fun stop() {
        Spark.stop()
    }

    companion object {
        fun getPort(): Int {
            if (System.getenv("PORT") != null) {
                return System.getenv("PORT").toInt()
            } else {
                return 8000
            }
        }
    }
}