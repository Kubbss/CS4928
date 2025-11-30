Trade-offs: Layered Monolith vs Partitioning

Currently we’re using a Layered Monolith on purpose. For this size of project, having everything in one codebase and one process is just simpler: one build, one deploy, and we can move quickly without worrying about network calls, versioning, or extra infrastructure. The layers (Presentation, Application, Domain, Infrastructure) already give us enough structure to keep things understandable and to separate UI, use-cases, core rules, and adapters.

If this system ever grew, we can already see a few clear “seams” that could be split into separate services later: Payments (talking to real payment providers), Notifications (email/SMS/push to customers), and maybe Reporting/Analytics. These areas are already fairly isolated in the code.

If we did partition the system in the future, we’d probably use events for domain things (like OrderPaid, OrderDelivered) and simple REST APIs between services (for example POST /payments, POST /notifications). The idea is to keep it as a clean layered monolith now, but design the boundaries so it’s not a big rewrite if Payments or Notifications need to be pulled out into their own services later.