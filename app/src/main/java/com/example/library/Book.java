package com.example.library;

public class Book {
    String ISBN;  //1
    String bookname; //2
    String author;  //3
    String price; //4
    int bookcover;  //5
    int inventory;  //6
    int surplus;  //7
    Book(String ISBN, String bookname, String author, String price,int bookcover, int inventory, int surplus) {

        this.bookcover = bookcover;
        this.bookname = bookname;
        this.ISBN = ISBN;
        this.author = author;
        this.price = price;
        this.inventory = inventory;
        this.surplus = surplus;
    }
}
