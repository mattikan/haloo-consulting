package fi.halooconsulting.ohturef

import fi.halooconsulting.ohturef.conversion.BibTexConverter
import fi.halooconsulting.ohturef.model.RefType
import fi.halooconsulting.ohturef.model.ReferenceEntity
import org.junit.Assert.*
import org.junit.Test

class BibTexConversion {
    @Test
    fun normalExample() {
        val ref = ReferenceEntity()
        ref.type      = RefType.INPROCEEDINGS
        ref.id        = "VPL11"
        ref.author    = "Vihavainen, Arto and Paksula, Matti and Luukkainen, Matti"
        ref.title     = "Extreme Apprenticeship Method in Teaching Programming for Beginners."
        ref.year      = 2011
        ref.booktitle = "SIGCSE '11: Proceedings of the 42nd SIGCSE technical symposium on Computer science education"

        val converted = BibTexConverter.toBibTex(ref)

        val expectedParts = arrayOf(
            "@inproceedings{VPL11",
            "author = {Vihavainen, Arto and Paksula, Matti and Luukkainen, Matti}",
            "title = {Extreme Apprenticeship Method in Teaching Programming for Beginners.}",
            "year = {2011}",
            "booktitle = {SIGCSE '11: Proceedings of the 42nd SIGCSE technical symposium on Computer science education}"
        )

        expectedParts.forEach { p -> assertTrue("Reference contains \"$p\"", converted.contains(p)) }
    }

    @Test
    fun umlautExample() {
        val ref = ReferenceEntity()
        ref.type      = RefType.INPROCEEDINGS
        ref.id        = "HM06"
        ref.author    = "Hassinen, Marko and Mäyrä, Hannu"
        ref.title     = "Learning programming by programming: a case study"
        ref.booktitle = "Baltic Sea '06: Proceedings of the 6th Baltic Sea conference on Computing education research: Koli Calling 2006"
        ref.year      = 2006
        ref.pages     = "117--119"
        ref.publisher = "ACM"

        val converted = BibTexConverter.toBibTex(ref)

        val expectedParts = arrayOf(
            "@inproceedings{HM06",
            "author = {Hassinen, Marko and M\\\"{a}yr\\\"{a}, Hannu}",
            "title = {Learning programming by programming: a case study}",
            "booktitle = {Baltic Sea '06: Proceedings of the 6th Baltic Sea conference on Computing education research: Koli Calling 2006}",
            "year = {2006}",
            "pages = {117--119}",
            "publisher = {ACM}"
        )

        expectedParts.forEach { p -> assertTrue("Reference should contain \"$p\", was \"$converted\"", converted.contains(p)) }
    }
}
