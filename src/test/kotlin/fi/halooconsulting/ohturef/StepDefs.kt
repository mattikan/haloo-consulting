package fi.halooconsulting.ohturef

import cucumber.api.java.en.When
import org.junit.Assert.assertEquals

class StepDefs {
    @When("returnOne is called it should return (\\d+)")
    fun returnOneReturns(x: Int) {
        assertEquals(x, getOne())
    }
}