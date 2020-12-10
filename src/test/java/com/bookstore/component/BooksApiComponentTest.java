package com.bookstore.component;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.http.Fault;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.ResourceUtils;

import java.io.IOException;
import java.nio.file.Files;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static io.restassured.RestAssured.when;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.StringContains.containsString;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BooksApiComponentTest {
    @LocalServerPort
    private int port;

    private static WireMockServer wireMockServer;

    @BeforeAll
    static void startWireMockServer() throws IOException {
        wireMockServer = new WireMockServer(wireMockConfig().port(8089));
        wireMockServer.start();
    }

    @AfterAll
    static void stopWireMockServer() {
        wireMockServer.stop();
    }

    @Test
    public void shouldReturnBookResponseWithPrice() throws Exception {
        wireMockServer.stubFor(get(urlEqualTo("/price"))
                .willReturn(aResponse()
                        .withBody(read("classpath:price_response.json"))
                        .withHeader(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withStatus(200)));

        when()
                .get(String.format("http://localhost:%s/book/price/123", port))
                .then()
                .log().all()
                .statusCode(is(200))
                .body(containsString("{\"id\":1,\"isbn\":\"123\",\"title\":\"Clean Code\",\"price\":10.0}"));
    }

    @Test
    public void shouldReturnBookResponseWithPriceWhenThereIsADelayOf10sFromPriceAPI() throws Exception {
        wireMockServer.stubFor(get(urlEqualTo("/price"))
                .willReturn(aResponse()
                        .withBody(read("classpath:price_response.json"))
                        .withHeader(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withStatus(200)
                        .withFixedDelay(10000)));

        when()
                .get(String.format("http://localhost:%s/book/price/123", port))
                .then()
                .log().all()
                .statusCode(is(200))
                .body(containsString("{\"id\":1,\"isbn\":\"123\",\"title\":\"Clean Code\",\"price\":10.0}"));
    }

    @Test
    public void shouldFailWhenThereIsMalformedResponseFromPriceAPI() throws Exception {
        wireMockServer.stubFor(get(urlEqualTo("/price"))
                .willReturn(aResponse()
                        .withBody(read("classpath:price_response.json"))
                        .withHeader(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withStatus(200)
                        .withFault(Fault.MALFORMED_RESPONSE_CHUNK)));

        when()
                .get(String.format("http://localhost:%s/book/price/123", port))
                .then()
                .log().all()
                .statusCode(is(500));
    }



    public static String read(String filePath) throws IOException {
        var file = ResourceUtils.getFile(filePath);
        return new String(Files.readAllBytes(file.toPath()));
    }
}
