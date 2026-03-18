package com.example.moon

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType // IMPORTANTE
import androidx.compose.ui.platform.LocalHapticFeedback // IMPORTANTE
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun LunarCalendarStrip(
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit
) {
    val haptic = LocalHapticFeedback.current

    // 1. Ampliamos a 60 días para que los shortcuts siempre encuentren la fase
    val days = remember { (0..60).map { LocalDate.now().plusDays(it.toLong()) } }
    val listState = rememberLazyListState()

    // 2. LOGICA DE AUTO-SCROLL: Cuando la fecha cambie por un shortcut, el calendario se mueve solo
    LaunchedEffect(selectedDate) {
        val index = days.indexOf(selectedDate)
        if (index != -1) {
            listState.animateScrollToItem(index)
        }
    }

    LazyRow(
        state = listState,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(days) { date ->
            // 3. Comparación exacta para el marcado
            val isSelected = date == selectedDate

            val backgroundColor by animateColorAsState(
                targetValue = if (isSelected) Color.Cyan.copy(alpha = 0.25f) else Color.Transparent,
                animationSpec = tween(durationMillis = 300)
            )
            val textColor by animateColorAsState(
                targetValue = if (isSelected) Color.Cyan else Color.Gray
            )

            Column(
                modifier = Modifier
                    .width(65.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(backgroundColor)
                    .border(
                        width = 1.dp,
                        color = if (isSelected) Color.Cyan.copy(0.4f) else Color.Transparent,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .clickable {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onDateSelected(date)
                    }
                    .padding(vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale("es", "ES")).uppercase(),
                    color = textColor,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = date.dayOfMonth.toString(),
                    color = if (isSelected) Color.White else Color.LightGray,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }
    }
}