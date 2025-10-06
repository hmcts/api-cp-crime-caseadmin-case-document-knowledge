package uk.gov.hmcts.cp.openapi.codegen;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import uk.gov.hmcts.cp.openapi.model.cdk.AnswerResponse;
import uk.gov.hmcts.cp.openapi.model.cdk.AnswerWithLlmResponse;
import uk.gov.hmcts.cp.openapi.model.cdk.QueryStatusResponse;
import uk.gov.hmcts.cp.openapi.model.cdk.QuerySummary;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.hmcts.cp.openapi.model.cdk.QueryLifecycleStatus.ANSWER_AVAILABLE;

class ExamplePayloadBindingTest {

    private final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    @Test
    void json_should_map_to_AnswerResponse_object() throws Exception {
        String json = "{\n" +
                "    \"queryId\": \"a1a6eeb3-06f5-4c33-ac48-c09d66991ca1\",\n" +
                "    \"userQuery\": \"Summary of case based on witness statements\",\n" +
                "    \"answer\": \"jkhkdvlvld ::SourcePage 12\",\n" +
                "    \"version\": 1,\n" +
                "    \"createdAt\": \"2025-01-01T11:11:11Z\"\n" +
                "}";

        AnswerResponse answerResponse = mapper.readValue(json, AnswerResponse.class);
        assertThat(answerResponse.getQueryId().toString()).isEqualTo("a1a6eeb3-06f5-4c33-ac48-c09d66991ca1");
        assertThat(answerResponse.getUserQuery()).isEqualTo("Summary of case based on witness statements");
        assertThat(answerResponse.getAnswer()).isEqualTo("jkhkdvlvld ::SourcePage 12");
        assertThat(answerResponse.getVersion()).isEqualTo(1);
        assertThat(answerResponse.getCreatedAt()).isEqualTo("2025-01-01T11:11:11Z");
    }

    @Test
    void json_should_map_to_AnswerWithLimResponse_object() throws Exception {
        String json = "{\n" +
                "    \"queryId\": \"a1a6eeb3-06f5-4c33-ac48-c09d66991ca1\",\n" +
                "    \"userQuery\": \"Summary of case based on witness statements\",\n" +
                "    \"answer\": \"jkhkdvlvld\",\n" +
                "    \"version\": 2,\n" +
                "    \"llmInput\": \"jv,dfnv,nv.,c .,c,\",\n" +
                "    \"createdAt\": \"2025-01-01T11:11:11Z\"\n" +
                "}";

        AnswerWithLlmResponse answerResponse = mapper.readValue(json, AnswerWithLlmResponse.class);
        assertThat(answerResponse.getQueryId().toString()).isEqualTo("a1a6eeb3-06f5-4c33-ac48-c09d66991ca1");
        assertThat(answerResponse.getUserQuery()).isEqualTo("Summary of case based on witness statements");
        assertThat(answerResponse.getAnswer()).isEqualTo("jkhkdvlvld");
        assertThat(answerResponse.getVersion()).isEqualTo(2);
        assertThat(answerResponse.getLlmInput()).isEqualTo("jv,dfnv,nv.,c .,c,");
        assertThat(answerResponse.getCreatedAt()).isEqualTo("2025-01-01T11:11:11Z");
    }

    @Test
    void json_should_map_to_QueryStatusResponse_object() throws Exception {
        String json = "{\n" +
                "    \"asOf\": \"2025-05-01T12:00:00Z\",\n" +
                "    \"queries\": [\n" +
                "        {\n" +
                "            \"queryId\": \"a1a6eeb3-06f5-4c33-ac48-c09d66991ca1\",\n" +
                "            \"userQuery\": \"Summary of case based on witness statements\",\n" +
                "            \"queryPrompt\": \"Summarise the case using all witness statements, focusing on timeline.\",\n" +
                "            \"status\": \"ANSWER_AVAILABLE\",\n" +
                "            \"effectiveAt\": \"2025-01-01T11:11:11Z\"\n" +
                "        }\n" +
                "    ]\n" +
                "}";

        QueryStatusResponse statusResponse = mapper.readValue(json, QueryStatusResponse.class);
        assertThat(statusResponse.getAsOf()).isEqualTo("2025-05-01T12:00:00Z");
        QuerySummary item0 = statusResponse.getQueries().get(0);
        assertThat(item0.getQueryId().toString()).isEqualTo("a1a6eeb3-06f5-4c33-ac48-c09d66991ca1");
        assertThat(item0.getUserQuery()).isEqualTo("Summary of case based on witness statements");
        assertThat(item0.getQueryPrompt()).isEqualTo("Summarise the case using all witness statements, focusing on timeline.");
        assertThat(item0.getStatus()).isEqualTo(ANSWER_AVAILABLE);
        assertThat(item0.getEffectiveAt()).isEqualTo("2025-01-01T11:11:11Z");
    }
}
