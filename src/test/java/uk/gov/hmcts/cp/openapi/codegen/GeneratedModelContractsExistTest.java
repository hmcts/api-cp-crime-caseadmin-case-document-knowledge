package uk.gov.hmcts.cp.openapi.codegen;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GeneratedModelContractsExistTest {

    @Test
    void generated_models_should_exist() {
        List<String> models = List.of(
                "uk.gov.hmcts.cp.openapi.model.AnswerResponse",
                "uk.gov.hmcts.cp.openapi.model.AnswerWithLlmResponse",
                "uk.gov.hmcts.cp.openapi.model.QueryStatusResponse",
                "uk.gov.hmcts.cp.openapi.model.QuerySummary",
                "uk.gov.hmcts.cp.openapi.model.IngestionStatus",   // <- renamed
                "uk.gov.hmcts.cp.openapi.model.QueryUpsertRequest" // <- new in spec
        );

        for (String fqcn : models) {
            assertTrue(classExists(fqcn), "Missing generated model: " + fqcn);
        }

        // Error model may be called 'Error' (your spec)
        assertTrue(classExists("uk.gov.hmcts.cp.openapi.model.Error"), "Missing Error model");
    }

    @Test
    void answerResponse_should_have_expected_accessors() throws Exception {
        Class<?> cls = Class.forName("uk.gov.hmcts.cp.openapi.model.AnswerResponse");
        assertHasGetter(cls, "getQueryId");
        assertHasGetter(cls, "getUserQuery");
        assertHasGetter(cls, "getAnswer");
        assertHasGetter(cls, "getDateCreated");
    }

    @Test
    void answerWithLlmResponse_should_extend_answer_contract_with_llmInput() throws Exception {
        Class<?> cls = Class.forName("uk.gov.hmcts.cp.openapi.model.AnswerWithLlmResponse");
        assertHasGetter(cls, "getQueryId");
        assertHasGetter(cls, "getUserQuery");
        assertHasGetter(cls, "getAnswer");
        assertHasGetter(cls, "getDateCreated");
        assertHasGetter(cls, "getLlmInput");
    }

    @Test
    void querySummary_should_have_expected_accessors() throws Exception {
        Class<?> cls = Class.forName("uk.gov.hmcts.cp.openapi.model.QuerySummary");
        assertHasGetter(cls, "getQueryId");
        assertHasGetter(cls, "getUserQuery");
    }

    @Test
    void ingestionStatus_enum_has_expected_values() throws Exception {
        Class<?> enumCls = Class.forName("uk.gov.hmcts.cp.openapi.model.IngestionStatus");
        assertTrue(enumCls.isEnum(), "IngestionStatus should be an enum");

        var names = Arrays.stream(enumCls.getEnumConstants())
                .map(Object::toString)
                .toList();

        assertTrue(names.containsAll(List.of("UPLOADED", "INGESTED", "ANSWERS_AVAILABLE")),
                "IngestionStatus must contain UPLOADED, INGESTED, ANSWERS_AVAILABLE. Was: " + names);
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
