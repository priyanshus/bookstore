package com.bookstore.client;
import com.bookstore.common.RestTemplateInterceptor;
import com.bookstore.listeners.BookListener;
import com.bookstore.model.PriceResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
public class PriceServiceClient {
    final static Logger log = LoggerFactory.getLogger(PriceServiceClient
            .class);
    private RestTemplate restTemplate;

    private String priceServiceUrl;

    public PriceServiceClient(@Value("${price.service.url}") String priceServiceUrl, RestTemplateBuilder restTemplateBuilder) {
        restTemplate = restTemplateBuilder.build();
        this.priceServiceUrl = priceServiceUrl;
    }

    public PriceResponse fetchPrice() {
        String url = String.format("%s/price", priceServiceUrl);
        try {
            log.info(String.format("Processing Price Api Response %s", url));
            return restTemplate.getForObject(url, PriceResponse.class);
        } catch (RestClientException e) {
            e.printStackTrace();
        }

        log.info("Error response from Price API");
        return null;
    }
}
