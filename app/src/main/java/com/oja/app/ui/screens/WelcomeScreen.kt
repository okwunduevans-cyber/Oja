package com.oja.app.ui.screens

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import com.oja.app.ui.LocalStrings
import kotlinx.coroutines.delay

@Composable
fun WelcomeScreen(onStart: () -> Unit) {
    val strings = LocalStrings.current
    val heroes = remember { rotatingHeroes }
    var heroIndex by remember { mutableStateOf(0) }

    LaunchedEffect(heroes) {
        while (heroes.size > 1) {
            delay(5000)
            heroIndex = (heroIndex + 1) % heroes.size
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(16.dp))
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(strings.welcome.title, textAlign = TextAlign.Center)
            Text(strings.welcome.subtitle, textAlign = TextAlign.Center)
        }
        Crossfade(targetState = heroIndex, label = "hero-art") { index ->
            val hero = heroes[index]
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .semantics { contentDescription = strings.welcome.heroContentDescription }
                    .background(Brush.linearGradient(hero.colors), RoundedCornerShape(28.dp))
                    .padding(24.dp),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.Start
            ) {
                Text(hero.title, style = MaterialTheme.typography.headlineSmall)
                Spacer(Modifier.height(8.dp))
                Text(hero.subtitle, style = MaterialTheme.typography.bodyMedium)
            }
        }
        Button(onClick = onStart, modifier = Modifier.fillMaxWidth()) { Text(strings.welcome.enterMarket) }
    }
}

private data class HeroCard(val title: String, val subtitle: String, val colors: List<Color>)

private val rotatingHeroes = listOf(
    HeroCard(
        title = "Chidi • Ajah",
        subtitle = "Guides fresh produce from road-side stalls to estates",
        colors = listOf(Color(0xFF00695C), Color(0xFF26A69A))
    ),
    HeroCard(
        title = "Zainab • Kano",
        subtitle = "Links grain markets with city kitchens before dawn",
        colors = listOf(Color(0xFF4A148C), Color(0xFFBA68C8))
    ),
    HeroCard(
        title = "Ngozi & Tayo • Enugu",
        subtitle = "Onboard new vendors and field-check deliveries",
        colors = listOf(Color(0xFF283593), Color(0xFF64B5F6))
    )
)
