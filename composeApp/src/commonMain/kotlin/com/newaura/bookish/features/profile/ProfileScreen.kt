package com.newaura.bookish.features.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.automirrored.rounded.TrendingUp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import com.newaura.bookish.core.common.BookStatCard
import com.newaura.bookish.core.common.GradientButton
import com.newaura.bookish.core.common.TextViewBody
import com.newaura.bookish.core.common.TextViewMedium
import com.newaura.bookish.core.ui.NetworkImage
import com.newaura.bookish.core.ui.StreakWidget
import com.newaura.bookish.core.util.toColor
import com.newaura.bookish.features.profile.data.ProfileResponse
import com.newaura.bookish.features.profile.data.StatsType
import com.newaura.bookish.features.profile.ui.ProfileScreenUiState
import com.newaura.bookish.features.profile.ui.ProfileViewModel
import com.newaura.bookish.features.profile.ui.ReadingGoalCard
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus
import org.koin.compose.viewmodel.koinViewModel
import kotlin.random.Random

class ProfileScreen : Screen {

    @Composable
    override fun Content() {

        val viewModel = koinViewModel<ProfileViewModel>()
        val screenState by viewModel.profileUiState.collectAsState()

        LaunchedEffect(Unit) {
            viewModel.getUserDetail()
        }

        Scaffold { paddingValues ->
            when (val uiState = screenState) {
                is ProfileScreenUiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                is ProfileScreenUiState.Error -> {
                    val message = uiState.message
                    TextViewMedium(message)
                }

                is ProfileScreenUiState.Success -> {
                    ProfileContent(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        data = uiState.profileData,
                        onEditProfileClicked = {
                            viewModel.onEditProfileClicked()
                        }
                    )
                }
                else -> {}
            }
        }
    }
}

@Composable
private fun ProfileContent(
    modifier: Modifier,
    data: ProfileResponse?,
    onEditProfileClicked: () -> Unit
) {
    val user = data?.user

    // TODO convert this in real data source
    val sampleData = remember {
        val data = mutableMapOf<LocalDate, Int>()
        val random = Random(42)
        var date = LocalDate(2026, 1, 1)
        val end = LocalDate(2026, 9, 30)

        while (date <= end) {
            if (random.nextFloat() > 0.5f) {
                data[date] = random.nextInt(80)
            }
            date = date.plus(DatePeriod(days = 1))
        }
        data
    }

    LazyColumn(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item { NetworkImage(
            url = user?.profileIcon ?: "",
            contentDescription = "Profile",
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(12.dp)),
            contentScale = ContentScale.Crop,
            placeHolder = {
                Icon(
                    Icons.Filled.Person,
                    contentDescription = "Profile placeholder",
                )
            }
        ) }
        item { Spacer(Modifier.height(16.dp)) }
        item { TextViewBody("You") }
        item { Spacer(Modifier.height(16.dp)) }
        item { TextViewBody("Exploring the world through books") }
        item { Spacer(Modifier.height(16.dp)) }
        item { GradientButton(
            text = "Edit Profile",
            onClick = {
                onEditProfileClicked()
            },
            modifier = Modifier
        )
            Spacer(Modifier.height(16.dp))
        }

        data?.stats?.let { stats ->
            item {
                Card(
                    modifier = Modifier.fillMaxSize(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                    ) {
                        stats.chunked(2).forEach { rowItems ->
                            androidx.compose.foundation.layout.Row(
                                modifier = Modifier.fillMaxWidth().padding(8.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                rowItems.forEach { stat ->
                                    BookStatCard(
                                        modifier = Modifier.weight(1f),
                                        icon = getStatsIcon(stat.statsType),
                                        count = stat.count,
                                        label = stat.label ?: "",
                                        iconColor = stat.iconColor?.toColor(),
                                        textColor = stat.iconColor?.toColor()
                                    )
                                }
                                if (rowItems.size < 2) {
                                    Spacer(Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }
            }
        }

        item { Spacer(Modifier.height(16.dp)) }

        data?.readingGoals?.let { goals ->
            items(goals) { item ->
                ReadingGoalCard(
                    year = item.year ?: 0,
                    booksRead = item.booksRead,
                    totalBooks = item.totalBooks,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        item {
            StreakWidget(
                year = 2026,
                activityData = sampleData
            )
        }
    }
}

private fun getStatsIcon(statsType: StatsType?): ImageVector {
    return when(statsType) {
        StatsType.READING -> Icons.AutoMirrored.Rounded.MenuBook
        StatsType.COMPLETED -> Icons.Rounded.CheckCircle
        StatsType.LIKES -> Icons.Rounded.Favorite
        else -> Icons.AutoMirrored.Rounded.TrendingUp
    }
}