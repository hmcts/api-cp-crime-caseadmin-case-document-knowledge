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
        assertEquals("API description", info.getDescription());

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
    void answerResponse_should_require_userQuery() {
        OpenAPI openAPI = new OpenAPIConfigurationLoader().openAPI();
        Schema<?> answer = openAPI.getComponents().getSchemas().get("AnswerResponse");
        assertNotNull(answer, "AnswerResponse schema missing");

        Map<String, Schema> props = answer.getProperties();
        assertNotNull(props, "AnswerResponse properties missing");
        assertTrue(props.containsKey("userQuery"), "AnswerResponse must contain userQuery");

        assertNotNull(answer.getRequired(), "AnswerResponse.required missing");
        assertTrue(answer.getRequired().contains("userQuery"), "userQuery must be required");
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
