package com.bookstore.listeners;

import com.bookstore.model.Book;
import com.bookstore.repositories.BookRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Service
public class BookListener {
    final static Logger log = LoggerFactory.getLogger(BookListener.class);

    @Autowired
    EventProcessor eventProcessor;

    @KafkaListener(topics = "${spring.kafka.template.topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void onMessage(String message, Acknowledgment acknowledgment) throws Exception{
        log.info(String.format("Received event %s", message));

        try {
            eventProcessor.processEvent(message);
            log.info("Event Processed");
        }catch (Exception e) {
            e.printStackTrace();
        }finally {
            acknowledgment.acknowledge();
        }
    }
}
