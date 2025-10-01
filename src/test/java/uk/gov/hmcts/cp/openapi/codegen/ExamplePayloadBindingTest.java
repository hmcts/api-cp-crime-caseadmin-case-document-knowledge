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
              "createdAt": "2025-01-01T11:11:11Z"
            }
            """.formatted(UUID.randomUUID());

        Class<?> cls = Class.forName("uk.gov.hmcts.cp.openapi.model.cdk.AnswerResponse");
        Object obj = mapper.readValue(json, cls);
        assertNotNull(obj);

        Object queryId = cls.getMethod("getQueryId").invoke(obj);
        Object userQuery = cls.getMethod("getUserQuery").invoke(obj);
        Object answer = cls.getMethod("getAnswer").invoke(obj);
        Object createdAt = cls.getMethod("getCreatedAt").invoke(obj);
        Object version = cls.getMethod("getVersion").invoke(obj);

        assertNotNull(queryId);
        assertEquals("Summary of case based on witness statements", userQuery);
        assertEquals("jkhkdvlvld ::SourcePage 12", answer);

        if (createdAt instanceof OffsetDateTime odt) {
            assertEquals(OffsetDateTime.parse("2025-01-01T11:11:11Z"), odt);
        } else {
            assertEquals("2025-01-01T11:11:11Z", String.valueOf(createdAt));
        }

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
              "createdAt": "2025-01-01T11:11:11Z"
            }
            """.formatted(UUID.randomUUID());

        Class<?> cls = Class.forName("uk.gov.hmcts.cp.openapi.model.cdk.AnswerWithLlmResponse");
        Object obj = mapper.readValue(json, cls);
        assertNotNull(obj);

        assertEquals("jv,dfnv,nv.,c .,c,", cls.getMethod("getLlmInput").invoke(obj));
        assertEquals(2, ((Number) cls.getMethod("getVersion").invoke(obj)).intValue());

        Object createdAt = cls.getMethod("getCreatedAt").invoke(obj);
        if (createdAt instanceof OffsetDateTime odt) {
            assertEquals(OffsetDateTime.parse("2025-01-01T11:11:11Z"), odt);
        } else {
            assertEquals("2025-01-01T11:11:11Z", String.valueOf(createdAt));
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    void can_serialize_roundtrip_QueryStatusResponse() throws Exception {
        String expectedAsOf = "2025-05-01T12:00:00Z";

        ObjectNode root = mapper.createObjectNode();
        root.put("asOf", expectedAsOf);

        ObjectNode q = mapper.createObjectNode();
        q.put("queryId", UUID.randomUUID().toString());
        q.put("userQuery", "Summary of case based on witness statements");
        q.put("queryPrompt", "Summarise the case using all witness statements, focusing on timeline.");
        q.put("status", "ANSWER_AVAILABLE");
        q.put("effectiveAt", "2025-05-01T11:59:00Z");

        root.putArray("queries").add(q);

        Class<?> respCls = Class.forName("uk.gov.hmcts.cp.openapi.model.cdk.QueryStatusResponse");
        Object resp = mapper.readValue(mapper.writeValueAsString(root), respCls);
        assertNotNull(resp);

        Object asOfObj = respCls.getMethod("getAsOf").invoke(resp);
        assertNotNull(asOfObj);
        if (asOfObj instanceof OffsetDateTime odt) {
            assertEquals(OffsetDateTime.parse(expectedAsOf), odt);
        } else {
            assertEquals(expectedAsOf, asOfObj.toString());
        }

        Object queriesObj = respCls.getMethod("getQueries").invoke(resp);
        assertNotNull(queriesObj);
        assertTrue(queriesObj instanceof List<?>);
        List<?> queries = (List<?>) queriesObj;
        assertEquals(1, queries.size());

        Object first = queries.get(0);
        Class<?> qCls = first.getClass();

        Object status = qCls.getMethod("getStatus").invoke(first);
        String statusText = (status instanceof Enum<?> e) ? e.name() : String.valueOf(status);
        assertEquals("ANSWER_AVAILABLE", statusText);

        assertEquals(
                "Summarise the case using all witness statements, focusing on timeline.",
                qCls.getMethod("getQueryPrompt").invoke(first)
        );
        assertEquals(
                "Summary of case based on witness statements",
                qCls.getMethod("getUserQuery").invoke(first)
        );

        assertNotNull(qCls.getMethod("getEffectiveAt").invoke(first), "effectiveAt must be present");
    }
}
