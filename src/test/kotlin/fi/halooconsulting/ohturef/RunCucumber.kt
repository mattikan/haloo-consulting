package fi.halooconsulting.ohturef

import fi.halooconsulting.ohturef.web.Server
import cucumber.api.CucumberOptions
import cucumber.api.junit.Cucumber
import fi.halooconsulting.ohturef.database.Database
import io.requery.sql.TableCreationMode
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.runner.RunWith

@RunWith(Cucumber::class)
@CucumberOptions(plugin = arrayOf("pretty"))
class RunCucumberTests {
    companion object {

        val db = Database.sqlite(creationMode = TableCreationMode.DROP_CREATE)
        val server = Server(db)

        @BeforeClass @JvmStatic
        fun setup() {
            server.start()
        }

        @AfterClass @JvmStatic
        fun teardown() {
            server.stop()
        }

    }
}
