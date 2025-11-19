# OJA Delivery Plan – Actionable Task Breakdown

This backlog converts the production blueprint into execution-ready tasks. Each task includes the concrete outcomes, dependencies, and validation steps needed to ship the feature without regressions.

## Baseline & context

### Current app snapshot (what already exists)
- Jetpack Compose single-module client backed by in-memory `Repo` implementations for catalog, cart, checkout, and courier flows.
- Buyer experience covers browse → cart → checkout hand-off with simulated logistics timeline once a Google Maps key is provided.
- Vendor onboarding, transporter signup, and payments screens are present as stubs awaiting real backend contracts.

### Hard constraints from Plan 2
- Payments rely on backend-initialised Paystack/Flutterwave flows before invoking the Android SDK hand-off.
- Google Maps APIs require billing, quota governance, autocomplete session reuse, and cached geocodes to keep spend predictable.
- Android background location must comply with Play policy: foreground-first request, persistent notification, and cadence between 15–30 seconds.
- NDPA compliance requires a Data Protection Officer, processor DPAs, 72-hour breach notification workflows, and subject-rights tooling.
- Logistics phase starts with Sendbox, followed by Kwik via a pluggable `LogisticsProvider` abstraction.

## Production blueprint (no-gap delivery plan)

## Launch-readiness & view-testing milestones

The table below reframes each delivery stage into testable launch checkpoints so teams know exactly when the Android app can be
opened, exercised, and exposed to real users. Every milestone requires that the listed validation steps be executed and signed
off before moving to the next stage.

| Stage | Focus | App availability | Required validation before advancing |
|-------|-------|------------------|---------------------------------------|
| Stage 0 – Foundation & guardrails | Compliance, processor contracts, infrastructure, IAM | **Internal prototype only.** App may be launched on internal devices using mock data, but no external users or production data. | ✅ Compliance checklist signed, ✅ infrastructure smoke deploys, ✅ security review for IAM/secrets. |
| Stage A – Payments & address correctness | Replace mocks with real auth/catalog/order/payment/address services and SDK integrations | **Closed staging build.** App can be installed by QA to validate welcome, browse, checkout, address picker, and ₦100 live payments end-to-end. | ✅ Contract/integration tests green, ✅ ₦100 payment run recorded, ✅ Android staging smoke (launch → browse → checkout → receipt). |
| Stage B – Real-time logistics | Sendbox integration, live tracking, transporter job board | **Staging w/ logistics pilots.** Launchable to pilot couriers and ops to view live order timelines. | ✅ WebSocket load test (<300 ms p95), ✅ Foreground tracking instrumentation test, ✅ Field drive test covering Lagos & Abuja routes. |
| Stage C – Vendor tooling | Vendor onboarding, catalog management, bulk ingestion | **Extended staging / limited beta.** Vendors can log in and manage catalog via the app. | ✅ Bulk upload stress test (200 images/100 products), ✅ Pen-test storage permissions, ✅ Vendor dashboard UX review on device. |
| Stage D – Courier operations | Proof-of-delivery, earnings, dispute workflows | **Beta-ready.** Courier network can run COD/POD flows on managed devices. | ✅ Concurrency stress (≥50 claims), ✅ Battery/performance profiling on low-end hardware, ✅ POD capture instrumentation. |
| Stage E – Trust, support, analytics | Support center, privacy tooling, analytics, notifications | **Public launch candidate.** Buyers, vendors, couriers can install from store once legal sign-off is complete. | ✅ Dispute lifecycle QA, ✅ Data-subject request drill, ✅ Notification/analytics SLI monitoring live. |

> **Launch gate reminder:** External distribution (Play Console listing or production backend traffic) is blocked until the prior
stage’s validation checklist is complete. Keep the staging build separate from production until Stage E verification concludes.

### Stage 0 – Foundation & guardrails
- **Organisational readiness:** Appoint DPO, document NDPA processing records, and finalise incident-response playbooks before ingesting production data.
- **Processor agreements:** Countersign Paystack, Flutterwave, Sendbox, and Kwik DPAs; enable Google Cloud billing with Maps budget alerts.
- **Platform setup:** Scaffold backend monorepo (Spring Boot or NestJS), configure CI, and provision managed Postgres + Redis through IaC.
- **Security & IAM:** Stand up JWT auth service with refresh rotation, allocate least-privilege service accounts, and centralise secrets in Secret Manager or Vault with audit logging.

