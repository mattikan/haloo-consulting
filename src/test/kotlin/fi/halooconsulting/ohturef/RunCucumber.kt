package fi.halooconsulting.ohturef

import fi.halooconsulting.ohturef.web.Server
import cucumber.api.CucumberOptions
import cucumber.api.junit.Cucumber
import fi.halooconsulting.ohturef.database.SqlDatabase
import io.requery.sql.TableCreationMode
import org.junit.AfterClass
import org.junit.Before
import org.junit.BeforeClass
import org.junit.runner.RunWith
import spark.Spark
import java.util.concurrent.TimeUnit

@RunWith(Cucumber::class)
@CucumberOptions(plugin = arrayOf("pretty"))
class RunCucumberTests {
    companion object {

        val db = SqlDatabase.sqlite(creationMode = TableCreationMode.DROP_CREATE)
        val server = Server(db)

        @Before
        fun setup() {
            db.populateWithTestData()
        }

    }
}
