 OJA v1 Production Plan (Android + Backend + Ops)

## 0) Hard constraints you must design for

* **Payments flow reality:** Paystack on Android is a **server → SDK** two-step: your backend creates the transaction/access\_code, then the SDK completes it in-app. Same shape for Flutterwave’s Drop-In. ([Paystack][1])
* **Maps costs & policy:** Directions/Places/Geocoding require **billing enabled**, and you should budget/monitor SKUs. Use Autocomplete sessions and cache geocodes to control spend. ([Google for Developers][2])
* **Android background location:** Request foreground first, justify background separately, respect Play policy, show a persistent notification when tracking. ([Android Developers][3])
* **Push:** FCM for device → channel mapping and high-reliability fan-out. ([Firebase][4])
* **Data protection (Nigeria NDPA 2023):** Appoint a DPO, keep processor agreements, and **notify within 72 hours** of qualifying personal-data breaches. Build subject-rights flows. ([ngCERT][5])
* **Logistics partners:** Start with those that actually expose APIs in NG: **Sendbox** (quotes, create, track) and **Kwik** (partner API). GIGL is great operationally but treat as a corporate integration unless/until they publish dev docs. ([Sendbox API Documentation][6])

---

## 1) Information Architecture — Click Layers & Screens

### Buyer app

* **Welcome & Onboarding**

  * Language picker (EN, Pidgin, Yoruba, Hausa, Igbo)
  * Phone/email sign-in; address consent; notifications consent
* **Home / Marketplace**

  * Search (text + category + price + store + delivery ETA)
  * Collections & promos; infinite list; chip filters
* **Product**

  * Image gallery (pinch-zoom), price, stock, variants, delivery ETA
  * Store card (rating, SLA, fees), “Add to Cart”, “Ask Seller”
* **Cart (multi-store)**

  * Groups by store; extra courier fee prompt per store
  * Coupons/Vouchers; tip courier; delivery window selector
* **Checkout**

  * Address picker (Places Autocomplete + map pin-drop + saved addresses)
  * Payment method: Paystack / Flutterwave / Pay on Delivery (config flag)
  * Summary → Pay → Success screen w/ order ID
* **Orders**

  * List + status chips; each opens **Order Detail**
  * **Order Detail**: items, charges, status timeline, courier live map, chat
* **Account**

  * Profile, saved addresses, cards (tokenized), notifications, language
  * Support: FAQs, chat, open dispute, returns
  * Legal: privacy, data export, delete account (NDPA)

### Vendor (seller) app area

* **Vendor Onboarding**

  * Business profile (name, CAC optional), bank payout, store address (map)
* **Vendor Dashboard**

  * Sales today, open orders, inventory alerts
* **Products**

  * Unlimited product creation: title, SKU, categories, attributes, **multi-image upload** (queue + retry), price, stock, variants
  * Bulk import (CSV) and image batch uploader
* **Orders**

  * New / Preparing / Ready-for-pickup; request pickup (partner auto-quote)
* **Store**

  * Hours, delivery fees, holidays, store photos, vacations

### Transporter (courier) app area

* **Job Board (WebSocket)**

  * Live feed of unclaimed jobs within geo-radius; filters by vehicle type
* **Job Detail**

  * Pickup (vendor map), drop-off (buyer map), items list, fees, SLA
  * **Claim Job** (atomic), start navigation
* **Live Run**

  * Background location (15–30s), route polyline, status: at-pickup → en-route → at-dropoff → delivered
  * Photo proof + e-signature; cash collected toggle (if COD)
* **Earnings**

  * Daily/weekly payouts, statements, disputes

### Admin (web, later)

* Users, vendors, transporters, products moderation
* Orders and SLA monitor; refunds; disputes; promotions
* Ops dashboard: WS health, queue depths, payment captures, API spend

---

## 2) Data & APIs (backend contracts that actually unblock Android)

**Auth**

* `POST /auth/signup` (role=buyer|vendor|courier), `POST /auth/login`, `POST /auth/otp/verify`
* `GET /me`, `DELETE /me` (account deletion, NDPA)

**Catalog**

* `GET /products` (q, category, store, sort, page)
* `POST /vendors/{id}/products` (multipart: images\[]; JSON: product)
* `PUT/PATCH /vendors/{id}/products/{pid}`, `DELETE …`
* `GET /stores/{id}`; `PUT /stores/{id}` (address lat/lng, fees, hours)

**Orders**

* `POST /orders` (cart payload grouped by store; calculates fees; returns draft)
* `POST /orders/{id}/pay` → returns `{ provider, accessCode | txRef }`
* Webhooks: `/webhooks/paystack`, `/webhooks/flutterwave` → confirm & capture
* `GET /orders`, `GET /orders/{id}`

