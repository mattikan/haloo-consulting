package fi.halooconsulting.ohturef.database

import fi.halooconsulting.ohturef.model.Models
import io.requery.sql.KotlinConfiguration
import io.requery.sql.KotlinEntityDataStore
import io.requery.sql.SchemaModifier
import io.requery.sql.TableCreationMode
import org.postgresql.ds.PGSimpleDataSource
import org.sqlite.SQLiteDataSource
import javax.sql.DataSource

class Database(private val source: DataSource, creationMode: TableCreationMode = TableCreationMode.CREATE_NOT_EXISTS) {
    val store: KotlinEntityDataStore<Any>

    companion object Factory {
        fun postgres(connectionString: String, creationMode: TableCreationMode = TableCreationMode.CREATE_NOT_EXISTS): Database {
            val source = PGSimpleDataSource()
            source.url = connectionString
            return Database(source, creationMode)
        }

        fun sqlite(databaseFile: String = "database.db", creationMode: TableCreationMode = TableCreationMode.CREATE_NOT_EXISTS): Database {
            val source = SQLiteDataSource()
            source.url = "jdbc:sqlite:$databaseFile"
            return Database(source, creationMode)
        }
    }

    init {
        SchemaModifier(source, Models.DEFAULT).createTables(creationMode)
        val conf = KotlinConfiguration(model = Models.DEFAULT, dataSource = source)
        store = KotlinEntityDataStore(conf)
    }
}