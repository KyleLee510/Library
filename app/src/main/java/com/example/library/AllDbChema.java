package com.example.library;
//描述数据表的条目
public class AllDbChema {
    public static final class ReaderTable {
        public static final String NAME = "readers";
        public static final class Cols {
            public static final String ID = "id";
            public static final String ReaderName = "readerName";
            public static final String sex = "sex";
        }
    }
    public static final class BookTable {
        public static final String NAME = "books";
        public static final class Cols {
            public static final String ISBN = "isbn";           //1
            public static final String BookName = "bookName";   //2
            public static final String Author = "author";       //3
            public static final String Price = "price";         //4
            public static final String BookCover = "bookCover"; //5
            public static final String Inventory = "inventory"; //6
            public static final String Surplus = "surplus";     //7
        }
    }

    public static final class BorrowTable {
        public static final String NAME = "borrow";
        public static final class Cols {
            public static final String ID = "id";       //作为外键
            public static final String ISBN = "isbn";  // 作为外键
            public static final String BorrowDate = "borrowDate";
            public static final String ReturnDate = "returnDate";
            public static final String isReturn = "isReturn";
        }
    }
}
