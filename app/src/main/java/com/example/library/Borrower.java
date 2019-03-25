package com.example.library;

public class Borrower {
    String onlykey;
    String id;
    String isbn;
    String name;
    String sex;
    String borrowerDate;
    String returnDate;
    String isReturn;
    String bookName;
    Borrower(String onlykey, String id, String isbn, String borrowerDate, String returnDate, String isReturn, String name, String sex, String bookName) {
        this.onlykey = onlykey;
        this.id = id;
        this.isbn = isbn;
        this.name = name;
        this.sex = sex;
        this.borrowerDate = borrowerDate;
        this.returnDate = returnDate;
        this.isReturn = isReturn;
        this.bookName = bookName;
    }
}
