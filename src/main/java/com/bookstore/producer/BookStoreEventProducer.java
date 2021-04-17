package com.bookstore.producer;

import com.bookstore.listeners.AddBookListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class BookStoreEventProducer {
    final static Logger log = LoggerFactory.getLogger(BookStoreEventProducer.class);

    private final KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    BookStoreEventProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(String message, String topicName) {
        kafkaTemplate.send(topicName, message);
        log.info(String.format("Produced event on %s", topicName));
    }
}
