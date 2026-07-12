# Car Dealership Inventory System

A complete full-stack Car Dealership Inventory System built with a Java Spring Boot 3 backend and a React Vite frontend. It features secure JWT authentication, role-based authorization (USER & ADMIN), dynamic catalog searching, inventory restocking, sales audit logs, and an **Admin Analytics Dashboard** using Recharts.

---

## 🛠️ Tech Stack

### Backend
- **Core**: Java 17/21, Spring Boot 3.3.4
- **Persistence**: MongoDB (Spring Data MongoDB)
- **Security**: Spring Security 6, JWT (io.jsonwebtoken 0.12.6), BCrypt Password Hashing, CORS Configuration
- **Tools**: Maven, Lombok, Jakarta Validation

### Frontend
- **Core**: React, Vite
- **Styling**: Tailwind CSS v4
- **Routing**: React Router v6
- **Charts**: Recharts
- **API Client**: Axios with interceptor
- **Icons**: Lucide React

---

##  Project Structure

### Backend
The backend follows a package-by-feature / clean architecture structure:
```
src/main/java/com/dealership/inventory
├── CarDealershipApplication.java (App entry point)
├── config
│   ├── SecurityConfig.java       (Spring Security & CORS configs)
│   ├── MongoConfig.java          (Isolated auditing configuration)
│   └── DatabaseSeeder.java       (Database CMD startup seeder)
├── controller
│   ├── AuthController.java       (Register / Login endpoints)
│   ├── VehicleController.java    (Vehicle CRUD and Restocking)
│   └── AnalyticsController.java  (Admin summary metrics REST endpoints)
├── dto
│   ├── request                   (Input payloads validations)
│   └── response                  (Output JSON schemas: VehicleResponse, AnalyticsResponse)
├── exception
│   ├── GlobalExceptionHandler.java (Controller advice mapping)
│   └── ResourceNotFoundException.java, InsufficientStockException.java, etc.
├── model
│   ├── User.java                 (User MongoDB document)
│   ├── Role.java                 (ROLE_USER, ROLE_ADMIN)
│   ├── Vehicle.java              (Vehicle MongoDB document with category)
│   └── PurchaseRecord.java       (Purchase MongoDB document)
├── repository
│   ├── UserRepository.java
│   ├── VehicleRepository.java
│   └── PurchaseRecordRepository.java
└── security
    ├── JwtTokenProvider.java     (HMAC-SHA256 helpers)
    ├── JwtAuthenticationFilter.java (Header parsing security filter)
    ├── JwtAuthenticationEntryPoint.java (Unauthorized json formatter)
    ├── UserPrincipal.java        (UserDetails mapper)
    └── CustomUserDetailsService.java (Mongo UserDetails service)
```

### Frontend
The React application resides under the `/frontend` directory:
```
frontend/src
├── main.jsx                      (Virtual DOM mounting)
├── App.jsx                       (Routing maps and layout layers)
├── index.css                     (Tailwind v4 directive imports & typography)
├── api
│   └── client.js                 (Axios wrapper with JWT interceptors)
├── context
│   └── AuthContext.jsx           (Global Auth & session manager)
├── components
│   ├── Navbar.jsx                (Responsive navigation headers)
│   ├── ProtectedRoute.jsx        (Session and Role guard wrappers)
│   ├── VehicleCard.jsx           (Adapts interfaces for admins/customers)
│   ├── VehicleFormModal.jsx      (Supports category creation and updates)
│   ├── RestockModal.jsx          (Restocks vehicle quantities)
│   └── PurchaseModal.jsx         (Handles customer purchase validation)
├── pages
│   ├── Login.jsx                 (Sign-in views)
│   ├── Register.jsx              (Sign-up views with role selections)
│   ├── Dashboard.jsx             (Customer inventory browsing & purchases)
│   └── AdminDashboard.jsx        (Admin CRUD actions, Recharts tables, and audit logs)
```

---

##  Default Credentials

A database startup seeder (`DatabaseSeeder`) automatically seeds the following credentials if the database is blank:
- **Administrator User**:
  - **Username**: `admin`
  - **Password**: `admin123`
  - **Roles**: `ROLE_ADMIN`, `ROLE_USER`