**Logistics**

* `POST /orders/{id}/shipment/quote` → { partner, fee, eta }
* `POST /orders/{id}/shipment/book` → creates partner shipment + tracking id
* `PATCH /jobs/{id}/status` (pickup\_arrived, picked, in\_transit, delivered)
* `PATCH /jobs/{id}/location` (lat, lng, speed, heading, ts)

**Realtime (WS topics)**

* `/ws/jobs` → `{ jobId, pickup {lat,lng}, dropoff {lat,lng}, storeName, fee, distance }`
* `/ws/orders/{id}` → `{ status, courier {lat,lng}, eta, events[] }`
* `/ws/vendors/{id}/orders` → new & status updates
* `/ws/ops` (internal) → heartbeat, queue metrics

**Payments**

* `POST /payments/paystack/init` → `{ access_code }` (client SDK completes) ([Paystack][1])
* `POST /payments/flutterwave/init` → `{ tx_ref }` (Drop-In uses it) ([developer.flutterwave.com][7])

**Addresses**

* Client uses **Places Autocomplete** to capture correct addresses; backend **geocodes once**, stores lat/lng + address components; cache geocoding results to cut API costs. ([Google for Developers][8])

---

## 3) Maps, Location, and Cost Control

* **Maps Compose** for all map screens (buyers, vendors, couriers). ([Google for Developers][9])
* **Routes:** Google Directions API for ETA/polyline; enable billing; add request quotas and exponential backoff. ([Google for Developers][2])
* **Autocomplete:** Use session tokens per capture; bias to Nigeria; store Place ID + components. ([Google for Developers][10])
* **Geocoding:** Only your backend calls Geocoding; cache results; monitor spend. ([Google for Developers][11])
* **Background location (courier):** Foreground service + persistent notification; request **ACCESS\_BACKGROUND\_LOCATION** only after user has enabled “While in use” and started a job; clear when delivered. ([Android Developers][3])

---

## 4) Unlimited products with images — without pain

* **On-device pipeline:** HEIF/WEBP compression, multiple resolutions (thumb/medium/original), EXIF scrub, retry with **WorkManager**.
* **Upload protocol:** resumable uploads with signed URLs; show per-file progress; queue many images.
* **CDN:** put images behind a CDN with resizing params to spare device RAM.
* **Bulk tools:** CSV import (products), ZIP import (images matched by SKU).

---

## 5) Logistics partner strategy (Nigeria-first)

* **Phase 1:** **Sendbox API** for quotes, create, track; escrow option if you want hold-until-delivered. ([Sendbox API Documentation][12])
* **Phase 2:** **Kwik Delivery** corporate API for same-day Lagos/Abuja; they advertise developer access for merchants. ([Kwik - ecommerce logistics made easy][13])
* **Abstraction:** `LogisticsProvider` interface → adapters for Sendbox/Kwik; backend chooses by geo, SLA, and price.

---

## 6) Risk, Trust & Safety

* **KYC-lite:** vendor bank name verification; optional document check via a 3rd-party (contractual, not stored long-term).
* **Fraud rules:** cap COD per user/day; velocity checks; block disposable emails; device fingerprinting (privacy-respecting).
* **Disputes/Returns:** ticketing with evidence upload; timers and automated reminders.
* **Content moderation:** image size/type validation; profanity/forbidden goods keyword scan; soft-delete queue for ops.

---

## 7) Observability & Ops

* **App:** Crashlytics + in-app logging toggle; FCM topic per order/courier; event analytics for funnel drop-offs. ([Firebase][4])
* **Backend:** structured logs, request IDs, SLOs: p95 API < 300ms; WS uptime ≥ 99.5%, payment webhook ack < 2s.
* **Maps spend:** monthly budget alert + per-API caps; report and alert anomalies. ([Google Maps Platform][14])
* **Security:** JWT rotation, short-lived upload URLs, encrypted PII at rest, least-privilege service accounts.

---

## 8) Multilingual & Inclusive UX (Nigeria-real)

* **Locales at launch:** EN, Pidgin, Yoruba, Hausa, Igbo — all core surfaces.
* **Address quirks:** accept landmarks, plus a “Drop pin on map” and free-text “Driver notes”.
* **Low-end devices:** images lazy-load; skeleton screens; background work throttled on battery saver.

---

## 9) Compliance (NDPA 2023) — what you must implement

* **DPO** named and reachable; **records of processing** for orders, locations, payments.
* **Consent & purpose limitation** (location, marketing).
* **Breach response**: assess & notify **within 72 hours** if risk is likely; maintain processor DPAs. ([ngCERT][5])

---

## 10) Phased Delivery — With “Run & Test” at each stage

### **Stage A — Payments + Address correctness**

