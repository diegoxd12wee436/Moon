package com.example.moon

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import kotlin.math.cos
import kotlin.math.PI
import kotlin.math.round

// --- 1. MODELOS DE DATOS ---
data class MoonState(
    val phaseName: String,
    val illumination: Double,
    val distanceMi: String = "384,400 km",
    val date: LocalDate = LocalDate.now()
)

data class WeatherResponse(val astronomy: AstronomyData?)
data class AstronomyData(val astro: AstroDetails?)
data class AstroDetails(
    val moon_phase: String?,
    val moon_illumination: String?
)

interface MoonApiService {
    @GET("v1/astronomy.json")
    suspend fun getMoonData(
        @Query("key") apiKey: String,
        @Query("q") location: String,
        @Query("dt") date: String
    ): WeatherResponse
}

class MoonViewModel : ViewModel() {

    private val API_KEY = "91d09c06796a47cb82a180730261803"

    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate: StateFlow<LocalDate> = _selectedDate.asStateFlow()

    private val _moonState = MutableStateFlow(calculateMoonData(LocalDate.now()))
    val moonState: StateFlow<MoonState> = _moonState.asStateFlow()

    private val _isApiConnected = MutableStateFlow(false)
    val isApiConnected: StateFlow<Boolean> = _isApiConnected.asStateFlow()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.weatherapi.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val apiService = retrofit.create(MoonApiService::class.java)

    init {
        fetchMoonData(LocalDate.now())
    }

    // --- LÓGICA DE RECOMENDACIÓN INTELIGENTE (ESTADO DE LA PLANTA) ---
    // Esta función determina qué consejos dar basándose en la fuerza (iluminación)
    // para que el cambio no sea brusco "de una".
    fun getPhaseForDate(date: LocalDate): String {
        // Obtenemos los datos técnicos de esa fecha
        val technicalState = calculateMoonData(date)
        val illumination = technicalState.illumination
        val name = technicalState.phaseName.lowercase()

        return when {
            // 🌑 EFECTO LUNA NUEVA: Mientras la luz sea débil (<= 12%),
            // la savia sigue abajo. No importa si el calendario dice "Creciente".
            illumination <= 12.0 -> "Luna Nueva"

            // 🌕 EFECTO LUNA LLENA: Si la luz es fuerte (>= 85%),
            // la savia ya está arriba. Ideal para cosecha.
            illumination >= 85.0 -> "Luna Llena"

            // 🌗 EFECTO MENGUANTE: Si la luz está bajando pero aún es alta,
            // la planta sigue en estado de "llena" por inercia.
            illumination > 60.0 && (name.contains("menguante") || name.contains("quarter")) -> "Luna Llena"

            // 🌱 RESTO DE FASES: Creciente o Menguante normal cuando la luz es moderada.
            else -> technicalState.phaseName
        }
    }

    fun fetchMoonData(date: LocalDate) {
        viewModelScope.launch {
            try {
                val response = apiService.getMoonData(API_KEY, "Jinotepe", date.toString())
                val astro = response.astronomy?.astro

                if (astro != null) {
                    val illuDouble = astro.moon_illumination?.toDoubleOrNull() ?: 0.0

                    // Calculamos la dirección del ciclo manualmente para no depender de la API
                    val testState = calculateMoonData(date)

                    _moonState.value = MoonState(
                        // FORZAMOS que el nombre venga de nuestra lógica de iluminación
                        phaseName = testState.phaseName,
                        illumination = illuDouble,
                        date = date,
                        distanceMi = if (illuDouble > 95) "357,000 km" else "384,400 km"
                    )
                    _isApiConnected.value = true
                }
            } catch (e: Exception) {
                _isApiConnected.value = false
                _moonState.value = calculateMoonData(date)
            }
        }
    }

    fun updateSelectedDate(date: LocalDate) {
        _selectedDate.value = date
        fetchMoonData(date)
    }

    fun resetToToday() {
        val today = LocalDate.now()
        _selectedDate.value = today
        fetchMoonData(today)
    }

    private fun translateMoonPhase(phase: String?): String {
        return when (phase) {
            "New Moon" -> "Luna Nueva"
            "Waxing Crescent" -> "Luna Creciente"
            "First Quarter" -> "Cuarto Creciente"
            "Waxing Gibbous" -> "Luna Creciente"
            "Full Moon" -> "Luna Llena"
            "Waning Gibbous" -> "Luna Menguante"
            "Last Quarter" -> "Cuarto Menguante"
            "Waning Crescent" -> "Luna Menguante"
            else -> "Luna Llena"
        }
    }
    // Dentro de MoonViewModel.kt
    fun jumpToNextPhase(targetPhase: String) {
        var checkDate = LocalDate.now()
        var bestDate = checkDate
        var bestDiff = Double.MAX_VALUE

        // Buscamos en los próximos 35 días para cubrir un ciclo completo
        for (i in 0..35) {
            val state = calculateMoonData(checkDate)

            // Definimos el "punto ideal" de iluminación para cada botón
            val targetIllumination = when (targetPhase) {
                "Llena" -> 100.0
                "Nueva" -> 0.0
                "Creciente", "Menguante" -> 50.0 // Punto medio de la fase
                else -> 50.0
            }

            // Si el nombre coincide, verificamos qué tan cerca está del punto ideal
            if (state.phaseName.contains(targetPhase, ignoreCase = true)) {
                val diff = Math.abs(state.illumination - targetIllumination)
                if (diff < bestDiff) {
                    bestDiff = diff
                    bestDate = checkDate
                }
            }
            checkDate = checkDate.plusDays(1)
        }
        updateSelectedDate(bestDate)
    }
    private fun calculateMoonData(date: LocalDate): MoonState {
        val refNewMoon = LocalDate.of(2024, 1, 11)
        val daysSince = ChronoUnit.DAYS.between(refNewMoon, date)
        val lunarCycle = 29.53059
        val phaseIndex = ((daysSince % lunarCycle) + lunarCycle) % lunarCycle
        val normalizedPhase = phaseIndex / lunarCycle

        val angle = normalizedPhase * 2 * Math.PI
        val illuminationDouble = ((1 - Math.cos(angle)) / 2) * 100
        val rounded = Math.round(illuminationDouble).toDouble()

        val name = when {
            rounded >= 99.0 -> "Luna Llena"
            rounded <= 1.0 -> "Luna Nueva"
            normalizedPhase < 0.5 -> "Luna Creciente"
            else -> "Luna Menguante"
        }

        return MoonState(phaseName = name, illumination = rounded, date = date)
    }
}