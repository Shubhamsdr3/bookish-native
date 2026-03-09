package com.newaura.bookish.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.newaura.bookish.core.common.TextViewBody
import com.newaura.bookish.core.common.TextViewBold
import com.newaura.bookish.core.common.TextViewSemiBold
import com.newaura.bookish.core.data.ActivityLevel
import com.newaura.bookish.core.util.buildMonthLabels
import com.newaura.bookish.core.util.buildWeekColumns
import com.newaura.bookish.core.util.calculateMaxStreak
import kotlinx.datetime.LocalDate

private val GreenTitle  = Color(0xFF2E7D5E)
private val TextPrimary = Color(0xFF1A1A1A)
private val TextMuted   = Color(0xFF888888)

private val DAY_LABELS = listOf("Sun", "", "Tue", "", "Thu", "", "Sat")

@Composable
fun StreakWidget(
    year: Int,
    activityData: Map<LocalDate, Int>,   // date → pages read
    modifier: Modifier = Modifier
) {
    val totalPages = remember(activityData) { activityData.values.sum() }
    val totalActiveDays = remember(activityData) { activityData.count { it.value > 0 } }
    val maxStreak = remember(activityData) { calculateMaxStreak(activityData) }

    val weeks = remember(year, activityData) {
        buildWeekColumns(year, activityData)
    }

    val monthLabels = remember(weeks) { buildMonthLabels(weeks) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            TextViewSemiBold(
                text = "$totalPages pages in $year",
                fontSize = 18.sp,
                color = GreenTitle
            )

            Spacer(Modifier.height(8.dp))

            Row {
                TextViewBody("Total active days: ", fontSize = 14.sp, color = TextPrimary)
                TextViewBold("$totalActiveDays", fontSize = 14.sp, color = TextPrimary)
                Spacer(Modifier.width(16.dp))
                TextViewBody("Max streak: ", fontSize = 14.sp, color = TextPrimary)
                TextViewBold("$maxStreak", fontSize = 14.sp, color = TextPrimary)
            }

            Spacer(Modifier.height(16.dp))

            Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
                // Y-axis day labels
                Column(
                    modifier = Modifier.padding(top = 20.dp), // offset for month row
                    verticalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    DAY_LABELS.forEach { label ->
                        Box(
                            modifier = Modifier
                                .height(14.dp)
                                .width(32.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            if (label.isNotEmpty()) {
                                Text(
                                    text = label,
                                    fontSize = 11.sp,
                                    color = TextMuted
                                )
                            }
                        }
                        Spacer(Modifier.height(3.dp))
                    }
                }

                Spacer(Modifier.width(4.dp))

                // Grid columns
                Column {
                    // Month labels row
                    Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                        weeks.forEachIndexed { index, _ ->
                            val label = monthLabels[index]
                            Box(modifier = Modifier.width(14.dp)) {
                                if (label != null) {
                                    Text(
                                        text = label,
                                        fontSize = 11.sp,
                                        color = TextMuted
                                    )
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(4.dp))

                    // Dot grid
                    Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                        weeks.forEach { week ->
                            Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                                week.forEach { level ->
                                    HeatmapDot(level = level)
                                }
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // ── Legend ─────────────────────────────────────────────
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text("Less", fontSize = 12.sp, color = TextMuted)
                ActivityLevel.entries.forEach { level ->
                    HeatmapDot(level = level)
                }
                Text("More", fontSize = 12.sp, color = TextMuted)
            }
        }
    }
}