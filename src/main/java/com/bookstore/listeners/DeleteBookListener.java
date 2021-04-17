package com.bookstore.listeners;

import com.bookstore.producer.BookStoreEventProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Service
public class DeleteBookListener {
    final static Logger log = LoggerFactory.getLogger(DeleteBookListener.class);

    @Value("${spring.kafka.bookstore.producer.topic.consumer-one}")
    private String consumerOneTopic;

    @Autowired
    EventProcessor eventProcessor;

    @Autowired
    BookStoreEventProducer eventProducer;

    @KafkaListener(topics = "${spring.kafka.bookstore.consumer.topic.removeentry}", groupId = "${spring.kafka.bookstore.consumer.group-id}")
    public void onMessage(String message, Acknowledgment acknowledgment) {
        log.info(String.format("Received event %s", message));
        try {
            eventProcessor.deleteBook(message);
            eventProducer.sendMessage(message, consumerOneTopic);
            log.info("Book deleted and produced on topic: " + consumerOneTopic);
        }catch (Exception e) {
            e.printStackTrace();
        }finally {
            acknowledgment.acknowledge();
        }
    }
}