### Stage A – Payments & address correctness
- **Backend:** Deliver auth/catalog APIs, `/orders` + `/orders/{id}/pay`, server-side Paystack/Flutterwave initialisation, webhook receivers, and address CRUD with cached geocodes.
- **Android:** Replace simulated repositories with Retrofit/Moshi remotes wired via Hilt, integrate payment SDKs, add `AddressPickerScreen`, and localise onboarding in EN/Pidgin/Yoruba/Hausa/Igbo.
- **Ops & QA:** Run automated contract tests, Android instrumentation for checkout/address, and perform the ₦100 end-to-end payment validation with receipts and NDPA logging.

### Stage B – Real-time logistics
- **Backend:** Introduce `LogisticsProvider`, ship Sendbox adapter, auto-book shipments on paid orders, and expose `/ws/orders/{id}` WebSocket timelines.
- **Android:** Build `LogisticsRepository`, foreground tracking service with persistent notification, live order detail polyline/ETA, and transporter job feed via `/ws/jobs`.
- **Ops & QA:** Load-test WebSocket hub (<300 ms p95) and confirm Lagos/Abuja field updates every 15–30 seconds without battery regressions.

### Stage C – Vendor tooling
- **Backend:** Vendor onboarding workflow with KYC-lite bank verification, geo-coded stores, unlimited product uploads, signed URLs, and bulk CSV/ZIP ingestion queues.
- **Android:** Compose screens for vendor dashboard, product CRUD with WorkManager upload queue, bulk import entry points, and conflict resolution UX.
- **Ops & QA:** Stress-test 200 images/100 products, pen-test storage permissions, and ensure dashboards reflect backend KPIs.

### Stage D – Courier operations
- **Backend:** Atomic job-claim endpoint, navigation links, job status lifecycle with POD storage, COD reconciliation, and earnings ledger.
- **Android:** Courier foreground service with policy-compliant location cadence, POD capture (camera + signature), dispute submission, and job filters.
- **Ops & QA:** Concurrency stress testing (≥50 simultaneous claims) plus battery/performance profiling on low-end devices.

### Stage E – Trust, support, analytics
- **Backend:** Dispute ticketing with evidence uploads, NDPA-compliant data export/delete flows, breach-notification tracking, and analytics pipeline (BigQuery/Amplitude).
- **Android:** Privacy center, consent controls, saved cards, support entry points, Crashlytics toggles, and FCM topic management with notification grouping.
- **Ops & QA:** Validate dispute lifecycle end-to-end, run data-subject request drills, and monitor SLIs (API p95, WebSocket uptime, webhook ack <2 s).

### Cross-cutting tracks
- **Logistics expansion:** Add Kwik adapter to provider abstraction with geo/SLA/cost routing and A/B cost analysis.
- **Fraud & risk:** Implement COD limits, velocity checks, disposable email blocking, device fingerprinting, and Trust & Safety dashboards.
- **Maps & cost optimisation:** Cache geocodes, reuse autocomplete sessions, batch directions calls, and enforce Maps budget alerts.
- **Localization & performance:** Maintain translation coverage, support landmark-based addresses and driver notes, throttle background work on low battery, and lazy-load imagery.

## Task backlog (execution-sized work packages)
1. Stand up compliant backend foundation (Stage 0 outputs).
2. Replace Android in-memory repositories with production APIs (Stage A networking).
3. Deliver Stage A payment & address flows and ₦100 validation.
4. Launch Stage B logistics realtime loop with Sendbox adapter and Android tracking service.
5. Complete Stage C vendor tooling across backend and Android.
6. Polish Stage D courier experience with POD, earnings, and concurrency resilience.
7. Roll out Stage E trust/support/analytics capabilities.
8. Embed risk/compliance monitoring plus Maps/Fraud cross-cutting tracks.

