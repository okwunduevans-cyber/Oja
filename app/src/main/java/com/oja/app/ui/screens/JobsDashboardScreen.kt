package com.oja.app.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.*
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

    LaunchedEffect(jobs.size) {
        // simulate auto-updating: new purchases add to the top elsewhere in Repo
    }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Unclaimed Deliveries")
        Spacer(Modifier.height(8.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(jobs.size) { i ->
                val j = jobs[i]
                JobCard(j, onTrack = { nav.navigate(Route.Track.path(j.orderId)) }, onClaim = {
                    scope.launch {
                        delay(300)
                        Repo.claimJob(j.id, transporterId = "tx-001")
                    }
                })
            }
        }
    }
}

@Composable
private fun JobCard(j: JobTicket, onTrack: () -> Unit, onClaim: () -> Unit) {
    Card(Modifier.fillMaxWidth().clickable(onClick = onTrack)) {
        Column(Modifier.padding(12.dp)) {
            Text("Job ${j.id} • ${j.method}")
            Text("Order ${j.orderId}")
            val status = if (j.claimedByTransporterId == null) "Unclaimed" else "Claimed by ${j.claimedByTransporterId}"
            Text(status)
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onTrack) { Text("Track") }
                if (j.claimedByTransporterId == null) Button(onClaim) { Text("Claim") }
            }
        }
    }
}
