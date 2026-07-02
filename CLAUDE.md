# CLAUDE.md — Case Documents AI Responses API

> Context for Claude Code and implementation agents. Read this before touching any file.

---

## What this repository is

An **OpenAPI-first specification library** for the HMCTS Crime Common Platform (CPP).

There is **no runtime service** in this repo. The sole deliverable is:
1. The OpenAPI 3.0.3 spec (`src/main/resources/openapi/case-admin-doc-knowledge-api.openapi.yml`)
2. Java model and API interface stubs **generated from it** at build time
3. Tests that verify the generated stubs match the spec's contracts

The published JAR (to GitHub Packages and Azure DevOps Artifacts) contains the generated interfaces and models. Downstream services (`cpp-context-*`) import this JAR to get type-safe stubs.

---

## Repository layout

```
.github/
  workflows/
    ci-draft.yml          # PR / push-to-main → draft versioned publish
    ci-released.yml       # GitHub release → stable publish
    code-analysis.yml     # PMD static analysis
    codeql.yml            # CodeQL security scan + SBOM upload
    lint-openapi.yml      # Spectral lint, internal-URL check, JSON schema validate
    secrets-scanner.yml   # Gitleaks secret scan
  pmd-ruleset.xml         # PMD rules (bestpractices, codestyle, errorprone, perf, security)
  CODEOWNERS / CONTRIBUTING.md / templates

src/main/
  java/uk/gov/hmcts/cp/config/
    OpenAPIConfigurationLoader.java   # Only hand-written Java file; loads the YAML from classpath
  resources/
    openapi/
      case-admin-doc-knowledge-api.openapi.yml   # THE spec — primary source of truth
      schema/                                     # Reserved for standalone JSON Schema files
    logback-api-cdk.xml              # Logstash JSON encoder, INFO level

src/test/
  java/uk/gov/hmcts/cp/openapi/model/cdk/
    GeneratedApiContractsExistTest.java    # Parses live YAML, asserts schema properties
    GeneratedModelContractsExistTest.java  # Reflection: class existence + getter names
    GeneratedObjectMappingTest.java        # Jackson round-trip deserialization tests

build.gradle    # Single build script — Gradle 8.14, Java 21 toolchain
.spectral.yml   # Spectral OAS ruleset (two rules disabled: example validators)
```

**Generated code** (never committed) lives at `build/generated/src/main/java/`:
- Models: `uk.gov.hmcts.cp.openapi.model.cdk.*`
- API interfaces: `uk.gov.hmcts.cp.openapi.api.cdk.*`

---

## Technology stack

| Concern | Tool / Version |
|---|---|
| Language | Java 21 (toolchain; also tested on 23 in CI) |
| Build | Gradle 8.14 |
| Code generation | openapi-generator-gradle-plugin 7.16.0, `spring` generator |
| Code formatting | Spotless 8.0.0 (Eclipse formatter downloaded from hmcts/api-cp-code-style) |
| Static analysis | PMD (all standard categories; `AtLeastOneConstructor` excluded) |
| Tests | JUnit 5 (junit-bom 6.0.0), AssertJ 3.6.1 |
| Spring runtime | Spring Boot 4.0.0-M3 (interfaces only — no `@SpringBootApplication`) |
| SBOM | CycloneDX 1.6 JSON, packaged at `META-INF/sbom/bom.json` |
| Logging | Logstash encoder (JSON to stdout) |

---

## Build pipeline (what runs in order)

```
downloadFormatter
    └─> openApiGenerate          ← reads *.openapi.yml, writes build/generated/
            └─> spotlessApply    ← formats generated Java
                    └─> compileJava
                            └─> test
                                    └─> jacocoTestReport
                                            └─> check
```

Key Gradle commands:
```bash
./gradlew build                       # full build + test
./gradlew build -DAPI_SPEC_VERSION=x  # set version (default 0.0.999)
./gradlew test                        # tests only (requires openApiGenerate first)
./gradlew openApiGenerate             # generate stubs only
./gradlew spotlessApply               # format generated sources
./gradlew pmdMain                     # only runs when explicitly named
```

**`compileJava` has `-Werror`** — any compiler warning is a build failure.  
**`pmdTest` is disabled** — PMD only runs on `src/main`.

---

## OpenAPI spec — conventions (enforce these on every change)

### File location and naming
- Spec: `src/main/resources/openapi/case-admin-doc-knowledge-api.openapi.yml`
- Version is always `0.0.0` in the file; CI injects the real version via `hmcts/update-openapi-version@main`

### Tags
Title-cased noun phrases. One tag per domain group.
```yaml
tags:
  - name: Query Catalogue
    description: One sentence describing what the tag covers.
```
Tags declared at the top-level `tags:` array control generation order. Always add a new tag there before using it in a path.

