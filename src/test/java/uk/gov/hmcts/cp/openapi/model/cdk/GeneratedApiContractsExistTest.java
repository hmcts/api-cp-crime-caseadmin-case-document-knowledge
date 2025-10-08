package uk.gov.hmcts.cp.openapi.model.cdk;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Schema;
import org.junit.jupiter.api.Test;
import uk.gov.hmcts.cp.config.OpenAPIConfigurationLoader;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class GeneratedApiContractsExistTest {

    @Test
    void openAPI_bean_should_have_expected_properties() {
        OpenAPI openAPI = new OpenAPIConfigurationLoader().openAPI();
        Info info = openAPI.getInfo();

        String thisRepository = "api-cp-crime-caseadmin-case-document-knowledge";
        String expectedVersion = System.getProperty("API_SPEC_VERSION", "0.0.0");
        String expectedUrl = String.format("https://virtserver.swaggerhub.com/HMCTS-DTS/%s/%s", thisRepository, expectedVersion);
        assertThat(openAPI.getServers().get(0).getUrl()).isEqualTo(expectedUrl);

        assertThat(info.getTitle()).isEqualTo("Case Documents AI Responses API");

        assertThat(info.getDescription()).contains("Versioned");
        assertThat(info.getDescription()).contains("as-of");
        assertThat(info.getDescription()).contains("Case");

        assertThat(info.getVersion()).isEqualTo(expectedVersion);

        assertThat(info.getLicense().getName()).isEqualTo("MIT");
        assertThat(info.getLicense().getUrl()).isEqualTo("https://opensource.org/licenses/MIT");

        assertThat(info.getContact().getEmail()).isEqualTo("no-reply@hmcts.com");
    }

    @Test
    void answerResponse_schema_should_require_userQuery_and_createdAt() {
        OpenAPI openAPI = new OpenAPIConfigurationLoader().openAPI();
        Schema<?> answerResponseSchema = openAPI.getComponents().getSchemas().get("AnswerResponse");

        Map<String, Schema> properties = answerResponseSchema.getProperties();
        assertThat(properties).containsKey("userQuery");
        assertThat(properties).containsKey("createdAt");

        Schema<?> createdAtSchema = properties.get("createdAt");
        assertThat(createdAtSchema.getType()).isEqualTo("string");
        assertThat(createdAtSchema.getFormat()).isEqualTo("date-time");

        assertThat(answerResponseSchema.getRequired()).contains("userQuery");
        assertThat(answerResponseSchema.getRequired()).contains("createdAt");
    }
}
