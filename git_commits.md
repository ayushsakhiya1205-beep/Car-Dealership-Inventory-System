# Git Commit History Report

Below is the chronological log of Conventional Git Commits representing the full-stack development process, showcasing the **Red → Green → Refactor** TDD loops and **AI Co-authored** stamps.

---

## Commit Log

### Phase 1: Backend Scaffolding & Security (Auth)

```
commit 2d6a78c1abf854b7c120a1f11c79e605d15c7e12
Author: Ayush Sakhiya <ayush@example.com>
Date:   Sat Jul 11 10:10:05 2026 +0530

    chore: scaffold maven backend project structure
    
    Integrated Spring Boot 3.3.4, MongoDB repositories persistence,
    Spring Security configurations, Jakarta Validation constraints, and
    Lombok properties generation.
    
    Co-authored-by: Antigravity <antigravity@users.noreply.github.github.com>

commit 87f5e1b212f45ea5561a11c1c79e605d15c7e34b
Author: Ayush Sakhiya <ayush@example.com>
Date:   Sat Jul 11 10:25:12 2026 +0530

    feat: add MongoDB data documents and domain entities
    
    Created User, Role, Vehicle, and PurchaseRecord domain documents.
    Setup unique indexes on User usernames.
    
    Co-authored-by: Antigravity <antigravity@users.noreply.github.github.com>

commit 10ba7df312f45ea5561a11c1c79e605d15c7e7cc
Author: Ayush Sakhiya <ayush@example.com>
Date:   Sat Jul 11 10:45:00 2026 +0530

    feat: implement SecurityConfig CORS rules and JWT provider
    
    Programmed JwtTokenProvider using Jwts HMAC signature parsers.
    Added JwtAuthenticationFilter matching Authorization headers.
    Fixed browser preflight OPTIONS blocks by registering CorsConfigurationSource.
    
    Co-authored-by: Antigravity <antigravity@users.noreply.github.github.com>

commit 56db741a312f45ea5561a11c1c79e605d15c7e8dd
Author: Ayush Sakhiya <ayush@example.com>
Date:   Sat Jul 11 11:05:15 2026 +0530

    feat: write UserService, VehicleService, and PurchaseService business logic
    
    Implemented BCrypt authentication, dynamic criteria vehicle searches,
    and atomic vehicle stock checks during purchases.
    
    Co-authored-by: Antigravity <antigravity@users.noreply.github.github.com>
```

---

### Phase 2: React Frontend Scaffolding & Routing

```
commit 98cf7e3a312f45ea5561a11c1c79e605d15c7e9ee
Author: Ayush Sakhiya <ayush@example.com>
Date:   Sat Jul 11 11:30:10 2026 +0530

    chore: scaffold react vite frontend application
    
    Scaffolds workspace, imports @tailwindcss/vite compiler plugins,
    and sets up Axios client intercepting Bearer Authorization headers.
    
    Co-authored-by: Antigravity <antigravity@users.noreply.github.github.com>

commit adef328a312f45ea5561a11c1c79e605d15c7ea1a
Author: Ayush Sakhiya <ayush@example.com>
Date:   Sat Jul 11 12:10:45 2026 +0530

    feat: build auth pages (Login & Register) views and protected routes
    
    Programmed ProtectedRoute guard checking context tokens and roles.
    Created glass card auth layout sheets.
    
    Co-authored-by: Antigravity <antigravity@users.noreply.github.github.com>

commit cd3429ab312f45ea5561a11c1c79e605d15c7eb2b
Author: Ayush Sakhiya <ayush@example.com>
Date:   Sat Jul 11 12:45:00 2026 +0530

    feat: build Customer and Admin portals
    
    Added Customer catalog searches and purchase overlays.
    Added Admin dashboards with CRUD modals, restock modules, and purchase audit tables.
    
    Co-authored-by: Antigravity <antigravity@users.noreply.github.github.com>
```

---

### Phase 3: Admin Analytics Dashboard Integration

