

* **Name:** OJA.
* **Iconography:** Keep the Igbo *oja* flute as an **optional/rotating cultural theme**; default logo stays neutral (basket/stall glyph). Seasonal skins opt-in only (no tribe/religion owns the UI).
* **Multilingual core:** EN + Nigerian Pidgin + Yoruba + Hausa + Igbo at launch; language switcher always visible. Nigeria’s split across major faiths → keep copy neutral. ([StatCounter Global Stats][1])

# Ground Truth: 2025 Nigeria constraints (refreshed)

* **Android reality:** Nigeria’s install base skews across Android 10–14 with 13/12/11 still heavy; 14 is rising. Optimize for 10–14; test on 15+ as it ramps. ([StatCounter Global Stats][1])
* **Bandwidth:** Design for mobile data & patchy coverage → PWA/offline caches, tiny assets, progressive loading. ([TC Insights][2])
* **Payments:** Ship Paystack + Flutterwave first; add **NQR** for QR at pickup/doorstep; keep **USSD** as resilience rail (2025 End-User Billing change improves reliability/charge flow). ([Paystack][3])
* **Compliance:** Build to **NDPA 2023** and align to **NDPC GAID 2025** (consent, DSR, RoPA, Nigerian hosting options). ([Nigeria Data Protection Commission][4])
* **Macro tailwind:** Cashless is surging—NIBSS data shows strong 2025 e-payment growth; design for high digital volume. ([Proshare][5])

# Logistics Model (replaced)

**Transporter marketplace, not courier API.**

* **Who:** Individual transporters (bicycle, bike, car) + micro-fleets register and verify.
* **How it works:**

  1. Buyer checks out; each **additional store** triggers a clear **extra logistics fee** prompt; buyer must **accept** before continuing.
  2. An **Unclaimed Job Ticket** appears instantly on the **Jobs Dashboard** with pickup(s), drop-off, and delivery method.
  3. Transporters **claim** jobs; app broadcasts live location → buyer tracks on map (InDrive-style).
  4. **Proof of delivery**: OTP/QR scan + photo; funds settle per policy.
* **Field agents:** Youth pros onboard/verify vendors & transporters; rotate their imagery on the **welcome screen** (non-permanent).
* **Map stack:** **Maps Compose** + Fused Location; server can snap routes and broadcast via WebSocket. ([Google for Developers][6])

# MVP Scope (90 days, revised)

**Buyer**

* Onboarding with language + location; search, categories, filters.
* Product detail with vendor rating, ETA bands by delivery method.
* **Multi-store cart** with surcharge warnings & **explicit acceptance**.
* Checkout: Paystack/Flutterwave (Card/Transfer/Bank), roadmap to **NQR at door** & **USSD** fallback. ([Paystack][3])
* Orders: live state, chat, **real-time map tracking**.

**Vendor**

* KYC-phased onboarding; store setup; camera/CSV catalog; promos.
* Orders board; hand-off flows; refunds; settlement statements.

**Transporter**

* Registration + KYC; vehicle modes; earnings wallet.
* **Job Board** (distance/fee sorting), claim, in-app nav, background GPS.

**Platform**

* **Jobs Dashboard (Ops):** Unclaimed/Claimed/En-route/Delivered; SLA & surge.
* **Trust:** Verified badges, dispute center, transparent fee breakdown.
* **Perf & offline:** 60fps lists, image CDN, offline cart/search.
* **A11y:** Large touch targets, dynamic type, color-safe palette.

# Product Differentiators (kept + strengthened)

* **Street-level discovery** of hawkers/stalls alongside big stores.
* **Delivery as a marketplace** (supply liquidity is visible).
* **Transparent multi-store surcharges** with consent.
* **Inclusive theming** (opt-in skins), **five languages** at launch.

# Tech Blueprint (concrete 2025 choices)

* **Android app:** Kotlin + Jetpack Compose + Navigation; **Maps Compose**; **Fused Location**; WebSocket client for jobs/tracking. ([Google for Developers][6])
* **Web app (PWA):** Next.js (App Router) + Workbox offline caches (browse/cart/orders).
* **Backend:** NestJS (Node) or Go; Postgres (+ PostGIS), Redis; WebSocket fan-out for orders/tracking.
* **Payments:**

  * **Paystack Android SDK** (beta UI components; init via server-issued access code). ([Paystack][3])
  * **Flutterwave Android (Rave) SDK** via JitPack (Drop-in UI available). ([jitpack][7])
  * **NQR** server flow (QR generation/validation) + **USSD** rails acknowledging 2025 EUB model. ([Nibss Plc][8])
