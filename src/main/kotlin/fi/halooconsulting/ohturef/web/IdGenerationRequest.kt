package fi.halooconsulting.ohturef.web

import com.github.salomonbrys.kotson.*
import com.google.gson.Gson

data class IdGenerationRequest(val authors: List<String>, val year: Int) {
    companion object {
        fun fromJson(json: String): IdGenerationRequest {
            val gson = Gson()
            return gson.fromJson<IdGenerationRequest>(json)
        }
    }
}