package fi.halooconsulting.ohturef.database

import fi.halooconsulting.ohturef.model.*
import io.requery.kotlin.eq
import io.requery.kotlin.like
import io.requery.kotlin.select
import io.requery.sql.KotlinConfiguration
import io.requery.sql.KotlinEntityDataStore
import io.requery.sql.SchemaModifier
import io.requery.sql.TableCreationMode
import org.postgresql.ds.PGSimpleDataSource
import org.sqlite.SQLiteDataSource
import java.util.*
import javax.sql.DataSource

class SqlDatabase(source: DataSource, creationMode: TableCreationMode = TableCreationMode.CREATE_NOT_EXISTS) : Database {
    private val store: KotlinEntityDataStore<Any>

    companion object Factory {
        fun postgres(connectionString: String, creationMode: TableCreationMode = TableCreationMode.CREATE_NOT_EXISTS): SqlDatabase {
            val source = PGSimpleDataSource()
            source.url = connectionString
            return SqlDatabase(source, creationMode)
        }

        fun sqlite(databaseFile: String = "database.db", creationMode: TableCreationMode = TableCreationMode.CREATE_NOT_EXISTS): SqlDatabase {
            val source = SQLiteDataSource()
            source.url = "jdbc:sqlite:$databaseFile"
            return SqlDatabase(source, creationMode)
        }
    }

    override fun getReferencesLike(likePattern: String): List<Reference> {
        return store {
            select(Reference::class) where Reference::id.like(likePattern)
        }.get().toList()
    }

    fun populateWithTestData() {
        // Delete everything
        store.delete().get()

        val ids = arrayOf("VPL11", "BA04", "Martin09")
        val types = arrayOf(RefType.INPROCEEDINGS, RefType.BOOK, RefType.BOOK)
        val authors = arrayOf("Vihavainen", "Beck", "Martin")
        val titles = arrayOf("Extreme Apprenticeship Method", "Extreme Programming Explained", "Clean Code")
        val years = arrayOf(2011, 2004, 2008)

        val refs = TreeSet<Reference>({ lhs, rhs -> lhs.id.compareTo(rhs.id)})
        for (i in ids.indices) {
            val ref: Reference = ReferenceEntity()
            ref.id = ids[i]
            ref.type = types[i]
            ref.author = authors[i]
            ref.title = titles[i]
            ref.year = years[i]
            refs.add(ref)
        }
        store.insert(entities = refs)

        val testTag = TagEntity()
        testTag.name = "hyvä"
        store.insert(testTag)

        val testRefTag = ReferenceTagEntity()
        testRefTag.ref = refs.first()
        testRefTag.tag = testTag
        store.insert(testRefTag)

        val testTag2 = TagEntity()
        testTag2.name = "huono"
        store.insert(testTag2)

        val testRefTag2 = ReferenceTagEntity()
        testRefTag2.ref = refs.last()
        testRefTag2.tag = testTag2
        store.insert(testRefTag2)

        val testTag3 = TagEntity()
        testTag3.name = "ok"
        store.insert(testTag3)

        val testRefTag3 = ReferenceTagEntity()
        testRefTag3.ref = refs.first()
        testRefTag3.tag = testTag3
        store.insert(testRefTag3)
    }

    override fun getReferenceById(id: String): Reference? {
        return store {
            select(Reference::class) where (Reference::id eq id)
        }.get().firstOrNull()
    }

    override fun insert(ref: Reference) {
        store.insert(ref)
    }

    override fun insert(ref: Tag) {
        store.insert(ref)
    }

    override fun insert(ref: ReferenceTag) {
        store.insert(ref)
    }

    override fun delete(ref: Reference) {
        store.delete(ref)
    }

    override fun getAllReferences(): List<Reference> {
        return store { select(Reference::class) }.get().toList()
    }

    override fun getAllTags(): List<Tag> {
        return store { select(Tag::class) }.get().toList()
    }

    override fun getReferenceByTitle(title: String): Reference {
        return store { select(Reference::class) where(Reference::title eq title) }.get().firstOrNull()
    }

    override fun getGroupedReferences(): Map<String, List<Reference>> {
        return store {
            select(Reference::class)
        }.get().groupBy { k -> k.type }.mapKeys { k -> k.key.name.toLowerCase() }
    }

    override fun getGroupedTags(): Map<String, List<Tag>> {
        return store {
            select(ReferenceTag::class)
        }.get().groupBy { t -> t.ref.id }.mapValues { l -> l.value.map{ t -> t.tag } }
    }

    override fun getOrCreateTag(name: String): Tag {
        var tag = store {select(Tag::class) where (Tag::name eq name)} .get().firstOrNull()
        if (tag == null) {
            tag = TagEntity()
            tag.name = name
            store.insert(tag)
        }
        return tag
    }

    override fun getOrCreateReferenceTag(ref: Reference, tag: Tag): ReferenceTag {
        var reftag = store {select(ReferenceTag::class) where ((ReferenceTag::ref eq ref) and (ReferenceTag::tag eq tag))} .get().firstOrNull()
        if (reftag == null) {
            reftag = ReferenceTagEntity()
            reftag.ref = ref
            reftag.tag = tag
            store.insert(reftag)
        }
        return reftag
    }

    init {
        SchemaModifier(source, Models.DEFAULT).createTables(creationMode)
        val conf = KotlinConfiguration(model = Models.DEFAULT, dataSource = source)
        store = KotlinEntityDataStore(conf)
    }
}