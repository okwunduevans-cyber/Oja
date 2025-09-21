# OJA Delivery Plan – Actionable Task Breakdown

This backlog converts the production blueprint into execution-ready tasks. Each task includes the concrete outcomes, dependencies, and validation steps needed to ship the feature without regressions.

## Execution Order

All workstreams move strictly in sequence. Stage 0 is a hard gate before any feature code ships. Stage A begins only after the Stage 0 exit criteria are signed off. Subsequent stages (B → E) and cross-cutting tracks start once Stage A is live in staging with the ₦100 payment validation completed.

For each numbered task below, execute the subtasks top-to-bottom; do not parallelise across stages unless their prerequisites explicitly allow it.

## Stage 0 – Foundation & Guardrails
- [ ] **S0.1 – Establish compliance & security baseline**
  - **Preparation**
    - [ ] Appoint named Data Protection Officer with documented responsibilities and escalation contacts.
    - [ ] Draft and approve NDPA processing register covering buyers, vendors, couriers, and logistics partners.
    - [ ] Produce incident-response playbooks (detection, containment, notification) that satisfy the 72-hour breach rule.
  - **Third-party agreements**
    - [ ] Obtain signed DPAs/processor terms from Paystack, Flutterwave, Sendbox, and Kwik; archive in compliance drive.
    - [ ] Review PCI-DSS obligations for payment processors and document scope boundaries.
  - **Validation**
    - [ ] Run tabletop breach-response dry run with legal + engineering; capture follow-up actions.
    - [ ] Sign off compliance checklist with legal counsel (stored in shared knowledge base).
- [ ] **S0.2 – Stand up platform infrastructure**
  - **Environments & tooling**
    - [ ] Create Google Cloud project with billing, enable Maps, Firebase, Secret Manager, Cloud SQL, Memorystore, and Cloud Build APIs.
    - [ ] Provision Terraform (or Pulumi) repo with CI enforcement; check in base infrastructure definitions.
    - [ ] Deploy managed Postgres (staging + prod) and Redis (session/cache) instances via IaC.
  - **Application scaffold**
    - [ ] Bootstrap backend monorepo (Kotlin/Spring or Node/Nest) with modules for auth, catalog, orders, payments, logistics.
    - [ ] Configure CI (lint, unit tests, integration stubs) with required checks on merge to `main`.
    - [ ] Set up Firebase project, upload SHA-1/SHA-256 certs, enable FCM, Crashlytics, Analytics.
  - **Validation**
    - [ ] Execute sample pipeline run (build + tests) and confirm green.
    - [ ] Deploy hello-world service to staging and verify health endpoint from monitoring location.
- [ ] **S0.3 – Implement IAM & secrets management**
  - **Identity & access**
    - [ ] Create service accounts per microservice with least-privilege IAM roles (database, storage, Firebase, Maps).
    - [ ] Stand up auth service issuing short-lived JWTs with refresh-token rotation and device binding.
  - **Secrets & auditability**
    - [ ] Store processor keys, JWT secrets, DB credentials in Secret Manager (or Vault) with automated rotation policies.
    - [ ] Enable Cloud Audit Logs (Admin + Data Access) and forward to SIEM for retention.
  - **Validation**
    - [ ] Perform security review covering token issuance, rotation schedule, and failure handling.
    - [ ] Run automated rotation drill (rotate key, validate services continue working, record proof).

## Stage A – Payments & Address Correctness
- [ ] **S1.1 – Ship user auth & catalog APIs**
  - **Auth implementation**
    - [ ] Model user entities with roles (buyer/vendor/courier) and verification state.
    - [ ] Implement `/auth/signup`, `/auth/login`, `/auth/otp/verify`, `/me`, `/me` deletion; integrate SMS/Email OTP provider.
    - [ ] Add rate limiting and device fingerprint hints to login/OTP flows.
  - **Catalog services**
    - [ ] Design Postgres schema for stores, products, variants, categories, inventory.
    - [ ] Build `/products`, `/stores/{id}`, `/stores/{id}` update endpoints with pagination and filtering.
    - [ ] Seed staging data via fixtures for QA smoke.
  - **Validation**
    - [ ] Contract/integration tests for auth + catalog edge cases (duplicate signup, out-of-stock, pagination bounds).
    - [ ] Android staging build switched to real endpoints for browse/login smoke test.
