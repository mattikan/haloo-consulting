package fi.halooconsulting.ohturef.model

import io.requery.*

@Entity
interface Tag : Persistable {
    @get:Key
    @get:Generated
    var id: Int

    @get:Column(unique = true)
    var name: String
}

@Entity
interface ReferenceTag : Persistable {
    @get:Key
    @get:ManyToOne
    var ref: Reference

    @get:Key
    @get:ManyToOne
    var tag: Tag
}
