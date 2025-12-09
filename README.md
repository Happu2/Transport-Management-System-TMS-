# 📦 Transport Management System (TMS) - Backend Assignment

[cite_start]This project implements the backend services for a Transport Management System (TMS) [cite: 4] [cite_start]using **Spring Boot 3.2+** and **PostgreSQL**[cite: 4, 115]. [cite_start]The system is designed to handle core logistics challenges, including multi-truck allocation [cite: 70] [cite_start]and concurrent booking prevention[cite: 75].

---

## 1. 🗺️ Database Schema Diagram

[cite_start]The database design uses five core entities (`Load`, `Bid`, `Transporter`, `Booking`, `Shipper`) and enforces relationships through Foreign Keys[cite: 120].

![](/original.png)

---

## 2. 🚀 Critical Business Rules Implemented

[cite_start]The application successfully implements the complex business logic required for a real-world logistics system[cite: 5, 159].

| Rule | Implementation Details | Status |
| :--- | :--- | :--- |
| **Multi-Truck Allocation** (Rule 3) | [cite_start]The `Load` entity tracks `remainingTrucks`[cite: 72]. [cite_start]Multiple bookings are allowed until the load is fully allocated (`remainingTrucks == 0`), which then sets the status to `BOOKED`[cite: 71, 74]. | ✅ |
| **Concurrency Prevention** (Rule 4) | [cite_start]Implemented **Optimistic Locking** using the `@Version` column in the `Load` entity[cite: 76, 123]. [cite_start]This prevents simultaneous acceptance of bids, ensuring the first transaction wins and the second fails with a `LoadAlreadyBookedException`[cite: 77, 127]. | ✅ |
| **Capacity Validation** (Rule 1) | [cite_start]**Deduction:** `allocatedTrucks` are deducted from the `Transporter`'s `availableTrucks` upon booking confirmation[cite: 59]. [cite_start]**Restoration:** Trucks are restored to the available pool upon booking cancellation[cite: 61, 111]. | ✅ |
| **Best Bid Calculation** (Rule 5) | [cite_start]The `GET /load/{loadId}/best-bids` API implements the required scoring logic to rank bids [cite: 79][cite_start]: $$\text{score} = (1 / \text{proposedRate}) \times 0.7 + (\text{rating} / 5) \times 0.3$$[cite: 80]. | ✅ |
| **Load Status Transitions** (Rule 2) | [cite_start]Status progresses from `POSTED` $\rightarrow$ `OPEN_FOR_BIDS` (on first bid received) $\rightarrow$ `BOOKED` (when fully allocated or bid accepted)[cite: 63, 64, 65]. | ✅ |

---

## 3. 🧪 Test Coverage Screenshot

[cite_start]Adequate unit test coverage (JUnit 5 + Mockito) was maintained, focusing heavily on the **Service Layer** to ensure all critical business rules and edge cases are handled[cite: 144, 160].

**(Please insert the screenshot showing your test coverage percentage here, specifically targeting the Service layer.)**

---

## 4. 🔗 API Documentation & Setup

[cite_start]All required APIs (15 total [cite: 82][cite_start]) are implemented, covering management of Loads [cite: 83][cite_start], Transporters [cite: 91][cite_start], Bids [cite: 96][cite_start], and Bookings[cite: 102].

### Access Links:

* **Swagger/OpenAPI Documentation URL (Live Link):**
    `http://localhost:8080/swagger-ui/index.html`

### Setup and Run Instructions:

1.  [cite_start]**Dependencies:** Ensure Java 17+ [cite: 114] and Maven are installed.
2.  **Database:** Start a PostgreSQL instance.
3.  **Configuration:** Update `application.properties` with your PostgreSQL credentials. Ensure `spring.jpa.hibernate.ddl-auto=update` is set.
4.  **Run:** Execute the application using the Maven wrapper script:
    ```bash
    ./mvnw spring-boot:run
    ```

---

