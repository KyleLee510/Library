package com.example.library;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.library.AllDbChema.ReaderTable;
import com.example.library.AllDbChema.BookTable;
import com.example.library.AllDbChema.BorrowTable;

public class ReaderBaseHelper extends SQLiteOpenHelper {
    private static final int version = 1;
    private static final String DATABASE_NAME = "readerBase.db";

    public ReaderBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        //创建读者表(SQLite创建表字段可以不指定数据类型)
        db.execSQL("create table " + ReaderTable.NAME +
                "("
                    + ReaderTable.Cols.ID + " primary key, "
                    + ReaderTable.Cols.ReaderName + ", "
                    + ReaderTable.Cols.sex +
                ")"
        );
        //创建图书表
        db.execSQL("create table " + BookTable.NAME +
                "("
                    + BookTable.Cols.ISBN + " primary key, "
                    + BookTable.Cols.BookName  + ", "
                    + BookTable.Cols.Author + ", "
                    + BookTable.Cols.Price +", "
                    + BookTable.Cols.BookCover + ", "
                    + BookTable.Cols.Inventory + " integer, "
                    + BookTable.Cols.Surplus + " integer" +
                ")"
        );

        //创建借阅表, 外键分别为读者ID和ISBN的ID,不用外键找匹配就是
        db.execSQL("create table " + BorrowTable.NAME +
                "("
                    + BorrowTable.Cols.OnlyKey  + " NOT NULL primary key,"
                    + BorrowTable.Cols.ID + ","
                    + BorrowTable.Cols.ISBN + ","
                    + BorrowTable.Cols.BorrowDate + " ,"
                    + BorrowTable.Cols.isReturn + ","
                    + BorrowTable.Cols.ReturnDate + " ,"
                    + BorrowTable.Cols.ReaderName + ","
                    + BorrowTable.Cols.Sex + ","
                    + BorrowTable.Cols.BookName +
                ")"
        );
        /*
         + "_num integer primary key autoincrement, "
        + "FOREIGN KEY " + "(" + BorrowTable.Cols.ID + ") REFERENCES "
                + ReaderTable.NAME + "(" + ReaderTable.Cols.ID + ")"+ ","
                + "FOREIGN KEY " + "(" + BorrowTable.Cols.ISBN + ") REFERENCES "
                + BookTable.NAME + "(" + BookTable.Cols.ISBN + ")"+     */

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //db.execSQL("drop if table exists readers");
        //db.execSQL("drop if table exists  books");
        ///onCreate(db);
    }
}
