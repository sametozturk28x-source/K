package com.example.globalnamazvakti.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.globalnamazvakti.domain.PrayerTimes
import com.example.globalnamazvakti.ui.components.AdBanner

@Composable
fun MainScreen(viewModel: HomeViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    val countdown by viewModel.countdown.collectAsState()

    Scaffold(
        bottomBar = {
            AdBanner(modifier = Modifier.fillMaxWidth())
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text(
                text = "Istanbul, Turkey",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Hijri: 01-01-1446",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(24.dp))
            CountdownSection(countdown)
            Spacer(modifier = Modifier.height(24.dp))
            when (val state = uiState) {
                UiState.Loading -> {
                    CircularProgressIndicator()
                }
                is UiState.Error -> {
                    Text(text = state.message, color = Color.Red)
                }
                is UiState.Success -> {
                    PrayerList(state.data)
                }
            }
        }
    }
}

@Composable
private fun CountdownSection(countdown: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "Time Remaining",
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(8.dp))
        CircularProgressIndicator()
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = countdown, style = MaterialTheme.typography.headlineSmall)
    }
}

@Composable
private fun PrayerList(prayers: List<PrayerTimes>) {
    LazyColumn(
        contentPadding = PaddingValues(bottom = 72.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(prayers) { prayer ->
            PrayerCard(prayer)
        }
    }
}

@Composable
private fun PrayerCard(prayer: PrayerTimes) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "${prayer.dateGregorian} / ${prayer.dateHijri}")
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text(text = "Fajr")
                    Text(text = prayer.fajr, fontWeight = FontWeight.SemiBold)
                }
                Column {
                    Text(text = "Dhuhr")
                    Text(text = prayer.dhuhr, fontWeight = FontWeight.SemiBold)
                }
                Column {
                    Text(text = "Asr")
                    Text(text = prayer.asr, fontWeight = FontWeight.SemiBold)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text(text = "Maghrib")
                    Text(text = prayer.maghrib, fontWeight = FontWeight.SemiBold)
                }
                Column {
                    Text(text = "Isha")
                    Text(text = prayer.isha, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}