- [ ] **S1.2 – Implement order & payment orchestration**
  - **Order drafting**
    - [ ] Define order/cart schema capturing multi-store breakdown, fees, discounts, and courier tips.
    - [ ] Implement `/orders` to validate cart, compute totals, reserve inventory, and issue draft order IDs.
  - **Payment flows**
    - [ ] Integrate Paystack init endpoint returning access code and persist awaiting-payment state.
    - [ ] Integrate Flutterwave init endpoint returning transaction reference; ensure currency and metadata alignment.
    - [ ] Build webhook receivers with signature verification, idempotency store, and failure retry policy.
  - **Validation**
    - [ ] Automate ₦100 end-to-end test (init payment → webhook → order marked paid → receipt stored).
    - [ ] Generate finance reconciliation report verifying totals vs processor dashboard.
- [ ] **S1.3 – Deliver address service**
  - **APIs & storage**
    - [ ] Create `addresses` table storing Place ID, formatted components, lat/lng, consent timestamp, and user linkage.
    - [ ] Implement CRUD endpoints plus default-address selection and delivery instructions field.
  - **Maps cost control**
    - [ ] Server-side geocode caching with TTL + manual invalidation; persist session tokens for audit.
    - [ ] Budget alerts on Maps project; set quota thresholds for Autocomplete/Geocoding APIs.
  - **Validation**
    - [ ] Integration tests ensuring repeated Place ID fetch hits cache not API.
    - [ ] Security review verifying NDPA consent logging and retention policy.
- [ ] **S1.4 – Replace Android in-memory data layer**
  - **Networking layer**
    - [ ] Introduce Retrofit interface definitions for auth, catalog, orders, payments, addresses, logistics placeholders.
    - [ ] Configure Moshi/Kotlinx serialization, logging interceptors, and error adapter for API -> domain mapping.
    - [ ] Wire repositories through Hilt modules replacing existing simulated `Repo` implementations.
  - **Resilience**
    - [ ] Add offline caching strategy (Room or in-memory with TTL) for catalog and saved addresses.
    - [ ] Implement standardized error handling + retry/backoff for payment/address calls.
  - **Validation**
    - [ ] Instrumented tests covering login, product browse, cart creation, checkout initiation.
    - [ ] QA script verifying cart extra-fee prompts unchanged.
- [ ] **S1.5 – Launch payments & address UI**
  - **Payments UX**
    - [ ] Expand checkout flow with payment-method selector (Paystack/Flutterwave/COD flag) and order summary screen.
    - [ ] Integrate Paystack & Flutterwave SDKs: handle success, failure, cancellation, and deep-link return.
    - [ ] Persist card tokens where processors support it (vaulted by backend) and surface last-used payment info.
  - **Address UX**
    - [ ] Build `AddressPickerScreen` combining Places Autocomplete search, map pin confirmation, saved addresses list, and NDPA consent copy.
    - [ ] Add delivery notes, language picker during onboarding, and localization (EN, Pidgin, Yoruba, Hausa, Igbo).
  - **Validation**
    - [ ] Manual QA for success/failure/cancel paths; ensure analytics + Crashlytics events fire.
    - [ ] Accessibility review (TalkBack, dynamic type) for checkout and address flows.

## Stage B – Real-time Logistics
- [ ] **S2.1 – Integrate Sendbox logistics backend**
  - **Backend foundations**
    - [ ] Define `LogisticsProvider` interface and data contracts for quotes, bookings, and tracking events.
    - [ ] Implement Sendbox REST adapter (auth, quote, create, track) with retries and exponential backoff.
    - [ ] Hook paid orders to auto-book shipments when serviceable and persist shipment IDs/status timeline.
  - **Realtime streaming**
    - [ ] Publish order timeline + courier location updates to `/ws/orders/{id}` WebSocket channel.
    - [ ] Emit logistics events to analytics + audit logs for SLA monitoring.
  - **Validation**
    - [ ] Load test Sendbox adapter + WebSocket hub; confirm p95 latency <300 ms and zero dropped events under expected load.
    - [ ] QA script verifying failed bookings raise actionable alerts.
- [ ] **S2.2 – Android logistics data layer**
  - **Data plumbing**
    - [ ] Create `LogisticsRepository` handling REST booking status and WebSocket subscription lifecycle.
    - [ ] Implement foreground service binding with coroutine scope for receiving track events while in-app/background.
    - [ ] Add reconnection/backoff strategy and offline cache for last-known courier location + ETA.
  - **Validation**
    - [ ] Instrumented tests simulating WebSocket reconnect, offline resume, and out-of-order events.
    - [ ] QA verifying job claims update in UI within 2 refresh cycles.
