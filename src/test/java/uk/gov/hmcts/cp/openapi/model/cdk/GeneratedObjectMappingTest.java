package uk.gov.hmcts.cp.openapi.model.cdk;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.hmcts.cp.openapi.model.cdk.QueryLifecycleStatus.ANSWER_AVAILABLE;

class GeneratedObjectMappingTest {

    private final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    @Test
    void json_should_map_toErrorResponse_object() throws JsonProcessingException {
        String json = "{\n" +
                "  \"error\": \"BAD_REQUEST\",\n" +
                "  \"message\": \"Bad client request\",\n" +
                "  \"timestamp\": \"2025-01-01T11:11:11Z\",\n" +
                "  \"traceId\": \"a1b2c3d4e5f6g7h8\"\n" +
                "}";

        ErrorResponse errorResponse = mapper.readValue(json, ErrorResponse.class);
        assertThat(errorResponse.getError()).isEqualTo("BAD_REQUEST");
        assertThat(errorResponse.getMessage()).isEqualTo("Bad client request");
        assertThat(toText(errorResponse.getTimestamp())).isEqualTo("2025-01-01T11:11:11Z");
        assertThat(errorResponse.getTraceId()).isEqualTo("a1b2c3d4e5f6g7h8");
    }

    @Test
    void json_should_map_to_AnswerResponse_array() throws JsonProcessingException {
        String json = "[\n" +
                "  {\n" +
                "    \"queryId\": \"a1a6eeb3-06f5-4c33-ac48-c09d66991ca1\",\n" +
                "    \"userQuery\": \"Summary of case based on witness statements\",\n" +
                "    \"answer\": \"jkhkdvlvld ::SourcePage 12\",\n" +
                "    \"version\": 1,\n" +
                "    \"createdAt\": \"2025-01-01T11:11:11Z\",\n" +
                "    \"defendantId\": \"def-123\",\n" +
                "    \"status\": \"ANSWER_AVAILABLE\"\n" +
                "  }\n" +
                "]";

        List<AnswerResponse> items = mapper.readValue(json, new TypeReference<List<AnswerResponse>>() {});
        assertThat(items).hasSize(1);

        AnswerResponse answerResponse = items.get(0);
        assertThat(answerResponse.getQueryId().toString()).isEqualTo("a1a6eeb3-06f5-4c33-ac48-c09d66991ca1");
        assertThat(answerResponse.getUserQuery()).isEqualTo("Summary of case based on witness statements");
        assertThat(answerResponse.getAnswer()).isEqualTo("jkhkdvlvld ::SourcePage 12");
        assertThat(answerResponse.getVersion()).isEqualTo(1);
        assertThat(toText(answerResponse.getCreatedAt())).isEqualTo("2025-01-01T11:11:11Z");
        assertThat(answerResponse.getDefendantId()).isEqualTo("def-123");
        assertThat(answerResponse.getStatus().toString()).isEqualTo("ANSWER_AVAILABLE");
    }

    @Test
    void json_should_map_to_AnswerWithLlmResponse_array() throws JsonProcessingException {
        String json = "[\n" +
                "  {\n" +
                "    \"queryId\": \"a1a6eeb3-06f5-4c33-ac48-c09d66991ca1\",\n" +
                "    \"userQuery\": \"Summary of case based on witness statements\",\n" +
                "    \"answer\": \"jkhkdvlvld\",\n" +
                "    \"version\": 2,\n" +
                "    \"llmInput\": \"jv,dfnv,nv.,c .,c,\",\n" +
                "    \"createdAt\": \"2025-01-01T11:11:11Z\",\n" +
                "    \"status\": \"ANSWER_AVAILABLE\"\n" +
                "  }\n" +
                "]";

        List<AnswerWithLlmResponse> items = mapper.readValue(json, new TypeReference<List<AnswerWithLlmResponse>>() {});
        assertThat(items).hasSize(1);

        AnswerWithLlmResponse answerResponse = items.get(0);
        assertThat(answerResponse.getQueryId().toString()).isEqualTo("a1a6eeb3-06f5-4c33-ac48-c09d66991ca1");
        assertThat(answerResponse.getUserQuery()).isEqualTo("Summary of case based on witness statements");
        assertThat(answerResponse.getAnswer()).isEqualTo("jkhkdvlvld");
        assertThat(answerResponse.getVersion()).isEqualTo(2);
        assertThat(answerResponse.getLlmInput()).isEqualTo("jv,dfnv,nv.,c .,c,");
        assertThat(toText(answerResponse.getCreatedAt())).isEqualTo("2025-01-01T11:11:11Z");
        assertThat(answerResponse.getStatus().toString()).isEqualTo("ANSWER_AVAILABLE");
    }