## Detailed staged task breakdown

## Execution Order

All workstreams move strictly in sequence. Stage 0 is a hard gate before any feature code ships. Stage A begins only after the Stage 0 exit criteria are signed off. Subsequent stages (B → E) and cross-cutting tracks start once Stage A is live in staging with the ₦100 payment validation completed.

For each numbered task below, execute the subtasks top-to-bottom; do not parallelise across stages unless their prerequisites explicitly allow it.

## Stage 0 – Foundation & Guardrails
- [ ] **S0.1 – Establish compliance & security baseline**
  - **Outcomes**
    - Data Protection Officer assigned with NDPA responsibilities documented.
    - NDPA processing records completed for buyers, vendors, couriers, and logistics partners.
    - Incident-response playbooks approved for the NDPA 72-hour breach rule.
    - Processor DPAs with Paystack, Flutterwave, Sendbox, and Kwik countersigned.
  - **Dependencies**
    - Legal review of compliance artefacts.
    - Executed vendor contracts covering data processing.
  - **Task checklist**
    - *Preparation*
      - [ ] Appoint named Data Protection Officer with documented responsibilities and escalation contacts.
      - [ ] Draft and approve NDPA processing register covering buyers, vendors, couriers, and logistics partners.
      - [ ] Produce incident-response playbooks (detection, containment, notification) that satisfy the 72-hour breach rule.
    - *Third-party agreements*
      - [ ] Obtain signed DPAs/processor terms from Paystack, Flutterwave, Sendbox, and Kwik; archive in compliance drive.
      - [ ] Review PCI-DSS obligations for payment processors and document scope boundaries.
  - **Validation**
    - [ ] Run tabletop breach-response dry run with legal + engineering; capture follow-up actions.
    - [ ] Sign off compliance checklist with legal counsel (stored in shared knowledge base).
- [ ] **S0.2 – Stand up platform infrastructure**
  - **Outcomes**
    - Backend monorepo scaffolded (Spring Boot or NestJS) with CI (build, test, lint) enforced.
    - Managed Postgres (staging + prod) and Redis provisioned with infrastructure-as-code.
    - Firebase project configured with Android SHA certs for FCM, Crashlytics, and Analytics.
  - **Dependencies**
    - Cloud accounts with billing enabled.
    - Infrastructure-as-code repository with CI hooks.
  - **Task checklist**
    - *Environments & tooling*
      - [ ] Create Google Cloud project with billing, enable Maps, Firebase, Secret Manager, Cloud SQL, Memorystore, and Cloud Build APIs.
      - [ ] Provision Terraform (or Pulumi) repo with CI enforcement; check in base infrastructure definitions.
      - [ ] Deploy managed Postgres (staging + prod) and Redis (session/cache) instances via IaC.
    - *Application scaffold*
      - [ ] Bootstrap backend monorepo (Kotlin/Spring or Node/Nest) with modules for auth, catalog, orders, payments, logistics.
      - [ ] Configure CI (lint, unit tests, integration stubs) with required checks on merge to `main`.
      - [ ] Set up Firebase project, upload SHA-1/SHA-256 certs, enable FCM, Crashlytics, Analytics.
  - **Validation**
    - [ ] Execute sample pipeline run (build + tests) and confirm green.
    - [ ] Deploy hello-world service to staging and verify health endpoint from monitoring location.
- [ ] **S0.3 – Implement IAM & secrets management**
  - **Outcomes**
    - JWT auth service issues short-lived access tokens with refresh rotation.
    - Service accounts follow least-privilege access to databases, storage, Firebase, and Maps.
    - Secrets stored in encrypted vault with audit logging baseline enabled.
  - **Dependencies**
    - Stage S0.2 environments deployed and reachable.
  - **Task checklist**
    - *Identity & access*
      - [ ] Create service accounts per microservice with least-privilege IAM roles (database, storage, Firebase, Maps).
      - [ ] Stand up auth service issuing short-lived JWTs with refresh-token rotation and device binding.
    - *Secrets & auditability*
      - [ ] Store processor keys, JWT secrets, DB credentials in Secret Manager (or Vault) with automated rotation policies.
      - [ ] Enable Cloud Audit Logs (Admin + Data Access) and forward to SIEM for retention.
  - **Validation**
    - [ ] Perform security review covering token issuance, rotation schedule, and failure handling.
    - [ ] Run automated rotation drill (rotate key, validate services continue working, record proof).

