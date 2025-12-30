package com.namazvaktiglobal.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.namazvaktiglobal.data.preferences.AppSettings
import com.namazvaktiglobal.data.preferences.SettingsDataStore
import com.namazvaktiglobal.data.preferences.ThemeMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsDataStore: SettingsDataStore
) : ViewModel() {
    private val _settings = MutableStateFlow<AppSettings?>(null)
    val settings: StateFlow<AppSettings?> = _settings.asStateFlow()

    init {
        viewModelScope.launch {
            settingsDataStore.settingsFlow.collectLatest { _settings.value = it }
        }
    }

    fun updateCalculationMethod(methodId: Int) {
        viewModelScope.launch { settingsDataStore.updateCalculationMethod(methodId) }
    }

    fun updateAsrMethod(asrMethod: Int) {
        viewModelScope.launch { settingsDataStore.updateAsrMethod(asrMethod) }
    }

    fun updateHijriAdjustment(adjustment: Int) {
        viewModelScope.launch { settingsDataStore.updateHijriAdjustment(adjustment) }
    }

    fun updateManualLocation(city: String, country: String, useManual: Boolean) {
        viewModelScope.launch { settingsDataStore.updateManualLocation(city, country, useManual) }
    }

    fun updatePrayerAdjustment(prayer: String, adjustment: Int) {
        viewModelScope.launch { settingsDataStore.updatePrayerAdjustment(prayer, adjustment) }
    }

    fun updatePrayerNotification(prayer: String, enabled: Boolean) {
        viewModelScope.launch { settingsDataStore.updatePrayerNotification(prayer, enabled) }
    }

    fun updateRewardExpiry(expiry: Long) {
        viewModelScope.launch { settingsDataStore.updateRewardExpiry(expiry) }
    }

    fun updateThemeMode(themeMode: ThemeMode) {
        viewModelScope.launch { settingsDataStore.updateThemeMode(themeMode) }
    }
}
