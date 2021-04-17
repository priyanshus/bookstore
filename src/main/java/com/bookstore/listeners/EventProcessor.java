package com.bookstore.listeners;

import com.bookstore.model.Book;
import com.bookstore.repositories.BookRepository;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
class EventProcessor {
    @Autowired
    BookRepository bookRepository;

    public EventProcessor(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    void addBook(String event) {
        log.info(String.format("Processing add book event -> %s", event));
        String[] details = {};
        if (event.contains(":")) {
            details = event.split(":");
            bookRepository.save(new Book(details[0], details[1]));
        }
    }

    void deleteBook(String event) {
        log.info(String.format("Processing delete book event -> %s", event));
        Book book = bookRepository.findByIsbn(event);
        if (book != null) {
            bookRepository.delete(book);
            log.info(String.format("Deleted book -> %s", event));
        }
    }
}
