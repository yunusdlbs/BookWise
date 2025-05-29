package com.example.bookwise.models;

import java.util.Date;

public class Book {
    private String title;
    private String author;
    private String description;
    private String imageUrl;
    private String category;
    private String pageCount;
    private int stock;

    public Book() {
        // Firebase için boş constructor gereklidir
    }

    private Date borrowedAt;

    public Date getBorrowedAt() {
        return borrowedAt;
    }

    public void setBorrowedAt(Date borrowedAt) {
        this.borrowedAt = borrowedAt;
    }

    public Book(String title, String author, String description, String imageUrl, String category, String pageCount, int stock) {
        this.title = title;
        this.author = author;
        this.description = description;
        this.imageUrl = imageUrl;
        this.category = category;
        this.stock = stock;
        this.pageCount = pageCount;
    }

    // Getter – Setter’lar
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getPageCount() {
        return pageCount;
    }

    public void setPageCount(String pageCount) {
        this.pageCount = pageCount;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

}