### Paths
Section comments use the pattern:
```yaml
  # ---------------------------
  # Section Name
  # ---------------------------
```

### operationId
camelCase, verb + noun. Examples:
- `listQueryCatalogue`, `getQueryCatalogueItem`, `setQueryCatalogueLabel`
- `upsertQueries`, `getQuery`, `listQueryVersions`
- `startIngestionProcess`, `getIngestionStatus`
- `upsertDiscoverySchedulerConfigurations`

### Vendor media types
Pattern: `application/vnd.casedocumentknowledge-service.{slug}+json`

Existing slugs:
| Slug | Used by |
|---|---|
| `query-catalogue` | Query Catalogue endpoints |
| `queries` | Queries endpoints |
| `answers` | Answers with-llm endpoint |
| `answers.v2` | Answers v2 endpoint |
| `ingestion` | Ingestion status |
| `ingestion-process` | Ingestion start |
| `document-content` | Document download |
| `discovery-scheduler-configuration` | Discovery Scheduler |

### HTTP status codes
| Status | When |
|---|---|
| `200` | Reads; also used for process-trigger responses |
| `201` | Resource creation (a new versioned record is persisted) |
| `202` | Accepted async batch (e.g. upsertQueries) |
| `400` | Missing/invalid fields |
| `404` | Resource not found |
| `409` | Uniqueness conflict |

Every non-2xx response **must** use `$ref: '#/components/schemas/ErrorResponse'` as its schema.

### Inline property shorthand
Simple properties use the compact form:
```yaml
schema: { type: string, format: uuid }
```
Multi-property objects use the full block form.

### Schema conventions
- Names: PascalCase (`DiscoverySchedulerConfigurationRequest`, `ErrorResponse`)
- Enums: separate named schema, SCREAMING_SNAKE_CASE values
- `required:` always present on request/response bodies
- `nullable: true` only where the field is genuinely optional in the response
- `format: uuid` for all ID fields, `format: date-time` for timestamps, `format: date` for calendar dates
- `minimum: 1` on monotonic integer version fields
- Descriptions: full sentences; use `|` block scalars for multi-line

### Example convention
Request bodies provide named examples (not just `example:`):
```yaml
examples:
  enabled:
    summary: Short human label
    value:
      field: value
  disabled:
    summary: ...
    value: ...
```
Response bodies use a single `example:` key.

### `components/schemas` ordering
Schemas are grouped by domain with section comments, in the same order as the paths:
1. Query Catalogue
2. Common
3. Queries
4. Answers v2 response / Answers
5. Ingestion
6. Discovery Scheduler
7. Errors

---

## Code generation details

Generator: `spring` template, `interfaceOnly: true`.  
Key config options that must not change:

```groovy
useLombok: "false"        // prevents duplicate constructor clash in generated models
openApiNullable: "false"  // no Optional wrappers
useJakartaEe: "true"      // Jakarta (not javax) annotations
useSpringBoot3: "true"
dateLibrary: "java8"      // LocalDate, OffsetDateTime
serializableModel: "true"
```

**Lombok is disabled on generated models** — if you add `useLombok: true` it will cause duplicate constructor compilation errors. Lombok is available for hand-written code only.

Generated getter naming rules (important for writing tests):
- String/Object field `fieldName` → `getFieldName()`
- Boolean field `isActive` → `getIsActive()` (NOT `isIsActive()` — generator uses `get` prefix for all)
- UUID field `courtCentreId` → `getCourtCentreId()` returns `java.util.UUID`
- Date field `uploadedDate` → `getUploadedDate()` returns `java.time.LocalDate`
- DateTime field `createdAt` → `getCreatedAt()` returns `java.time.OffsetDateTime`

---

## Hand-written Java (`src/main/java`)

Only one class: `OpenAPIConfigurationLoader`.

- Loads `openapi/case-admin-doc-knowledge-api.openapi.yml` from the classpath using swagger-parser
- The `openAPI()` method is the entry point used by all three test classes
- Uses `@Slf4j` (Lombok) — only Lombok usage in the repo
- `SPEC_PATH` constant must match the filename in `src/main/resources/openapi/`

Do not add Spring `@Configuration`, `@Bean`, or `@Component` here — this is a library, not a service.

---

## Test patterns

There are exactly **three test classes**. Every new schema or endpoint must be reflected in all three.

### 1. `GeneratedApiContractsExistTest` — spec-level assertions

Parses the live YAML via `new OpenAPIConfigurationLoader().openAPI()` and asserts schema structure.

