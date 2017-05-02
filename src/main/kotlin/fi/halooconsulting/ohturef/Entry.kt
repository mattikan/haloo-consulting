
package fi.halooconsulting.ohturef

import fi.halooconsulting.ohturef.database.SqlDatabase
import fi.halooconsulting.ohturef.web.Server
import io.requery.sql.TableCreationMode

// Models ja ReferenceEntity saattavat näyttää siltä että eivät ole olemassa, mutta älkööt huolestuko, kokeilkaa
// gradle buildia
fun main(args: Array<String>) {
    val dburl = System.getenv("JDBC_DATABASE_URL")

    val db: SqlDatabase

    if (dburl == null) {
        println("No database URL found - using sqlite")
        db = SqlDatabase.sqlite(creationMode = TableCreationMode.DROP_CREATE)
    } else {
        println("Using PostgreSQL @ $dburl")
        db = SqlDatabase.postgres(dburl, TableCreationMode.DROP_CREATE)
    }

    db.populateWithTestData()

    val server = Server(db)
    server.start()
}

