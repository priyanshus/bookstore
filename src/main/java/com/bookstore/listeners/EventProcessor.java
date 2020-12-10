package com.bookstore.listeners;

import com.bookstore.model.Book;
import com.bookstore.repositories.BookRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
class EventProcessor {
    final static Logger log = LoggerFactory.getLogger(EventProcessor.class);

    @Autowired
    BookRepository bookRepository;

    public EventProcessor(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    void processEvent(String event) {
        log.info("################################");
        log.info(String.format("Processing event -> %s", event));
        String[] details = {};
        if (event.contains(":")) {
            details = event.split(":");
            bookRepository.save(new Book(details[0], details[1]));
        }
    }
}
