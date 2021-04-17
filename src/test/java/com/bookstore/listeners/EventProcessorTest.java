package com.bookstore.listeners;

import com.bookstore.model.Book;
import com.bookstore.repositories.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(SpringRunner.class)
@DataJpaTest
class EventProcessorTest {
    @Autowired
    BookRepository bookRepository;

    private EventProcessor eventProcessor;

    @BeforeEach
    void beforeEach() {
        eventProcessor = new EventProcessor(bookRepository);
    }

    @Test
    void shouldProcessTheEvent() {
        String message = "123:Mocked";
        eventProcessor.addBook(message);
        List<Book> books = (List<Book>) bookRepository.findAll();
        assertEquals(books.get(0).getTitle(), "Mocked");
    }

    @Test
    void shouldNotProcessTheEvent() {
        eventProcessor = new EventProcessor(bookRepository);
        String message = "123Mocked";
        eventProcessor.addBook(message);
        List<Book> books = (List<Book>) bookRepository.findAll();
        assertEquals(books.size(), 0);
    }

    @Test
    void shouldDeleteBook() {
        eventProcessor = new EventProcessor(bookRepository);
        String message = "123:Mocked";
        eventProcessor.addBook(message);
        eventProcessor.deleteBook("123");
        List<Book> books = (List<Book>) bookRepository.findAll();
        assertEquals(books.size(), 0);
    }

}