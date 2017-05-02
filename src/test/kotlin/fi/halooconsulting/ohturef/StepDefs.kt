package fi.halooconsulting.ohturef

import cucumber.api.java.en.Given
import cucumber.api.java.en.Then
import cucumber.api.java.en.When
import fi.halooconsulting.ohturef.database.SqlDatabase
import fi.halooconsulting.ohturef.model.Reference
import fi.halooconsulting.ohturef.web.Server
import io.requery.kotlin.eq
import io.requery.sql.TableCreationMode
import org.junit.Assert
import org.openqa.selenium.remote.DesiredCapabilities
import org.openqa.selenium.remote.RemoteWebDriver
import org.openqa.selenium.support.ui.Select
import java.net.URL

class StepDefs {
    val username = System.getenv("SAUCE_USERNAME")
    val accessKey = System.getenv("SAUCE_ACCESS_KEY")
    val baseUrl = "http://localhost:${Server.getPort()}"
    var driver: RemoteWebDriver

    var latestRef: String? = null

    init {
        var caps = DesiredCapabilities.firefox()
        caps.setCapability("platform", "Linux")
        caps.setCapability("version", "45.0")
        caps.setCapability("tunnel-identifier", System.getenv("TRAVIS_JOB_NUMBER"))
        caps.setCapability("build", System.getenv("TRAVIS_BUILD_NUMBER"))
        driver = RemoteWebDriver(URL("http://$username:$accessKey@ondemand.saucelabs.com:4445/wd/hub"), caps)
    }

    @Given("^main page is loaded$")
    fun main_page_loaded() {
        driver.get(baseUrl)
    }

    @Given("^new reference page is loaded$")
    fun new_reference_loaded() {
        driver.get("$baseUrl/new")
    }

    @When("^reference type is set to (Book|Inproceedings|Article)$")
    fun reference_type_is_set_to(referenceType: String) {
        val typeSelector = Select(driver.findElementByName("type"))
        typeSelector.selectByVisibleText(referenceType)
    }

    @When("^field \"([^\"]*)\" is set to \"([^\"]*)\"$")
    fun field_is_set_to(field: String, value: String) {
        val input = driver.findElementByName(field)
        input.sendKeys(value)
    }

    @When("^create is clicked$")
    fun create_is_clicked() {
        val id = driver.findElementByName("id")
        latestRef = id.text

        val createButton = driver.findElementById("submit")
        createButton.submit()
    }

    @Then("^reference with title \"([^\"]*)\" is visible")
    fun reference_is_visible(title: String) {
        val element = driver.findElementByLinkText(title)
        Assert.assertNotNull(element)
    }

    @Then("^reference with title \"([^\"]*)\" exists")
    fun reference_exists(title: String) {
        val ref = SqlDatabase.sqlite().store.select(Reference::class).where(Reference::title eq title).get().first()
        Assert.assertNotNull(ref)
    }

    @cucumber.api.java.Before
    fun setUp() {
        // recreate db
        SqlDatabase.sqlite(creationMode = TableCreationMode.DROP_CREATE)
    }

    @cucumber.api.java.After
    fun tearDown() {
        driver.quit()
    }
}