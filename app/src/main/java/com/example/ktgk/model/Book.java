package com.example.ktgk.model;

import java.io.Serializable;

/**
 * Model đại diện cho thông tin một cuốn sách trong thư viện
 */
public class Book implements Serializable {
    private int id;
    private String code;     // Mã sách (Duy nhất)
    private String title;    // Tên sách
    private String author;   // Tác giả
    private String category; // Thể loại
    private String status;   // Trạng thái: "Có sẵn" hoặc "Đang được mượn"

    public static final String STATUS_AVAILABLE = "Có sẵn";
    public static final String STATUS_BORROWED = "Đang được mượn";

    public Book() {
    }

    public Book(int id, String code, String title, String author, String category, String status) {
        this.id = id;
        this.code = code;
        this.title = title;
        this.author = author;
        this.category = category;
        this.status = status;
    }

    public Book(String code, String title, String author, String category, String status) {
        this.code = code;
        this.title = title;
        this.author = author;
        this.category = category;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return code + " - " + title + " (" + status + ")";
    }
}
