package com.bookstore.client;

import com.bookstore.config.CommonConfig;
import com.bookstore.model.PriceResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;

import java.io.IOException;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withException;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RunWith(SpringRunner.class)
@RestClientTest(PriceServiceClient.class)
class PriceServiceClientTest {
    @Autowired
    private PriceServiceClient client;

    @Autowired
    private MockRestServiceServer server;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${price.service.url}")
    private String priceServiceUrl;

    @Test
    public void shouldReturnCorrectPriceResponse()
            throws Exception {
        String priceResponse =
                objectMapper.writeValueAsString(new PriceResponse("type", "booktitle", "id", "isbn", 12.09));
        server.expect(ExpectedCount.once(), requestTo(priceServiceUrl + "/price"))
                .andRespond(withSuccess(priceResponse, MediaType.APPLICATION_JSON));

        PriceResponse actualResponse = client.fetchPrice();
        Assert.assertEquals("id", actualResponse.getId());
    }

    @Test
    public void shouldBeNullFieldsInPriceResponseWhenUnexpectedResponse() {
        String json = "{\"key\" : \"value\"}";

        server.expect(ExpectedCount.once(), requestTo(priceServiceUrl + "/price"))
                .andRespond(withSuccess(json, MediaType.APPLICATION_JSON));
        PriceResponse actualResponse = client.fetchPrice();
        Assert.assertNull(actualResponse.getId());
    }

    @Test
    public void shouldBeNullWhenExceptionThrownFromClient() {
        server.expect(ExpectedCount.once(), requestTo(priceServiceUrl + "/price"))
                .andRespond(withException(new IOException("Issue with Service")));
        PriceResponse actualResponse = client.fetchPrice();
        Assert.assertNull(actualResponse);
    }
}