You can also use the **Register** interface in the frontend to instantly sign up new custom Accounts with either **Customer** (`ROLE_USER`) or **Admin** (`ROLE_ADMIN`) permissions.

---

## 📡 API Specification & Schemas

### 1. Authentication
#### Register User
- **Route**: `POST /api/auth/register`
- **Request Body**:
  ```json
  {
    "username": "john_doe",
    "password": "securePassword123",
    "role": "USER"
  }
  ```
- **Response (201 Created)**:
  ```json
  {
    "id": "64ebd3df76a0845f...",
    "username": "john_doe",
    "roles": ["ROLE_USER"]
  }
  ```

#### Login User
- **Route**: `POST /api/auth/login`
- **Request Body**:
  ```json
  {
    "username": "john_doe",
    "password": "securePassword123"
  }
  ```
- **Response (200 OK)**:
  ```json
  {
    "accessToken": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJqb2huX2RvZSIs...",
    "tokenType": "Bearer"
  }
  ```

---

### 2. Vehicle Catalog
#### Create Vehicle (Admin Only)
- **Route**: `POST /api/vehicles`
- **Request Body**:
  ```json
  {
    "make": "Porsche",
    "model": "911 GT3",
    "year": 2023,
    "price": 169700.00,
    "stock": 3,
    "description": "Chalk Exterior with Clubsport Package",
    "category": "Sports"
  }
  ```
- **Response (201 Created)**:
  ```json
  {
    "id": "64ebd49b76a0845f...",
    "make": "Porsche",
    "model": "911 GT3",
    "year": 2023,
    "price": 169700.00,
    "stock": 3,
    "description": "Chalk Exterior with Clubsport Package",
    "category": "Sports",
    "createdAt": "2026-07-11T13:42:01",
    "updatedAt": "2026-07-11T13:42:01"
  }
  ```

#### Search & List Vehicles
- **Route**: `GET /api/vehicles`
- **Query Parameters (Optional)**: `keyword` (regex matches make/model/desc), `make`, `model`, `year`, `minPrice`, `maxPrice`.
- **Response (200 OK)**:
  ```json
  [
    {
      "id": "64ebd49b76a0845f...",
      "make": "Porsche",
      "model": "911 GT3",
      "year": 2023,
      "price": 169700.00,
      "stock": 3,
      "description": "Chalk Exterior with Clubsport Package",
      "category": "Sports",
      "createdAt": "2026-07-11T13:42:01",
      "updatedAt": "2026-07-11T13:42:01"
    }
  ]
  ```

#### Restock Vehicle Stock (Admin Only)
- **Route**: `PATCH /api/vehicles/{id}/restock`
- **Request Body**:
  ```json
  {
    "quantity": 10
  }
  ```
- **Response (200 OK)**:
  ```json
  {
    "id": "64ebd49b76a0845f...",
    "make": "Porsche",
    "model": "911 GT3",
    "year": 2023,
    "price": 169700.00,
    "stock": 13,
    "description": "Chalk Exterior with Clubsport Package",
    "category": "Sports"
  }
  ```

---

### 3. Purchases & Invoices
#### Purchase Vehicle (Authenticated User)
- **Route**: `POST /api/purchases`
- **Request Body**:
  ```json
  {
    "vehicleId": "64ebd49b76a0845f...",
    "quantity": 2
  }
  ```
- **Response (201 Created)**:
  ```json
  {
    "id": "64ebd56a76a0845f...",
    "userId": "64ebd3df76a0845f...",
    "vehicleId": "64ebd49b76a0845f...",
    "purchasePrice": 169700.00,
    "quantity": 2,
    "totalAmount": 339400.00,
    "purchasedAt": "2026-07-11T13:45:10"
  }
  ```

#### Download PDF Invoice (Secured)
- **Route**: `GET /api/purchases/{id}/invoice`
- **Headers Needed**: `Authorization: Bearer <token>`
- **Response (200 OK)**: Raw binary file content with `Content-Type: application/pdf` and `Content-Disposition: attachment; filename=invoice-{id}.pdf`.

---

