

1. Create Android Studio project: **Empty Compose Activity**
   App name: **OJA**
   Package: **com.oja.app**
   Min SDK: 23
   Kotlin: yes, Compose: yes.
   Do **not** pick “Google Maps Activity” (we’re using Maps Compose). ([Google for Developers][2])

2. Replace the generated files with the **exact files below** (paths included). Sync after each Gradle file.

3. Run the app. It will boot to **Welcome** → **Home** → **Jobs** → **Track**. Maps screen shows a placeholder if no API key is set.

4. After boot, wire payments in test mode (server will give `access_code` for Paystack; Flutterwave can run drop-in), then add your real backend URLs into `WsConfig.kt`. ([Paystack][1])

---

# Files to paste (full replacements)

## 1) `settings.gradle.kts`

```kotlin
pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}
rootProject.name = "OJA"
include(":app")
```

> **Compatibility note:** Compose BOM `2025.08.00` resolves to Compose 1.9, which requires Android Gradle Plugin 8.6.0 or newer. Make sure your project uses AGP ≥8.6.0 (and the matching Gradle wrapper) to avoid the Kotlin metadata/D8 warning referenced in the Android build docs.

## 2) Root `build.gradle.kts`

```kotlin
plugins {
    id("com.android.application") version "8.6.0" apply false
    id("org.jetbrains.kotlin.android") version "2.0.20" apply false
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.20" apply false
}
```

## 3) `gradle.properties`

```properties
org.gradle.jvmargs=-Xmx4g -Dfile.encoding=UTF-8
android.useAndroidX=true
kotlin.code.style=official
```

## 4) `app/build.gradle.kts`

```kotlin
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "com.oja.app"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.oja.app"
        minSdk = 23
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        vectorDrawables { useSupportLibrary = true }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug { isMinifyEnabled = false }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }

    buildFeatures { compose = true }

    packaging {
        resources.excludes += setOf(
            "/META-INF/{AL2.0,LGPL2.1}",
            "META-INF/INDEX.LIST"
        )
    }
}

dependencies {
    val composeBom = platform("androidx.compose:compose-bom:2025.08.00")
    implementation(composeBom)
    androidTestImplementation(composeBom)

    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.activity:activity-compose:1.9.2")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.4")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.4")
    implementation("androidx.navigation:navigation-compose:2.8.0")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")

    // Maps Compose (explicit version as per docs)
    implementation("com.google.maps.android:maps-compose:6.7.1") // :contentReference[oaicite:3]{index=3}
    implementation("com.google.android.gms:play-services-maps:19.0.0")
    implementation("com.google.android.gms:play-services-location:21.0.1") // :contentReference[oaicite:4]{index=4}

    // WebSockets + JSON (Ktor 3.x)
    implementation("io.ktor:ktor-client-okhttp:3.2.3") // :contentReference[oaicite:5]{index=5}
    implementation("io.ktor:ktor-client-websockets:3.2.3")
    implementation("io.ktor:ktor-client-content-negotiation:3.2.3")
    implementation("io.ktor:ktor-serialization-kotlinx-json:3.2.3")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")

    // Image loading
    implementation("io.coil-kt:coil-compose:2.7.0")

    // QR (scan/generate)
    implementation("com.journeyapps:zxing-android-embedded:4.3.0") // :contentReference[oaicite:6]{index=6}
    implementation("com.google.zxing:core:3.5.3")

    // Payments (adapters + SDKs)
    implementation("com.paystack.android:paystack-ui:0.0.10") // check docs for latest before ship :contentReference[oaicite:7]{index=7}
    implementation("com.github.flutterwave.rave-android:rave_android:2.2.1") // via JitPack :contentReference[oaicite:8]{index=8}

    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
}
```

## 5) `app/src/main/AndroidManifest.xml`

```xml
<manifest
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools">
    <application
        android:name=".OjaApp"
        android:allowBackup="false"
        tools:replace="android:allowBackup"
        android:label="OJA"
        android:theme="@style/Theme.Material3.DayNight.NoActionBar">
        <activity
            android:name=".MainActivity"
            android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.MAIN"/>
                <category android:name="android.intent.category.LAUNCHER"/>
            </intent-filter>
        </activity>
        <meta-data
            android:name="com.google.android.geo.API_KEY"
            android:value="@string/google_maps_key"/>
    </application>

    <uses-permission android:name="android.permission.INTERNET"/>
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"/>
    <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION"/>
</manifest>
```

## 6) `app/src/main/res/values/strings.xml`

```xml
<resources>
    <string name="app_name">OJA</string>
    <string name="google_maps_key">YOUR_MAPS_API_KEY_HERE</string>
    <string name="welcome_title">Welcome to OJA</string>
    <string name="welcome_sub">Your neighborhood market—street to doorstep</string>
</resources>
```

