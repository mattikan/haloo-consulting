package fi.halooconsulting.ohturef

import com.gargoylesoftware.htmlunit.WebAssert
import fi.halooconsulting.ohturef.web.Server

import cucumber.api.java.en.Given
import cucumber.api.java.en.Then
import cucumber.api.java.en.When
import fi.halooconsulting.ohturef.database.Database
import fi.halooconsulting.ohturef.model.Reference
import io.requery.kotlin.eq
import org.junit.After
import org.junit.Assert
import org.openqa.selenium.By
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

    @When("^id is set to \"([^\"]*)\"$")
    fun id_is_set_to(id: String) {
        val idInput = driver.findElementByName("id")
        idInput.sendKeys(id)
    }

    @When("^author is set to \"([^\"]*)\"$")
    fun author_is_set_to(id: String) {
        val idInput = driver.findElementByName("author")
        idInput.sendKeys(id)
    }

    @When("^title is set to \"([^\"]*)\"$")
    fun title_is_set_to(id: String) {
        val idInput = driver.findElementByName("title")
        idInput.sendKeys(id)
    }

    @When("^create is clicked$")
    fun create_is_clicked() {
        val id = driver.findElementByName("id")
        latestRef = id.text

        val createButton = driver.findElementById("submit")
        createButton.submit()
    }

    @Then("^reference with title \"([^\"]*)\" exists")
    fun reference_is_visible(title: String) {
        val element = driver.findElementByLinkText(title)
        Assert.assertNotNull(element)
    }

    @After
    fun tearDown() {
        driver.quit()

        if (latestRef != null) {
            val db = Database.sqlite()
            val refEntity = db.store {
                select (Reference::class) where (Reference::id eq latestRef)
            }.get().first()
            db.store.delete(refEntity)
        }
    }
}