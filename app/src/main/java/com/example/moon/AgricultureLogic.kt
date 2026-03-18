package com.example.moon

object AgricultureExpertLogic {
    fun getTechnicalAdvice(phase: String): List<Pair<String, String>> {
        return when (phase) {
            "Luna Nueva" -> listOf(
                "🌑" to "**ESTADO:** La savia está concentrada en las raíces. El árbol descansa.",
                "✂️" to "**PODA:** Ideal para podas de formación. El árbol no 'sangra' tanto.",
                "🐛" to "**PLAGAS:** Aplicar controles al tronco y suelo (Gallina Ciega o Barrenador).",
                "🌿" to "**DESHIERBE:** Eliminar maleza ahora retrasa su rebrote.",
                "🧬" to "**RECOMENDACIÓN:** Buen momento para aplicar abonos orgánicos al suelo."
            )
            "Luna Creciente" -> listOf(
                "🌱" to "**ESTADO:** La savia empieza a subir hacia las ramas y hojas.",
                "💧" to "**RIEGO:** Aumentar frecuencia; la planta está activa y consume más agua.",
                "🍃" to "**FOLIAR:** Momento perfecto para abonos a la hoja (Zinc, Boro, Magnesio).",
                "🥑" to "**INJERTO:** Fase ideal para injertar; la unión pega más rápido por el flujo de savia.",
                "⚠️" to "**CUIDADO:** Evite podas fuertes, la planta perdería mucha energía."
            )
            "Luna Llena" -> listOf(
                "🌕" to "**ESTADO:** Máxima concentración de savia en follaje y frutos.",
                "🧺" to "**COSECHA:** Los frutos están más pesados, jugosos y con mejor sabor.",
                "🦟" to "**ALERTA:** Mayor actividad de insectos chupadores (Pulgón, Trips). Monitorear.",
                "🍊" to "**TRASPLANTE:** No se recomienda trasplantar hoy; las raíces sufren más.",
                "🧴" to "**CONTROL:** Aplicar repelentes biológicos fuertes en el envés de la hoja."
            )
            "Luna Menguante" -> listOf(
                "🌗" to "**ESTADO:** La savia comienza a bajar de nuevo hacia el tronco.",
                "🧹" to "**LIMPIEZA:** Retirar frutos caídos o enfermos del suelo para evitar hongos.",
                "🍄" to "**HONGOS:** Aplicar fungicidas (Caldo Bordelés o Cobre) para prevenir Antracnosis.",
                "🪵" to "**ESTACAS:** Buen momento para cortar estacas de propagación; duran más.",
                "📉" to "**FERTILIZACIÓN:** Aplicar fertilizantes granulados al suelo (NPK) para llenado de raíz."
            )
            else -> listOf("📅" to "Siga el monitoreo habitual de humedad y plagas.")
        }
    }
}