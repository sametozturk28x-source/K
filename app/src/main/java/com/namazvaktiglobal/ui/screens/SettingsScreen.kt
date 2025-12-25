package com.namazvaktiglobal.ui.screens

import android.app.Activity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
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
import com.namazvaktiglobal.data.preferences.ThemeMode
import com.namazvaktiglobal.ui.viewmodel.AdViewModel
import com.namazvaktiglobal.ui.viewmodel.SettingsViewModel
import java.time.Instant
import java.time.temporal.ChronoUnit

@Composable
fun SettingsScreen(viewModel: SettingsViewModel = hiltViewModel()) {
    val settings by viewModel.settings.collectAsState()
    val context = LocalContext.current
    val adViewModel = hiltViewModel<AdViewModel>()
    val adManager = adViewModel.adManager
    val consentManager = adViewModel.consentManager
    val currentSettings = settings ?: return

    var methodExpanded by remember { mutableStateOf(false) }
    val methods = listOf(
        2 to "ISNA",
        3 to "MWL",
        4 to "Umm al-Qura",
        5 to "Egypt",
        8 to "Gulf",
        12 to "Diyanet"
    ) 
    val themeOptions = listOf(ThemeMode.SYSTEM, ThemeMode.LIGHT, ThemeMode.DARK)
    var themeExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (consentManager.canRequestAds()) {
            adManager.loadInterstitial()
            adManager.showInterstitial(context as Activity, 10) {}
        }
    }

    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Calculation method", style = MaterialTheme.typography.titleLarge)
        TextButton(onClick = { methodExpanded = true }) {
            Text(methods.firstOrNull { it.first == currentSettings.calculationMethod }?.second ?: "Custom")
        }
        DropdownMenu(expanded = methodExpanded, onDismissRequest = { methodExpanded = false }) {
            methods.forEach { (id, label) ->
                DropdownMenuItem(
                    text = { Text(label) },
                    onClick = {
                        viewModel.updateCalculationMethod(id)
                        methodExpanded = false
                    }
                )
            }
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Asr method (Hanafi)")
            Switch(
                checked = currentSettings.asrMethod == 1,
                onCheckedChange = { viewModel.updateAsrMethod(if (it) 1 else 0) }
            )
        }

        Text("Per-prayer adjustments (minutes)", style = MaterialTheme.typography.titleLarge)
        currentSettings.adjustments.forEach { (name, value) ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(name)
                Row {
                    Button(onClick = { viewModel.updatePrayerAdjustment(name, value - 1) }) { Text("-") }
                    Text("$value", modifier = Modifier.padding(horizontal = 8.dp))
                    Button(onClick = { viewModel.updatePrayerAdjustment(name, value + 1) }) { Text("+") }
                }
            }
        }

        Text("Notifications", style = MaterialTheme.typography.titleLarge)
        currentSettings.notifications.forEach { (name, enabled) ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(name)
                Switch(
                    checked = enabled,
                    onCheckedChange = { viewModel.updatePrayerNotification(name, it) }
                )
            }
        }

        Text("Hijri adjustment", style = MaterialTheme.typography.titleLarge)
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Button(onClick = { viewModel.updateHijriAdjustment(currentSettings.hijriAdjustment - 1) }) { Text("-") }
            Text("${currentSettings.hijriAdjustment}")
            Button(onClick = { viewModel.updateHijriAdjustment(currentSettings.hijriAdjustment + 1) }) { Text("+") }
        }

        Text("Manual location", style = MaterialTheme.typography.titleLarge)
        var city by remember { mutableStateOf(currentSettings.manualCity) }
        var country by remember { mutableStateOf(currentSettings.manualCountry) }
        OutlinedTextField(value = city, onValueChange = { city = it }, label = { Text("City") })
        OutlinedTextField(value = country, onValueChange = { country = it }, label = { Text("Country") })
        Button(onClick = { viewModel.updateManualLocation(city, country, true) }) {
            Text("Use manual location")
        }
        Button(onClick = { viewModel.updateManualLocation("", "", false) }) {
            Text("Use device location")
        }

        Text("Rewarded ads", style = MaterialTheme.typography.titleLarge)
        val rewardActive = currentSettings.rewardExpiry > System.currentTimeMillis()
        Text(if (rewardActive) "Reward active" else "Reward inactive")
        Text("Watch ad to unlock 30-day calendar export for 24 hours")
        Button(onClick = {
            if (consentManager.canRequestAds()) {
                adManager.showRewarded(context as Activity, onReward = {
                    val expiry = Instant.now().plus(24, ChronoUnit.HOURS).toEpochMilli()
                    viewModel.updateRewardExpiry(expiry)
                }, onDismissed = {})
            }
        }) {
            Text("Watch rewarded ad")
        }

        Text("Theme", style = MaterialTheme.typography.titleLarge)
        TextButton(onClick = { themeExpanded = true }) {
            Text(currentSettings.themeMode.lowercase().replaceFirstChar { it.titlecase() })
        }
        DropdownMenu(expanded = themeExpanded, onDismissRequest = { themeExpanded = false }) {
            themeOptions.forEach { mode ->
                DropdownMenuItem(
                    text = { Text(mode.name.lowercase().replaceFirstChar { it.titlecase() }) },
                    onClick = {
                        viewModel.updateThemeMode(mode)
                        themeExpanded = false
                    }
                )
            }
        }
    }
}