## Stage A – Payments & Address Correctness
- [ ] **S1.1 – Ship user auth & catalog APIs**
  - **Outcomes**
    - `/auth/signup`, `/auth/login`, `/auth/otp/verify`, `/me` endpoints live.
    - Catalog endpoints for stores and products with pagination and filtering delivered.
  - **Dependencies**
    - Stage S0.2 backend scaffold and CI in place.
  - **Task checklist**
    - *Auth implementation*
      - [ ] Model user entities with roles (buyer/vendor/courier) and verification state.
      - [ ] Implement `/auth/signup`, `/auth/login`, `/auth/otp/verify`, `/me`, `/me` deletion; integrate SMS/Email OTP provider.
      - [ ] Add rate limiting and device fingerprint hints to login/OTP flows.
    - *Catalog services*
      - [ ] Design Postgres schema for stores, products, variants, categories, inventory.
      - [ ] Build `/products`, `/stores/{id}`, `/stores/{id}` update endpoints with pagination and filtering.
      - [ ] Seed staging data via fixtures for QA smoke.
  - **Validation**
    - [ ] Contract/integration tests for auth + catalog edge cases (duplicate signup, out-of-stock, pagination bounds).
    - [ ] Android staging build switched to real endpoints for browse/login smoke test.
- [ ] **S1.2 – Implement order & payment orchestration**
  - **Outcomes**
    - `/orders` and `/orders/{id}/pay` endpoints orchestrate drafts and processor hand-offs.
    - Paystack and Flutterwave payment initiation plus webhook receivers with idempotency keys in production.
  - **Dependencies**
    - Stage S1.1 auth and catalog APIs available.
  - **Task checklist**
    - *Order drafting*
      - [ ] Define order/cart schema capturing multi-store breakdown, fees, discounts, and courier tips.
      - [ ] Implement `/orders` to validate cart, compute totals, reserve inventory, and issue draft order IDs.
    - *Payment flows*
      - [ ] Integrate Paystack init endpoint returning access code and persist awaiting-payment state.
      - [ ] Integrate Flutterwave init endpoint returning transaction reference; ensure currency and metadata alignment.
      - [ ] Build webhook receivers with signature verification, idempotency store, and failure retry policy.
  - **Validation**
    - [ ] Automate ₦100 end-to-end test (init payment → webhook → order marked paid → receipt stored).
    - [ ] Generate finance reconciliation report verifying totals vs processor dashboard.
- [ ] **S1.3 – Deliver address service**
  - **Outcomes**
    - Address CRUD API accepts Google Place IDs with server-side cache and consent logging.
  - **Dependencies**
    - Maps billing enabled and Stage S0.3 security baseline active.
  - **Task checklist**
    - *APIs & storage*
      - [ ] Create `addresses` table storing Place ID, formatted components, lat/lng, consent timestamp, and user linkage.
      - [ ] Implement CRUD endpoints plus default-address selection and delivery instructions field.
    - *Maps cost control*
      - [ ] Server-side geocode caching with TTL + manual invalidation; persist session tokens for audit.
      - [ ] Budget alerts on Maps project; set quota thresholds for Autocomplete/Geocoding APIs.
  - **Validation**
    - [ ] Integration tests ensuring repeated Place ID fetch hits cache not API.
    - [ ] Security review verifying NDPA consent logging and retention policy.