* **Observability & integrity:** OpenTelemetry, Grafana/Tempo; idempotent payment intents; audit logs for order/payment state transitions.
* **Privacy:** NDPA + **GAID 2025** alignment: consent ledger, DSR portal, Nigerian data residency option. ([Nigeria Data Protection Commission][4])

# Data & Schemas (no change, add jobs/tracking detail)

* **Order**: items, surcharges (per extra store), method, totals, timeline.
* **JobTicket**: orderId, pickups\[], drop, method, price, state, assignedTransporterId, lastPing.
* **Tracking**: courier pings (lat/lng/ts), ETA, POD artifacts (OTP/QR/photo).
* **Payment**: gateway, method (Card/Transfer/NQR/USSD), status, receipts, webhooks.

# Inclusive UX (amended)

* **Welcome screen**: neutral default + **rotating field-agent hero** (curated, not permanent).
* **Language switcher** always visible.
* **Bias-free ratings**: service metrics only (on-time, as-described).

# Payments & Cashflow (clarified 2025 realities)

* **Stage 1:** Card/Transfer via Paystack & Flutterwave (test → live). ([Paystack][3])
* **Stage 2:** **NQR at pickup/doorstep** + **USSD** fallback under EUB. ([Nibss Plc][8])
* **Stage 3:** BNPL, wallet & escrow.
* **Reconciliation:** Webhooks + verification endpoints; downloadable vendor statements.

# Security & Compliance (tightened)

* **NDPA + GAID 2025**: lawful bases, consent logs, DPO contact, RoPA; selective Nigerian data residency. ([Nigeria Data Protection Commission][4])
* **App security:** JWT with rotation, device risk scoring, rate limits, 2FA on payouts.
* **KYC & fraud:** staged limits; courier checks; geofenced claim radius.

# Roadmap (shifted for marketplace logistics)

**Phase 0 (2 weeks):** IA, wireframes (buyer/vendor/transporter/admin), language pack, brand kit (neutral + cultural skins + agent hero).
**Phase 1 (6–8 weeks):** Buyer/vendor flows, **Transporter Job Board**, multi-store surcharge UX, real-time jobs via WebSocket, map tracking MVP, Paystack/Flutterwave test mode, NDPA baseline.
**Phase 2 (4–6 weeks):** NQR + USSD, transporter background GPS + turn-by-turn, dispute center, surge/bonus logic.
**Phase 3 (ongoing):** Vendor analytics, promos, loyalty, BNPL, pickup points/neighborhood captains.

# What I’ll deliver next (same promise, tuned to your model)

1. **Screen-by-screen wire map** incl. transporter flows + Jobs Dashboard states.
2. **API contracts** (orders, jobs, tracking pings, payments init/verify, POD).
3. **Copy deck v1 (5 languages)** for core screens.
4. **Brand kit v1**: neutral logo pack + cultural skins + **field-agent hero** brief.

If you want me to proceed, I’ll drop the **wire map + API contracts** first, then we generate the **agent hero** artwork and localize the strings.

[1]: https://gs.statcounter.com/android-version-market-share/mobile-tablet/nigeria?utm_source=chatgpt.com "Mobile & Tablet Android Version Market Share Nigeria"
[2]: https://insights.techcabal.com/report/nigeria-payments-report-2025/?utm_source=chatgpt.com "Nigeria Payments Report 2025 - TechCabal Insights"
[3]: https://paystack.com/docs/developer-tools/android-sdk/?utm_source=chatgpt.com "Android SDK | Paystack Developer Documentation"
[4]: https://ndpc.gov.ng/wp-content/uploads/2025/07/NDP-ACT-GAID-2025-MARCH-20TH.pdf?utm_source=chatgpt.com "NDP-ACT-GAID-2025-MARCH-20TH.pdf"
[5]: https://proshare.co/articles/nigerias-e-payment-transactions-grew-by-17.7-year-on-year-yoy-in-q1-2025?category=ECOMMERCE&classification=Read&menu=Technology&utm_source=chatgpt.com "Nigeria's E-Payment Transactions Grew by 17.7% Year ... - Proshare"
[6]: https://developers.google.com/maps/documentation/android-sdk/maps-compose?utm_source=chatgpt.com "Maps Compose Library | Maps SDK for Android"
[7]: https://jitpack.io/p/Flutterwave/rave-android?utm_source=chatgpt.com "Flutterwave / AndroidSDK Download"
[8]: https://nibss-plc.com.ng/nqr/?utm_source=chatgpt.com "NQR"
