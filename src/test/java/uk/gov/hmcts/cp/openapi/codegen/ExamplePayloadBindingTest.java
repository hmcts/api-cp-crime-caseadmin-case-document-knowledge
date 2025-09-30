package uk.gov.hmcts.cp.openapi.codegen;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ExamplePayloadBindingTest {

    // Enable java.time (OffsetDateTime) support for tests
    private final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    @Test
    void can_deserialize_AnswerResponse_example() throws Exception {
        String json = """
            {
              "queryId": "%s",
              "userQuery": "Summary of case based on witness statements",
              "answer": "jkhkdvlvld ::SourcePage 12",
              "version": 1,
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
        Object version = answerResponse.getMethod("getVersion").invoke(obj);

        assertNotNull(queryId);
        assertEquals("Summary of case based on witness statements", userQuery);
        assertEquals("jkhkdvlvld ::SourcePage 12", answer);
        assertEquals("2025-01-01 11:11:11", dateCreated);
        assertEquals(1, ((Number) version).intValue());
    }

    @Test
    void can_deserialize_AnswerWithLlmResponse_example() throws Exception {
        String json = """
            {
              "queryId": "%s",
              "userQuery": "Summary of case based on witness statements",
              "answer": "jkhkdvlvld",
              "version": 2,
              "llmInput": "jv,dfnv,nv.,c .,c,",
              "dateCreated": "2025-01-01 11:11:11"
            }
            """.formatted(UUID.randomUUID());

        Class<?> cls = Class.forName("uk.gov.hmcts.cp.openapi.model.AnswerWithLlmResponse");
        Object obj = mapper.readValue(json, cls);
        assertNotNull(obj);

        assertEquals("jv,dfnv,nv.,c .,c,", cls.getMethod("getLlmInput").invoke(obj));
        assertEquals(2, ((Number) cls.getMethod("getVersion").invoke(obj)).intValue());
    }

    @Test
    @SuppressWarnings("unchecked")
    void can_serialize_roundtrip_QueryStatusResponse() throws Exception {
        // Build a minimal object tree and round-trip it (status now lives per-query)
        String expectedAsOf = "2025-05-01T12:00:00Z";

        ObjectNode root = mapper.createObjectNode();
        root.put("asOf", expectedAsOf); // OffsetDateTime in model

        ObjectNode q = mapper.createObjectNode();
        q.put("queryId", UUID.randomUUID().toString());
        q.put("userQuery", "Summary of defendant based on witness statements");
        q.put("queryPrompt", "Summarise the defendant using all witness statements, focusing on timeline.");
        q.put("status", "INGESTED"); // per-query status

        root.putArray("queries").add(q);

        Class<?> respCls = Class.forName("uk.gov.hmcts.cp.openapi.model.QueryStatusResponse");
        Object resp = mapper.readValue(mapper.writeValueAsString(root), respCls);
        assertNotNull(resp);

        // Assert asOf precisely
        Object asOfObj = respCls.getMethod("getAsOf").invoke(resp);
        assertNotNull(asOfObj);
        if (asOfObj instanceof OffsetDateTime odt) {
            assertEquals(OffsetDateTime.parse(expectedAsOf), odt);
        } else {
            // Fallback if generator used String
            assertEquals(expectedAsOf, asOfObj.toString());
        }

        // Extract queries and assert fields on first item
        Object queriesObj = respCls.getMethod("getQueries").invoke(resp);
        assertNotNull(queriesObj);
        assertTrue(queriesObj instanceof List<?>);
        List<?> queries = (List<?>) queriesObj;
        assertEquals(1, queries.size());

        Object first = queries.get(0);
        Class<?> qCls = first.getClass();

        // status may be an enum; normalise to its name()
        Object status = qCls.getMethod("getStatus").invoke(first);
        String statusText = (status instanceof Enum<?> e) ? e.name() : String.valueOf(status);
        assertEquals("INGESTED", statusText);

        assertEquals(
                "Summarise the defendant using all witness statements, focusing on timeline.",
                qCls.getMethod("getQueryPrompt").invoke(first)
        );
        assertEquals(
                "Summary of defendant based on witness statements",
                qCls.getMethod("getUserQuery").invoke(first)
        );
    }
}
