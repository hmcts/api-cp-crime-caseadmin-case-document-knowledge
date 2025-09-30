# Case Q&A API 

**Repository:** api-cp-crime-caseadmin-case-document-knowledge

**Purpose:** This repository contains the OpenAPI specification, generated server/client stubs and test harness for the Case Documents AI Responses API — a service that accepts user queries about case documents (e.g., "Summary of witness statements"), ingests case materials, runs retrieval-augmented generation (RAG) and returns AI-generated answers with source citations and the original user question echoed back.

---

## Quick links

* OpenAPI spec: `src/main/resources/openapi/case-admin-doc-knowledge-api.openapi.yml`
* Generated sources: `build/generated/src/main/java` (populated by `openApiGenerate`)
* Main Gradle tasks:

    * `gradle openApiGenerate` — generate APIs & models from OpenAPI
    * `gradle clean build` — full build
    * `gradle test` — run tests

---

## API summary (contract highlights)

This API exposes a small, focused set of endpoints to list queued queries and retrieve AI answers.

### Endpoints

* `GET /queries` — list pipeline status and queued `queries`.

    * Response: `QueryStatusResponse` with `status` (UPLOADED | INGESTED | ANSWERS_AVAILABLE) and an array of `QuerySummary` (`queryId`, `userQuery`).

* `GET /answers/{queryId}` — get the AI-generated answer for a `queryId` (also returns the `userQuery`).

    * Response: `AnswerResponse` with fields: `queryId`, `userQuery`, `answer`, `dateCreated`.

* `POST /answers/{queryId}` — POST variant that returns the same `AnswerResponse` (supported for UI flexibility).

* `GET /answers/{queryId}/integration` — integration/testing endpoint; returns `AnswerWithLlmResponse` (includes `llmInput` used by the LLM).

* `POST /answers/{queryId}/integration` — POST variant for the integration endpoint.

> All path parameters are declared at the path level. Responses use JSON and timestamps use the format `YYYY-MM-DD HH:mm:ss`.

---

## OpenAPI & generation notes

1. **Spec file name**: `case-qna-api.openapi.v1.yml` (under `src/main/resources/openapi`). Keep this name — Gradle expects `*.openapi.yml`.

2. **Generator configuration** (recommended):

    * Use OpenAPI Generator `spring` server template (see `build.gradle`).
    * `configOptions` used in this repo intentionally set `useLombok=false` for generated models to avoid duplicate-constructor compilation errors (the generator emits explicit constructors).

3. **Avoid model name collisions**: rename generic models like `Error` to `ApiError` in the spec if you see conflicts with `java.lang.Error`.

4. **Regenerate** after editing the spec:

```bash
./gradlew clean openApiGenerate spotlessApply build
```

This project uses Spotless to format generated sources; `spotlessApply` runs automatically before `compileJava`.

---

## Building & testing

1. Build and generate sources

```bash
gradle clean openApiGenerate build
```

2. Run tests

```bash
gradlew test
```

3. If you hit Lombok/model constructor issues, see `build.gradle` config: ensure generated models are created without `@AllArgsConstructor`/`@NoArgsConstructor` annotations (this project disables Lombok on generated models).

---

## Example requests

**List queries**

```bash
curl -sS https://api.example.com/queries | jq '.'
```

**Get answer (GET)**

```bash
curl -sS https://api.example.com/answers/1c9d3a9a-1f1b-4a2a-9d7c-3f6b4b9d1f10 | jq '.'
```

**Get answer (POST)**

```bash
curl -sS -X POST https://api.example.com/answers/1c9d3a9a-1f1b-4a2a-9d7c-3f6b4b9d1f10 -H 'Content-Type: application/json' -d '{}' | jq '.'
```

**Integration endpoint (GET)**

```bash
curl -sS https://api.example.com/answers/1c9d3a9a-1f1b-4a2a-9d7c-3f6b4b9d1f10/integration | jq '.'
```

---

## Developer notes

* The loader `OpenAPIConfigurationLoader` reads the YAML spec at runtime and exposes it as a Spring bean (if wired). Tests validate the expected `info` block and schema presence.
* Tests in `src/test/java` include reflection-based checks for generated APIs and models, and example payload (de)serialization tests.
* Keep `API_SPEC_VERSION` in Gradle or via `-DAPI_SPEC_VERSION` during tests to control the `info.version` used by the loader.

---

## Contributing

Follow the repository template conventions. See `.github/CONTRIBUTING.md` for PR and branching rules. After creating a repository from this template, **delete** template-specific docs you don’t need (see repo root instructions).

---

## License

This project is licensed under the MIT License. See `LICENSE` for details.
