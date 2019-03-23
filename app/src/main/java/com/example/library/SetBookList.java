package com.example.library;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;
import com.example.library.AllDbChema.*;

import java.util.ArrayList;
import java.util.List;

public class SetBookList {
    private SQLiteDatabase mBookDatabase;
    private Context mContext;

    public SetBookList(Context context, SQLiteDatabase db) {
        mContext = context;
        mBookDatabase = db;
    }
    private ContentValues getContentValues(Book book) {
        ContentValues values = new ContentValues();

        values.put(BookTable.Cols.ISBN, book.ISBN); //1
        values.put(BookTable.Cols.BookName, book.bookname);//2
        values.put(BookTable.Cols.Author, book.author); //3
        values.put(BookTable.Cols.Price, book.price); //4
        values.put(BookTable.Cols.BookCover, book.bookcover); //5
        values.put(BookTable.Cols.Inventory, book.inventory); //6
        values.put(BookTable.Cols.Surplus, book.surplus); //7
        return values;
    }
    //负责加入新数据至数据库存储
    public void addBook(Book book) {
        ContentValues values = getContentValues(book);
        mBookDatabase.insert(BookTable.NAME, null, values);
    }

    public class BookCursorWrapper extends CursorWrapper {
        public BookCursorWrapper(Cursor cursor) {
            super(cursor);
        }

        public Book getBook() {
            String isbn = getString(getColumnIndex(BookTable.Cols.ISBN)); //1
            String bookName = getString(getColumnIndex(BookTable.Cols.BookName)); //2
            String author = getString(getColumnIndex(BookTable.Cols.Author));  //3
            String price = getString(getColumnIndex(BookTable.Cols.Price)); //4
            int bookcover = getInt(getColumnIndex(BookTable.Cols.BookCover)); //5
            int inventory = getInt(getColumnIndex(BookTable.Cols.Inventory)); //6
            int surplus = getInt(getColumnIndex(BookTable.Cols.Surplus));  //7
            Book book = new Book(isbn, bookName, author, price, bookcover, inventory, surplus);
            return book;
        }
    }

    private SetBookList.BookCursorWrapper queryBooks(String whereClause, String[] whereArgs) {
        Cursor cursor = mBookDatabase.query(
                BookTable.NAME,
                null, //null代表选择所有行
                whereClause,
                whereArgs,
                null,
                null,
                null
        );
        return new SetBookList.BookCursorWrapper(cursor);
    }
    public List<Book> getBooks() {
        List<Book> books = new ArrayList<>();

        SetBookList.BookCursorWrapper cursor = queryBooks(null, null);
        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                books.add(cursor.getBook());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
        return books;
    }
}