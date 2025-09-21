package com.oja.app.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.oja.app.data.JobTicket
import com.oja.app.data.Repo
import com.oja.app.navigation.Route
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun JobsDashboardScreen(nav: NavHostController) {
    val jobs by Repo.jobs.collectAsState()
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Unclaimed Deliveries")
        Spacer(Modifier.height(8.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(jobs) { job ->
                JobCard(
                    job = job,
                    onTrack = { nav.navigate(Route.Track.path(job.orderId)) },
                    onClaim = {
                        scope.launch {
                            delay(300)
                            Repo.claimJob(job.id, transporterId = "tx-001")
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun JobCard(job: JobTicket, onTrack: () -> Unit, onClaim: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onTrack)
    ) {
        Column(Modifier.padding(12.dp)) {
            Text("Job ${'$'}{job.id} • ${'$'}{job.method}")
            Text("Order ${'$'}{job.orderId}")
            val status = if (job.claimedByTransporterId == null) "Unclaimed" else "Claimed by ${'$'}{job.claimedByTransporterId}"
            Text(status)
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = onTrack) { Text("Track") }
                if (job.claimedByTransporterId == null) {
                    Button(onClick = onClaim) { Text("Claim") }
                }
            }
        }
    }
}
