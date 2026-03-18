package com.example.moon

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

// --- 1. MODELO DE DATOS ---
data class TechnicalPlague(
    val type: String,          // "🥑 AGUACATE" o "🍊 CÍTRICO"
    val name: String,          // Nombre común
    val scientific: String,    // Nombre científico
    val howToIdentify: String, // Identificación visual
    val damage: String,        // Qué hace (Daño)
    val bioControl: String,    // Manejo Orgánico
    val chemicalControl: String, // Manejo Químico
    val season: String,        // CUÁNDO SE ALBOROTA (La nota que pediste)
    val isEmergency: Boolean   // Switch de rescate
)

@Composable
fun MoonFarmScreen(viewModel: MoonViewModel) {
    // --- NUEVAS VARIABLES PARA ACTUALIZACIÓN ---
    val context = androidx.compose.ui.platform.LocalContext.current
    var updateUrl by remember { mutableStateOf<String?>(null) }
    var showUpdateDialog by remember { mutableStateOf(false) }

    val haptic = androidx.compose.ui.platform.LocalHapticFeedback.current
    val moonState by viewModel.moonState.collectAsState()
    val selectedDate by viewModel.selectedDate.collectAsState()
    val isApiConnected by viewModel.isApiConnected.collectAsState()
    val isToday = selectedDate == LocalDate.now()

    var currentTime by remember { mutableStateOf(LocalTime.now()) }

    // --- LÓGICA DE ACTUALIZACIÓN AL ENTRAR ---
    LaunchedEffect(Unit) {
        val currentBuild = 1 // ⚠️ Cambia esto a 2 cuando subas la nueva versión
        val url = UpdateChecker.checkUpdate(currentBuild)
        if (url != null) {
            updateUrl = url
            showUpdateDialog = true
            // VIBRACIÓN DE ALERTA (Haptic Feedback)
            haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
        }
    }

    LaunchedEffect(Unit) {
        while (true) {
            currentTime = LocalTime.now()
            kotlinx.coroutines.delay(1000)

        }
    }

    var selectedCrop by remember { mutableStateOf("🥑 AGUACATE") }
    var showEmergency by remember { mutableStateOf(false) }

    // --- CONTENEDOR RAÍZ ÚNICO ---
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                androidx.compose.ui.graphics.Brush.verticalGradient(
                    colors = listOf(Color(0xFF0B0D1A), Color(0xFF161930))
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(50.dp))

            // --- RELOJ CON INDICADOR CENTRADO ARRIBA ---
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
            ) {
                Surface(
                    modifier = Modifier.size(width = 280.dp, height = 130.dp),
                    color = Color.White.copy(alpha = 0.05f),
                    shape = RoundedCornerShape(28.dp),
                    border = BorderStroke(1.dp, Color.Cyan.copy(alpha = 0.2f))
                ) {
                    Box(modifier = Modifier.fillMaxSize().padding(top = 8.dp)) {
                        Column(
                            modifier = Modifier.align(Alignment.TopCenter),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            ConnectionIndicator(isApiConnected)
                        }
                    }
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(top = 10.dp)
                ) {
                    Text(
                        text = currentTime.format(DateTimeFormatter.ofPattern("hh:mm")),
                        style = androidx.compose.ui.text.TextStyle(
                            fontSize = 82.sp,
                            fontWeight = FontWeight.W900,
                            brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                                colors = listOf(Color.White, Color.White.copy(0.6f))
                            ),
                            letterSpacing = (-4).sp
                        )
                    )

                    Surface(
                        modifier = Modifier.offset(y = (-10).dp),
                        color = Color.Cyan,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = currentTime.format(DateTimeFormatter.ofPattern("a")).uppercase(),
                            color = Color(0xFF0B0D1A),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 1.dp),
                            letterSpacing = 2.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // --- PANEL DE FECHA ---
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White.copy(alpha = 0.05f),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, Color.Cyan.copy(alpha = 0.15f))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier.size(40.dp).background(Color.Cyan.copy(0.1f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.DateRange, null, tint = Color.Cyan, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = selectedDate.format(DateTimeFormatter.ofPattern("EEEE d", Locale("es", "NI"))).uppercase(),
                            color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Black
                        )
                        Text(
                            text = selectedDate.format(DateTimeFormatter.ofPattern("MMMM, yyyy", Locale("es", "NI"))).replaceFirstChar { it.uppercase() },
                            color = Color.Cyan.copy(0.5f), fontSize = 13.sp
                        )
                    }
                }
            }

            // --- BLOQUE 3: LUNA ---
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                MoonDisplay(
                    state = moonState,
                    onPhaseJump = { fase -> viewModel.jumpToNextPhase(fase) }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // --- BLOQUE 4: PROYECCIÓN LUNAR ---
            Text("PROYECCIÓN LUNAR", color = Color.Gray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            LunarCalendarStrip(
                selectedDate = selectedDate,
                onDateSelected = {
                    viewModel.updateSelectedDate(it)
                    haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
                }
            )

            if (!isToday) {
                TextButton(onClick = { viewModel.resetToToday() }) {
                    Text("VOLVER A HOY", color = Color.Cyan, fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // --- BLOQUE 5: CONSEJOS AGRÍCOLAS ---
            val smartPhase = viewModel.getPhaseForDate(selectedDate)
            TechnicalAdviceList(smartPhase)

            Spacer(modifier = Modifier.height(30.dp))

            // --- BLOQUE 6: SELECTORES Y PLAGAS ---
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                CropTabButton("🥑 AGUACATE", selectedCrop == "🥑 AGUACATE", Modifier.weight(1f)) { selectedCrop = it }
                CropTabButton("🍊 CÍTRICOS", selectedCrop == "🍊 CÍTRICOS", Modifier.weight(1f)) { selectedCrop = it }
            }

            Spacer(modifier = Modifier.height(20.dp))

            EmergencyToggle(showEmergency) { showEmergency = it }
            ChemicalUsageNotice(isVisible = showEmergency)

            Spacer(modifier = Modifier.height(15.dp))

            val plagas = getFullPlagueData(showEmergency).filter {
                if (selectedCrop.contains("AGUACATE")) it.type.contains("AGUACATE")
                else it.type.contains("CÍTRICO")
            }

            plagas.forEach { EnhancedPlagueCard(it) }
            Spacer(modifier = Modifier.height(50.dp))
        }

        // --- DIÁLOGO DE ACTUALIZACIÓN CYBER-AGRO ---
        if (showUpdateDialog && updateUrl != null) {
            AlertDialog(
                onDismissRequest = { showUpdateDialog = false },
                properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false),
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .border(1.dp, Color.Cyan.copy(0.3f), RoundedCornerShape(28.dp)),
                containerColor = Color(0xFF0B0D1A),
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Refresh, null, tint = Color.Cyan, modifier = Modifier.size(30.dp))
                        Spacer(Modifier.width(12.dp))
                        Text("SISTEMA ACTUALIZADO", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Black)
                    }
                },
                text = {
                    Text(
                        "Se han detectado nuevas mejoras en los algoritmos lunares y control de plagas.",
                        color = Color.LightGray, fontSize = 14.sp
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(updateUrl))
                            context.startActivity(intent)
                            showUpdateDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Cyan),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("INSTALAR AHORA", color = Color(0xFF0B0D1A), fontWeight = FontWeight.Black)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showUpdateDialog = false }) {
                        Text("MÁS TARDE", color = Color.Gray)
                    }
                }
            )
        }
    }
}
// --- COMPONENTES DE APOYO ---

