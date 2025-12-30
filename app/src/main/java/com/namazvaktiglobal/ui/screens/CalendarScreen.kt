package com.namazvaktiglobal.ui.screens

import android.app.Activity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.namazvaktiglobal.ui.viewmodel.AdViewModel
import com.namazvaktiglobal.ui.viewmodel.CalendarViewModel
import com.namazvaktiglobal.ui.viewmodel.SettingsViewModel

@Composable
fun CalendarScreen(
    calendarViewModel: CalendarViewModel = hiltViewModel(),
    settingsViewModel: SettingsViewModel = hiltViewModel()
) {
    val state by calendarViewModel.state.collectAsState()
    val settings by settingsViewModel.settings.collectAsState()
    val context = LocalContext.current
    val adViewModel = hiltViewModel<AdViewModel>()
    val adManager = adViewModel.adManager
    val consentManager = adViewModel.consentManager

    LaunchedEffect(Unit) {
        calendarViewModel.loadMonth()
        if (consentManager.canRequestAds()) {
            adManager.loadInterstitial()
            adManager.showInterstitial(context as Activity, 10) {}
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        if (state.isLoading) {
            CircularProgressIndicator(modifier = Modifier.padding(16.dp))
        }
        if (state.error != null) {
            Text(text = state.error ?: "", color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(16.dp))
        }
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(state.days) { day ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(day.date.readable, style = MaterialTheme.typography.titleLarge)
                        Text("Fajr: ${day.timings.fajr}")
                        Text("Dhuhr: ${day.timings.dhuhr}")
                        Text("Asr: ${day.timings.asr}")
                        Text("Maghrib: ${day.timings.maghrib}")
                        Text("Isha: ${day.timings.isha}")
                    }
                }
            }
        }
        val rewardActive = (settings?.rewardExpiry ?: 0L) > System.currentTimeMillis()
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Export / Share", style = MaterialTheme.typography.titleLarge)
            Text(if (rewardActive) "Unlocked for 24 hours" else "Watch rewarded ad to unlock")
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = { }, enabled = rewardActive) {
                Text("Export 30-day calendar")
            }
        }
    }
}