Pattern for a new schema:
```java
@Test
void myNewSchema_schema_should_declare_required_fields_and_correct_formats() {
    OpenAPI openAPI = new OpenAPIConfigurationLoader().openAPI();
    Schema<?> schema = openAPI.getComponents().getSchemas().get("MyNewSchema");

    assertThat(schema).isNotNull();
    assertThat(schema.getRequired()).containsAll(List.of("field1", "field2"));

    Map<String, Schema> properties = schema.getProperties();
    assertThat(properties.get("field1").getType()).isEqualTo("string");
    assertThat(properties.get("field1").getFormat()).isEqualTo("uuid");
}
```

### 2. `GeneratedModelContractsExistTest` — reflection-based class/getter tests

Two things to do for every new schema:

**a) Add the FQCN to the existence list:**
```java
"uk.gov.hmcts.cp.openapi.model.cdk.MyNewSchema",
```

**b) Add an accessor test:**
```java
@Test
void myNewSchema_should_have_expected_accessors() throws Exception {
    Class<?> cls = Class.forName("uk.gov.hmcts.cp.openapi.model.cdk.MyNewSchema");
    assertHasGetter(cls, "getField1");
    assertHasGetter(cls, "getField2");
}
```

For inline array item schemas (e.g. `queriesInner`), use `classOrNull` with a fallback:
```java
Class<?> inner = classOrNull("uk.gov.hmcts.cp.openapi.model.cdk.MySchemaItemsInner");
if (inner == null) inner = classOrNull("uk.gov.hmcts.cp.openapi.model.cdk.MySchemalItems");
assertNotNull(inner, "Missing generated inner model");
```

For new enums, add an enum values test:
```java
@Test
void myEnum_enum_has_expected_values() throws Exception {
    Class<?> enumCls = Class.forName("uk.gov.hmcts.cp.openapi.model.cdk.MyEnum");
    assertTrue(enumCls.isEnum());
    var names = enumNames(enumCls);
    assertTrue(names.containsAll(List.of("VALUE_A", "VALUE_B")));
}
```

### 3. `GeneratedObjectMappingTest` — Jackson round-trip tests

Uses a shared `ObjectMapper` with `JavaTimeModule` and `WRITE_DATES_AS_TIMESTAMPS` disabled.

Pattern:
```java
@Test
void json_should_map_to_MyNewSchema_object() throws JsonProcessingException {
    String json = "{\n" +
            "  \"field1\": \"some-uuid\",\n" +
            "  \"field2\": \"2026-06-16\"\n" +
            "}";

    MyNewSchema result = mapper.readValue(json, MyNewSchema.class);
    assertThat(result.getField1().toString()).isEqualTo("some-uuid");
    assertThat(toText(result.getField2())).isEqualTo("2026-06-16");
}
```

Use `toText(value)` (private helper: `value == null ? null : value.toString()`) for dates, datetimes, UUIDs, and enums — avoids type-specific imports and keeps assertions uniform.

---

## CI/CD pipeline

### Lint gate (`lint-openapi.yml`) — runs on every PR
- **Spectral**: `spectral lint "src/main/resources/openapi/*.{yml,yaml}"` using `.spectral.yml` (extends `spectral:oas`; two example-validator rules disabled)
- **Internal URL check**: grep for `cjscp.org.uk|service.gov.uk|justice.gov.uk|hmcts.net|ejudiciary.net` — will fail the PR if found in the spec
- **JSON schema lint**: `jsonlint` on `src/main/resources/openapi/schema/*.json` (non-example files)
- **JSON schema validate**: AJV validation of schema + example pairs

### Draft publish (`ci-draft.yml`) — PR and push to main
1. `hmcts/artefact-version-action@v1` generates a draft version (e.g. `1.2.3-SNAPSHOT-abc1234`)
2. `hmcts/update-openapi-version@main` stamps the version into the spec YAML and uploads it as an artifact
3. `gradle build -DAPI_SPEC_VERSION=<version>` runs tests on Java 21 and 23
4. On push (not PR): publishes to GitHub Packages and Azure DevOps Artifacts

### Release publish (`ci-released.yml`) — GitHub release event
Same as draft but uses the stable release version tag.

### Required secrets
- `GITHUB_TOKEN` — auto-provided
- `AZURE_DEVOPS_ARTIFACT_USERNAME` / `AZURE_DEVOPS_ARTIFACT_TOKEN` — for Azure Artifacts publish
- `GITLEAKS_LICENSE` / `HMCTS_CP_GITLEAKS_REGEX_INTERNAL_URL` — for secrets scanner

---

## Adding a new API endpoint — complete checklist

1. **Spec** (`case-admin-doc-knowledge-api.openapi.yml`):
   - [ ] Add tag to top-level `tags:` if new domain
   - [ ] Add path under the correct section comment
   - [ ] Use correct `operationId` (camelCase verb+noun)
   - [ ] Use correct vendor media type (`application/vnd.casedocumentknowledge-service.{slug}+json`)
   - [ ] All non-2xx responses reference `$ref: '#/components/schemas/ErrorResponse'`
   - [ ] Add request schema to `components/schemas` in correct domain group
   - [ ] Add response schema to `components/schemas`

