package com.bookstore.controllers;

import com.bookstore.client.PriceServiceClient;
import com.bookstore.model.Book;
import com.bookstore.model.PriceResponse;
import com.bookstore.repositories.BookRepository;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = BookController.class)
class BookControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookRepository bookRepository;

    @MockBean
    private PriceServiceClient priceServiceClient;

    @Test
    void shouldReturnAllBooks() throws Exception {
        Book book = new Book("121212", "A Book");
        ArrayList<Book> books = new ArrayList<>();
        books.add(book);

        given(bookRepository.findAll()).willReturn(books);

        mockMvc.perform(get("/books"))
                .andDo(print())
                .andExpect(content().json("[{\"id\":" + null + ", \"isbn\":\"121212\",\"title\":\"A Book\"}]"))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void shouldReturnBookByIsbn() throws Exception {
        Book book = new Book("121212", "A Book");
        given(bookRepository.findByIsbn("121212")).willReturn(book);

        mockMvc.perform(get("/book/{isbn}", "121212"))
                .andDo(print())
                .andExpect(content().json("{\"id\":" + null + ", \"isbn\":\"121212\",\"title\":\"A Book\"}"))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void shouldReturnBookNotFoundWhenIsbnIsNotAvailable() throws Exception {
        mockMvc.perform(get("/book/{isbn}", "123"))
                .andExpect(content().json("{\"error\":\"error\",\"message\":\"not found\"}"))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void shouldReturnBookPriceWhenIsbnIsCorrect() throws Exception {
        Book book = new Book("999", "Some Book Title");
        given(bookRepository.findByIsbn("999")).willReturn(book);
        given(priceServiceClient.fetchPrice()).willReturn(new PriceResponse("booktype", "some book title", "12", "123", 13.90));

        mockMvc.perform(get("/book/price/{isbn}", "999"))
                .andExpect(content().json("{\"id\":" + null + ", \"isbn\":\"999\",\"title\":\"Some Book Title\", \"price\":13.90}"))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void shouldReturnNotBookFoundIfIsbnIsInvalid() throws Exception {
        mockMvc.perform(get("/book/price/{isbn}", "888"))
                .andExpect(content().json("{\"error\":\"error\",\"message\":\"not found\"}"))
                .andExpect(status().is2xxSuccessful());
    }
}