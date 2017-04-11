
package fi.halooconsulting.ohturef

import fi.halooconsulting.ohturef.database.Database
import fi.halooconsulting.ohturef.model.*
import fi.halooconsulting.ohturef.web.Server
import io.requery.sql.KotlinConfiguration
import io.requery.sql.KotlinEntityDataStore
import io.requery.sql.SchemaModifier
import io.requery.sql.TableCreationMode
import org.sqlite.SQLiteDataSource
import org.postgresql.ds.PGSimpleDataSource
import java.util.*
import javax.sql.DataSource

// Models ja ReferenceEntity saattavat näyttää siltä että eivät ole olemassa, mutta älkööt huolestuko, kokeilkaa
// gradle buildia
fun main(args: Array<String>) {
    val dburl = System.getenv("JDBC_DATABASE_URL")

    val db: Database

    if (dburl == null) {
        println("No database URL found - using sqlite")
        db = Database.sqlite(creationMode = TableCreationMode.DROP_CREATE)
    } else {
        println("Using PostgreSQL @ $dburl")
        db = Database.postgres(dburl, TableCreationMode.DROP_CREATE)
    }

    populate(db)

    val server = Server(db)
    server.start()
    
}

fun populate(data: Database) {
    val ids = arrayOf("VPL11", "BA04", "Martin09")
    val types = arrayOf(RefType.INPROCEEDINGS, RefType.BOOK, RefType.BOOK)
    val authors = arrayOf("Vihavainen", "Beck", "Martin")
    val titles = arrayOf("Extreme Apprenticeship Method", "Extreme Programming Explained", "Clean Code")
    val years = arrayOf(2011, 2004, 2008)

    val refs = TreeSet(Comparator<Reference> { lhs, rhs -> lhs.id.compareTo(rhs.id)})
    for (i in ids.indices) {
        val ref: Reference = ReferenceEntity()
        ref.id = ids[i]
        ref.type = types[i]
        ref.author = authors[i]
        ref.title = titles[i]
        ref.year = years[i]
        refs.add(ref)
    }
    data.store.insert(entities = refs)

    val testTag = TagEntity()
    testTag.name = "Testitagi 1"
    data.store.insert(testTag)

    val testRefTag = ReferenceTagEntity()
    testRefTag.ref = refs.first()
    testRefTag.tag = testTag
    data.store.insert(testRefTag)
}

fun getOne(): Int = 1
