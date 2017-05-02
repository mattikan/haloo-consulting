package fi.halooconsulting.ohturef.model

import io.requery.*

enum class RefType {
    ARTICLE, BOOK, INPROCEEDINGS;

    companion object {
        private val types: Map<String, RefType> = mapOf(
            "article" to ARTICLE,
            "book" to BOOK,
            "inproceedings" to INPROCEEDINGS)

        fun fromString(str: String): RefType? = types[str.trim().toLowerCase()]
    }
}

@Entity
interface Reference : Persistable {

    @get:Key
    var id: String // @inproceedings{VPL11, ... } --> id = "VPL11"

    var type: RefType

    var author: String // TODO: Halutaanko näitä tallettaa erillisiin tauluihin ja tallettaa vain referenssejä?
    var title: String
    var year: Int
    var publisher: String?
    var address: String?
    var pages: String?
    var journal: String?
    var volume: Int?
    var number: Int?
    var booktitle: String?

    @get:OneToMany
    var tags: Set<ReferenceTag>
}
