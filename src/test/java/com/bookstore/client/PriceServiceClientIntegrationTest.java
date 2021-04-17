package com.bookstore.client;

import com.bookstore.model.PriceResponse;
import com.github.tomakehurst.wiremock.WireMockServer;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;

@SpringBootTest
@Tag("integration-test")
public class PriceServiceClientIntegrationTest {
    @Autowired
    private PriceServiceClient subject;

    private static WireMockServer wireMockServer;

    @BeforeAll
    static void startWireMockServer() {
        wireMockServer = new WireMockServer(wireMockConfig().port(8089));
        wireMockServer.start();
    }

    @AfterAll
    static void stopWireMockServer() {
        wireMockServer.stop();
    }

    @Test
    public void shouldCallWeatherService() throws Exception {
        wireMockServer.stubFor(get(urlEqualTo("/price"))
                .willReturn(aResponse()
                        .withBody(read("classpath:price_response.json"))
                        .withHeader(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withStatus(200)));
        PriceResponse weatherResponse = subject.fetchPrice();
        assertThat(weatherResponse.getPrice(), is(10.00));
    }

    public String read(String filePath) throws IOException {
        File file = ResourceUtils.getFile(filePath);
        return new String(Files.readAllBytes(file.toPath()));
    }
}
