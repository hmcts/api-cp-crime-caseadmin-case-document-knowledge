package uk.gov.hmcts.cp.openapi.codegen;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ExamplePayloadBindingTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void can_deserialize_AnswerResponse_example() throws Exception {
        String json = """
            {
              "queryId": "%s",
              "userQuery": "Summary of case based on witness statements",
              "answer": "jkhkdvlvld ::SourcePage 12",
              "dateCreated": "2025-01-01 11:11:11"
            }
            """.formatted(UUID.randomUUID());

        Class<?> answerResponse = Class.forName("uk.gov.hmcts.cp.openapi.model.AnswerResponse");
        Object obj = mapper.readValue(json, answerResponse);
        assertNotNull(obj);

        // basic sanity via reflection getters
        Object queryId = answerResponse.getMethod("getQueryId").invoke(obj);
        Object userQuery = answerResponse.getMethod("getUserQuery").invoke(obj);
        Object answer = answerResponse.getMethod("getAnswer").invoke(obj);
        Object dateCreated = answerResponse.getMethod("getDateCreated").invoke(obj);

        assertNotNull(queryId);
        assertEquals("Summary of case based on witness statements", userQuery);
        assertEquals("jkhkdvlvld ::SourcePage 12", answer);
        assertEquals("2025-01-01 11:11:11", dateCreated);
    }

    @Test
    void can_deserialize_AnswerWithLlmResponse_example() throws Exception {
        String json = """
            {
              "queryId": "%s",
              "userQuery": "Summary of case based on witness statements",
              "answer": "jkhkdvlvld",
              "llmInput": "jv,dfnv,nv.,c .,c,",
              "dateCreated": "2025-01-01 11:11:11"
            }
            """.formatted(UUID.randomUUID());

        Class<?> cls = Class.forName("uk.gov.hmcts.cp.openapi.model.AnswerWithLlmResponse");
        Object obj = mapper.readValue(json, cls);
        assertNotNull(obj);

        assertNotNull(cls.getMethod("getLlmInput").invoke(obj));
    }

    @Test
    void can_serialize_roundtrip_QueryStatusResponse() throws Exception {
        // Build a minimal object tree and round-trip it
        ObjectNode root = mapper.createObjectNode();
        root.put("status", "INGESTED");

        ObjectNode q = mapper.createObjectNode();
        q.put("queryId", UUID.randomUUID().toString());
        q.put("userQuery", "Summary of defendant based on witness statements");
        root.putArray("queries").add(q);

        Class<?> cls = Class.forName("uk.gov.hmcts.cp.openapi.model.QueryStatusResponse");
        Object obj = mapper.readValue(mapper.writeValueAsString(root), cls);
        assertNotNull(obj);

        // ToString presence (most generators implement it)
        assertTrue(obj.toString().contains("INGESTED"));
    }
}