* Wire server-initiated **Paystack** (then Flutterwave).
* Add **Places Autocomplete** + saved addresses + pin-drop.
* **Run & Test:** pay a ₦100 test item end-to-end; verify webhook capture; confirm order timeline & receipt. ([Paystack][1])

### **Stage B — Real-time logistics**

* Backend: Sendbox adapter (quote, create, track).
* Android: **/ws/orders/{id}** live map with route/ETA.
* **Run & Test:** place order → auto-book shipment → see courier path update every 15–30s; check battery/foreground service behavior. ([Sendbox API Documentation][6])

### **Stage C — Vendor reality**

* Seller onboarding; unlimited product uploads (queue + retry); bulk import.
* **Run & Test:** upload 200 images across 100 products on a mid-tier device; verify no OOM; thumbnails crisp.

### **Stage D — Courier app polish**

* Job board feed; atomic claim; navigation handoff; POD (photo + signature).
* **Run & Test:** simulate 50 nearby jobs; measure WS latency and claim collisions.

### **Stage E — Trust, support, analytics**

* Disputes/returns; SLA alerts; Crashlytics + funnels; privacy center (export/delete).
* **Run & Test:** open dispute → ops resolves; verify NDPA data export and delete flows.

---

## 11) Cost-control guardrails (Maps & Push)

* **Maps:** cache geocodes, leverage Autocomplete sessions, batch Directions requests, and enforce per-day caps; monitor SKUs in Cloud Console. ([Google for Developers][15])
* **Push:** prefer data messages for order updates; group notifications by order to avoid spam. ([Firebase][4])

---

## 12) What I’ll ship next (when you say go)

Full, drop-in Android implementations (no snippets, full files) for:

* `VendorRepository` + product create/edit with multi-image upload (WorkManager + progress UI).
* `LogisticsRepository` + WebSocket handlers for `/ws/jobs` and `/ws/orders/{id}`; foreground service for courier location.
* `AddressPickerScreen` with Places Autocomplete + map confirm + saved addresses store.
* `PaymentsManager` bridging Paystack and Flutterwave (server-first init).
* `OrderDetailScreen` with live route polyline/ETA, chat stub, and POD viewer.
* DI graph (Hilt), paging lists, image pipeline (Coil with memory/disk policy).

I’ve kept this plan ruthless and Nigeria-realistic. When you’re ready to execute **Stage A**, I’ll return **full replacement scripts** for the Android client (files + exact paths), along with the backend endpoint contracts you can mirror.

[1]: https://paystack.com/docs/developer-tools/android-sdk/?utm_source=chatgpt.com "Android SDK | Paystack Developer Documentation"
[2]: https://developers.google.com/maps/documentation/directions/usage-and-billing?utm_source=chatgpt.com "Directions API Usage and Billing"
[3]: https://developer.android.com/develop/sensors-and-location/location/permissions?utm_source=chatgpt.com "Request location permissions | Sensors and location"
[4]: https://firebase.google.com/docs/cloud-messaging?utm_source=chatgpt.com "Firebase Cloud Messaging"
[5]: https://cert.gov.ng/ngcert/resources/Nigeria_Data_Protection_Act_2023.pdf?utm_source=chatgpt.com "Nigeria Data Protection Act, 2023"
[6]: https://docs.sendbox.co/shipping/request-shipping-quotes?utm_source=chatgpt.com "Request Shipping Quotes"
[7]: https://developer.flutterwave.com/?utm_source=chatgpt.com "Flutterwave Documentation"
[8]: https://developers.google.com/maps/documentation/places/android-sdk/autocomplete-tutorial?utm_source=chatgpt.com "Add Place Autocomplete to an address form"
[9]: https://developers.google.com/maps/documentation/android-sdk/maps-compose?utm_source=chatgpt.com "Maps Compose Library | Maps SDK for Android"
[10]: https://developers.google.com/maps/documentation/places/android-sdk/place-autocomplete?utm_source=chatgpt.com "Autocomplete (New) | Places SDK for Android"
[11]: https://developers.google.com/maps/documentation/geocoding/usage-and-billing?utm_source=chatgpt.com "Geocoding API Usage and Billing - Google for Developers"
[12]: https://docs.sendbox.co/api/basics?utm_source=chatgpt.com "Basics | Sendbox API Documentation"
[13]: https://kwik.delivery/home/developer/?utm_source=chatgpt.com "Kwik Tools for Developers - Kwik Delivery"
[14]: https://mapsplatform.google.com/pricing/?utm_source=chatgpt.com "Platform Pricing & API Costs - Google Maps Platform"
[15]: https://developers.google.com/maps/documentation/geocoding/report-monitor?utm_source=chatgpt.com "Reporting & Monitoring Overview | Geocoding API"
