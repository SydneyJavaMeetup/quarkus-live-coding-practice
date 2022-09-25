package meetup.sydney.java;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
public class GreetingResourceTest {

    @Test
    public void testHelloEndpoint() {
        given()
                .when().get("/meetup/clear-greetings")
                .then()
                .statusCode(200)
                .body(containsString("Greetings cleared"));


        given()
                .when().get("/meetup/insert-greeting?greeting=Hello Testing!")
                .then()
                .statusCode(200)
                .body(containsString("Hello Testing!"));

        given()
          .when().get("/meetup")
          .then()
             .statusCode(200)
             .body(is("Hello Testing!"));
    }

}