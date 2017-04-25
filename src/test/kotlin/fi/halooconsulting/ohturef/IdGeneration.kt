package fi.halooconsulting.ohturef

import fi.halooconsulting.ohturef.database.Database
import fi.halooconsulting.ohturef.database.IdGenerator
import fi.halooconsulting.ohturef.database.NopDatabase
import fi.halooconsulting.ohturef.model.Reference
import fi.halooconsulting.ohturef.model.ReferenceEntity
import fi.halooconsulting.ohturef.web.IdGenerationRequest
import org.junit.Assert
import org.junit.Test

class IdGeneration {
    @Test
    fun singleAuthor() {
        val req = IdGenerationRequest(authors = listOf("Vihavainen"), year = 2017)
        val mockDb = mockDbWithEntries()
        val generator = IdGenerator(mockDb)
        val id = generator.generateUniqueId(req)
        Assert.assertEquals("Vih17", id)
    }

    @Test
    fun singleAuthorAlreadyExists() {
        val req = IdGenerationRequest(authors = listOf("Vihavainen"), year = 2017)
        val mockDb = mockDbWithEntries(referenceWithId("Vih17"))
        val generator = IdGenerator(mockDb)
        val id = generator.generateUniqueId(req)
        Assert.assertEquals("Vih17a", id)
    }

    @Test
    fun multipleAuthors() {
        val req = IdGenerationRequest(authors = listOf("Luukkainen", "Vihavainen"), year = 2017)
        val mockDb = mockDbWithEntries()
        val generator = IdGenerator(mockDb)
        val id = generator.generateUniqueId(req)
        Assert.assertEquals("LV17", id)
    }

    @Test
    fun multipleAuthorsAlreadyExists() {
        val req = IdGenerationRequest(authors = listOf("Luukkainen", "Vihavainen"), year = 2017)
        val mockDb = mockDbWithEntries(referenceWithId("LV17"))
        val generator = IdGenerator(mockDb)
        val id = generator.generateUniqueId(req)
        Assert.assertEquals("LV17a", id)
    }

    private fun referenceWithId(id: String): Reference {
        val ref = ReferenceEntity()
        ref.id = id
        return ref
    }

    private fun mockDbWithEntries(vararg refs: Reference): Database {
        return object : NopDatabase() {
            override fun getReferencesLike(likePattern: String): List<Reference> {
                return listOf(*refs)
            }
        }
    }
}