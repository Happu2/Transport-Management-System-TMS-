# 📦 Transport Management System (TMS) - Backend Assignment

This project implements the backend services for a Transport Management System (TMS)using **Spring Boot 3.2+** and **PostgreSQL** The system is designed to handle core logistics challenges, including multi-truck allocation and concurrent booking prevention

---

## 1. 🗺️ Database Schema Diagram

The database design uses five core entities (`Load`, `Bid`, `Transporter`, `Booking`, `Shipper`) and enforces relationships through Foreign Keys

![](/package.png)

---

## 2. 🚀 Critical Business Rules Implemented

The application successfully implements the complex business logic required for a real-world logistics system.

| Rule | Implementation Details | Status |
| :--- | :--- | :--- |
| **Multi-Truck Allocation** (Rule 3) | The `Load` entity tracks `remainingTrucks`Multiple bookings are allowed until the load is fully allocated (`remainingTrucks == 0`), which then sets the status to `BOOKED`. | ✅ |
| **Concurrency Prevention** (Rule 4) | Implemented **Optimistic Locking** using the `@Version` column in the `Load` entity. This prevents simultaneous acceptance of bids, ensuring the first transaction wins and the second fails with a `LoadAlreadyBookedException`. | ✅ |
| **Capacity Validation** (Rule 1) | **Deduction:** `allocatedTrucks` are deducted from the `Transporter`'s `availableTrucks` upon booking confirmation. **Restoration:** Trucks are restored to the available pool upon booking cancellation. | ✅ |
| **Best Bid Calculation** (Rule 5) | The `GET /load/{loadId}/best-bids` API implements the required scoring logic to rank bids : $$\text{score} = (1 / \text{proposedRate}) \times 0.7 + (\text{rating} / 5) \times 0.3$$. | ✅ |
| **Load Status Transitions** (Rule 2) | Status progresses from `POSTED` $\rightarrow$ `OPEN_FOR_BIDS` (on first bid received) $\rightarrow$ `BOOKED` (when fully allocated or bid accepted). | ✅ |

---

## 3. 🧪 Test Coverage Screenshot

[cite_start]Adequate unit test coverage (JUnit 5 + Mockito) was maintained, focusing heavily on the **Service Layer** to ensure all critical business rules and edge cases are handled[cite: 144, 160].

![](/testcoverage.png)

---

## 4. 🔗 API Documentation & Setup

All required APIs (15 total ) are implemented, covering management of Loads , Transporters , Bids , and Bookings.

### Access Links:

* **Swagger/OpenAPI Documentation URL:**
    `http://localhost:8080/swagger-ui/index.html`

### Setup and Run Instructions:

1.  **Dependencies:** Ensure Java 17+  and Maven are installed.
2.  **Database:** Start a PostgreSQL instance.
3.  **Configuration:** Update `application.properties` with your PostgreSQL credentials. Ensure `spring.jpa.hibernate.ddl-auto=update` is set.
4.  **Run:** Execute the application using the Maven wrapper script:
    ```bash
    ./mvnw spring-boot:run
    ```

---

