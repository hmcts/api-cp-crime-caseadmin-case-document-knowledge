package uk.gov.hmcts.cp.openapi.codegen;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.media.Schema;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.hmcts.cp.config.OpenAPIConfigurationLoader;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class GeneratedApiContractsExistTest {

    private static final Logger log = LoggerFactory.getLogger(GeneratedApiContractsExistTest.class);

    @Test
    void openAPI_bean_should_have_expected_properties() {
        OpenAPIConfigurationLoader config = new OpenAPIConfigurationLoader();
        OpenAPI openAPI = config.openAPI();
        assertNotNull(openAPI);

        Info info = openAPI.getInfo();
        assertNotNull(info);
        assertEquals("Case Documents AI Responses API", info.getTitle());

        String desc = info.getDescription();
        assertNotNull(desc, "Info.description must be set");
        String lower = desc.toLowerCase();

        boolean hasVersioned = lower.contains("versioned");
        boolean hasAsOf = lower.contains("as-of");
        boolean hasQueries = lower.contains("queries") || lower.contains("query");
        boolean hasAnswers = lower.contains("answers") || lower.contains("answer");
        boolean mentionsIngestion = lower.contains("ingest");
        boolean mentionsCase = lower.contains("case");

        assertTrue(hasVersioned, "Description should mention versioned resources");
        assertTrue(hasAsOf, "Description should mention as-of views");
        assertTrue(hasQueries, "Description should mention queries");
        assertTrue(hasAnswers, "Description should mention answers");
        assertTrue(mentionsIngestion, "Description should mention ingestion");
        assertTrue(mentionsCase, "Description should mention Case context");

        String apiGitHubRepository = "api-cp-crime-caseadmin-case-document-knowledge";
        String expectedVersion = System.getProperty("API_SPEC_VERSION", "0.0.0");
        log.info("API version set to: {}", expectedVersion);
        assertEquals(expectedVersion, info.getVersion());

        License license = info.getLicense();
        assertNotNull(license);
        assertEquals("MIT", license.getName());
        assertEquals("https://opensource.org/licenses/MIT", license.getUrl());

        assertNotNull(info.getContact());
        assertEquals("no-reply@hmcts.com", info.getContact().getEmail());

        assertNotNull(openAPI.getServers());
        assertFalse(openAPI.getServers().isEmpty());
        assertEquals(
                "https://virtserver.swaggerhub.com/HMCTS-DTS/" + apiGitHubRepository + "/" + expectedVersion,
                openAPI.getServers().get(0).getUrl()
        );
    }

    @Test
    void answerResponse_schema_should_require_userQuery_and_createdAt() {
        OpenAPI openAPI = new OpenAPIConfigurationLoader().openAPI();
        Schema<?> answer = openAPI.getComponents().getSchemas().get("AnswerResponse");
        assertNotNull(answer, "AnswerResponse schema missing");

        Map<String, Schema> props = answer.getProperties();
        assertNotNull(props, "AnswerResponse properties missing");
        assertTrue(props.containsKey("userQuery"), "AnswerResponse must contain userQuery");
        assertTrue(props.containsKey("createdAt"), "AnswerResponse must contain createdAt");

        Schema<?> createdAt = props.get("createdAt");
        assertNotNull(createdAt, "createdAt schema missing");
        assertEquals("string", createdAt.getType(), "createdAt must be a string");
        assertEquals("date-time", createdAt.getFormat(), "createdAt must have format=date-time");

        assertNotNull(answer.getRequired(), "AnswerResponse.required missing");
        assertTrue(answer.getRequired().contains("userQuery"), "userQuery must be required");
        assertTrue(answer.getRequired().contains("createdAt"), "createdAt must be required");
    }

    @Test
    void loadOpenApiFromClasspath_should_throw_for_missing_resource() {
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                OpenAPIConfigurationLoader.loadOpenApiFromClasspath("nonexistent-file.yaml")
        );
        assertTrue(exception.getMessage().contains("Missing resource"));
    }

    @Test
    void loadOpenApiFromClasspath_should_throw_for_blank_path() {
        assertThrows(IllegalArgumentException.class, () ->
                OpenAPIConfigurationLoader.loadOpenApiFromClasspath(" ")
        );
    }

    @Test
    void loadOpenApiFromClasspath_should_throw_for_null_path() {
        assertThrows(IllegalArgumentException.class, () ->
                OpenAPIConfigurationLoader.loadOpenApiFromClasspath(null)
        );
    }
}