@Composable
fun TechnicalAdviceList(phase: String) {
    val tips = AgricultureExpertLogic.getTechnicalAdvice(phase)

    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF161930)),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, Color.Cyan.copy(0.2f))
    ) {
        Column(Modifier.padding(20.dp)) {
            // Título dinámico según la fase
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Settings, null, tint = Color.Cyan, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(10.dp))
                Text(
                    text = "RECOMENDACIONES: ${phase.uppercase()}",
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.sp
                )
            }

            Spacer(Modifier.height(15.dp))

            tips.forEach { (emoji, text) ->
                Row(
                    modifier = Modifier.padding(vertical = 8.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    // Círculo para el emoji
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(Color.White.copy(0.07f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(emoji, fontSize = 18.sp)
                    }

                    Spacer(Modifier.width(14.dp))

                    // Texto con formato (procesando las negritas **)
                    val parts = text.split("**")
                    Text(
                        text = androidx.compose.ui.text.buildAnnotatedString {
                            if (parts.size >= 3) {
                                append(parts[0])
                                withStyle(style = androidx.compose.ui.text.SpanStyle(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Cyan)
                                ) {
                                    append(parts[1])
                                }
                                append(parts[2])
                            } else {
                                append(text)
                            }
                        },
                        color = Color.LightGray,
                        fontSize = 13.sp,
                        lineHeight = 19.sp
                    )
                }
                // Línea divisoria sutil entre consejos
                Divider(color = Color.White.copy(0.03f), modifier = Modifier.padding(start = 50.dp))
            }
        }
    }
}