- [ ] **S2.3 – Tracking & transporter UI upgrades**
  - **Buyer tracking**
    - [ ] Render live polyline + ETA badge in order detail; show event timeline with statuses + timestamps.
    - [ ] Surface alerts for delays/cancelled shipments with support CTA.
  - **Courier experience**
    - [ ] Implement `/ws/jobs` feed consumption with filters by vehicle type and service area.
    - [ ] Build persistent notification + background location cadence (15–30 s) with battery safeguards.
  - **Validation**
    - [ ] Lagos/Abuja field test ensuring updates every 15–30 s without ANR/battery drain beyond thresholds.
    - [ ] Play Store background location compliance checklist signed off.

## Stage C – Vendor Tooling
- [ ] **S3.1 – Vendor onboarding workflow**
  - **Identity capture**
    - [ ] Implement onboarding API capturing business profile, CAC (optional), KYC-lite bank verification via provider (e.g., Paystack Resolve).
    - [ ] Collect store address with map confirmation + geocode; persist SLA/fee preferences.
  - **State management**
    - [ ] Build onboarding state machine (draft → pending review → approved/blocked) with audit log.
    - [ ] Add admin review tools (internal) for compliance approval.
  - **Validation**
    - [ ] Test vendor flows in staging covering approval/rejection; ensure notifications delivered.
- [ ] **S3.2 – Product & inventory management backend**
  - **Product services**
    - [ ] Design product, variant, and inventory tables with stock reservations and low-stock alerts.
    - [ ] Implement CRUD endpoints using signed upload URLs (Cloud Storage/S3) with callbacks on completion.
    - [ ] Build bulk CSV import pipeline (queue + worker) and image ZIP ingestion with SKU mapping.
  - **Validation**
    - [ ] Load test (200 images/100 products) verifying throughput, retry handling, and CDN resizing.
    - [ ] Security review confirming vendor scoping and storage permissions.
- [ ] **S3.3 – Android vendor experience**
  - **Feature build**
    - [ ] Implement Compose screens for product list/detail/edit/create with WorkManager upload queue + progress UI.
    - [ ] Add bulk import entry, CSV template download, and conflict resolution UI for failed rows.
    - [ ] Build vendor dashboard summarizing sales KPIs, low-stock alerts, and new orders feed.
  - **Validation**
    - [ ] Instrumented tests covering upload pause/resume, offline drafts, and conflict handling.
    - [ ] Manual QA verifying dashboard metrics align with backend reports.

## Stage D – Courier Operations
- [ ] **S4.1 – Harden courier job workflows backend**
  - **Job lifecycle**
    - [ ] Implement atomic job-claim endpoint with optimistic locking + conflict metrics.
    - [ ] Provide navigation links (Google Maps/Waze) and SLA metadata in job payloads.
    - [ ] Track status transitions (`pickup_arrived`, `picked`, `in_transit`, `delivered`) with proof-of-delivery storage (photos/signature).
  - **Financials**
    - [ ] Capture COD reconciliation workflow and earnings ledger with payout schedule.
  - **Validation**
    - [ ] Concurrency stress test (≥50 simultaneous claims) ensuring fairness + no duplicate assignment.
    - [ ] Finance QA verifying COD settlements reconcile with processor payouts.
- [ ] **S4.2 – Courier Android service & tools**
  - **Mobile experience**
    - [ ] Ship courier foreground service for navigation + status updates; maintain persistent notification per Play policy.
    - [ ] Build POD capture (camera, gallery fallback, signature pad) with upload retry + compression.
    - [ ] Add dispute submission flow with evidence upload and job board filters (vehicle type/workload).
  - **Validation**
    - [ ] Battery/performance profiling on low-end + mid-range devices; ensure background location rationale screens pass review.
    - [ ] Field QA verifying POD uploads appear in backend ledger promptly.

## Stage E – Trust, Support, Analytics
- [ ] **S5.1 – Disputes, returns, and support tooling**
  - **Backend & tooling**
    - [ ] Implement dispute ticketing with SLA timers, evidence attachments, escalation workflow, and refund triggers.
    - [ ] Build admin dashboard for support team with queue views, canned responses, and webhook notifications to Slack/Email.
  - **Validation**
    - [ ] End-to-end dispute simulation from buyer submission to resolution; capture training material + runbook sign-off.
- [ ] **S5.2 – Privacy center & NDPA controls**
  - **Compliance features**
    - [ ] Implement data export/delete endpoints with asynchronous job processing and audit logging.
    - [ ] Build breach-notification workflow with timer tracking and templated communication.
    - [ ] Add in-app privacy center for consent management, notification preferences, and data subject requests.
  - **Validation**
    - [ ] Dry run data subject access/deletion request verifying completion within statutory window.
    - [ ] Legal sign-off on breach-notification templates and consent UX.
- [ ] **S5.3 – Analytics & observability**
  - **Data plumbing**
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

