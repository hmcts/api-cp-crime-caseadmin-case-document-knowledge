package uk.gov.hmcts.cp.openapi.model.cdk;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Schema;
import org.junit.jupiter.api.Test;
import uk.gov.hmcts.cp.config.OpenAPIConfigurationLoader;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class GeneratedApiContractsExistTest {

    @Test
    void openAPI_bean_should_have_expected_properties() {
        OpenAPI openAPI = new OpenAPIConfigurationLoader().openAPI();
        Info info = openAPI.getInfo();

        String thisRepository = "api-cp-crime-caseadmin-case-document-knowledge";
        String expectedVersion = System.getProperty("API_SPEC_VERSION", "0.0.0");
        String expectedUrl = String.format("https://%s.net/{version}", thisRepository);
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
    void discoverySchedulerConfigurationRequest_schema_should_declare_required_fields_and_correct_formats() {
        OpenAPI openAPI = new OpenAPIConfigurationLoader().openAPI();
        Schema<?> schema = openAPI.getComponents().getSchemas().get("DiscoverySchedulerConfigurationRequest");

        assertThat(schema).isNotNull();
        assertThat(schema.getRequired()).containsAll(
            List.of("courtCentreId", "courtRoomId", "uploadedDate", "version", "isActive")
        );

        Map<String, Schema> properties = schema.getProperties();
        assertThat(properties).containsKeys("courtCentreId", "courtRoomId", "uploadedDate", "version", "isActive");

        assertThat(properties.get("courtCentreId").getType()).isEqualTo("string");
        assertThat(properties.get("courtCentreId").getFormat()).isEqualTo("uuid");

        assertThat(properties.get("courtRoomId").getType()).isEqualTo("string");
        assertThat(properties.get("courtRoomId").getFormat()).isEqualTo("uuid");

        assertThat(properties.get("uploadedDate").getType()).isEqualTo("string");
        assertThat(properties.get("uploadedDate").getFormat()).isEqualTo("date");

        assertThat(properties.get("version").getType()).isEqualTo("integer");
    }

    @Test
    void ingestionProcessByCaseRequest_schema_should_declare_required_fields_and_correct_formats() {
        OpenAPI openAPI = new OpenAPIConfigurationLoader().openAPI();
        Schema<?> schema = openAPI.getComponents().getSchemas().get("IngestionProcessByCaseRequest");

        assertThat(schema).isNotNull();
        assertThat(schema.getRequired()).containsAll(List.of("caseId"));

        Map<String, Schema> properties = schema.getProperties();
        assertThat(properties.get("caseId").getType()).isEqualTo("string");
        assertThat(properties.get("caseId").getFormat()).isEqualTo("uuid");
    }

    @Test
    void ingestionProcessPhase_enum_schema_should_declare_expected_values() {
        OpenAPI openAPI = new OpenAPIConfigurationLoader().openAPI();
        Schema<?> schema = openAPI.getComponents().getSchemas().get("IngestionProcessPhase");

        assertThat(schema).isNotNull();
        List<String> enumValues = new java.util.ArrayList<>();
        for (Object value : schema.getEnum()) {
            enumValues.add(String.valueOf(value));
        }
        assertThat(enumValues).containsAll(List.of("STARTED", "FAILED", "NOT_REQUIRED"));
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
