package com.oja.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.maps.android.compose.*
import com.oja.app.data.Repo
import com.oja.app.ui.LocalStrings
import kotlinx.coroutines.delay
import kotlin.math.min

@Composable
fun TrackScreen(orderId: String, onBack: () -> Unit) {
    val strings = LocalStrings.current
    val job = Repo.jobs.value.firstOrNull { it.orderId == orderId }
    Column(Modifier.fillMaxSize()) {
        if (job == null) {
            Text(strings.tracking.noTracking(orderId))
            Button(onBack) { Text(strings.tracking.back) }
            return
        }

        val start = com.google.android.gms.maps.model.LatLng(job.pickupLat, job.pickupLng)
        val end = com.google.android.gms.maps.model.LatLng(job.dropLat, job.dropLng)
        val cameraPositionState = rememberCameraPositionState {
            position = com.google.android.gms.maps.model.CameraPosition.fromLatLngZoom(start, 13f)
        }

        var progress by remember { mutableStateOf(job.progress?.toFloat() ?: 0f) }
        LaunchedEffect(orderId, job.progress) {
            if (job.progress != null) {
                progress = job.progress.toFloat()
            } else {
                while (progress < 1f) {
                    delay(800)
                    progress = min(1f, progress + 0.08f)
                }
            }
        }

        val interpolated = com.google.android.gms.maps.model.LatLng(
            start.latitude + (end.latitude - start.latitude) * progress,
            start.longitude + (end.longitude - start.longitude) * progress
        )
        val current = if (job.courierLat != null && job.courierLng != null) {
            com.google.android.gms.maps.model.LatLng(job.courierLat, job.courierLng)
        } else interpolated

        if (com.google.android.libraries.maps.BuildConfig.VERSION_NAME != null) {
            GoogleMap(
                modifier = Modifier.weight(1f),
                cameraPositionState = cameraPositionState
            ) {
                Polyline(points = listOf(start, end))
                Marker(state = rememberMarkerState(position = start), title = strings.tracking.pickup)
                Marker(state = rememberMarkerState(position = end), title = strings.tracking.dropoff)
                Marker(state = rememberMarkerState(position = current), title = strings.tracking.courier)
            }
        } else {
            Box(Modifier.weight(1f)) { Text(strings.tracking.placeholder) }
        }
        Row(Modifier.fillMaxWidth().padding(12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onBack) { Text(strings.tracking.back) }
            Text(strings.tracking.progress((progress * 100).toInt()))
        }
    }
}
