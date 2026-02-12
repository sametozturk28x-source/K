package com.example.globalnamazvakti.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.globalnamazvakti.domain.CalculationMethod
import com.example.globalnamazvakti.domain.Coordinates
import com.example.globalnamazvakti.domain.PrayerRepository
import com.example.globalnamazvakti.domain.PrayerTimes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

sealed class UiState {
    data object Loading : UiState()
    data class Success(val data: List<PrayerTimes>) : UiState()
    data class Error(val message: String) : UiState()
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: PrayerRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _countdown = MutableStateFlow("--:--:--")
    val countdown: StateFlow<String> = _countdown.asStateFlow()

    fun getPrayerTimes(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            val now = LocalDate.now()
            repository.getPrayerTimes(
                coordinates = Coordinates(latitude, longitude),
                method = CalculationMethod.DIYANET,
                month = now.monthValue,
                year = now.year
            ).collectLatest { result ->
                result.onSuccess { prayers ->
                    _uiState.value = UiState.Success(prayers)
                    updateCountdown(prayers)
                }.onFailure { throwable ->
                    _uiState.value = UiState.Error(throwable.message ?: "Unknown error")
                }
            }
        }
    }

    private fun updateCountdown(prayers: List<PrayerTimes>) {
        viewModelScope.launch {
            val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")
            while (true) {
                val next = prayers.firstOrNull() ?: break
                val nextTime = LocalDateTime.parse(
                    "${next.dateGregorian} ${next.fajr}",
                    formatter
                )
                val diff = Duration.between(LocalDateTime.now(), nextTime).seconds
                _countdown.value = if (diff > 0) formatSeconds(diff) else "00:00:00"
                delay(1000)
            }
        }
    }

    private fun formatSeconds(totalSeconds: Long): String {
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60
        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }
}