2. **`GeneratedApiContractsExistTest`**:
   - [ ] Add `@Test` method asserting required fields and formats on the new request schema

3. **`GeneratedModelContractsExistTest`**:
   - [ ] Add new model FQCNs to `generated_models_should_exist` list
   - [ ] Add `@Test` method asserting getters on request model
   - [ ] Add `@Test` method asserting getters on response model
   - [ ] Add enum test if a new enum schema was introduced

4. **`GeneratedObjectMappingTest`**:
   - [ ] Add `@Test` round-trip test for the response model (happy path)
   - [ ] Add `@Test` for significant alternate state (e.g. `isActive: false`, null fields)

---

## Versioning model in the spec

Several resources use an explicit integer `version` field (not server-generated). Rules:
- `version` is always `integer`, `minimum: 1`, `required`
- The **latest version** is always what the runtime scheduler/consumer uses
- Older versions are retained for audit trail — there is no DELETE
- A `(domainKey1, domainKey2, version)` triple must be unique → `409` if it already exists

Example: `DiscoverySchedulerConfigurationRequest` uses `(courtCentreId, courtRoomId, version)` as the composite uniqueness key.

---

## What NOT to do

- **Do not add `@SpringBootApplication` or `@RestController`** — this is a library, not a service
- **Do not commit generated code** — `build/generated/` is gitignored
- **Do not add `useLombok: true`** to `openApiGenerate` — causes duplicate constructor compilation error
- **Do not use internal HMCTS URLs** in the spec — the lint gate will block the PR
- **Do not skip the three-test-file update** when adding a schema — all three must stay in sync
- **Do not add standalone JSON schema files** to `src/main/resources/openapi/schema/` without a matching `.example.json` — AJV validation will warn; lint-openapi expects pairs
- **Do not amend the spec `version: 0.0.0`** — CI injects the real version; changing it breaks the build

---

## Common gotchas

| Symptom | Cause | Fix |
|---|---|---|
| `ClassNotFoundException` in model tests | Generated class name differs from expected | Check openapi-generator naming: inline schemas become `*Inner`, enums may get suffix |
| `getIsActive()` vs `isIsActive()` | OpenAPI generator uses `get` prefix for all properties, even those starting with `is` | Always use `getIsActive()` in tests |
| `OffsetDateTime.toString()` returns `2025-01-01T11:11:11Z` but test asserts `2025-05-01T12:00Z` | Trailing seconds `:00` omitted by `OffsetDateTime` when zero | Use `toText()` helper and match what `toString()` actually returns |
| `spotlessApply` fails | `downloadFormatter` has not run | Run `./gradlew downloadFormatter spotlessApply` |
| PMD fails on hand-written code | New code violates bestpractices/codestyle/errorprone rules | Fix the violation; do not add suppressions without strong justification |
| Spectral lint fails on PR | Vendor media type missing `+json` suffix, or `operationId` missing, or `400` response missing | Fix in spec; run `spectral lint` locally first |

---

## Domain model quick reference

### Current tags and their endpoints

| Tag | Endpoints |
|---|---|
| Query Catalogue | `GET /query-catalogue`, `GET /query-catalogue/{queryId}`, `PUT /query-catalogue/{queryId}/label` |
| Queries | `GET /queries`, `POST /queries`, `GET /queries/{queryId}`, `GET /queries/{queryId}/versions` |
| Answers | `GET /v2/cases/{caseId}/queries/{queryId}/answers`, `GET /answers/{caseId}/{queryId}/with-llm` |
| Ingestion | `GET /ingestions/status`, `POST /ingestions/start` |
| Document | `GET /document/{documentId}/content` |
| Discovery Scheduler | `POST /discovery-scheduler/configurations` (batch upsert, max 500 items) |

### Key enums

| Schema | Values |
|---|---|
| `QueryLevel` | `CASE`, `DEFENDANT`, `CASE_ALL_DOCUMENTS` |
| `QueryLifecycleStatus` | `ANSWER_NOT_AVAILABLE`, `ANSWER_AVAILABLE` |
| `AnswerStatus` | `ANSWER_AVAILABLE`, `ANSWER_NOT_AVAILABLE`, `IDPC_NOT_FOUND` |
| `DocumentIngestionPhase` | `NOT_FOUND`, `UPLOADING`, `UPLOADED`, `INGESTING`, `INGESTED`, `FAILED` |
| `IngestionProcessPhase` | `STARTED`, `FAILED` |