## 7) `app/src/main/java/com/oja/app/OjaApp.kt`

```kotlin
package com.oja.app

import android.app.Application

class OjaApp : Application()
```

## 8) `app/src/main/java/com/oja/app/MainActivity.kt`

```kotlin
package com.oja.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.ExperimentalMaterial3Api
import com.oja.app.ui.AppRoot

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { AppRoot() }
    }
}
```

## 9) `app/src/main/java/com/oja/app/ui/Theme.kt`

```kotlin
package com.oja.app.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val scheme = darkColorScheme()

@Composable
fun OjaTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = scheme, content = content)
}
```

## 10) `app/src/main/java/com/oja/app/navigation/Nav.kt`

```kotlin
package com.oja.app.navigation

sealed class Route(val path: String) {
    data object Welcome: Route("welcome")
    data object Home: Route("home")
    data object Cart: Route("cart")
    data object Jobs: Route("jobs")
    data object Track: Route("track/{orderId}") {
        fun path(orderId: String) = "track/$orderId"
    }
    data object TransporterSignup: Route("transporter")
    data object VendorSignup: Route("vendor")
    data object Payments: Route("payments")
}
```

## 11) `app/src/main/java/com/oja/app/ui/AppRoot.kt`

```kotlin
package com.oja.app.ui

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.oja.app.navigation.Route
import com.oja.app.ui.screens.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppRoot() {
    OjaTheme {
        val nav = rememberNavController()
        NavHost(navController = nav, startDestination = Route.Welcome.path) {
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
```

## 12) `app/src/main/java/com/oja/app/data/Models.kt`

```kotlin
package com.oja.app.data

enum class DeliveryMethod { BICYCLE, BIKE, CAR }

data class Store(val id: String, val name: String)
data class Product(val id: String, val storeId: String, val name: String, val price: Long)

data class CartItem(val product: Product, val qty: Int)
data class CartState(
    val items: List<CartItem> = emptyList(),
    val acceptedExtraFees: Set<String> = emptySet() // storeId -> accepted
) {
    val groupedByStore: Map<String, List<CartItem>> get() = items.groupBy { it.product.storeId }
    val subtotal: Long get() = items.sumOf { it.product.price * it.qty }
}

data class Order(val id: String, val storeIds: List<String>, val method: DeliveryMethod, val total: Long)

data class JobTicket(
    val id: String,
    val orderId: String,
    val method: DeliveryMethod,
    val pickupLat: Double,
    val pickupLng: Double,
    val dropLat: Double,
    val dropLng: Double,
    val claimedByTransporterId: String? = null
)

data class TransporterProfile(val id: String, val name: String, val methods: Set<DeliveryMethod>)
```

## 13) `app/src/main/java/com/oja/app/data/Repo.kt`

```kotlin
package com.oja.app.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlin.random.Random

object Repo {
    private val _cart = MutableStateFlow(CartState())
    val cart: StateFlow<CartState> = _cart

    private val _jobs = MutableStateFlow<List<JobTicket>>(emptyList())
    val jobs: StateFlow<List<JobTicket>> = _jobs

    private val stores = listOf(
        Store("s1","Idumota Plastics"),
        Store("s2","Lekki Grocer"),
        Store("s3","Ajah Tools")
    )
    val products = listOf(
        Product("p1","s1","Broom", 1200),
        Product("p2","s2","Indomie Pack", 5000),
        Product("p3","s3","Spanner", 2500)
    )

    fun addToCart(p: Product) { _cart.update { it.copy(items = it.items + CartItem(p, 1)) } }
    fun acceptExtraFeeFor(storeId: String) { _cart.update { it.copy(acceptedExtraFees = it.acceptedExtraFees + storeId) } }
    fun clearCart() { _cart.value = CartState() }

    fun createOrder(method: DeliveryMethod): Order {
        val cs = _cart.value
        val storeIds = cs.groupedByStore.keys.toList()
        val base = cs.subtotal
        val extraFee = (storeIds.size - 1).coerceAtLeast(0) * 700 // per extra store
        val total = base + extraFee
        val order = Order(id = "o-${Random.nextInt(10000, 99999)}", storeIds, method, total)
        val jt = JobTicket(
            id = "j-${Random.nextInt(10000,99999)}",
            orderId = order.id,
            method = method,
            pickupLat = 6.449, pickupLng = 3.602, // Ajah-ish
            dropLat = 6.453, dropLng = 3.611
        )
        _jobs.update { listOf(jt) + it }
        _cart.value = CartState()
        return order
    }

    fun claimJob(jobId: String, transporterId: String) {
        _jobs.update { list -> list.map { if (it.id == jobId) it.copy(claimedByTransporterId = transporterId) else it } }
    }
}
```

