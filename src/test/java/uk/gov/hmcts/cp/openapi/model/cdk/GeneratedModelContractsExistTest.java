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
                "uk.gov.hmcts.cp.openapi.model.cdk.ErrorResponse"
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
}
