package fi.halooconsulting.ohturef.database

import fi.halooconsulting.ohturef.model.Reference
import fi.halooconsulting.ohturef.model.ReferenceTag
import fi.halooconsulting.ohturef.model.Tag

open class NopDatabase : Database {
    override fun getAllReferences(): List<Reference> = emptyList()
    override fun getAllTags(): List<Tag> = emptyList()
    override fun getGroupedReferences(): Map<String, List<Reference>> = emptyMap()
    override fun getGroupedTags(): Map<String, List<Tag>> = emptyMap()
    override fun getReferenceById(id: String): Reference? = null
    override fun getReferenceByTitle(title: String): Reference? = null
    override fun getReferencesLike(likePattern: String): List<Reference> = emptyList()
    override fun getOrCreateTag(name: String): Tag? = null
    override fun getOrCreateReferenceTag(ref: Reference, tag: Tag): ReferenceTag? = null

    override fun insert(ref: Reference) { }
    override fun insert(ref: Tag) { }
    override fun insert(ref: ReferenceTag) { }

    override fun delete(ref: Reference) { }
}