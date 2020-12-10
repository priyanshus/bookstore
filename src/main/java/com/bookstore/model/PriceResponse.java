package com.bookstore.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PriceResponse {
    private String type;
    private String title;
    private String id;
    private String isbn;
    private Double price;

    public PriceResponse() {
    }

    public PriceResponse(String type, String title, String id, String isbn, Double price) {
        this.type = type;
        this.title = title;
        this.id = id;
        this.isbn = isbn;
        this.price = price;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "PriceResponse{" +
                "type='" + type + '\'' +
                ", title='" + title + '\'' +
                ", id='" + id + '\'' +
                ", isbn='" + isbn + '\'' +
                ", price=" + price +
                '}';
    }
}


