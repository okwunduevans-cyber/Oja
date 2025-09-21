# OJA Android App

OJA is a Jetpack Compose prototype for a three-sided Nigerian commerce marketplace. Buyers can browse products from multiple stores, manage multi-store carts, accept extra logistics fees, and follow deliveries in real time while vendors and transporters have dedicated entry points for onboarding and job claiming.

## Feature highlights
- **Guided launch flow** – A welcome screen leads into the home marketplace surface built with Material 3 and Navigation Compose.
- **Marketplace home** – Buyers can browse seeded inventory, add products to cart, and jump to logistics or transporter experiences.
- **Multi-store cart guardrails** – The cart groups items by store, requires explicit approval for additional courier fees, and simulates order creation once checkout is accepted.
- **Transporter job board** – Newly created orders appear as unclaimed tickets that transporters can claim, reflecting updates in the in-memory repository.
- **Live delivery tracking** – The tracking screen renders pickup, courier, and drop-off markers using Maps Compose with a simulated route progress loop.
- **Role-specific sign-up surfaces** – Placeholder vendor and transporter enrollment screens demonstrate future onboarding flows.
- **Payments staging area** – The payments screen scaffolds integration points for Paystack and Flutterwave SDKs while the backend contract is finalized.

## Recent updates
- Consolidated the `data` and `net` packages directly under the app module for easier navigation and wiring of repositories.
- Added explicit lazy list item imports so Compose previews and builds succeed without wildcard fallbacks.
- Renamed cart item variables for clarity when iterating over store groupings.

## Project structure
```text
app/
├── src/main/java/com/oja/app
│   ├── data/        # In-memory models and repository that simulate marketplace behavior
│   ├── net/         # WebSocket configuration placeholder pointing to the future backend
│   ├── navigation/  # Route definitions for Navigation Compose
│   ├── ui/          # Compose theme, root scaffold, and feature screens
│   └── MainActivity.kt / OjaApp.kt
├── src/main/res    # Resources including the Google Maps API key placeholder
├── build.gradle.kts
└── ...
```

## Getting started
1. Install the latest Android Studio (Ladybug or newer) with Android Gradle Plugin 8.4+ and JDK 17.
2. Clone this repository and open it in Android Studio. Sync Gradle when prompted so Compose BOM `2025.08.00` and Maps dependencies download.
3. Replace `YOUR_MAPS_API_KEY_HERE` in `app/src/main/res/values/strings.xml` with a valid Maps SDK for Android key.
4. Update `WsConfig.WS_URL` when your real WebSocket backend is available; the UI currently simulates job updates without a connection.
5. Build and run on an emulator or device running Android 10–14 using **Run > Run 'app'** or `./gradlew installDebug`.

## Simulated flow walkthrough
1. Launching the app lands on the welcome hero and transitions into the marketplace home.
2. Adding items from multiple stores to the cart will prompt for extra logistics fees per additional store before checkout can complete.
3. Checking out generates an order and drops a job ticket into the Jobs Dashboard, where a transporter can claim it.
4. Selecting a job opens the tracking screen to visualize courier progress between seeded pickup and drop-off coordinates.

## Next steps
- Swap the placeholder hero artwork on the welcome screen with the field-agent rotation once assets are ready.
- Wire the Payments screen to server-issued Paystack access codes and Flutterwave Drop-In once endpoints are live.
- Replace the simulated repository with production APIs, persisting cart, order, and tracking data through your backend services.
