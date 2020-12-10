package com.bookstore.bootstrap;

import com.bookstore.model.Book;
import com.bookstore.repositories.BookRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class BootStrapData implements CommandLineRunner {
    private final BookRepository bookRepository;

    public BootStrapData(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        Book cleanCode = new Book("123", "Clean Code");
        bookRepository.save(cleanCode);
    }
}