@Composable
fun ConnectionIndicator(isConnected: Boolean) {
    val statusColor by animateColorAsState(if (isConnected) Color(0xFF00E676) else Color(0xFFFFD600))
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(statusColor.copy(0.1f))
            .border(0.5.dp, statusColor.copy(0.4f), RoundedCornerShape(12.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(statusColor))
        Spacer(Modifier.width(6.dp))
        Text(
            text = if (isConnected) "LIVE" else "LOCAL",
            color = statusColor,
            fontSize = 8.sp,
            fontWeight = FontWeight.ExtraBold
        )
    }
}

@Composable
fun MoonDisplay(state: MoonState, onPhaseJump: (String) -> Unit) {
    val haptic = androidx.compose.ui.platform.LocalHapticFeedback.current

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(240.dp)) {
            // --- TU CANVAS (LO DEJO IGUAL PARA QUE NO SE BUGUEE) ---
            Canvas(modifier = Modifier.size(220.dp)) {
                drawCircle(
                    brush = androidx.compose.ui.graphics.Brush.radialGradient(
                        colors = listOf(Color.Cyan.copy(alpha = 0.08f), Color.Transparent),
                        center = center,
                        radius = size.width / 1.2f
                    )
                )
            }
            Canvas(modifier = Modifier.size(170.dp)) {
                val radius = size.minDimension / 2
                val moonPath = androidx.compose.ui.graphics.Path().apply {
                    addOval(androidx.compose.ui.geometry.Rect(center, radius))
                }
                drawContext.canvas.save()
                drawContext.canvas.clipPath(moonPath)
                drawCircle(
                    brush = androidx.compose.ui.graphics.Brush.radialGradient(
                        colors = listOf(Color(0xFFFDFCF0), Color(0xFFC2C2B0)),
                        center = Offset(center.x - 20f, center.y - 20f),
                        radius = radius * 1.5f
                    ),
                    radius = radius
                )
                val craterColor = Color(0xFF000000).copy(alpha = 0.08f)
                drawCircle(craterColor, radius * 0.25f, center = Offset(center.x, center.y + radius * 0.4f))
                drawCircle(craterColor, radius * 0.15f, center = Offset(center.x - radius * 0.4f, center.y - radius * 0.2f))
                drawCircle(craterColor, radius * 0.12f, center = Offset(center.x + radius * 0.3f, center.y - radius * 0.5f))
                drawCircle(craterColor, radius * 0.08f, center = Offset(center.x + radius * 0.6f, center.y + radius * 0.1f))
                drawCircle(craterColor, radius * 0.06f, center = Offset(center.x - radius * 0.5f, center.y + radius * 0.5f))

                val illumination = (state.illumination.toFloat() / 100f).coerceIn(0f, 1f)
                val phase = state.phaseName.lowercase()
                val isWaning = phase.contains("menguante") || phase.contains("last") || phase.contains("waning")
                val maxOffset = radius * 2.1f
                val currentOffset = maxOffset * illumination
                val xOffset = if (isWaning) currentOffset else -currentOffset

                drawCircle(
                    color = Color(0xFF0B0D1A).copy(alpha = 0.92f),
                    radius = radius * 1.05f,
                    center = Offset(center.x + xOffset, center.y)
                )
                drawContext.canvas.restore()
                drawCircle(
                    color = Color.White.copy(alpha = 0.15f),
                    radius = radius,
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.dp.toPx())
                )
            }
        }

        Text(text = state.phaseName.uppercase(), color = Color.Cyan, fontSize = 16.sp, letterSpacing = 4.sp, fontWeight = FontWeight.Bold)
        Text(text = "${state.illumination.toInt()}%", fontSize = 68.sp, color = Color.White, fontWeight = FontWeight.Black)

        // --- SOLO AÑADIMOS ESTO PARA LOS SHORTCUTS ---
        Spacer(modifier = Modifier.height(16.dp))
        val phases = listOf("Nueva" to "🌑", "Creciente" to "🌓", "Llena" to "🌕", "Menguante" to "🌗")
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(phases) { (name, icon) ->
                Surface(
                    modifier = Modifier.clickable {
                        haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
                        onPhaseJump(name)
                    },
                    color = Color.White.copy(0.05f),
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.dp, Color.Cyan.copy(0.2f))
                ) {
                    Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(icon, fontSize = 12.sp)
                        Spacer(Modifier.width(6.dp))
                        Text(name.uppercase(), color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun CropTabButton(label: String, isSelected: Boolean, modifier: Modifier, onClick: (String) -> Unit) {
    Button(
        onClick = { onClick(label) },
        modifier = modifier.height(48.dp),
        colors = ButtonDefaults.buttonColors(containerColor = if (isSelected) Color(0xFF3D5AFE) else Color.White.copy(0.05f)),
        shape = RoundedCornerShape(12.dp)
    ) { Text(label, fontSize = 12.sp, fontWeight = FontWeight.Bold) }
}


@Composable
fun EmergencyToggle(isOn: Boolean, onToggle: (Boolean) -> Unit) {
    val haptic = androidx.compose.ui.platform.LocalHapticFeedback.current // <--- Obtener el haptic

    Card(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(
            containerColor = if (isOn) Color(0xFF421010) else Color.White.copy(0.05f)
        ),
        border = BorderStroke(1.dp, if (isOn) Color.Red.copy(0.5f) else Color.Transparent)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Warning,
                null,
                tint = if (isOn) Color.Red else Color.Gray,
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    "MODO RESCATE (QUÍMICO)",
                    color = if (isOn) Color.White else Color.Gray,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                if (isOn) {
                    Text("Uso de emergencia para salvar cosecha", color = Color.Red.copy(0.8f), fontSize = 10.sp)
                }
            }
            Switch(
                checked = isOn,
                onCheckedChange = {
                    // VIBRACIÓN FUERTE AL ACTIVAR EMERGENCIA
                    haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
                    onToggle(it)
                },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.Red,
                    checkedTrackColor = Color.Red.copy(alpha = 0.3f)
                )
            )
        }
    }
}
@Composable
fun ChemicalUsageNotice(isVisible: Boolean) {
    AnimatedVisibility(
        visible = isVisible,
        enter = expandVertically() + fadeIn(),
        exit = shrinkVertically() + fadeOut()
    ) {
        Surface(
            modifier = Modifier.padding(top = 10.dp, bottom = 15.dp),
            color = Color.Red.copy(alpha = 0.1f),
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(1.dp, Color.Red.copy(0.3f))
        ) {
            Column(Modifier.padding(12.dp)) {
                Text(
                    "💡 ¿POR QUÉ USAR QUÍMICOS?",
                    color = Color(0xFFFF8A80),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "Se recurre al control químico cuando la plaga supera el umbral económico y el control biológico es insuficiente para salvar el árbol. " +
                            "Es una medida de choque para evitar la pérdida total de la inversión.",
                    color = Color.LightGray,
                    fontSize = 11.sp,
                    lineHeight = 15.sp
                )
                Divider(Modifier.padding(vertical = 8.dp), color = Color.Red.copy(0.2f))
                Text(
                    "✅ RECOMENDACIÓN: Aplique solo en los focos afectados (no a todo el huerto), use equipo de protección y respete el periodo de carencia antes de cosechar.",
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun EnhancedPlagueCard(plaga: TechnicalPlague) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E2036).copy(0.8f)),
        border = BorderStroke(1.dp, Color.White.copy(0.1f))
    ) {
        Column(Modifier.padding(16.dp)) {
            // CABECERA: Nombre y Científico
            Text(
                text = plaga.name,
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Black
            )
            Text(
                text = plaga.scientific,
                color = Color.Cyan.copy(0.7f),
                fontSize = 12.sp,
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
            )

            Divider(Modifier.padding(vertical = 12.dp), color = Color.White.copy(0.05f))

            // CUERPO: Identificación y Daño
            InfoSection("🔍 IDENTIFICACIÓN", plaga.howToIdentify)
            InfoSection("⚠️ DAÑO QUE CAUSA", plaga.damage)

            Spacer(modifier = Modifier.height(12.dp))

            // NOTA DE TEMPORADA (Resaltada)
            Surface(
                color = Color.Cyan.copy(0.1f),
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    Modifier.padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Info,
                        contentDescription = null,
                        tint = Color.Cyan,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = plaga.season,
                        color = Color.Cyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // PIE: Controles (Bio vs Químico)
            ControlBox(
                isEmergency = plaga.isEmergency,
                text = if (plaga.isEmergency) plaga.chemicalControl else plaga.bioControl
            )
        }
    }
}

@Composable
fun InfoSection(label: String, content: String) {
    Column(Modifier.padding(vertical = 4.dp)) {
        Text(label, color = Color.Gray, fontSize = 9.sp, fontWeight = FontWeight.Bold)
        Text(content, color = Color.LightGray, fontSize = 13.sp, lineHeight = 18.sp)
    }
}

@Composable
fun ControlBox(isEmergency: Boolean, text: String) {
    val bgColor = if (isEmergency) Color(0x22F44336) else Color(0x224CAF50)
    val borderColor = if (isEmergency) Color.Red.copy(0.4f) else Color.Green.copy(0.4f)
    val labelColor = if (isEmergency) Color(0xFFFF8A80) else Color(0xFFB9F6CA)

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, borderColor)
    ) {
        Column(Modifier.padding(12.dp).fillMaxWidth()) {
            Text(
                text = if (isEmergency) "🚨 MANEJO QUÍMICO (EMERGENCIA)" else "🌿 MANEJO ECOLÓGICO",
                color = labelColor,
                fontSize = 10.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = text,
                color = Color.White,
                fontSize = 13.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

fun getFullPlagueData(isEmergency: Boolean): List<TechnicalPlague> {
    return listOf(
        // --- AGUACATE (4 PLAGAS) ---
        TechnicalPlague(
            "🥑 AGUACATE", "Barrenador del Tallo", "Heilipus lauri",
            "Presencia de aserrín blanco y savia seca (lloro) en el tronco.",
            "Perfora la madera bloqueando la savia; causa muerte de ramas o del árbol.",
            "Podar ramas secas y sellar con pasta de cal y azufre.",
            "Uso de insecticidas sistémicos dirigidos al tronco en focos detectados.",
            "⚠️ Se alborota en invierno (época lluviosa) por la humedad en la corteza.",
            isEmergency
        ),
        TechnicalPlague(
            "🥑 AGUACATE", "Trips", "Scirtothrips perseae",
            "Insectos diminutos en flores y frutos recién cuajados.",
            "Deforma el fruto creando una cáscara áspera (piel de cocodrilo).",
            "Aplicar extracto de Neem o Jabón Potásico al atardecer.",
            "Abamectina aplicada en dosis controladas tras la floración.",
            "☀️ Se alborota en verano (época seca) y durante la floración.",
            isEmergency
        ),
        TechnicalPlague(
            "🥑 AGUACATE", "Ácaro Cristalino", "Oligonychus punicae",
            "Hojas con manchas rojizas o cafés que parecen quemadas.",
            "Causa la caída masiva de hojas, dejando el fruto expuesto al sol.",
            "Uso de azufre elemental humectable en el follaje.",
            "Acaricidas de contacto si la población supera 10 ácaros por hoja.",
            "🔥 Se alborota con el calor fuerte y polvo del verano (Marzo-Abril).",
            isEmergency
        ),
        TechnicalPlague(
            "🥑 AGUACATE", "Chinche de Encaje", "Pseudacysta perseae",
            "Manchas amarillas en la cara superior y puntos negros en el envés.",
            "Seca las hojas rápidamente, debilitando la producción del año.",
            "Aspersiones de aceite mineral para asfixiar las ninfas.",
            "Insecticidas a base de piretroides en aplicaciones dirigidas.",
            "🌬️ Se alborota en la transición de verano a invierno.",
            isEmergency
        ),

        // --- CÍTRICOS (4 PLAGAS) ---
        TechnicalPlague(
            "🍊 CÍTRICO", "Psílido Asiático", "Diaphorina citri",
            "Insectos pequeños inclinados a 45° en brotes tiernos.",
            "Transmite el HLB (muerte total del árbol) y genera fumagina.",
            "Controlar hormigas y usar hongos entomopatógenos.",
            "Insecticidas sistémicos para proteger los brotes nuevos.",
            "🌱 Se alborota en cada época de brotación (salida de hojas nuevas).",
            isEmergency
        ),
        TechnicalPlague(
            "🍊 CÍTRICO", "Minador de la Hoja", "Phyllocnistis citrella",
            "Caminos plateados y hojas que se enrollan como tabaco.",
            "Detiene el crecimiento de árboles jóvenes y daña el follaje.",
            "Aceite de Neem sobre los brotes más tiernos.",
            "Spinosad cuando se ven las primeras galerías brillantes.",
            "🌧️ Se alborota con las primeras lluvias y el crecimiento de hojas.",
            isEmergency
        ),
        TechnicalPlague(
            "🍊 CÍTRICO", "Pulgón Negro", "Toxoptera aurantii",
            "Colonias de insectos negros apretados en las puntas de ramas.",
            "Deforma las hojas nuevas y transmite el virus de la Tristeza.",
            "Agua jabonosa o infusión de tabaco y ajo.",
            "Piretroides suaves si la infestación es muy alta.",
            "🌤️ Se alborota en climas frescos y húmedos.",
            isEmergency
        ),
        TechnicalPlague(
            "🍊 CÍTRICO", "Escama de Nieve", "Unaspis citri",
            "Troncos que parecen manchados con cal o nieve en polvo.",
            "Agrieta la corteza y puede matar ramas principales.",
            "Cepillar el tronco con agua y jabón; aplicar aceite mineral.",
            "Insecticidas mezclados con aceite para penetrar la escama.",
            "🪵 Se alborota en árboles descuidados con poca poda y mucha sombra.",
            isEmergency
        )
    )
}