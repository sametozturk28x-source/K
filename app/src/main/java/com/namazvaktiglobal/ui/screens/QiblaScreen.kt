package com.namazvaktiglobal.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.namazvaktiglobal.ui.viewmodel.QiblaViewModel

@Composable
fun QiblaScreen(viewModel: QiblaViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.refresh()
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (state.error != null) {
            Text(state.error ?: "", color = MaterialTheme.colorScheme.error)
            Button(onClick = { viewModel.refresh() }) {
                Text("Retry")
            }
        } else {
            Text("Qibla direction", style = MaterialTheme.typography.titleLarge)
            Text("${state.bearing.toInt()}°")
            if (!state.sensorAvailable) {
                Text("Compass sensor unavailable. Showing direction based on location only.")
            }
            Canvas(modifier = Modifier.weight(1f).fillMaxSize()) {
                val arrow = Path().apply {
                    moveTo(size.width / 2, size.height * 0.2f)
                    lineTo(size.width * 0.6f, size.height * 0.6f)
                    lineTo(size.width / 2, size.height * 0.5f)
                    lineTo(size.width * 0.4f, size.height * 0.6f)
                    close()
                }
                val adjusted = (state.bearing - state.deviceAzimuth + 360) % 360
                rotate(adjusted.toFloat(), pivot = Offset(size.width / 2, size.height / 2)) {
                    drawPath(path = arrow, color = Color.Red)
                }
            }
            Text("Hold phone flat for best accuracy. If arrow jitters, move phone in a figure-8 for calibration.")
        }
    }
}
