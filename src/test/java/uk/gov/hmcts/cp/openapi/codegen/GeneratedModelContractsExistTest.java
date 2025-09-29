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
                "uk.gov.hmcts.cp.openapi.model.PipelineStatus"
        );

        for (String fqcn : models) {
            assertTrue(classExists(fqcn), "Missing generated model: " + fqcn);
        }

        // Error model may be called 'Error' (your spec) — avoid direct import to prevent confusion with java.lang.Error
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
