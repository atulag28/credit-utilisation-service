# Credit Utilisation Service

Java 17 / Spring Boot REST API for tracking customer credit transactions and calculating credit utilisation.

## Approach

The challenge explicitly asks for an in-memory service and says the emphasis is on approach, clean code and testability. I therefore kept the design small:

`Controller -> Service -> Repository -> In-memory store`

The controller handles HTTP concerns. The service owns the credit calculations. The repository hides the storage implementation so it can later be replaced by a database without changing the business logic.

## APIs

### POST /transactions

```json
{
  "customerId": "customer-1",
  "type": "PURCHASE",
  "amount": 120.00,
  "description": "Sony Headphones",
  "date": "2026-09-20"
}
```

`date` is optional and defaults to the current date.

### GET /transactions?customerId=customer-1

Returns the customer's transactions ordered by date.

### GET /credit-summary?customerId=customer-1

```json
{
  "customerId": "customer-1",
  "creditLimit": 2000.00,
  "currentBalance": 150.00,
  "creditUtilisationPercentage": 7.50,
  "totalPurchases": 200.00,
  "totalPayments": 50.00,
  "estimatedMonthsToPayOff": 3
}
```

## Assumptions

1. Credit limit is fixed at £2,000, as specified by the challenge.
2. `customerId` identifies the credit account. It is included in POST and supplied as a query parameter for GET because the challenge does not specify authentication/customer context.
3. `BigDecimal` is used for all monetary calculations.
4. Purchases increase the balance; payments reduce it.
5. Balance is floored at £0.00 because the challenge does not define an overpayment/credit-balance concept.
6. Transactions are immutable after creation.
7. Data is in-memory and is lost when the application restarts.
8. The optional payoff estimate uses the average monthly payment from the payment history. With no payment history, the value is `null` rather than inventing a repayment rate.
9. Inputs are validated for required fields and positive amounts.

## Running

Requirements: Java 17+ and Gradle 8+.

```bash
gradle test
gradle bootRun
```

The application runs on `http://localhost:8080`.

## Example requests

```bash
curl -X POST http://localhost:8080/transactions \
  -H 'Content-Type: application/json' \
  -d '{"customerId":"customer-1","type":"PURCHASE","amount":120.00,"description":"Sony Headphones","date":"2026-09-20"}'
```

```bash
curl -X POST http://localhost:8080/transactions \
  -H 'Content-Type: application/json' \
  -d '{"customerId":"customer-1","type":"PAYMENT","amount":50.00,"description":"Monthly payment","date":"2026-09-25"}'
```

```bash
curl 'http://localhost:8080/transactions?customerId=customer-1'
curl 'http://localhost:8080/credit-summary?customerId=customer-1'
```

## Tests

Tests cover balance/utilisation, payoff calculation, zero balance, customer isolation, transaction creation and the credit summary endpoint.

## If this were production

I would add persistent storage, authentication/authorisation, transaction idempotency, structured logging/metrics, OpenAPI documentation, and customer-specific credit limits. I would also make the repayment estimate use an explicit repayment schedule rather than inferring it from transaction history.
