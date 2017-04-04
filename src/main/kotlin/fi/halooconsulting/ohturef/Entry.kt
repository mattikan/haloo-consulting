
package fi.halooconsulting.ohturef

import fi.halooconsulting.ohturef.web.Server
import fi.halooconsulting.ohturef.model.Reference
import fi.halooconsulting.ohturef.model.ReferenceEntity
import fi.halooconsulting.ohturef.model.Models
import fi.halooconsulting.ohturef.model.RefType
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

    val source: DataSource
    val dburl = System.getenv("DATABASE_URL")
    if (dburl == null) {
        println("No database URL found - using sqlite @ jdbc:sqlite:database.db")
        source = SQLiteDataSource()
        source.url = "jdbc:sqlite:database.db"
    } else {
        println("Using PostgreSQL @ $dburl")
        source = PGSimpleDataSource()
        source.url = dburl
    }
    val model = Models.DEFAULT
    SchemaModifier(source, model).createTables(TableCreationMode.DROP_CREATE) //TODO: Poista tää ja populate
    val conf = KotlinConfiguration(model = model, dataSource = source)
    val data = KotlinEntityDataStore<Any>(conf)
    populate(data)

    val server = Server(data)
    server.start()
    
}

fun populate(data: KotlinEntityDataStore<Any>) {
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
    data.insert(entities = refs)
}

fun getOne(): Int = 1