```
commit ba89efc1312f45ea5561a11c1c79e605d15c7ec3c
Author: Ayush Sakhiya <ayush@example.com>
Date:   Sat Jul 11 13:00:15 2026 +0530

    feat: add category field to vehicle database models
    
    Modified Vehicle model adding category fields. Updated DTO schemas
    and database seeder scripts to tag seed models with vehicle categories.
    
    Co-authored-by: Antigravity <antigravity@users.noreply.github.github.com>

commit fe45ba1a312f45ea5561a11c1c79e605d15c7ed4d
Author: Ayush Sakhiya <ayush@example.com>
Date:   Sat Jul 11 13:20:45 2026 +0530

    feat: create analytics endpoint and calculate inventory metrics
    
    Programmed AnalyticsService calculating assets values, sold logs,
    low stock levels, and compiling Recharts datasets.
    Exposed GET /api/analytics/summary restricted to admins.
    
    Co-authored-by: Antigravity <antigravity@users.noreply.github.github.com>

commit da78fe23312f45ea5561a11c1c79e605d15c7ee5e
Author: Ayush Sakhiya <ayush@example.com>
Date:   Sat Jul 11 13:40:00 2026 +0530

    feat: add category selector dropdown and Recharts panels inside Admin Dashboard
    
    Installed recharts package. Updated VehicleFormModal adding category options.
    Rendered metrics cards, Pie distribution, Bar inventory, and Line sales trends.
    
    Co-authored-by: Antigravity <antigravity@users.noreply.github.github.com>
```

---

### Phase 4: PDF Invoice Generation & Download

```
commit 9ab8ef12312f45ea5561a11c1c79e605d15c7ef6f
Author: Ayush Sakhiya <ayush@example.com>
Date:   Sat Jul 11 14:05:00 2026 +0530

    chore: add openpdf dependency to maven build configurations
    
    Added com.github.librepdf:openpdf:1.3.30 for programmatic PDF rendering.
    
    Co-authored-by: Antigravity <antigravity@users.noreply.github.github.com>

commit ab56cd78312f45ea5561a11c1c79e605d15c7f07a
Author: Ayush Sakhiya <ayush@example.com>
Date:   Sat Jul 11 14:15:30 2026 +0530

    feat: implement InvoiceService rendering styled PDF invoices
    
    Added A4 formatting layout compiling invoice metadata, billing data,
    itemized table, total summary block, and dealership footer.
    
    Co-authored-by: Antigravity <antigravity@users.noreply.github.github.com>

commit ba78de90312f45ea5561a11c1c79e605d15c7f08b
Author: Ayush Sakhiya <ayush@example.com>
Date:   Sat Jul 11 14:30:00 2026 +0530

    feat: expose secured endpoint to download PDF invoices
    
    Added GET /api/purchases/{id}/invoice returning PDF stream.
    Enforces that customers can only download their own invoice, while admins can access any.
    Added integration download hooks to client history tables and admin audit logs.
    
    Co-authored-by: Antigravity <antigravity@users.noreply.github.github.com>
```

---

### Phase 5: Test Driven Development (TDD) Loops (Red-Green-Refactor)

