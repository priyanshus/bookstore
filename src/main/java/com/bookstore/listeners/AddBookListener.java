package com.bookstore.listeners;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AddBookListener {
    @Autowired
    EventProcessor eventProcessor;

    @KafkaListener(topics = "${spring.kafka.bookstore.consumer.topic.newentry}", groupId = "${spring.kafka.bookstore.consumer.group-id}")
    public void onMessage(String message, Acknowledgment acknowledgment) {
        log.info(String.format("Received event %s", message));
        try {
            eventProcessor.addBook(message);
            log.info("Book Added");
        }catch (Exception e) {
            e.printStackTrace();
        }finally {
            acknowledgment.acknowledge();
        }
    }
}
