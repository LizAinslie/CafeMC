package dev.lizainslie.cafemc.util

import dev.lizainslie.cafemc.CafeMC
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.UUID

interface MojangResponse {
    @Serializable
    data class Success(
        val name: String,
        val id: String,
    )
    
    @Serializable
    data class Error(
        val error: String,
        val errorMessage: String
    )
}

object AccountUtils {
    private val httpClient = OkHttpClient()
    
    fun getUuidForAccountName(name: String): UUID? {
        val request = Request.Builder()
            .url("https://api.mojang.com/users/profiles/minecraft/$name")
            .build()
        
        val response = httpClient.newCall(request).execute()
        
        return when (response.code) {
            200 -> {
                val mojangResponse = Json.decodeFromString<MojangResponse.Success>(response.body!!.string())
                convertUuidWithoutDashesToUuidWithDashes(mojangResponse.id).let(UUID::fromString)
            }
            204 -> null
            400, 405, 429 -> {
                val mojangResponse = Json.decodeFromString<MojangResponse.Error>(response.body!!.string())
                CafeMC.instance.logger.warning("""
                    Failed to get player UUID from username $name:
                    HTTP Status Code: ${response.code}
                    Error: ${mojangResponse.error}
                    Error Message: ${mojangResponse.errorMessage}
                """.trimIndent())
                null
            }
            else -> null
        }
    }
}