- [ ] **S1.4 – Replace Android in-memory data layer**
  - **Outcomes**
    - Retrofit/Moshi remote repositories injected via Hilt for catalog, cart, orders, payments, addresses, and logistics placeholders.
    - Offline-safe error handling for buyer checkout flows.
  - **Dependencies**
    - Stable Stage S1.1–S1.3 APIs.
  - **Task checklist**
    - *Networking layer*
      - [ ] Introduce Retrofit interface definitions for auth, catalog, orders, payments, addresses, logistics placeholders.
      - [ ] Configure Moshi/Kotlinx serialization, logging interceptors, and error adapter for API → domain mapping.
      - [ ] Wire repositories through Hilt modules replacing existing simulated `Repo` implementations.
    - *Resilience*
      - [ ] Add offline caching strategy (Room or in-memory with TTL) for catalog and saved addresses.
      - [ ] Implement standardized error handling + retry/backoff for payment/address calls.
  - **Validation**
    - [ ] Instrumented tests covering login, product browse, cart creation, checkout initiation.
    - [ ] QA script verifying cart extra-fee prompts unchanged.
- [ ] **S1.5 – Launch payments & address UI**
  - **Outcomes**
    - Payments screen integrates Paystack & Flutterwave SDKs with selector and deep-link handling.
    - Address picker delivers autocomplete, pin drop, saved list, and localized onboarding.
  - **Dependencies**
    - Stage S1.4 remote repositories live in app build.
  - **Task checklist**
    - *Payments UX*
      - [ ] Expand checkout flow with payment-method selector (Paystack/Flutterwave/COD flag) and order summary screen.
      - [ ] Integrate Paystack & Flutterwave SDKs: handle success, failure, cancellation, and deep-link return.
      - [ ] Persist card tokens where processors support it (vaulted by backend) and surface last-used payment info.
    - *Address UX*
      - [ ] Build `AddressPickerScreen` combining Places Autocomplete search, map pin confirmation, saved addresses list, and NDPA consent copy.
      - [ ] Add delivery notes, language picker during onboarding, and localization (EN, Pidgin, Yoruba, Hausa, Igbo).
  - **Validation**
    - [ ] Manual QA for success/failure/cancel paths; ensure analytics + Crashlytics events fire.
    - [ ] Accessibility review (TalkBack, dynamic type) for checkout and address flows.

## Stage B – Real-time Logistics
- [ ] **S2.1 – Integrate Sendbox logistics backend**
  - **Outcomes**
    - `LogisticsProvider` abstraction with Sendbox adapter for quote, create, and track workflows.
    - Paid orders auto-book shipments; `/ws/orders/{id}` streams courier timeline and location.
  - **Dependencies**
    - Stage A order lifecycle available.
  - **Task checklist**
    - *Backend foundations*
      - [ ] Define `LogisticsProvider` interface and data contracts for quotes, bookings, and tracking events.
      - [ ] Implement Sendbox REST adapter (auth, quote, create, track) with retries and exponential backoff.
      - [ ] Hook paid orders to auto-book shipments when serviceable and persist shipment IDs/status timeline.
    - *Realtime streaming*
      - [ ] Publish order timeline + courier location updates to `/ws/orders/{id}` WebSocket channel.
      - [ ] Emit logistics events to analytics + audit logs for SLA monitoring.
  - **Validation**
    - [ ] Load test Sendbox adapter + WebSocket hub; confirm p95 latency <300 ms and zero dropped events under expected load.
    - [ ] QA script verifying failed bookings raise actionable alerts.
- [ ] **S2.2 – Android logistics data layer**
  - **Outcomes**
    - `LogisticsRepository` consumes REST + WebSocket with reconnection/backoff.
    - Background refresh policies respect Android rules with persistent notification.
  - **Dependencies**
    - Stage S2.1 WebSocket endpoints.
  - **Task checklist**
    - *Data plumbing*
      - [ ] Create `LogisticsRepository` handling REST booking status and WebSocket subscription lifecycle.
      - [ ] Implement foreground service binding with coroutine scope for receiving track events while in-app/background.
      - [ ] Add reconnection/backoff strategy and offline cache for last-known courier location + ETA.
  - **Validation**
    - [ ] Instrumented tests simulating WebSocket reconnect, offline resume, and out-of-order events.
    - [ ] QA verifying job claims update in UI within 2 refresh cycles.
