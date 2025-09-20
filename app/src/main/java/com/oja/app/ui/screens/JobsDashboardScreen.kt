package com.oja.app.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.oja.app.ui.AppStrings
import com.oja.app.ui.LocalStrings
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun JobsDashboardScreen(nav: NavHostController) {
    val strings = LocalStrings.current
    val jobs by Repo.jobs.collectAsState()
    val scope = rememberCoroutineScope()

    LaunchedEffect(jobs.size) {
        // simulate auto-updating: new purchases add to the top elsewhere in Repo
    }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text(strings.jobs.title)
        Spacer(Modifier.height(8.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(jobs.size) { i ->
                val j = jobs[i]
                JobCard(strings, j, onTrack = { nav.navigate(Route.Track.path(j.orderId)) }, onClaim = {
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
private fun JobCard(strings: AppStrings, j: JobTicket, onTrack: () -> Unit, onClaim: () -> Unit) {
    Card(Modifier.fillMaxWidth().clickable(onClick = onTrack)) {
        Column(Modifier.padding(12.dp)) {
            Text("${strings.jobs.jobLabel} ${j.id} • ${strings.cart.methodLabel(j.method)}")
            Text("${strings.jobs.orderLabel} ${j.orderId}")
            val statusText = j.status ?: if (j.claimedByTransporterId == null) strings.jobs.unclaimed else strings.jobs.claimedBy(j.claimedByTransporterId)
            Text(statusText)
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onTrack) { Text(strings.jobs.track) }
                if (j.claimedByTransporterId == null) Button(onClaim) { Text(strings.jobs.claim) }
            }
        }
    }
}
