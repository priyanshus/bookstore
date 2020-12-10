package com.bookstore.controllers;

import com.bookstore.client.PriceServiceClient;
import com.bookstore.common.EntityNotFound;
import com.bookstore.model.Book;
import com.bookstore.model.PriceResponse;
import com.bookstore.repositories.BookRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class BookController {
    private BookRepository bookRepository;
    private PriceServiceClient priceServiceClient;

    public BookController(BookRepository bookRepository, PriceServiceClient priceServiceClient) {
        this.bookRepository = bookRepository;
        this.priceServiceClient = priceServiceClient;
    }

    @GetMapping("/books")
    public String getAllBooks() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        List<Book> books = (List<Book>) bookRepository.findAll();
        if (books.size() == 0) {
            return mapper.writeValueAsString(new EntityNotFound("error", "not found"));
        }
        return mapper.writeValueAsString(books);
    }

    @GetMapping("/book/{isbn}")
    public String getBookByIsbnName(@PathVariable String isbn) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        Book book = bookRepository.findByIsbn(isbn);
        if (book == null) {
            return mapper.writeValueAsString(new EntityNotFound("error", "not found"));
        }
        return mapper.writeValueAsString(book);
    }

    @GetMapping("/book/price/{isbn}")
    public String getBookPrice(@PathVariable String isbn) throws JsonProcessingException {
        Book book = bookRepository.findByIsbn(isbn);
        ObjectMapper mapper = new ObjectMapper();
        if (book == null) {
            return mapper.writeValueAsString(new EntityNotFound("error", "not found"));
        }
        PriceResponse response = priceServiceClient.fetchPrice();
        book.setPrice(response.getPrice());
        return mapper.writeValueAsString(book);
    }
}