    @Test
    void json_should_map_to_QueryStatusResponse_object_with_level() throws JsonProcessingException {
        String json = "{\n" +
                "  \"asOf\": \"2025-05-01T12:00:00Z\",\n" +
                "  \"queries\": [\n" +
                "    {\n" +
                "      \"queryId\": \"a1a6eeb3-06f5-4c33-ac48-c09d66991ca1\",\n" +
                "      \"userQuery\": \"Summary of case based on witness statements\",\n" +
                "      \"queryPrompt\": \"Summarise the case using all witness statements, focusing on timeline.\",\n" +
                "      \"status\": \"ANSWER_AVAILABLE\",\n" +
                "      \"effectiveAt\": \"2025-01-01T11:11:11Z\",\n" +
                "      \"level\": \"DEFENDANT\"\n" +
                "    }\n" +
                "  ]\n" +
                "}";

        QueryStatusResponse statusResponse = mapper.readValue(json, QueryStatusResponse.class);
        assertThat(toText(statusResponse.getAsOf())).isEqualTo("2025-05-01T12:00Z");

        QuerySummary item0 = statusResponse.getQueries().get(0);
        assertThat(item0.getQueryId().toString()).isEqualTo("a1a6eeb3-06f5-4c33-ac48-c09d66991ca1");
        assertThat(item0.getUserQuery()).isEqualTo("Summary of case based on witness statements");
        assertThat(item0.getQueryPrompt()).isEqualTo("Summarise the case using all witness statements, focusing on timeline.");
        assertThat(item0.getStatus()).isEqualTo(ANSWER_AVAILABLE);
        assertThat(toText(item0.getEffectiveAt())).isEqualTo("2025-01-01T11:11:11Z");
        assertThat(item0.getLevel().toString()).isEqualTo("DEFENDANT");
    }

    @Test
    void json_should_map_negative_answer_item_with_status_only() throws JsonProcessingException {
        String json = "[\n" +
                "  {\n" +
                "    \"queryId\": \"2a9ae797-7f70-4be5-927f-2dae65489e69\",\n" +
                "    \"userQuery\": \"give me a chronology of the facts of the offences\",\n" +
                "    \"status\": \"ANSWER_NOT_AVAILABLE\",\n" +
                "    \"defendantId\": \"def-999\"\n" +
                "  }\n" +
                "]";

        List<AnswerResponse> items = mapper.readValue(json, new TypeReference<List<AnswerResponse>>() {});
        assertThat(items).hasSize(1);

        AnswerResponse item = items.get(0);
        assertThat(item.getQueryId().toString()).isEqualTo("2a9ae797-7f70-4be5-927f-2dae65489e69");
        assertThat(item.getUserQuery()).isEqualTo("give me a chronology of the facts of the offences");
        assertThat(item.getStatus().toString()).isEqualTo("ANSWER_NOT_AVAILABLE");
        assertThat(item.getDefendantId()).isEqualTo("def-999");
        assertThat(item.getAnswer()).isNull();
        assertThat(item.getVersion()).isNull();
        assertThat(item.getCreatedAt()).isNull();
    }

    @Test
    void json_should_map_to_DiscoverySchedulerConfigurationRequest_object() throws JsonProcessingException {
        String json = "{\n" +
                "  \"courtCentreId\": \"f8254db1-1683-483e-afb3-b87fde5a0a26\",\n" +
                "  \"courtRoomId\": \"9e4932f7-97b2-3010-b942-ddd2624e4dd8\",\n" +
                "  \"uploadedDate\": \"2026-06-16\",\n" +
                "  \"version\": 1,\n" +
                "  \"isActive\": true\n" +
                "}";

        DiscoverySchedulerConfigurationRequest request = mapper.readValue(json, DiscoverySchedulerConfigurationRequest.class);
        assertThat(request.getCourtCentreId().toString()).isEqualTo("f8254db1-1683-483e-afb3-b87fde5a0a26");
        assertThat(request.getCourtRoomId().toString()).isEqualTo("9e4932f7-97b2-3010-b942-ddd2624e4dd8");
        assertThat(toText(request.getUploadedDate())).isEqualTo("2026-06-16");
        assertThat(request.getVersion()).isEqualTo(1);
        assertThat(request.getIsActive()).isTrue();
    }

