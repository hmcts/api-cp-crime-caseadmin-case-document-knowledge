package uk.gov.hmcts.cp.openapi.model.cdk;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GeneratedModelContractsExistTest {

    @Test
    void generated_models_should_exist() {
        List<String> models = List.of(
                "uk.gov.hmcts.cp.openapi.model.cdk.Scope",
                "uk.gov.hmcts.cp.openapi.model.cdk.AnswerResponse",
                "uk.gov.hmcts.cp.openapi.model.cdk.AnswerWithLlmResponse",
                "uk.gov.hmcts.cp.openapi.model.cdk.QueryStatusResponse",
                "uk.gov.hmcts.cp.openapi.model.cdk.QuerySummary",
                "uk.gov.hmcts.cp.openapi.model.cdk.QueryUpsertRequest",
                "uk.gov.hmcts.cp.openapi.model.cdk.QueryLifecycleStatus",
                "uk.gov.hmcts.cp.openapi.model.cdk.IngestionStatusResponse",
                "uk.gov.hmcts.cp.openapi.model.cdk.DocumentIngestionPhase",
                "uk.gov.hmcts.cp.openapi.model.cdk.ErrorResponse",
                "uk.gov.hmcts.cp.openapi.model.cdk.QueryLevel",
                "uk.gov.hmcts.cp.openapi.model.cdk.AnswerStatus"
        );

        for (String fqcn : models) {
            assertTrue(classExists(fqcn), "Missing generated model: " + fqcn);
        }
    }

    @Test
    void answerResponse_should_have_expected_accessors() throws Exception {
        Class<?> cls = Class.forName("uk.gov.hmcts.cp.openapi.model.cdk.AnswerResponse");
        assertHasGetter(cls, "getQueryId");
        assertHasGetter(cls, "getUserQuery");
        assertHasGetter(cls, "getAnswer");
        assertHasGetter(cls, "getCreatedAt");
        assertHasGetter(cls, "getVersion");
    }

    @Test
    void answerWithLlmResponse_should_extend_answer_contract_with_llmInput() throws Exception {
        Class<?> cls = Class.forName("uk.gov.hmcts.cp.openapi.model.cdk.AnswerWithLlmResponse");
        assertHasGetter(cls, "getQueryId");
        assertHasGetter(cls, "getUserQuery");
        assertHasGetter(cls, "getAnswer");
        assertHasGetter(cls, "getCreatedAt");
        assertHasGetter(cls, "getVersion");
        assertHasGetter(cls, "getLlmInput");
    }

    @Test
    void querySummary_should_have_expected_accessors() throws Exception {
        Class<?> cls = Class.forName("uk.gov.hmcts.cp.openapi.model.cdk.QuerySummary");
        assertHasGetter(cls, "getQueryId");
        assertHasGetter(cls, "getUserQuery");
        assertHasGetter(cls, "getQueryPrompt");
        assertHasGetter(cls, "getStatus");      // QueryLifecycleStatus
        assertHasGetter(cls, "getEffectiveAt"); // required in schema
        assertHasGetter(cls, "getCaseId");      // lineage (no defendantId anymore)
    }

    @Test
    void queryLifecycleStatus_enum_has_expected_values() throws Exception {
        Class<?> enumCls = Class.forName("uk.gov.hmcts.cp.openapi.model.cdk.QueryLifecycleStatus");
        assertTrue(enumCls.isEnum(), "QueryLifecycleStatus should be an enum");

        var names = Arrays.stream(enumCls.getEnumConstants())
                .map(Object::toString)
                .toList();

        assertTrue(names.containsAll(List.of("ANSWER_NOT_AVAILABLE", "ANSWER_AVAILABLE")),
                "QueryLifecycleStatus must contain UPLOADED, INGESTED, ANSWERS_AVAILABLE. Was: " + names);
    }

    @Test
    void documentIngestionPhase_enum_has_expected_values() throws Exception {
        Class<?> enumCls = Class.forName("uk.gov.hmcts.cp.openapi.model.cdk.DocumentIngestionPhase");
        assertTrue(enumCls.isEnum(), "DocumentIngestionPhase should be an enum");

        var names = Arrays.stream(enumCls.getEnumConstants())
                .map(Object::toString)
                .toList();

        assertTrue(names.containsAll(List.of("UPLOADING", "UPLOADED", "INGESTING", "INGESTED", "FAILED")),
                "DocumentIngestionPhase must contain expected states. Was: " + names);
    }


    @Test
    void answerWithLlmResponse_should_extend_answer_contract_with_llmInput_and_new_fields() throws Exception {
        Class<?> cls = Class.forName("uk.gov.hmcts.cp.openapi.model.cdk.AnswerWithLlmResponse");
        assertHasGetter(cls, "getQueryId");
        assertHasGetter(cls, "getUserQuery");
        assertHasGetter(cls, "getAnswer");
        assertHasGetter(cls, "getCreatedAt");
        assertHasGetter(cls, "getVersion");
        assertHasGetter(cls, "getLlmInput");

        // inherited/new optional fields
        assertHasGetter(cls, "getDefendantId");
        assertHasGetter(cls, "getStatus");
    }

    @Test
    void querySummary_should_have_expected_accessors_including_level() throws Exception {
        Class<?> cls = Class.forName("uk.gov.hmcts.cp.openapi.model.cdk.QuerySummary");
        assertHasGetter(cls, "getQueryId");
        assertHasGetter(cls, "getUserQuery");
        assertHasGetter(cls, "getQueryPrompt");
        assertHasGetter(cls, "getStatus");
        assertHasGetter(cls, "getEffectiveAt");
        assertHasGetter(cls, "getCaseId");
        assertHasGetter(cls, "getLevel");
    }

    @Test
    void queryUpsertRequest_query_item_should_have_level() throws Exception {
        // openapi-generator usually emits this for inline array item schema
        Class<?> inner = classOrNull("uk.gov.hmcts.cp.openapi.model.cdk.QueryUpsertRequestQueriesInner");

        // fallback if generator naming differs
        if (inner == null) {
            inner = classOrNull("uk.gov.hmcts.cp.openapi.model.cdk.QueryUpsertRequestQueries");
        }

        assertNotNull(inner, "Missing generated inner query item model for QueryUpsertRequest");
        assertHasGetter(inner, "getQueryId");
        assertHasGetter(inner, "getUserQuery");
        assertHasGetter(inner, "getQueryPrompt");
        assertHasGetter(inner, "getLevel");
    }


    @Test
    void queryLevel_enum_has_expected_values() throws Exception {
        Class<?> enumCls = Class.forName("uk.gov.hmcts.cp.openapi.model.cdk.QueryLevel");
        assertTrue(enumCls.isEnum(), "QueryLevel should be an enum");

        var names = enumNames(enumCls);
        assertTrue(names.containsAll(List.of("case", "defendant")),
                "QueryLevel must contain case and defendant. Was: " + names);
    }

    @Test
    void answerStatus_enum_has_expected_values() throws Exception {
        Class<?> enumCls = Class.forName("uk.gov.hmcts.cp.openapi.model.cdk.AnswerStatus");
        assertTrue(enumCls.isEnum(), "AnswerStatus should be an enum");

        var names = enumNames(enumCls);
        assertTrue(names.contains("ANSWER_AVAILABLE"),
                "AnswerStatus must contain ANSWER_AVAILABLE. Was: " + names);
        assertTrue(names.contains("ANSWER_NOT_AVAILABLE"),
                "AnswerStatus must contain ANSWER_NOT_AVAILABLE. Was: " + names);
        assertTrue(names.contains("IDPC_NOT_FOUND"),
                "AnswerStatus must contain IDPC_NOT_FOUND. Was: " + names);
    }

    // ---------- helpers ----------

    private static boolean classExists(String fqcn) {
        try {
            Class.forName(fqcn);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    private static void assertHasGetter(Class<?> type, String methodName) {
        boolean found = Arrays.stream(type.getMethods())
                .map(Method::getName)
                .anyMatch(methodName::equals);
        assertTrue(found, () -> "Expected getter '" + methodName + "' on " + type.getName());
    }

    private static Class<?> classOrNull(String fqcn) {
        try {
            return Class.forName(fqcn);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }


    private static List<String> enumNames(Class<?> enumClass) {
        return Arrays.stream(enumClass.getEnumConstants())
                .map(Object::toString)
                .toList();
    }
}
