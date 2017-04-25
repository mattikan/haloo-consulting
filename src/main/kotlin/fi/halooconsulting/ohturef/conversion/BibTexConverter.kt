package fi.halooconsulting.ohturef.conversion

import fi.halooconsulting.ohturef.model.Reference

data class FieldSelector(val name: String, val selector: (ref: Reference) -> String?)

object BibTexConverter {
    fun toBibTex(ref: Reference): String {
        val sb = StringBuilder()

        sb.appendln("@${ref.type.name.toLowerCase()}{${ref.id},")
        sb.appendln("    author = {${ref.author}},")
        sb.appendln("    title = {${ref.title}},")
        sb.appendln("    year = {${ref.year}},")

        val optionalProps = fieldSelectors
            .map { fs -> Pair(fs.name, fs.selector(ref)) }
            .filter { pair -> !pair.second.isNullOrEmpty() }
            .map { pair -> "    ${pair.first} = {${pair.second}}" }
            .joinToString(",\n")

        sb.append(optionalProps)
        sb.append("}")

        return escapeUmlauts(sb.toString())
    }

    val fieldSelectors: Array<FieldSelector> = arrayOf(
        FieldSelector("publisher", Reference::publisher),
        FieldSelector("address", Reference::address),
        FieldSelector("booktitle", Reference::booktitle),
        FieldSelector("pages", Reference::pages),
        FieldSelector("journal", Reference::journal),
        FieldSelector("volume", { ref -> ref.volume?.toString()}),
        FieldSelector("number", { ref -> ref.number?.toString()})
    )

    val conversions = mapOf(
        "ä" to "\\\"{a}",
        "ö" to "\\\"{o}",
        "å" to "\\a{a}"
    )

    fun escapeUmlauts(bibTex: String) : String {
        var escapedTex = bibTex
        conversions.forEach({from, to -> escapedTex = escapedTex.replace(from, to)})
        return escapedTex
    }
}