## 14) `app/src/main/java/com/oja/app/net/WsConfig.kt`

```kotlin
package com.oja.app.net

object WsConfig {
    // Replace with your backend when ready. Leave as-is; UI will simulate.
    const val WS_URL = "wss://example.com/oja/ws"
}
```

## 15) `app/src/main/java/com/oja/app/ui/screens/WelcomeScreen.kt`

```kotlin
package com.oja.app.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.oja.app.R

@Composable
fun WelcomeScreen(onStart: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(16.dp))
        Text("Welcome to OJA", textAlign = TextAlign.Center)
        // Placeholder hero; swap with generated “field agents” artwork later.
        Image(painterResource(id = R.drawable.ic_launcher_foreground), contentDescription = "Agents")
        Button(onClick = onStart, modifier = Modifier.fillMaxWidth()) { Text("Enter Market") }
    }
}
```

## 16) `app/src/main/java/com/oja/app/ui/screens/HomeScreen.kt`

```kotlin
package com.oja.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.oja.app.data.Repo
import com.oja.app.navigation.Route

@Composable
fun HomeScreen(nav: NavHostController) {
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button({ nav.navigate(Route.Cart.path) }) { Text("Cart") }
            Button({ nav.navigate(Route.Jobs.path) }) { Text("Jobs") }
            Button({ nav.navigate(Route.TransporterSignup.path) }) { Text("Be a Transporter") }
        }
        Spacer(Modifier.height(8.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(Repo.products.size) { idx ->
                val p = Repo.products[idx]
                Card(Modifier.fillMaxWidth().padding(2.dp)) {
                    Column(Modifier.padding(12.dp)) {
                        Text(p.name)
                        Text("₦${p.price}")
                        Spacer(Modifier.height(8.dp))
                        Button({ Repo.addToCart(p) }) { Text("Add to Cart") }
                    }
                }
            }
        }
    }
}
```

## 17) `app/src/main/java/com/oja/app/ui/screens/CartScreen.kt`

```kotlin
package com.oja.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.oja.app.data.*
import com.oja.app.navigation.Route

@Composable
fun CartScreen(nav: NavHostController) {
    val cart by Repo.cart.collectAsState()
    var method by remember { mutableStateOf(DeliveryMethod.BIKE) }
    var pendingStoreId by remember { mutableStateOf<String?>(null) }

    val groups = cart.groupedByStore
    val extraStores = (groups.keys.size - 1).coerceAtLeast(0)
    val extraFee = extraStores * 700L
    val needsAccept = groups.keys.any { it !in cart.acceptedExtraFees } && groups.keys.size > 1

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        LazyColumn(Modifier.weight(1f)) {
            groups.forEach { (storeId, items) ->
                item { Text("Store: $storeId") }
                items(items.size) { i ->
                    val it = items[i]
                    Text("${it.product.name} x${it.qty} — ₦${it.product.price}")
                }
            }
        }
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button({ method = DeliveryMethod.BICYCLE }) { Text("Bicycle") }
            Button({ method = DeliveryMethod.BIKE }) { Text("Bike") }
            Button({ method = DeliveryMethod.CAR }) { Text("Car") }
        }
        Spacer(Modifier.height(8.dp))
        Text("Subtotal: ₦${cart.subtotal}")
        if (extraStores > 0) Text("Extra store fee: ₦$extraFee")
        Spacer(Modifier.height(8.dp))
        Button({
            if (needsAccept) {
                pendingStoreId = groups.keys.first { it !in cart.acceptedExtraFees }
            } else {
                val order = Repo.createOrder(method)
                nav.navigate(Route.Track.path(order.id))
            }
        }, enabled = cart.items.isNotEmpty()) { Text("Checkout") }
    }

    val sid = pendingStoreId
    if (sid != null) {
        AlertDialog(
            onDismissRequest = { pendingStoreId = null },
            title = { Text("Extra logistics cost") },
            text = { Text("Adding items from $sid adds courier cost. Accept?") },
            confirmButton = {
                TextButton({
                    Repo.acceptExtraFeeFor(sid)
                    pendingStoreId = null
                }) { Text("Accept") }
            },
            dismissButton = { TextButton({ pendingStoreId = null }) { Text("Cancel") } }
        )
    }
}
```

## 18) `app/src/main/java/com/oja/app/ui/screens/JobsDashboardScreen.kt`

```kotlin
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
```

## 19) `app/src/main/java/com/oja/app/ui/screens/TrackScreen.kt`

