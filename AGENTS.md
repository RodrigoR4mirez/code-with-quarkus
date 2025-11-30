# Repository Guidelines

## Project Structure & Module Organization
- Single Maven module (`pom.xml`) with Java 21 target. Add application code under `src/main/java/...` following the `org.acme` base package.
- Runtime configuration lives in `src/main/resources/application.yml`; keep environment-specific overrides externalized rather than committed.
- Data and tooling assets: CSV samples in `src/main/resources/data/`, SQL helpers in `src/main/resources/scripts/`, JMeter plans in `src/main/resources/config/`, and container recipes in `src/main/docker/`. Build outputs land in `target/`.

## Build, Test, and Development Commands
- `./mvnw quarkus:dev` — hot-reload dev mode with the Dev UI at `/q/dev`.
- `./mvnw clean test` — compile and run unit tests.
- `./mvnw package` — build a JVM runner in `target/quarkus-app/`.
- `./mvnw package -Dquarkus.package.jar.type=uber-jar` — produce a single fat JAR.
- `./mvnw package -Dnative` (or add `-Dquarkus.native.container-build=true`) — create a native binary under `target/`.

## Coding Style & Naming Conventions
- Use 4-space indentation and keep imports ordered; favor constructor/field injection via CDI over manual lookups.
- Follow clean architecture boundaries: domain (pure logic) isolated from adapters (REST/resources, DB repositories) and framework specifics; keep cross-cutting concerns in dedicated utilities.
- REST endpoints should live in `.../resource` packages and end with `Resource`; Panache entities end with `Entity`, repositories with `Repository`.
- Prefer immutable DTOs/records for payloads; validate inputs at the boundary and log with structured messages. Document each public method with a brief Javadoc about behavior and constraints; add short inline comments when implementation choices are non-obvious.
- Keep configuration keys lower-kebab-case in `application.yml` and document any new ones inline.

## Testing Guidelines
- Use JUnit 5 with `@QuarkusTest` for integration-style tests; name unit tests `*Test` in `src/test/java/...` and integration tests `*IT` run by Failsafe.
- Default build skips ITs (`-DskipITs`), so run `./mvnw verify -DskipITs=false` before merging features that touch IO or persistence.
- Provide representative data in test resources instead of reusing `src/main/resources/data/`; keep test names descriptive (behavioral style preferred).
- Validate correctness before pushing: run the relevant test goal, ensure reactive flows propagate backpressure and errors, and verify that new adapters respect timeouts and non-blocking APIs.

## Commit & Pull Request Guidelines
- Follow the existing history: short, imperative subjects (e.g., `add db, kafka`). Scope commits narrowly and include context in the body when needed.
- PRs should link issues, summarize changes, list dev/test commands executed, and note schema or config updates (including docker changes).
- Include screenshots or API samples when altering external behavior; highlight any migration steps for application.yml or scripts.

## Security & Configuration Tips
- Do not commit credentials; source them from environment variables or a secrets manager and reference via `application.yml`.
- Keep sample data anonymous; strip PII from CSVs and JMX scripts. Review Dockerfiles for pinned base images and update as needed.
- For reactive code, avoid blocking calls in event loops; prefer Uni/Multi pipelines with clear cancellation and error handling, and add brief comments describing non-trivial reactive chains.
