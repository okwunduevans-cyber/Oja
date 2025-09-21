# Boot Progress Report

## Stage 1 – Smoke-test boot experience
- Resolved the lazy list DSL imports and naming collisions so every Compose screen builds, allowing navigation through Welcome → Home → Cart → Jobs → Track.
- Added `RepoSmokeTest` coverage to validate cart grouping, subtotal math, and order creation so the multi-store fee prompt and job claim flows are backed by deterministic data.
- Confirmed that the tracking screen gracefully falls back to the placeholder map copy when no Google Maps key is present, matching the documented boot expectations.

## Stage 2 – Placeholder inventory
The following placeholders remain and must be swapped before launch-readiness:

| Area | Location | Placeholder behaviour | Replacement trigger |
| --- | --- | --- | --- |
| Payments | `app/src/main/java/com/oja/app/ui/screens/PaymentsScreen.kt` | Buttons have no backing SDK integrations yet. | Implement Paystack and Flutterwave test flows, then production rails. |
| Job feed & cart data | `app/src/main/java/com/oja/app/data/Repo.kt` | In-memory samples, random IDs, no persistence. | Connect to real repositories and WebSocket job feed. |
| WebSocket endpoints | `app/src/main/java/com/oja/app/net/WsConfig.kt` | Points to `wss://example.com` placeholder. | Update with staging/production backend URLs. |
| Tracking map | `app/src/main/java/com/oja/app/ui/screens/TrackScreen.kt` | Shows placeholder panel without a Maps API key. | Supply API key and production routing data. |
| Welcome hero art | `app/src/main/java/com/oja/app/ui/screens/WelcomeScreen.kt` | Static placeholder vector graphic. | Swap in curated rotating field-agent imagery. |
| Localization | `app/src/main/res/values/strings.xml` | English-only strings, no switcher UI. | Provide multi-language string packs and persistent toggle. |
| Signup flows | `app/src/main/java/com/oja/app/ui/screens/TransporterSignupScreen.kt`, `VendorSignupScreen.kt` | Capture-only stubs without validation or submission. | Connect to onboarding services and validation logic. |

## Next steps
1. Stage 3 – Replace payment placeholders with real Paystack/Flutterwave test-mode integrations.
2. Stage 4 – Bring the real-time logistics backend online (WebSocket feed, live tracking, persistence).
3. Stage 5 – Deliver multilingual UI, inclusive theming, and production-ready payment rails.
