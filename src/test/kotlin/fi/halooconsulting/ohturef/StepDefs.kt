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
import org.openqa.selenium.htmlunit.HtmlUnitDriver
import org.openqa.selenium.support.ui.Select

class StepDefs {
    val driver = HtmlUnitDriver()
    val baseUrl = "http://localhost:${Server.getPort()}"

    var latestRef: String? = null

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