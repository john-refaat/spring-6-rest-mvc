package guru.springframework.spring6restmvc.controller;

import com.atlassian.oai.validator.OpenApiInteractionValidator;
import com.atlassian.oai.validator.restassured.OpenApiValidationFilter;
import com.atlassian.oai.validator.whitelist.ValidationErrorsWhitelist;
import com.atlassian.oai.validator.whitelist.rule.WhitelistRules;
import guru.springframework.spring6restmvc.config.TestConfig;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import static io.restassured.RestAssured.given;

@ActiveProfiles("restassured")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestConfig.class)
@ComponentScan(basePackages = "guru.springframework.spring6restmvc")
public class BeerControllerRestAssuredTest {

    @LocalServerPort
    Integer localPort;

    OpenApiValidationFilter filter = new OpenApiValidationFilter(
            OpenApiInteractionValidator.createForSpecificationUrl("oa3.yml")
                    .withWhitelist(ValidationErrorsWhitelist.create().withRule("Ignore Date Format",
                            WhitelistRules.messageHasKey("validation.response.body.schema.format.date-time"))
                    )
                    .build());


    @BeforeEach
    void setUp() throws Exception {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = localPort;
    }

    @Test
    void testListBeers() {
        given().contentType(ContentType.JSON)
                .when()
                .filter(filter)
                .get("/api/v1/beer")
                .then().assertThat().statusCode(200);
    }

}