    @Test
    void json_should_map_to_DiscoverySchedulerConfigurationRequest_with_isActive_false() throws JsonProcessingException {
        String json = "{\n" +
                "  \"courtCentreId\": \"f8254db1-1683-483e-afb3-b87fde5a0a26\",\n" +
                "  \"courtRoomId\": \"9e4932f7-97b2-3010-b942-ddd2624e4dd8\",\n" +
                "  \"uploadedDate\": \"2026-06-17\",\n" +
                "  \"version\": 2,\n" +
                "  \"isActive\": false\n" +
                "}";

        DiscoverySchedulerConfigurationRequest request = mapper.readValue(json, DiscoverySchedulerConfigurationRequest.class);
        assertThat(request.getVersion()).isEqualTo(2);
        assertThat(request.getIsActive()).isFalse();
    }

    @Test
    void json_should_map_to_DiscoveryTriggerRequest_intraday() throws JsonProcessingException {
        String json = "{\n" +
                "  \"discoveryOperation\": \"INTRADAY\"\n" +
                "}";

        DiscoveryTriggerRequest request = mapper.readValue(json, DiscoveryTriggerRequest.class);
        assertThat(toText(request.getDiscoveryOperation())).isEqualTo("INTRADAY");
    }

    @Test
    void json_should_map_to_DiscoveryTriggerRequest_nightly() throws JsonProcessingException {
        String json = "{\n" +
                "  \"discoveryOperation\": \"NIGHTLY\"\n" +
                "}";

        DiscoveryTriggerRequest request = mapper.readValue(json, DiscoveryTriggerRequest.class);
        assertThat(toText(request.getDiscoveryOperation())).isEqualTo("NIGHTLY");
    }

    @Test
    void json_should_map_to_DiscoveryTriggerResponse_object() throws JsonProcessingException {
        String json = "{\n" +
                "  \"discoveryOperation\": \"NIGHTLY\",\n" +
                "  \"message\": \"Discovery run dispatched.\",\n" +
                "  \"correlationId\": \"8f14e45f-ceea-467e-adc5-5b3a4dee8a5a\"\n" +
                "}";

        DiscoveryTriggerResponse response = mapper.readValue(json, DiscoveryTriggerResponse.class);
        assertThat(toText(response.getDiscoveryOperation())).isEqualTo("NIGHTLY");
        assertThat(response.getMessage()).isEqualTo("Discovery run dispatched.");
        assertThat(response.getCorrelationId()).isEqualTo("8f14e45f-ceea-467e-adc5-5b3a4dee8a5a");
    }

    @Test
    void json_should_map_to_DiscoveryTriggerResponse_without_correlationId() throws JsonProcessingException {
        String json = "{\n" +
                "  \"discoveryOperation\": \"INTRADAY\",\n" +
                "  \"message\": \"Discovery run dispatched.\"\n" +
                "}";

        DiscoveryTriggerResponse response = mapper.readValue(json, DiscoveryTriggerResponse.class);
        assertThat(toText(response.getDiscoveryOperation())).isEqualTo("INTRADAY");
        assertThat(response.getCorrelationId()).isNull();
    }

    @Test
    void json_should_map_to_IngestionProcessByCaseRequest_object() throws JsonProcessingException {
        String json = "{\n" +
                "  \"caseId\": \"c0b2d8c2-3d62-4d7c-a2f5-1a4efc1d0c3b\"\n" +
                "}";

        IngestionProcessByCaseRequest request = mapper.readValue(json, IngestionProcessByCaseRequest.class);
        assertThat(request.getCaseId().toString()).isEqualTo("c0b2d8c2-3d62-4d7c-a2f5-1a4efc1d0c3b");
    }

    @Test
    void json_should_map_to_IngestionProcessResponse_with_notRequired_phase() throws JsonProcessingException {
        String json = "{\n" +
                "  \"phase\": \"NOT_REQUIRED\",\n" +
                "  \"lastUpdated\": \"2026-06-29T10:15:23.256710385Z\",\n" +
                "  \"message\": \"Ingestion process not started because no newer IDPC version is available and an Answers version already exists.\"\n" +
                "}";

        IngestionProcessResponse response = mapper.readValue(json, IngestionProcessResponse.class);
        assertThat(response.getPhase().toString()).isEqualTo("NOT_REQUIRED");
        assertThat(toText(response.getLastUpdated())).isEqualTo("2026-06-29T10:15:23.256710385Z");
        assertThat(response.getMessage()).isEqualTo("Ingestion process not started because no newer IDPC version is available and an Answers version already exists.");
    }

    private static String toText(Object value) {
        return value == null ? null : value.toString();
    }
}
