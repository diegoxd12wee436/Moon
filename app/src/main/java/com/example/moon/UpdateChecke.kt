package com.example.moon

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

object UpdateChecker {
    // URL limpia sin tokens porque ya es público
    private const val JSON_URL = "https://raw.githubusercontent.com/diegoxd12wee436/Moon/main/app/version.json"

    suspend fun checkUpdate(currentVersion: Int): String? {
        return withContext(Dispatchers.IO) {
            try {
                val client = OkHttpClient()
                val request = Request.Builder()
                    .url(JSON_URL)
                    .build()

                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) return@withContext null

                    // USA ESTA LÍNEA: Evita el error de visibilidad de .body
                    val jsonData = response.peekBody(2048).string()

                    if (jsonData.isNotEmpty()) {
                        val json = JSONObject(jsonData)
                        val latestVersion = json.getInt("versionCode")

                        // Si la versión en GitHub (2) es mayor a la de tu código (1)
                        if (latestVersion > currentVersion) {
                            return@withContext json.getString("downloadUrl")
                        }
                    }
                }
                null
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
}