### 4. Admin Analytics
#### Retrieve Summary Dashboard Metrics (Admin Only)
- **Route**: `GET /api/analytics/summary`
- **Response (200 OK)**:
  ```json
  {
    "totalVehicles": 25,
    "totalDistinctModels": 6,
    "totalInventoryValue": 1250000.00,
    "totalSoldVehicles": 12,
    "lowStockVehicles": 2,
    "outOfStockVehicles": 1,
    "categoryDistribution": [
      { "name": "Sports", "value": 3.0 },
      { "name": "SUV", "value": 2.0 }
    ],
    "inventoryByCategory": [
      { "name": "Sports", "value": 509100.0 },
      { "name": "SUV", "value": 110000.0 }
    ],
    "monthlySales": [
      { "name": "Feb", "value": 2.0 },
      { "name": "Mar", "value": 4.0 }
    ]
  }
  ```

---

##  Running & Deployment Instructions

### Prerequisites
- **Java JDK 17** or higher
- **Maven 3.8+**
- **Node.js** (v18+) & **NPM**
- **MongoDB** (Local instance running on port `27017` OR a MongoDB Atlas URI)

---

### Step 1: Run the Backend

1. **Configure MongoDB Connection**:
   By default, the backend connects to `mongodb://localhost:27017/car_dealership`.
   To use your own MongoDB Atlas cluster, set the environment variable:
   ```bash
   # Windows (PowerShell)
   $env:SPRING_DATA_MONGODB_URI="mongodb+srv://<username>:<password>@cluster.mongodb.net/car_dealership"
   ```
   Or edit the connection URI directly inside [application.yml](file:///e:/Incubyte/src/main/resources/application.yml).

2. **Build and package the JAR**:
   ```bash
   mvn clean package
   ```

3. **Execute the Application**:
   ```bash
   java -jar target/inventory-0.0.1-SNAPSHOT.jar
   ```
   The backend will start and listen on port `8080`.

---

### Step 2: Run the Frontend

1. **Install Node Modules**:
   Navigate to the `/frontend` directory:
   ```bash
   cd frontend
   npm install
   ```

2. **Configure API URL** (Optional):
   By default, the frontend client points to `http://localhost:8080`. You can override this by creating a `.env` file inside `/frontend`:
   ```env
   VITE_API_BASE_URL=http://localhost:8080
   ```

3. **Launch Dev Server**:
   ```bash
   npm run dev
   ```
   Open your browser and navigate to the printed URL (typically `http://localhost:5173`).

4. **Production Build**:
   To compile and compress the React code into optimized assets under `/dist` folder:
   ```bash
   npm run build
   ```

---

##  My AI Usage

###  AI Tools Used
- **Antigravity**: An agentic coding assistant developed by Google DeepMind, utilizing advanced reasoning model agents capable of managing terminal processes, analyzing repository structures, and editing frontend/backend code.

###  How AI Was Used
1. **Fullstack Architecture Scaffolding**: Assisted in scaffolding the React Vite frontend and Spring Boot structure, integrating JWT authentication details, CORS headers, and MongoDB criteria queries.
2. **TDD Cycles & JUnit Testing**: Generated test structures, mock context configurations, slice test suites, and executed verification test loops.
3. **Dashboard & PDF Invoices Integration**: Constructed analytics card structures, integrated Recharts visual graphs, mapped category metric calculations, integrated OpenPDF service logic, and added PDF triggers to React pages.
4. **Clean Code Refactoring**: Modularized long methods, isolated query builders, and separated layout compilers in compliance with SOLID principles.

###  My Review Process
- **Implementation Plan Sign-offs**: Outlining and signing off on detailed implementation design steps before changing source code.
- **Continuous Compilation Verification**: Running Maven validation tasks (`mvn test`) and Vite builders (`npm run build`) after each milestone to capture syntax anomalies.
- **Manual Code Quality Auditing**: Inspecting variables, Spring DI annotations, REST request path variables, and visual layouts to maintain styling harmony.

###  Reflection on AI Usage
- **Efficiency and Velocity**: Utilizing an agentic assistant dramatically cut down the time required to scaffold boilerplate code, configure security configs (JWT/CORS), draft layout components, and generate testing suites.
- **True Pair Programming**: The AI went beyond basic code completion, proposing architectures, running validation loops, troubleshooting context failures (e.g. mock security filter issues), and refactoring classes to comply with SOLID standards.
- **Documentation Alignment**: Synchronously aligned code comments, walkthrough logs, conventional git logs, and README specs at every step of the development cycle.