```kotlin
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
    Column(Modifier.fillMaxSize()) {
        if (job == null) {
            Text("No tracking for $orderId")
            Button(onBack) { Text("Back") }
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

        if (com.google.android.libraries.maps.BuildConfig.VERSION_NAME != null) {
            GoogleMap(
                modifier = Modifier.weight(1f),
                cameraPositionState = cameraPositionState
            ) {
                Polyline(points = listOf(start, end))
                Marker(state = rememberMarkerState(position = start), title = "Pickup")
                Marker(state = rememberMarkerState(position = end), title = "Dropoff")
                Marker(state = rememberMarkerState(position = current), title = "Courier")
            }
        } else {
            Box(Modifier.weight(1f)) { Text("Map placeholder (add Maps API key)") }
        }
        Row(Modifier.fillMaxWidth().padding(12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onBack) { Text("Back") }
            Text("Progress: ${(progress * 100).toInt()}%")
        }
    }
}
```

## 20) `app/src/main/java/com/oja/app/ui/screens/TransporterSignupScreen.kt`

```kotlin
package com.oja.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.oja.app.data.DeliveryMethod

@Composable
fun TransporterSignupScreen(nav: NavHostController) {
    var name by remember { mutableStateOf("") }
    var bike by remember { mutableStateOf(true) }
    var bicycle by remember { mutableStateOf(false) }
    var car by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Transporter Registration")
        Row { Checkbox(bicycle, { bicycle = it }); Text("Bicycle") }
        Row { Checkbox(bike, { bike = it }); Text("Bike") }
        Row { Checkbox(car, { car = it }); Text("Car") }
        Spacer(Modifier.height(8.dp))
        Button({ nav.popBackStack() }) { Text("Submit") }
    }
}
```

## 21) `app/src/main/java/com/oja/app/ui/screens/VendorSignupScreen.kt`

```kotlin
package com.oja.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@Composable
fun VendorSignupScreen(nav: NavHostController) {
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Vendor Onboarding")
        Spacer(Modifier.height(8.dp))
        Button({ nav.popBackStack() }) { Text("Submit") }
    }
}
```

## 22) `app/src/main/java/com/oja/app/ui/screens/PaymentsScreen.kt`

```kotlin
package com.oja.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@Composable
fun PaymentsScreen(nav: NavHostController) {
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Payments (Test Mode)")
        Spacer(Modifier.height(8.dp))
        Button({ /* trigger Paystack PaymentSheet with server access_code */ }) { Text("Pay with Paystack") }
        Spacer(Modifier.height(8.dp))
        Button({ /* open Flutterwave Drop-In */ }) { Text("Pay with Flutterwave") }
    }
}
```

---

# Why this boots and scales

* Compose + Navigation + simple state flows = no fragile fragments, no ViewBinding hassles.
* Maps Compose pinned to **6.7.1** per Google’s doc (fresh, 2025-09-18). ([Google for Developers][2])
* Ktor WebSockets is ready for your real backend later; today you get local “auto-update” to prove the UI. ([Ktor Framework][3])
* Paystack UI SDK uses **server-initialized** `access_code` by design (keeps secrets off device). Flutterwave Drop-In is available via JitPack. NQR is supported via Flutterwave APIs and your own QR generator if you want to own the flow. ([Paystack][1])
* Location uses **play-services-location** and Maps SDK. ([Google for Developers][4])


---

# Next expansions (when you’re ready)

* **Payments real**: Server endpoint `/init/paystack` returns `access_code`. Wire to Paystack `PaymentSheet` per docs; add `/verify` webhook. ([Paystack][1])
* **NQR**: Generate dynamic QR for doorstep/pickup; verify using Flutterwave NQR or your PSP; scan with ZXing. ([Flutterwave Developer Portal][5])
* **Transporter app mode**: Background location updates + order claiming with live position over WebSocket.
* **“Agents” hero image**: I can generate the welcome artwork on your cue and we’ll swap the placeholder in `WelcomeScreen`.

---



* Add the **payments adapters** (Paystack `PaymentSheet`, Flutterwave Drop-In) with full server contracts.
* Replace the simulated job feed with a **real WebSocket** client (Ktor) + DTOs + reconnection/backoff.
* Add **QR pay at pickup** and a transporter **turn-by-turn** progress UI.



[1]: https://paystack.com/docs/developer-tools/android-sdk/ "Android SDK | Paystack Developer Documentation"
[2]: https://developers.google.com/maps/documentation/android-sdk/maps-compose "Maps Compose Library  |  Maps SDK for Android  |  Google for Developers"
[3]: https://ktor.io/docs/client-websockets.html?utm_source=chatgpt.com "WebSockets in Ktor Client"
[4]: https://developers.google.com/android/guides/setup?utm_source=chatgpt.com "Set up Google Play services"
[5]: https://developer.flutterwave.com/v3.0/docs/nibss-qr?utm_source=chatgpt.com "NIBSS QR"
