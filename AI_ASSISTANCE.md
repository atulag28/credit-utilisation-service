# AI assistance used during the challenge

AI was used as a development aid. The implementation and assumptions should be reviewed and understood by the author before submission.

## Prompts used

### 1. Understand the challenge

> Review the attached technical challenge. Identify the required REST endpoints, business calculations, technical constraints, ambiguities, and a sensible implementation approach for a Java 17 Spring Boot service. Do not invent requirements that are not stated; call out assumptions separately.

### 2. Architecture review

> Act as a senior Java backend engineer and review a small Spring Boot in-memory credit utilisation service. Suggest a simple Controller -> Service -> Repository structure, with BigDecimal for monetary values and unit tests for business calculations. Keep it appropriate for a 2–3 hour technical challenge rather than over-engineering it.

### 3. Edge-case review

> Review the proposed credit balance and utilisation calculation. Identify edge cases around payments, zero balances, missing payment history, decimal money calculations, customer isolation, validation, and date handling. Explain what should be handled in code and what should be documented as an assumption.

### 4. Test review

> Review the unit and controller tests for a Spring Boot credit utilisation API. Identify the important behaviours that should be covered and suggest concise tests for them.

## How AI suggestions were used

The final solution was kept deliberately small. The main decisions were to use an in-memory repository because persistence was explicitly not required, `BigDecimal` for money, a service layer for business calculations, immutable Java records for data, documented assumptions for ambiguous requirements, and tests around the important behaviours.
