package com.bookstore.e2e;

import com.bookstore.repositories.BookRepository;
import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static io.restassured.RestAssured.when;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.StringContains.containsString;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BookStoreE2ETest extends TestUtil {
    @LocalServerPort
    private int port;

    @Autowired
    BookRepository bookRepository;

    private static WireMockServer wireMockServer;

    @BeforeAll
    static void before() {
        startKafka();

        wireMockServer = new WireMockServer(wireMockConfig().port(8089));
        wireMockServer.start();
    }

    @AfterAll
    static void after() {
        wireMockServer.stop();
    }

    @BeforeEach
    void beforeEach() throws IOException {
        wireMockServer.stubFor(get(urlEqualTo("/price"))
                .willReturn(aResponse()
                        .withBody(read("classpath:price_response.json"))
                        .withHeader(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withStatus(200)));
    }

    @Test
    void shouldReturnBook() {
        when()
                .get(String.format("http://localhost:%s/book/price/123", port))
                .then()
                .log().all()
                .statusCode(is(200))
                .body(containsString("{\"id\":1,\"isbn\":\"123\",\"title\":\"Clean Code\",\"price\":10.0}"));
    }

    @Test
    void shouldReturnBooksListenedOnKafkaTopic() throws ExecutionException, InterruptedException, IOException {
        produceEvent("books", "156:Java Book");
        produceEvent("books", "157:Clean Code");
        await()
                .atMost(60, TimeUnit.SECONDS)
                .untilAsserted(
                        () -> {
                            Assertions.assertEquals(bookRepository.count(), 3);
                        });

        when()
                .get(String.format("http://localhost:%s/book/price/156", port))
                .then()
                .log().all()
                .statusCode(is(200))
                .body(containsString("{\"id\":2,\"isbn\":\"156\",\"title\":\"Java Book\",\"price\":10.0}"));
    }

    @Test
    void shouldFailWhenPriceAPIReturnsError() {
        when()
                .get(String.format("http://localhost:%s/book/price/160", port))
                .then()
                .log().all()
                .statusCode(is(200))
                .body(containsString("{\"error\":\"error\",\"message\":\"not found\"}"));
    }
}
