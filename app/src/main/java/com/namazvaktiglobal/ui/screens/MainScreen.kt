package com.namazvaktiglobal.ui.screens

import android.Manifest
import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.namazvaktiglobal.ads.BannerAd
import com.namazvaktiglobal.domain.PrayerTimes
import com.namazvaktiglobal.ui.viewmodel.AdViewModel
import com.namazvaktiglobal.ui.viewmodel.MainViewModel

@Composable
fun MainScreen(
    onNavigate: (String) -> Unit,
    viewModel: MainViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val adViewModel = androidx.hilt.navigation.compose.hiltViewModel<AdViewModel>()
    val adManager = adViewModel.adManager
    val consentManager = adViewModel.consentManager

    val locationLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            viewModel.refresh()
        }
    }

    val notificationLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { }

    var adsAllowed by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        consentManager.requestConsent(context as Activity)
        adsAllowed = consentManager.canRequestAds()
        if (adsAllowed) {
            adManager.loadInterstitial()
            adManager.loadRewarded()
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(text = state.locationLabel, style = MaterialTheme.typography.titleLarge)
                Text(text = state.prayerTimes?.hijriDate ?: "", style = MaterialTheme.typography.bodyLarge)
            }
            item {
                if (state.isLoading) {
                    CircularProgressIndicator()
                } else if (state.error != null) {
                    Text(text = state.error ?: "", color = MaterialTheme.colorScheme.error)
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = { viewModel.refresh() }) {
                        Text("Retry")
                    }
                }
            }
            item {
                if (state.prayerTimes == null) {
                    Button(onClick = { locationLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION) }) {
                        Text("Use current location")
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    TextButton(onClick = { onNavigate("settings") }) {
                        Text("Enter location manually")
                    }
                }
            }
            item {
                NextPrayerCard(
                    prayerName = state.nextPrayerName,
                    time = state.nextPrayerTime,
                    countdownMinutes = state.countdownMinutes
                )
            }
            item {
                val times = state.prayerTimes
                if (times != null) {
                    PrayerTimesList(times, state.nextPrayerName)
                }
            }
            item {
                Divider()
                Text("Quick actions", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = { onNavigate("qibla") }, modifier = Modifier.fillMaxWidth()) {
                    Text("Qibla direction")
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = { onNavigate("calendar") }, modifier = Modifier.fillMaxWidth()) {
                    Text("Prayer calendar")
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = { onNavigate("settings") }, modifier = Modifier.fillMaxWidth()) {
                    Text("Settings")
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = { notificationLauncher.launch(Manifest.permission.POST_NOTIFICATIONS) }) {
                    Text("Enable notifications")
                }
                Spacer(modifier = Modifier.height(8.dp))
                TextButton(onClick = { viewModel.sendTestNotification() }) {
                    Text("Test notification")
                }
            }
        }
        if (adsAllowed) {
            BannerAd(
                modifier = Modifier.fillMaxWidth().padding(ShownAdPadding),
                adUnitId = adManager.bannerAdUnitId()
            )
        }
    }
}

@Composable
private fun NextPrayerCard(prayerName: String, time: String, countdownMinutes: Long) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Next prayer", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(8.dp))
            Text(prayerName, style = MaterialTheme.typography.headlineLarge)
            Text("$time • $countdownMinutes min", style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Composable
private fun PrayerTimesList(prayerTimes: PrayerTimes, highlightPrayer: String) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        prayerTimes.times.forEach { (name, time) ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    val style = if (name == highlightPrayer) {
                        MaterialTheme.typography.titleLarge.copy(color = MaterialTheme.colorScheme.primary)
                    } else {
                        MaterialTheme.typography.titleLarge
                    }
                    Text(name, style = style)
                    Text(time.toString(), style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    }
}

private val ShownAdPadding = 8.dp
