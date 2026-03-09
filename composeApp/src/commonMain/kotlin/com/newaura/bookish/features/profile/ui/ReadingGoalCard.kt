package com.newaura.bookish.features.profile.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.newaura.bookish.core.common.TextViewBody
import com.newaura.bookish.core.common.TextViewMedium
import com.newaura.bookish.core.common.TextViewSemiBold

@Composable
fun ReadingGoalCard(
    year: Int,
    booksRead: Int,
    totalBooks: Int,
    modifier: Modifier = Modifier
) {
    val progress = if (totalBooks > 0) booksRead / totalBooks.toFloat() else 0f
    val percentage = (progress * 100).toInt()
    val remaining = totalBooks - booksRead

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
            TextViewBody(
                text = "$year Reading Goal",
                fontSize = 16.sp,
                color = Color(0xFF2E7D5E)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextViewMedium(
                    text = "$booksRead of $totalBooks books",
                    fontSize = 18.sp,
                )
                TextViewSemiBold(
                    text = "$percentage%",
                    fontSize = 26.sp,
                    color = Color(0xFF2E7D5E)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = Color(0xFF13C489),
                trackColor = Color(0xFFBCE2D7),
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = if (remaining > 0) "$remaining more to go!" else "🎉 Goal achieved!",
                fontSize = 15.sp,
                fontWeight = FontWeight.Normal,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}