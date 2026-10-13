# DukaanSarthi Smart Inventory Backend

Spring Boot 3.4 / Java 21 / Spring Data JPA / PostgreSQL implementation for the React Smart Inventory UI.

## Endpoints

Base URL: `http://localhost:8080/api`

- `GET /smart-inventory/reorder-suggestions` — product forecast and reorder list
- `GET /smart-inventory/forecast?productId=1` — forecast for one product
- `GET /smart-inventory/suppliers/1` — supplier offers for a product
- `POST /smart-inventory/purchase-recommendations/approve` — creates a **pending purchase request**; it does not place an order with a supplier

The response fields are compatible with the included React dashboard's API contract. Supplier `id` is a supplier code string, e.g. `S1`.

## Run

1. Create a PostgreSQL database:
   ```sql
   CREATE DATABASE dukaansarthi;
   ```
2. Set database credentials in environment variables if needed:
   ```bash
   export DB_URL=jdbc:postgresql://localhost:5432/dukaansarthi
   export DB_USERNAME=postgres
   export DB_PASSWORD=your_password
   ```
3. Run from this folder:
   ```bash
   mvn spring-boot:run
   ```

Default Hibernate `ddl-auto=update` creates the example tables prefixed `si_` on startup. For production, use migrations (Flyway/Liquibase) instead.

## Data model

- `si_products`: current stock, SKU, category, safety stock, lead time, quantity already on order, selling price
- `si_sales_records`: quantity sold and sale date per product
- `si_supplier_offers`: supplier-specific price, delivery time, availability, rating
- `si_purchase_recommendations`: submitted purchase requests and their status

The service forecasts using the average daily sales over the last 30 days. The seven-day forecast is a simple baseline, not an ML model. Suggested quantity accounts for target stock, current stock and existing on-order quantity.

## Example request

```http
POST /api/smart-inventory/purchase-recommendations/approve
Content-Type: application/json
```

```json
{
  "productId": 1,
  "supplierId": "S1",
  "supplierName": "Example Supplier",
  "quantity": 40,
  "unitPrice": 36,
  "status": "PENDING_APPROVAL"
}
```

The backend resolves the supplier and price from the database instead of trusting the supplied price. It checks that requested quantity does not exceed the supplier's listed availability and stores a pending request.

## Important integration notes

This is a standalone example module. It uses its own `si_` tables to avoid assuming DukaanSarthi's existing schema. Before connecting to production, map it to the actual product, POS sales, supplier and purchase entities; enforce tenant/shop isolation and authentication; restrict CORS to the deployed frontend origin; add audit/user fields and transaction rules; and review approval permissions. Do not use `ddl-auto=update` for production.

## Product and supplier APIs

The catalog controller exposes CRUD endpoints for inventory products and supplier offers.

### Products

- `GET /api/products` — list all products
- `GET /api/products/{id}` — get one product
- `POST /api/products` — create a product
- `PUT /api/products/{id}` — update a product
- `DELETE /api/products/{id}` — delete a product (returns 409 if referenced by related records)

Example `POST /api/products` body:
```json
{
  "name": "Maggi Noodles",
  "sku": "MG001",
  "category": "Food & Beverages",
  "currentStock": 12,
  "safetyStock": 10,
  "leadTimeDays": 3,
  "onOrderQuantity": 0,
  "sellingPrice": 15.00
}
```

### Supplier offers

A supplier offer links one supplier and their price/availability to a product.

- `GET /api/suppliers` — list all supplier offers
- `GET /api/suppliers/{id}` — get one supplier offer
- `GET /api/products/{productId}/supplier-offers` — list active offers for a product
- `POST /api/suppliers` — create an offer
- `PUT /api/suppliers/{id}` — update an offer
- `DELETE /api/suppliers/{id}` — delete an offer

Example `POST /api/suppliers` body:
```json
{
  "productId": 1,
  "supplierCode": "SUP-001",
  "supplierName": "Shree Traders",
  "unitPrice": 36.00,
  "deliveryDays": 2,
  "availableQuantity": 250,
  "rating": 4.5,
  "active": true
}
```

`POST` endpoints return `201 Created`; successful `DELETE` endpoints return `204 No Content`. Validation failures return `400`, missing IDs return `404`, and products that are still referenced by related records cannot be deleted. Supplier offers are not a separate supplier master record: the same supplier can have multiple offers for different products. For a production system, consider adding a dedicated `Supplier` entity and referencing it from supplier offers.

Use the existing database configuration in `src/main/resources/application.properties`. The product and supplier endpoints use the `si_products` and `si_supplier_offers` tables created by JPA (`spring.jpa.hibernate.ddl-auto=update` in local development).
