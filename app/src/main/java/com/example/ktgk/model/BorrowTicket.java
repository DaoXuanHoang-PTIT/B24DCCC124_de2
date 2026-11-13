package com.example.ktgk.model;

import java.io.Serializable;

/**
 * Model đại diện cho thông tin một Phiếu mượn sách
 */
public class BorrowTicket implements Serializable {
    private int id;
    private String bookCode;      // Mã sách mượn
    private String bookTitle;     // Tên sách mượn (để hiển thị)
    private String borrowerName;  // Họ tên người mượn
    private String borrowerPhone; // Số điện thoại người mượn
    private String borrowDate;    // Ngày mượn (dd/MM/yyyy)
    private String returnDate;    // Ngày dự kiến trả (dd/MM/yyyy)
    private String status;        // Trạng thái phiếu: "Đang mượn" hoặc "Đã trả"

    public static final String TICKET_STATUS_BORROWING = "Đang mượn";
    public static final String TICKET_STATUS_RETURNED = "Đã trả";

    public BorrowTicket() {
    }

    public BorrowTicket(int id, String bookCode, String bookTitle, String borrowerName, String borrowerPhone, String borrowDate, String returnDate, String status) {
        this.id = id;
        this.bookCode = bookCode;
        this.bookTitle = bookTitle;
        this.borrowerName = borrowerName;
        this.borrowerPhone = borrowerPhone;
        this.borrowDate = borrowDate;
        this.returnDate = returnDate;
        this.status = status;
    }

    public BorrowTicket(String bookCode, String borrowerName, String borrowerPhone, String borrowDate, String returnDate, String status) {
        this.bookCode = bookCode;
        this.borrowerName = borrowerName;
        this.borrowerPhone = borrowerPhone;
        this.borrowDate = borrowDate;
        this.returnDate = returnDate;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBookCode() {
        return bookCode;
    }

    public void setBookCode(String bookCode) {
        this.bookCode = bookCode;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public String getBorrowerName() {
        return borrowerName;
    }

    public void setBorrowerName(String borrowerName) {
        this.borrowerName = borrowerName;
    }

    public String getBorrowerPhone() {
        return borrowerPhone;
    }

    public void setBorrowerPhone(String borrowerPhone) {
        this.borrowerPhone = borrowerPhone;
    }

    public String getBorrowDate() {
        return borrowDate;
    }

    public void setBorrowDate(String borrowDate) {
        this.borrowDate = borrowDate;
    }

    public String getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(String returnDate) {
        this.returnDate = returnDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
