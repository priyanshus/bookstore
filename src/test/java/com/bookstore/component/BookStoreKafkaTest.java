package com.bookstore.component;

import com.bookstore.component.util.TestKafkaConsumer;
import com.bookstore.component.util.TestUtil;
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
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static io.restassured.RestAssured.when;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.StringContains.containsString;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;

@Tag("component-test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BookStoreKafkaTest extends TestUtil {
    @LocalServerPort
    private int port;

    @Autowired
    BookRepository bookRepository;

    private static WireMockServer wireMockServer;

    @BeforeAll
    static void before() {
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
    void shouldAddBookWhenEventReceivedForNewEntry() throws ExecutionException, InterruptedException {
        produceEvent("new.entry", "156:Java Book");
        produceEvent("new.entry", "157:Clean Code");
        await()
                .atMost(20, TimeUnit.SECONDS)
                .untilAsserted(
                        () -> {
                            Assertions.assertEquals(bookRepository.count(), 3);
                        });

        when()
                .get(String.format("http://localhost:%s/book/price/156", port))
                .then()
                .log().all()
                .statusCode(is(200))
                .body(containsString("\"isbn\":\"156\",\"title\":\"Java Book\",\"price\":10.0}"));
    }

    @Test
    void shouldDeleteBookWhenEventReceivedToDeleteBook() throws ExecutionException, InterruptedException {
        produceEvent("remove.entry", "123");

        List<String> eventsReceived = consumeEvents("consumer-one");
        Assertions.assertEquals(1, eventsReceived.size(), "Events Received Count");
        Assertions.assertEquals("123", eventsReceived.get(0), "Event Content");
    }

    List<String> consumeEvents(String topic) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        TestKafkaConsumer kafkaConsumer = new TestKafkaConsumer(consumer,topic, 1, latch);
        kafkaConsumer.start();
        latch.await(20, TimeUnit.SECONDS);
        return kafkaConsumer.getMessagesReceived();
    }
}
