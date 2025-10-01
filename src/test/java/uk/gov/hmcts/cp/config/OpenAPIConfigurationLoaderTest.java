package uk.gov.hmcts.cp.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.media.Schema;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class OpenAPIConfigurationLoaderTest {

    private static final Logger log = LoggerFactory.getLogger(OpenAPIConfigurationLoaderTest.class);

    @Test
    void openAPI_bean_should_have_expected_properties() {
        OpenAPI openAPI = new OpenAPIConfigurationLoader().openAPI();
        assertNotNull(openAPI);

        Info info = openAPI.getInfo();
        assertNotNull(info);
        assertEquals("Case Documents AI Responses API", info.getTitle());

        // Resilient checks (wording may change slightly)
        String desc = info.getDescription();
        assertNotNull(desc);
        String lower = desc.toLowerCase();
        assertTrue(lower.contains("versioned"), "Description should mention versioned resources");
        assertTrue(lower.contains("as-of"), "Description should mention as-of views");
        assertTrue(lower.contains("case"), "Description should mention Case context");
        assertTrue(lower.contains("ingest"), "Description should mention ingestion pipeline");

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
        Exception ex = assertThrows(IllegalArgumentException.class,
                () -> OpenAPIConfigurationLoader.loadOpenApiFromClasspath("nonexistent-file.yaml"));
        assertTrue(ex.getMessage().contains("Missing resource"));
    }

    @Test
    void loadOpenApiFromClasspath_should_throw_for_blank_path() {
        assertThrows(IllegalArgumentException.class,
                () -> OpenAPIConfigurationLoader.loadOpenApiFromClasspath(" "));
    }

    @Test
    void loadOpenApiFromClasspath_should_throw_for_null_path() {
        assertThrows(IllegalArgumentException.class,
                () -> OpenAPIConfigurationLoader.loadOpenApiFromClasspath(null));
    }
}
