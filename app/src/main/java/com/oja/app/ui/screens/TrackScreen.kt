package com.oja.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.maps.android.compose.*
import com.oja.app.data.Repo
import kotlinx.coroutines.delay
import kotlin.math.min

@Composable
fun TrackScreen(orderId: String, onBack: () -> Unit) {
    val job = Repo.jobs.value.firstOrNull { it.orderId == orderId }
    
    if (job == null) {
        Column(
            Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
        ) {
            Text("No tracking for $orderId")
            Button(onClick = onBack) { Text("Back") }
        }
        return
    }

    val start = com.google.android.gms.maps.model.LatLng(job.pickupLat, job.pickupLng)
    val end = com.google.android.gms.maps.model.LatLng(job.dropLat, job.dropLng)
    val cameraPositionState = rememberCameraPositionState {
        position = com.google.android.gms.maps.model.CameraPosition.fromLatLngZoom(start, 13f)
    }

    var progress by remember { mutableStateOf(0f) }
    LaunchedEffect(orderId) {
        while (progress < 1f) {
            delay(800)
            progress = min(1f, progress + 0.08f)
        }
    }

    val current = com.google.android.gms.maps.model.LatLng(
        start.latitude + (end.latitude - start.latitude) * progress,
        start.longitude + (end.longitude - start.longitude) * progress
    )

    Column(Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.weight(1f),
            cameraPositionState = cameraPositionState
        ) {
            Polyline(points = listOf(start, end))
            Marker(state = rememberMarkerState(position = start), title = "Pickup")
            Marker(state = rememberMarkerState(position = end), title = "Dropoff")
            Marker(state = rememberMarkerState(position = current), title = "Courier")
        }
        Row(Modifier.fillMaxWidth().padding(12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onBack) { Text("Back") }
            Text("Progress: ${(progress * 100).toInt()}%")
        }
    }
}