- [ ] **S2.3 – Tracking & transporter UI upgrades**
  - **Outcomes**
    - Buyer order detail renders live polyline and ETA badges.
    - Transporter job list streams via `/ws/jobs` with policy-compliant location cadence.
  - **Dependencies**
    - Stage S2.2 repository wiring inside Android app.
  - **Task checklist**
    - *Buyer tracking*
      - [ ] Render live polyline + ETA badge in order detail; show event timeline with statuses + timestamps.
      - [ ] Surface alerts for delays/cancelled shipments with support CTA.
    - *Courier experience*
      - [ ] Implement `/ws/jobs` feed consumption with filters by vehicle type and service area.
      - [ ] Build persistent notification + background location cadence (15–30 s) with battery safeguards.
  - **Validation**
    - [ ] Lagos/Abuja field test ensuring updates every 15–30 s without ANR/battery drain beyond thresholds.
    - [ ] Play Store background location compliance checklist signed off.

## Stage C – Vendor Tooling
- [ ] **S3.1 – Vendor onboarding workflow**
  - **Outcomes**
    - KYC-lite onboarding with bank verification, store geocoding, and status transitions delivered.
  - **Dependencies**
    - Stage A authentication stack and compliance approvals.
  - **Task checklist**
    - *Identity capture*
      - [ ] Implement onboarding API capturing business profile, CAC (optional), KYC-lite bank verification via provider (e.g., Paystack Resolve).
      - [ ] Collect store address with map confirmation + geocode; persist SLA/fee preferences.
    - *State management*
      - [ ] Build onboarding state machine (draft → pending review → approved/blocked) with audit log.
      - [ ] Add admin review tools (internal) for compliance approval.
  - **Validation**
    - [ ] Test vendor flows in staging covering approval/rejection; ensure notifications delivered.
- [ ] **S3.2 – Product & inventory management backend**
  - **Outcomes**
    - CRUD APIs with signed upload URLs, variant support, inventory counts, and bulk CSV import pipeline.
  - **Dependencies**
    - Stage S3.1 vendor identity plus storage bucket access.
  - **Task checklist**
    - *Product services*
      - [ ] Design product, variant, and inventory tables with stock reservations and low-stock alerts.
      - [ ] Implement CRUD endpoints using signed upload URLs (Cloud Storage/S3) with callbacks on completion.
      - [ ] Build bulk CSV import pipeline (queue + worker) and image ZIP ingestion with SKU mapping.
  - **Validation**
    - [ ] Load test (200 images/100 products) verifying throughput, retry handling, and CDN resizing.
    - [ ] Security review confirming vendor scoping and storage permissions.
- [ ] **S3.3 – Android vendor experience**
  - **Outcomes**
    - VendorRepository and Compose screens for product list/detail/create/edit with WorkManager uploads.
    - Bulk import UI with template download and conflict resolution shipped.
  - **Dependencies**
    - Stage S3.2 APIs and Stage A Android networking stack.
  - **Task checklist**
    - *Feature build*
      - [ ] Implement Compose screens for product list/detail/edit/create with WorkManager upload queue + progress UI.
      - [ ] Add bulk import entry, CSV template download, and conflict resolution UI for failed rows.
      - [ ] Build vendor dashboard summarizing sales KPIs, low-stock alerts, and new orders feed.
  - **Validation**
    - [ ] Instrumented tests covering upload pause/resume, offline drafts, and conflict handling.
    - [ ] Manual QA verifying dashboard metrics align with backend reports.

## Stage D – Courier Operations
- [ ] **S4.1 – Harden courier job workflows backend**
  - **Outcomes**
    - Atomic job-claim endpoint with navigation deep links and SLA metadata.
    - Status transitions recorded with proof-of-delivery storage, COD reconciliation, and earnings ledger APIs.
  - **Dependencies**
    - Stage B logistics foundation.
  - **Task checklist**
    - *Job lifecycle*
      - [ ] Implement atomic job-claim endpoint with optimistic locking + conflict metrics.
      - [ ] Provide navigation links (Google Maps/Waze) and SLA metadata in job payloads.
      - [ ] Track status transitions (`pickup_arrived`, `picked`, `in_transit`, `delivered`) with proof-of-delivery storage (photos/signature).
    - *Financials*
      - [ ] Capture COD reconciliation workflow and earnings ledger with payout schedule.
  - **Validation**
    - [ ] Concurrency stress test (≥50 simultaneous claims) ensuring fairness + no duplicate assignment.
    - [ ] Finance QA verifying COD settlements reconcile with processor payouts.
