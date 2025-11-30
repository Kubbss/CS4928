# EventBus vs Direct Calls (ADR Summary)

## Context / Problem
In our ADR we looked at how different parts of the app should react to order events. The problem was that several components (UI, kitchen view, delivery view, maybe notifications later) all care when an order is created or paid, but we didn’t want everything to be tightly wired together with direct method calls.

## Alternatives Considered
We considered three main options:
1. **Direct calls** from controllers/services to every listener (simple but very coupled).
2. **Observer only on `Order`** (works for some things but not all application-level events).
3. **A small in-process EventBus** so components can publish and subscribe to events.

## Decision
We decided to use a simple **EventBus** in the application layer. Components publish events like `OrderPaid` or `OrderDelivered`, and other parts of the system subscribe to them. The EventBus is in-process and lightweight, just enough to handle pub/sub without adding extra infrastructure.

## Consequences (Pros / Cons)
**Pros:**
- Looser coupling between UI, services, and views.
- Easy to add new listeners without changing existing code.
- Fits well with our layered architecture and future microservice-style splits.

**Cons:**
- A bit more wiring to set up and understand.
- We need to keep event names and payload formats clearly documented.

## Connection to Code
You can see this decision in the code where we publish events through the EventBus instead of calling kitchen or delivery views directly. This matches the ADR and shows how we keep components decoupled while still reacting to the same order events.