package com.example.library;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import static com.example.library.AllDbChema.*;

public class SetBorrowerList {
    private SQLiteDatabase mBorrowerDatabase;
    private Context mContext;

    public SetBorrowerList(Context context, SQLiteDatabase db) {
        mContext = context;
        mBorrowerDatabase = db;
    }
    private ContentValues getContentValues(Borrower borrower) {
        ContentValues values = new ContentValues();
        values.put(BorrowTable.Cols.OnlyKey, borrower.onlykey);
        values.put(BorrowTable.Cols.ID, borrower.id);
        values.put(BorrowTable.Cols.ISBN, borrower.isbn);
        values.put(BorrowTable.Cols.BorrowDate, borrower.borrowerDate);
        values.put(BorrowTable.Cols.ReturnDate, borrower.returnDate);
        values.put(BorrowTable.Cols.isReturn, borrower.isReturn);
        values.put(BorrowTable.Cols.ReaderName, borrower.name);
        values.put(BorrowTable.Cols.Sex, borrower.sex);
        values.put(BorrowTable.Cols.BookName, borrower.bookName);
        return values;
    }
    //负责加入新数据至数据库存储
    public void addBorrower(Borrower borrower) {
        ContentValues values = getContentValues(borrower);
        mBorrowerDatabase.insert(BorrowTable.NAME, null, values);
    }
    //删除Borrow信息通过onlyKey
    public void deleteBorrower(String onlyKey) {
        String[] whereArgs = new String[] {onlyKey};
        mBorrowerDatabase.delete(BorrowTable.NAME, BorrowTable.Cols.OnlyKey + "= ?" , whereArgs);
    }
    //删除Borrow信息通过ISBN
    public void deleteBorrowerByISBN(String ISBN) {
        String[] whereArgs = new String[] {ISBN};
        mBorrowerDatabase.delete(BorrowTable.NAME, BorrowTable.Cols.ISBN + "= ?" , whereArgs);
    }
    //删除Borrow信息通过ID
    public void deleteBorrowerByID(String ID) {
        String[] whereArgs = new String[] {ID};
        mBorrowerDatabase.delete(BorrowTable.NAME, BorrowTable.Cols.ID + "= ?" , whereArgs);
    }


    public class BorrowerCursorWrapper extends CursorWrapper {
        public BorrowerCursorWrapper(Cursor cursor) {
            super(cursor);
        }

        public Borrower getBorrower() {
            String onlyKey = getString(getColumnIndex(BorrowTable.Cols.OnlyKey));
            String id = getString(getColumnIndex(BorrowTable.Cols.ID));
            String isbn = getString(getColumnIndex(BorrowTable.Cols.ISBN));
            String borrowDate = getString(getColumnIndex(BorrowTable.Cols.BorrowDate));
            String returnDtae = getString(getColumnIndex(BorrowTable.Cols.ReturnDate));
            String isReturn = getString(getColumnIndex(BorrowTable.Cols.isReturn));
            String readerName = getString(getColumnIndex(BorrowTable.Cols.ReaderName));
            String sex = getString(getColumnIndex(BorrowTable.Cols.Sex));
            String bookName = getString(getColumnIndex(BorrowTable.Cols.BookName));
            Borrower borrower = new Borrower(onlyKey, id, isbn, borrowDate, returnDtae, isReturn, readerName, sex, bookName);
            return borrower;
        }
    }

    private BorrowerCursorWrapper queryBorrowers(String whereClause, String[] whereArgs) {
        Cursor cursor = mBorrowerDatabase.query(
                BorrowTable.NAME,
                null, //null代表选择所有行
                whereClause,
                whereArgs,
                null,
                null,
                null
        );
        return new BorrowerCursorWrapper(cursor);
    }
    public List<Borrower> getBorrowers() {
        List<Borrower> borrowers = new ArrayList<>();

        BorrowerCursorWrapper cursor = queryBorrowers(null, null);
        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                borrowers.add(cursor.getBorrower());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
        return borrowers;
    }

    public List<Borrower> getBorrowersByISBN (String ISBN) {
        List<Borrower> borrowers = new ArrayList<>();
        String whereClause = BorrowTable.Cols.ISBN + " = ?";
        String[] whereArgs = new String[] {ISBN};
        BorrowerCursorWrapper cursor = queryBorrowers(whereClause, whereArgs);
        try {
            if(cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    borrowers.add(cursor.getBorrower());
                }
            }
        } finally {
            cursor.close();
        }
        return borrowers;
    }
    public List<Borrower> getBorrowersByID (String ID) {
        List<Borrower> borrowers = new ArrayList<>();
        String whereClause = BorrowTable.Cols.ID + " = ?";
        String[] whereArgs = new String[] {ID};
        BorrowerCursorWrapper cursor = queryBorrowers(whereClause, whereArgs);
        try {
            if(cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    borrowers.add(cursor.getBorrower());
                }
            }
        } finally {
            cursor.close();
        }
        return borrowers;
    }

    public Borrower queryBorrow(String onlyKey) {
        String sql =  BorrowTable.Cols.OnlyKey + " = ?";
        //String sql = "select * from readers where id=? ";
        String[] whereArgs = new String[] {onlyKey};
        BorrowerCursorWrapper cursor = queryBorrowers(sql, whereArgs);
        try {
            while (cursor.moveToNext()) {
                Borrower borrower = cursor.getBorrower();
                Log.d("擦", "ogg");
                return borrower;
            }
            Log.d("好", "ogg");
            return null;
        } finally {
            Log.d("再见", "CCC");
            cursor.close();
        }
    }

    public void upDateReturnDate(String onlyKey) {
        ContentValues values = new ContentValues();
        String[] whereArgs = new String[] {onlyKey};
        try {
            values.put(BorrowTable.Cols.ReturnDate,getDate());
            values.put(BorrowTable.Cols.isReturn, "1");
            mBorrowerDatabase.update(BorrowTable.NAME, values, BorrowTable.Cols.OnlyKey + "= ?", whereArgs);
        }catch (Exception e) {}

    }
    public String getDate() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        //获取当前时间
        Date date = new Date(System.currentTimeMillis());//若为空也表示当前时间
        String today = simpleDateFormat.format(date);
        return today;
    }


}
