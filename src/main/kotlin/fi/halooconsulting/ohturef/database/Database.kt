package fi.halooconsulting.ohturef.database

import fi.halooconsulting.ohturef.model.Reference
import fi.halooconsulting.ohturef.model.ReferenceTag
import fi.halooconsulting.ohturef.model.Tag

interface Database {
    fun getReferenceById(id: String): Reference?
    fun getAllReferences(): List<Reference>
    fun getGroupedReferences(): Map<String, List<Reference>>
    fun getReferencesLike(likePattern: String): List<Reference>
    fun getReferenceByTitle(title: String): Reference?

    fun insert(ref: Reference)
    fun insert(ref: Tag)
    fun insert(ref: ReferenceTag)

    fun delete(ref: Reference)
}