# OJA Delivery Plan – Actionable Task Breakdown

This backlog converts the production blueprint into execution-ready tasks. Each task includes the concrete outcomes, dependencies, and validation steps needed to ship the feature without regressions.

## Stage 0 – Foundation & Guardrails
- [ ] **S0.1 – Establish compliance & security baseline**
  - Outcomes: Data Protection Officer assigned, NDPA processing records documented, incident-response playbooks approved, processor DPAs (Paystack, Flutterwave, Sendbox, Kwik) countersigned.
  - Dependencies: Legal review, vendor contracts.
  - Validation: Compliance checklist signed off; breach-response dry run completed.
- [ ] **S0.2 – Stand up platform infrastructure**
  - Outcomes: Backend monorepo scaffolded (Spring Boot or NestJS), CI pipeline (build + test + lint) active, staging/prod environments with managed Postgres + Redis, Firebase project configured with SHA certs.
  - Dependencies: Cloud accounts with billing; infra-as-code repo.
  - Validation: CI green on sample commit; staging deployment health check responding.
- [ ] **S0.3 – Implement IAM & secrets management**
  - Outcomes: JWT auth service with refresh token rotation, least-privilege service accounts, encrypted secrets store, audit logging baseline.
  - Dependencies: S0.2 environments.
  - Validation: Security review passes; automated rotation verified.

## Stage A – Payments & Address Correctness
- [ ] **S1.1 – Ship user auth & catalog APIs**
  - Outcomes: `/auth/signup`, `/auth/login`, `/auth/otp/verify`, `/me` endpoints; catalog endpoints for stores/products with pagination.
  - Dependencies: S0.2 backend scaffold.
  - Validation: Contract tests verifying happy/edge cases; Android consumes staging endpoints.
- [ ] **S1.2 – Implement order & payment orchestration**
  - Outcomes: `/orders`, `/orders/{id}/pay`, Paystack/Flutterwave init flows, webhook receivers with idempotency keys.
  - Dependencies: S1.1 auth/catalog.
  - Validation: ₦100 end-to-end flow succeeds; webhook-driven status update recorded.
- [ ] **S1.3 – Deliver address service**
  - Outcomes: Address CRUD API accepting Google Place IDs, server-side geocode cache, NDPA consent logging.
  - Dependencies: Maps billing enabled, S0.3 security.
  - Validation: Integration tests confirm caching; Android saved-address sync works.
- [ ] **S1.4 – Replace Android in-memory data layer**
  - Outcomes: Retrofit/Moshi remote repositories injected via Hilt for catalog, cart, orders, payments, addresses; offline-safe error handling.
  - Dependencies: S1.1–S1.3 APIs stable.
  - Validation: Instrumented tests for sign-in → checkout; manual regression on cart extra-fee prompts.
- [ ] **S1.5 – Launch payments & address UI**
  - Outcomes: Payments screen with Paystack & Flutterwave SDK hand-off, AddressPickerScreen with autocomplete + pin drop + saved list, localized onboarding flow.
  - Dependencies: S1.4 remote repositories.
  - Validation: QA script covering payment success, failure, cancellation, and address capture.

## Stage B – Real-time Logistics
- [ ] **S2.1 – Integrate Sendbox logistics backend**
  - Outcomes: `LogisticsProvider` abstraction, Sendbox adapter (quote/create/track), shipment booking on paid orders, WebSocket `/ws/orders/{id}` streaming timeline + courier location.
  - Dependencies: Stage A order lifecycle.
  - Validation: Load test ensures <300ms p95 latency; auto-booking verified.
- [ ] **S2.2 – Android logistics data layer**
  - Outcomes: `LogisticsRepository` consuming REST + WebSocket, background refresh policies with reconnection handling.
  - Dependencies: S2.1 WebSocket endpoints.
  - Validation: Instrumented tests simulate track updates & offline recovery.
- [ ] **S2.3 – Tracking & transporter UI upgrades**
  - Outcomes: Live route polyline & ETA badges, transporter job list streaming via `/ws/jobs`, battery-aware location updates with foreground service & persistent notification.
  - Dependencies: S2.2 repository wiring.
  - Validation: Field test (Lagos/Abuja) ensuring stable updates every 15–30s.

## Stage C – Vendor Tooling
- [ ] **S3.1 – Vendor onboarding workflow**
  - Outcomes: KYC-lite flow, bank verification, store profile geocoding, onboarding status management.
  - Dependencies: Stage A auth; compliance approvals.
  - Validation: Test vendors progress through onboarding; audit trail stored.
- [ ] **S3.2 – Product & inventory management backend**
  - Outcomes: CRUD APIs with signed URL uploads, variant support, inventory counts, bulk CSV import queue.
  - Dependencies: S3.1 vendor identity; storage bucket.
  - Validation: Stress tests (200 images/100 products) without failures; queue resiliency confirmed.
- [ ] **S3.3 – Android vendor experience**
  - Outcomes: VendorRepository, Compose screens for list/detail/create/edit, WorkManager-backed multi-image uploads, bulk import UI with template download.
  - Dependencies: S3.2 APIs; Android networking stack from Stage A.
  - Validation: Instrumented tests for upload retry/resume; manual QA of dashboards & KPIs.

## Stage D – Courier Operations
- [ ] **S4.1 – Harden courier job workflows backend**
  - Outcomes: Atomic job-claim endpoint, navigation deep links, status transitions, POD storage, COD reconciliation, earnings ledger.
  - Dependencies: Stage B logistics foundation.
  - Validation: Concurrency tests (50 simultaneous claims); financial reconciliation checks.
- [ ] **S4.2 – Courier Android service & tools**
  - Outcomes: Persistent foreground service with background location policy compliance, POD capture (camera + signature), dispute submission, job board filters by vehicle type.
  - Dependencies: S4.1 endpoints; Android logistics service.
  - Validation: Battery & performance profiling; Play Store background location checklist satisfied.

## Stage E – Trust, Support, Analytics
- [ ] **S5.1 – Disputes, returns, and support tooling**
  - Outcomes: Ticketing system with evidence uploads, SLA timers, admin dashboards for escalations.
  - Dependencies: Stages C & D order data.
  - Validation: End-to-end dispute lifecycle test; support team training complete.
- [ ] **S5.2 – Privacy center & NDPA controls**
  - Outcomes: Data export/delete APIs, breach notification workflow, consent & notification preference center within Android.
  - Dependencies: S0.1 compliance artifacts; Stage A auth.
  - Validation: Dry run of data subject request within statutory window; legal sign-off.
- [ ] **S5.3 – Analytics & observability**
  - Outcomes: BigQuery/Amplitude pipeline, Crashlytics toggles, FCM topic architecture, monitoring dashboards for API p95, WebSocket uptime, webhook ack times.
  - Dependencies: Earlier stages emitting events.
  - Validation: Dashboards live with alerting; analytics QA ensures funnel correctness.

## Cross-cutting Tracks
- [ ] **X1 – Logistics partner expansion**: Add Kwik adapter behind provider abstraction, implement routing rules by geo/SLA/cost, run A/B cost analysis.
- [ ] **X2 – Fraud & risk controls**: COD limits, velocity checks, disposable email blocking, privacy-safe device fingerprinting, Trust & Safety dashboard.
- [ ] **X3 – Maps & cost optimisation**: Cache geocodes, enforce Autocomplete session token reuse, batch Directions calls, configure budget alerts.
- [ ] **X4 – Localization & performance**: Maintain translation coverage, landmark-based addresses, driver notes, low-battery throttling, lazy image loading optimizations.

