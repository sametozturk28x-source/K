package com.namazvaktiglobal.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.preferencesDataStore
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsDataStore(private val context: Context) {
    private val methodKey = intPreferencesKey("calc_method")
    private val asrMethodKey = intPreferencesKey("asr_method")
    private val hijriAdjustmentKey = intPreferencesKey("hijri_adjustment")
    private val manualCityKey = stringPreferencesKey("manual_city")
    private val manualCountryKey = stringPreferencesKey("manual_country")
    private val useManualLocationKey = booleanPreferencesKey("use_manual_location")
    private val rewardExpiryKey = longPreferencesKey("reward_expiry")
    private val themeKey = stringPreferencesKey("theme_mode")

    private fun prayerAdjustmentKey(name: String) = intPreferencesKey("adjustment_$name")
    private fun prayerNotificationKey(name: String) = booleanPreferencesKey("notify_$name")

    val settingsFlow: Flow<AppSettings> = context.dataStore.data.map { prefs ->
        AppSettings(
            calculationMethod = prefs[methodKey] ?: 2,
            asrMethod = prefs[asrMethodKey] ?: 0,
            hijriAdjustment = prefs[hijriAdjustmentKey] ?: 0,
            manualCity = prefs[manualCityKey] ?: "",
            manualCountry = prefs[manualCountryKey] ?: "",
            useManualLocation = prefs[useManualLocationKey] ?: false,
            adjustments = mapOf(
                "Fajr" to (prefs[prayerAdjustmentKey("fajr")] ?: 0),
                "Sunrise" to (prefs[prayerAdjustmentKey("sunrise")] ?: 0),
                "Dhuhr" to (prefs[prayerAdjustmentKey("dhuhr")] ?: 0),
                "Asr" to (prefs[prayerAdjustmentKey("asr")] ?: 0),
                "Maghrib" to (prefs[prayerAdjustmentKey("maghrib")] ?: 0),
                "Isha" to (prefs[prayerAdjustmentKey("isha")] ?: 0)
            ),
            notifications = mapOf(
                "Fajr" to (prefs[prayerNotificationKey("fajr")] ?: true),
                "Sunrise" to (prefs[prayerNotificationKey("sunrise")] ?: false),
                "Dhuhr" to (prefs[prayerNotificationKey("dhuhr")] ?: true),
                "Asr" to (prefs[prayerNotificationKey("asr")] ?: true),
                "Maghrib" to (prefs[prayerNotificationKey("maghrib")] ?: true),
                "Isha" to (prefs[prayerNotificationKey("isha")] ?: true)
            ),
            rewardExpiry = prefs[rewardExpiryKey] ?: 0L,
            themeMode = prefs[themeKey] ?: ThemeMode.SYSTEM.name
        )
    }

    suspend fun updateCalculationMethod(methodId: Int) {
        context.dataStore.edit { it[methodKey] = methodId }
    }

    suspend fun updateAsrMethod(method: Int) {
        context.dataStore.edit { it[asrMethodKey] = method }
    }

    suspend fun updateHijriAdjustment(adjustment: Int) {
        context.dataStore.edit { it[hijriAdjustmentKey] = adjustment }
    }

    suspend fun updateManualLocation(city: String, country: String, useManual: Boolean) {
        context.dataStore.edit {
            it[manualCityKey] = city
            it[manualCountryKey] = country
            it[useManualLocationKey] = useManual
        }
    }

    suspend fun updatePrayerAdjustment(prayer: String, adjustment: Int) {
        context.dataStore.edit {
            it[prayerAdjustmentKey(prayer.lowercase())] = adjustment
        }
    }

    suspend fun updatePrayerNotification(prayer: String, enabled: Boolean) {
        context.dataStore.edit {
            it[prayerNotificationKey(prayer.lowercase())] = enabled
        }
    }

    suspend fun updateRewardExpiry(expiry: Long) {
        context.dataStore.edit { it[rewardExpiryKey] = expiry }
    }

    suspend fun updateThemeMode(themeMode: ThemeMode) {
        context.dataStore.edit { it[themeKey] = themeMode.name }
    }
}

data class AppSettings(
    val calculationMethod: Int,
    val asrMethod: Int,
    val hijriAdjustment: Int,
    val manualCity: String,
    val manualCountry: String,
    val useManualLocation: Boolean,
    val adjustments: Map<String, Int>,
    val notifications: Map<String, Boolean>,
    val rewardExpiry: Long,
    val themeMode: String
)

enum class ThemeMode {
    SYSTEM, LIGHT, DARK
}