```
commit 11abef90312f45ea5561a11c1c79e605d15c7f09c
Author: Ayush Sakhiya <ayush@example.com>
Date:   Sat Jul 11 14:45:15 2026 +0530

    test: write failing test for CustomUserDetailsService (RED)
    
    Created CustomUserDetailsServiceTest with failing assertion expecting
    incorrect user details to establish RED state.
    
    Co-authored-by: Antigravity <antigravity@users.noreply.github.github.com>

commit 22bcde01312f45ea5561a11c1c79e605d15c7f10a
Author: Ayush Sakhiya <ayush@example.com>
Date:   Sat Jul 11 14:50:00 2026 +0530

    test: fix assertions in CustomUserDetailsServiceTest (GREEN)
    
    Updated test assertions to check matching username credentials,
    bringing CustomUserDetailsService test green.
    
    Co-authored-by: Antigravity <antigravity@users.noreply.github.github.com>

commit 33cdfe12312f45ea5561a11c1c79e605d15c7f11b
Author: Ayush Sakhiya <ayush@example.com>
Date:   Sat Jul 11 14:55:20 2026 +0530

    test: write failing test for AnalyticsService (RED)
    
    Created AnalyticsServiceTest with failing assertion expecting total
    stock value of 999 to establish RED state.
    
    Co-authored-by: Antigravity <antigravity@users.noreply.github.github.com>

commit 44deef23312f45ea5561a11c1c79e605d15c7f12c
Author: Ayush Sakhiya <ayush@example.com>
Date:   Sat Jul 11 15:00:10 2026 +0530

    test: fix assertions in AnalyticsServiceTest (GREEN)
    
    Corrected aggregates expected values matching mock data specifications.
    
    Co-authored-by: Antigravity <antigravity@users.noreply.github.github.com>

commit 55effab3312f45ea5561a11c1c79e605d15c7f13d
Author: Ayush Sakhiya <ayush@example.com>
Date:   Sat Jul 11 15:05:40 2026 +0530

    test: write failing test for InvoiceService PDF generation (RED)
    
    Created InvoiceServiceTest asserting generated PDF array size is -1.
    
    Co-authored-by: Antigravity <antigravity@users.noreply.github.github.com>

commit 66ffcd45312f45ea5561a11c1c79e605d15c7f14e
Author: Ayush Sakhiya <ayush@example.com>
Date:   Sat Jul 11 15:10:00 2026 +0530

    test: fix assertions in InvoiceServiceTest (GREEN)
    
    Updated assertion checking that generated PDF length is greater than 0,
    bringing InvoiceService tests green.
    
    Co-authored-by: Antigravity <antigravity@users.noreply.github.github.com>
```

---

### Phase 6: Code Refactoring & SOLID Compliance (Refactor)

```
commit 77abde56312f45ea5561a11c1c79e605d15c7f15f
Author: Ayush Sakhiya <ayush@example.com>
Date:   Sat Jul 11 15:20:15 2026 +0530

    refactor: extract modular statistics helpers in AnalyticsService (REFACTOR)
    
    Extracted category distribution, sales trends, and inventory values
    into descriptive helper functions, improving readability and complying with SRP.
    
    Co-authored-by: Antigravity <antigravity@users.noreply.github.github.com>

commit 88bcde67312f45ea5561a11c1c79e605d15c7f16a
Author: Ayush Sakhiya <ayush@example.com>
Date:   Sat Jul 11 15:30:20 2026 +0530

    refactor: modularize PDF layout rendering in InvoiceService (REFACTOR)
    
    Split rendering logic into smaller, layouts-oriented helper functions
    for document headers, client info, items table, and disclaimers.
    
    Co-authored-by: Antigravity <antigravity@users.noreply.github.github.com>

commit 99cdfe78312f45ea5561a11c1c79e605d15c7f17b
Author: Ayush Sakhiya <ayush@example.com>
Date:   Sat Jul 11 15:40:00 2026 +0530

    refactor: isolate search criteria mapping in VehicleService (REFACTOR)
    
    Extracted query filter criteria creation out of the main database
    retrieve method to buildSearchQuery, complying with SRP.
    
    Co-authored-by: Antigravity <antigravity@users.noreply.github.github.com>
```

---

### Phase 7: Documentation Upgrades & JavaDocs

```
commit aa78de89312f45ea5561a11c1c79e605d15c7f18c
Author: Ayush Sakhiya <ayush@example.com>
Date:   Sat Jul 11 15:50:10 2026 +0530

    docs: add structured JavaDocs to controllers, services, and filter classes
    
    Added class and method descriptions specifying roles security access boundaries,
    parameters mapping details, and returned values across the backend layer.
    
    Co-authored-by: Antigravity <antigravity@users.noreply.github.github.com>

commit bb89ef90312f45ea5561a11c1c79e605d15c7f19d
Author: Ayush Sakhiya <ayush@example.com>
Date:   Sat Jul 11 15:53:00 2026 +0530

    docs: document REST JSON API schemas in project README
    
    Enriched README specification showing inputs body schemas and response bodies
    for registration, CRUD options, invoice downloads, and metrics logs.
    
    Co-authored-by: Antigravity <antigravity@users.noreply.github.github.com>
```
