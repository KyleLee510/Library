package com.example.library;

public class NetBook {
    String ISBN;
    String BookName;
    String Author;
    String Publishing;
    String BookCode;
    String ASIN;
    String Brand;
    String Weight;
    String Size;
    String Pages;
    String Price;
    String PhotoUrl;
    String ID;
    NetBook(String ISBN, String BookName, String Author, String Publishing, String BookCode, String ASIN, String Brand, String Weight, String Size, String Pages, String Price, String PhotoUrl, String ID) {
        this.ISBN = ISBN;
        this.BookName = BookName;
        this.Author = Author;
        this.Publishing = Publishing;
        this.BookCode = BookCode;
        this.ASIN = ASIN;
        this.Brand = Brand;
        this.Weight = Weight;
        this.Size = Size;
        this.Pages = Pages;
        this.Price = Price;
        this.PhotoUrl = PhotoUrl;
        this.ID = ID;
    }
}