- [ ] **S4.2 – Courier Android service & tools**
  - **Outcomes**
    - Courier foreground service meets Play policy with background location cadence.
    - POD capture, dispute submission, and job board filters available in-app.
  - **Dependencies**
    - Stage S4.1 endpoints plus Stage B Android logistics service.
  - **Task checklist**
    - *Mobile experience*
      - [ ] Ship courier foreground service for navigation + status updates; maintain persistent notification per Play policy.
      - [ ] Build POD capture (camera, gallery fallback, signature pad) with upload retry + compression.
      - [ ] Add dispute submission flow with evidence upload and job board filters (vehicle type/workload).
  - **Validation**
    - [ ] Battery/performance profiling on low-end + mid-range devices; ensure background location rationale screens pass review.
    - [ ] Field QA verifying POD uploads appear in backend ledger promptly.

## Stage E – Trust, Support, Analytics
- [ ] **S5.1 – Disputes, returns, and support tooling**
  - **Outcomes**
    - Ticketing system with evidence uploads, SLA timers, escalation workflow, and refund triggers.
    - Admin dashboard for support with queue management and notifications.
  - **Dependencies**
    - Data from Stages C and D available for dispute context.
  - **Task checklist**
    - *Backend & tooling*
      - [ ] Implement dispute ticketing with SLA timers, evidence attachments, escalation workflow, and refund triggers.
      - [ ] Build admin dashboard for support team with queue views, canned responses, and webhook notifications to Slack/Email.
  - **Validation**
    - [ ] End-to-end dispute simulation from buyer submission to resolution; capture training material + runbook sign-off.
- [ ] **S5.2 – Privacy center & NDPA controls**
  - **Outcomes**
    - Data export/delete APIs, breach notification workflow, and consent/notification preference center.
  - **Dependencies**
    - Stage S0.1 compliance artefacts and Stage A authentication stack.
  - **Task checklist**
    - *Compliance features*
      - [ ] Implement data export/delete endpoints with asynchronous job processing and audit logging.
      - [ ] Build breach-notification workflow with timer tracking and templated communication.
      - [ ] Add in-app privacy center for consent management, notification preferences, and data subject requests.
  - **Validation**
    - [ ] Dry run data subject access/deletion request verifying completion within statutory window.
    - [ ] Legal sign-off on breach-notification templates and consent UX.
- [ ] **S5.3 – Analytics & observability**
  - **Outcomes**
    - BigQuery/Amplitude pipeline with Crashlytics toggles, FCM topic architecture, and monitoring dashboards.
  - **Dependencies**
    - Event emissions from earlier stages.
  - **Task checklist**
    - *Data plumbing*
      - [ ] Deploy event pipeline (BigQuery or Amplitude) with versioned schema, ingestion tests, and retention policy.
      - [ ] Configure Crashlytics toggles, custom keys, and release health dashboards.
      - [ ] Architect FCM topics (per order, per courier) with notification grouping + rate limits.
  - **Validation**
    - [ ] Monitoring dashboards for API p95, WebSocket uptime, webhook ack <2 s; alerting integrated with on-call rotation.
    - [ ] Analytics QA verifying funnels match product expectations.

## Cross-cutting Tracks
- [ ] **X1 – Logistics partner expansion**: Add Kwik adapter behind provider abstraction, implement routing rules by geo/SLA/cost, run A/B cost analysis.
- [ ] **X2 – Fraud & risk controls**: COD limits, velocity checks, disposable email blocking, privacy-safe device fingerprinting, Trust & Safety dashboard.
- [ ] **X3 – Maps & cost optimisation**: Cache geocodes, enforce Autocomplete session token reuse, batch Directions calls, configure budget alerts.
- [ ] **X4 – Localization & performance**: Maintain translation coverage, landmark-based addresses, driver notes, low-battery throttling, lazy image loading optimizations.

