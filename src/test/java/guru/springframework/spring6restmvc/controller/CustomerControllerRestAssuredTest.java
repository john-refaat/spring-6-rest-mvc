package guru.springframework.spring6restmvc.controller;

import com.atlassian.oai.validator.OpenApiInteractionValidator;
import com.atlassian.oai.validator.restassured.OpenApiValidationFilter;
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
public class CustomerControllerRestAssuredTest {

    @LocalServerPort
    Integer localPort;

    OpenApiValidationFilter filter = new OpenApiValidationFilter(
            OpenApiInteractionValidator.createForSpecificationUrl("oa3.yml").build());


    @BeforeEach
    void setUp() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = localPort;
    }

    @Test
    void testListCustomers() {
        given().contentType(ContentType.JSON)
                .when()
                .filter(filter)
                .get("/api/v1/customer")
                .then().assertThat().statusCode(200);
    }

}
