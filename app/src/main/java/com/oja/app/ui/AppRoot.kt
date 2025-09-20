package com.oja.app.ui

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import com.oja.app.ui.ThemeSkin
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.oja.app.navigation.Route
import com.oja.app.ui.screens.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppRoot() {
    var languageKey by rememberSaveable { mutableStateOf(Language.ENGLISH.name) }
    var skinKey by rememberSaveable { mutableStateOf(ThemeSkin.NEUTRAL.name) }
    val language = remember(languageKey) { Language.valueOf(languageKey) }
    val skin = remember(skinKey) { ThemeSkin.valueOf(skinKey) }
    val strings = remember(language) { language.strings }
    val nav = rememberNavController()

    CompositionLocalProvider(LocalLanguage provides language, LocalStrings provides strings) {
        OjaTheme(skin = skin) {
            Scaffold(
                topBar = {
                    ControlBar(
                        languageLabel = strings.languageLabel,
                        themeLabel = strings.themeLabel,
                        currentLanguage = language,
                        onLanguageSelected = { languageKey = it.name },
                        currentSkin = skin,
                        onSkinSelected = { skinKey = it.name }
                    )
                }
            ) { padding ->
                NavHost(
                    navController = nav,
                    startDestination = Route.Welcome.path,
                    modifier = Modifier.padding(padding)
                ) {
                    composable(Route.Welcome.path) { WelcomeScreen(onStart = { nav.navigate(Route.Home.path) }) }
                    composable(Route.Home.path) { HomeScreen(nav) }
                    composable(Route.Cart.path) { CartScreen(nav) }
                    composable(Route.Jobs.path) { JobsDashboardScreen(nav) }
                    composable(Route.TransporterSignup.path) { TransporterSignupScreen(nav) }
                    composable(Route.VendorSignup.path) { VendorSignupScreen(nav) }
                    composable(Route.Payments.path) { PaymentsScreen(nav) }
                    composable(Route.Track.path) { backStack ->
                        val orderId = backStack.arguments?.getString("orderId").orEmpty()
                        TrackScreen(orderId = orderId, onBack = { nav.popBackStack() })
                    }
                }
            }
        }
    }
}

@Composable
private fun ControlBar(
    languageLabel: String,
    themeLabel: String,
    currentLanguage: Language,
    onLanguageSelected: (Language) -> Unit,
    currentSkin: ThemeSkin,
    onSkinSelected: (ThemeSkin) -> Unit
) {
    val languageScroll = rememberScrollState()
    val themeScroll = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text(languageLabel, style = MaterialTheme.typography.labelLarge)
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.horizontalScroll(languageScroll),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Language.values().forEach { language ->
                val selected = language == currentLanguage
                AssistChip(
                    onClick = { onLanguageSelected(language) },
                    label = { Text(language.nativeName) },
                    colors = if (selected) {
                        AssistChipDefaults.assistChipColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            labelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    } else {
                        AssistChipDefaults.assistChipColors()
                    }
                )
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(themeLabel, style = MaterialTheme.typography.labelLarge)
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.horizontalScroll(themeScroll),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ThemeSkin.values().forEach { skin ->
                val selected = skin == currentSkin
                AssistChip(
                    onClick = { onSkinSelected(skin) },
                    label = { Text(skin.displayName) },
                    colors = if (selected) {
                        AssistChipDefaults.assistChipColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            labelColor = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    } else {
                        AssistChipDefaults.assistChipColors()
                    }
                )
            }
        }
    }
}
