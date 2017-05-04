package fi.halooconsulting.ohturef.database

import fi.halooconsulting.ohturef.web.IdGenerationRequest
import kotlin.coroutines.experimental.buildSequence

class IdGenerator(private val database: Database) {
    // Generates reference IDs from generation requests, and checks that they are valid
    fun generateUniqueId(req: IdGenerationRequest) : String? {
        val candidates = getIdCandidates(req)
        val first = candidates.first()
        val alreadyExistingIds = getIdsStartingWith(first).toSet()

        return candidates.find { x -> !alreadyExistingIds.contains(x) }
    }

    fun getIdCandidates(req: IdGenerationRequest) : Sequence<String> {
        val authors = req.authors.filter { it.isNotEmpty() }
        return buildSequence {
            val yearSuffix = req.year.toString().substring(2..3)
            val authorPrefix = if (authors.count() == 1) {
                authors.single().take(3)
            } else {
                authors.map(String::first).joinToString("")
            }
            val baseId = "$authorPrefix$yearSuffix"
            yield(baseId)
            yieldAll(('a'..'z').asSequence().map { "$baseId$it" })
        }
    }

    private fun getIdsStartingWith(prefix: String) : Set<String> {
        return database.getReferencesLike("$prefix%").map{it.id}.toSet()
    }
}