package fi.halooconsulting.ohturef.database

import fi.halooconsulting.ohturef.model.*
import io.requery.kotlin.like
import io.requery.sql.KotlinConfiguration
import io.requery.sql.KotlinEntityDataStore
import io.requery.sql.SchemaModifier
import io.requery.sql.TableCreationMode
import org.postgresql.ds.PGSimpleDataSource
import org.sqlite.SQLiteDataSource
import java.util.TreeSet
import javax.sql.DataSource

interface Database {
    fun getReferencesLike(likePattern: String): List<Reference>
}

class SqlDatabase(private val source: DataSource, creationMode: TableCreationMode = TableCreationMode.CREATE_NOT_EXISTS) : Database {
    val store: KotlinEntityDataStore<Any>

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
        testTag.name = "Testitagi 1"
        store.insert(testTag)

        val testRefTag = ReferenceTagEntity()
        testRefTag.ref = refs.first()
        testRefTag.tag = testTag
        store.insert(testRefTag)
    }

    init {
        SchemaModifier(source, Models.DEFAULT).createTables(creationMode)
        val conf = KotlinConfiguration(model = Models.DEFAULT, dataSource = source)
        store = KotlinEntityDataStore(conf)
    }
}