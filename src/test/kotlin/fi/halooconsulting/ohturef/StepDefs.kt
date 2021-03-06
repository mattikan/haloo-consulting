package fi.halooconsulting.ohturef

import com.gargoylesoftware.htmlunit.BrowserVersion
import com.gargoylesoftware.htmlunit.WebClient
import cucumber.api.java.en.Given
import cucumber.api.java.en.Then
import cucumber.api.java.en.When
import fi.halooconsulting.ohturef.database.SqlDatabase
import fi.halooconsulting.ohturef.model.Reference
import fi.halooconsulting.ohturef.web.Server
import io.requery.kotlin.eq
import io.requery.sql.TableCreationMode
import org.junit.Assert
import org.openqa.selenium.By
import org.openqa.selenium.NoSuchElementException
import org.openqa.selenium.WebDriver
import org.openqa.selenium.htmlunit.HtmlUnitDriver
import org.openqa.selenium.remote.DesiredCapabilities
import org.openqa.selenium.remote.RemoteWebDriver
import org.openqa.selenium.support.ui.Select
import org.openqa.selenium.support.ui.WebDriverWait
import org.openqa.selenium.support.ui.ExpectedConditions.*
import java.net.URL
import java.sql.Time
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern

class StepDefs {
    val username = System.getenv("SAUCE_USERNAME")
    val accessKey = System.getenv("SAUCE_ACCESS_KEY")
    val baseUrl = "http://localhost:${Server.getPort()}"
    var driver: WebDriver

    var latestRef: String? = null

    init {
        if(username != null && accessKey != null) {
            var caps = DesiredCapabilities.firefox()
            caps.setCapability("platform", "Windows 10")
            caps.setCapability("version", "52.0")
            caps.setCapability("tunnel-identifier", System.getenv("TRAVIS_JOB_NUMBER"))
            caps.setCapability("build", System.getenv("TRAVIS_BUILD_NUMBER"))
            driver = RemoteWebDriver(URL("https://$username:$accessKey@ondemand.saucelabs.com:443/wd/hub"), caps)
        } else {
            driver = object : HtmlUnitDriver(true) {
                override fun modifyWebClient(client: WebClient?): WebClient {
                    val modClient = super.modifyWebClient(client)
                    modClient.options.isThrowExceptionOnScriptError = false
                    return modClient
                }
            }
        }
        driver.manage().timeouts().pageLoadTimeout(5, TimeUnit.SECONDS)
    }

    @Given("^main page is loaded$")
    fun main_page_loaded() {
        driver.get(baseUrl)
    }

    @Given("^new reference page is loaded$")
    fun new_reference_loaded() {
        driver.get("$baseUrl/new")
    }

    @Given("^example reference exists$")
    fun example_reference_exists() {
        main_page_loaded()
        try {
            reference_is_visible("Erkin Esimerkkireferenssi")
        } catch (e: NoSuchElementException) {
            new_reference_loaded()
            reference_type_is_set_to("Article")
            field_is_set_to("title", "Erkin Esimerkkireferenssi")
            field_is_set_to("author", "Erkki Esimerkki")
            field_is_set_to("id", "ESIM1")
            create_is_clicked()
        }
    }

    @When("^page is reloaded$")
    fun page_is_reloaded() {
        driver.navigate().refresh()
    }

    @When("^reference type is set to (Book|Inproceedings|Article)$")
    fun reference_type_is_set_to(referenceType: String) {
        val typeSelector = Select(driver.findElement(By.id("type")))
        typeSelector.selectByVisibleText(referenceType)
    }

    @When("^field \"([^\"]*)\" is set to \"([^\"]*)\"$")
    fun field_is_set_to(field: String, value: String) {
        val input = driver.findElement(By.id(field))
        input.clear()
        input.sendKeys(value)
    }

    @When("^create is clicked$")
    fun create_is_clicked() {
        val createButton = driver.findElement(By.id("submit"))
        createButton.click()
    }

    @When("^id generation button is clicked$")
    fun generate_id_is_clicked() {
        val button = driver.findElement(By.id("generate-id"))
        button.click()
        WebDriverWait(driver, 5, 200).until { page ->
            val element = page?.findElement(By.id("id"))?.getAttribute("value")
            print(element)
            !element.isNullOrBlank()
        }
    }

    @When("^export bibtex button is clicked$")
    fun export_bibtex_is_clicked() {
        val button = driver.findElement(By.id("export"))
        button.click()
        WebDriverWait(driver, 5, 200).until {
            and(visibilityOfElementLocated(By.id("bibtex")), attributeToBeNotEmpty(driver.findElement(By.id("bibtex")), "value"))
        }
    }

    @When("^delete for title \"([^\"]*)\" is clicked$")
    fun delete_is_clicked(id: String) {
        val buttons = driver.findElements(By.className("inline-btn"))
        for(button in buttons) {
            if(button.getAttribute("onclick") == "deleteReference(\"$id\")") {
                button.click()
                TimeUnit.SECONDS.sleep(1)
                return
            }
        }
        Assert.assertTrue("No delete button for $id found!", false)
    }

    @When("^reference with title \"([^\"]*)\" is clicked$")
    fun reference_is_clicked(title: String) {
        val link = driver.findElement(By.linkText(title))
        link.click()
    }

    @Then("^reference with title \"([^\"]*)\" is visible")
    fun reference_is_visible(title: String) {
        val element = driver.findElement(By.linkText(title))
        Assert.assertNotNull(element)
    }

    @Then("^reference with title \"([^\"]*)\" is not visible")
    fun reference_is_not_visible(title: String) {
        try {
            val element = driver.findElement(By.linkText(title))
            Assert.assertNull(element)
        } catch (e: NoSuchElementException) {}
    }

    @Then("^reference with title \"([^\"]*)\" exists")
    fun reference_exists(title: String) {
        val ref = SqlDatabase.sqlite().getReferenceByTitle(title)
        Assert.assertNotNull(ref)
    }

    @Then("^reference with id \"([^\"]*)\" exists")
    fun reference_exists_id(id: String) {
        val ref = SqlDatabase.sqlite().getReferenceById(id)
        Assert.assertNotNull(ref)
    }

    @Then("^bibtex for (book|inproceedings|article) \"([^\"]*)\" is visible$")
    fun bibtex_is_visible(type: String, id: String) {
        val bibtexStart = driver.findElement(By.id("bibtex"))?.getAttribute("value")?.split("\n")?.get(0)
        Assert.assertEquals("@$type{$id,", bibtexStart)
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