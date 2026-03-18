package com.example.moon

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

object UpdateChecker {
    // ⚠️ RECUERDA: Cambia TU_USUARIO y TU_REPO por tus datos reales de GitHub
    private const val JSON_URL = "https://raw.githubusercontent.com/TU_USUARIO/TU_REPO/main/version.json"

    suspend fun checkUpdate(currentVersion: Int): String? {
        return withContext(Dispatchers.IO) {
            try {
                val client = OkHttpClient()
                val request = Request.Builder()
                    .url(JSON_URL)
                    .build()

                // .execute() realiza la petición
                client.newCall(request).execute().use { response ->
                    // Si la respuesta no es 200 OK, salimos
                    if (!response.isSuccessful) return@withContext null

                    // IMPORTANTE: Usamos .body() con paréntesis para evitar el error package-private
                    val responseBody = response.body()
                    val jsonData = responseBody?.string()

                    if (!jsonData.isNullOrEmpty()) {
                        val json = JSONObject(jsonData)
                        val latestVersion = json.getInt("versionCode")

                        // Si la versión en la nube es mayor a la instalada
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