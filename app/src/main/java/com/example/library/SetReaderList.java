package com.example.library;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;
import static com.example.library.AllDbChema.*;

public class SetReaderList {
    private SQLiteDatabase mReaderDatabase;
    private Context mContext;

    public SetReaderList(Context context, SQLiteDatabase db) {
        mContext = context;
        mReaderDatabase = db;
    }
    private ContentValues getContentValues(Reader reader) {
        ContentValues values = new ContentValues();
        values.put(ReaderTable.Cols.ID, reader.ID);
        values.put(ReaderTable.Cols.ReaderName, reader.readerName);
        values.put(ReaderTable.Cols.sex, reader.sex);
        return values;
    }
    //负责加入新数据至数据库存储
    public void addReader(Reader reader) {
        ContentValues values = getContentValues(reader);
        mReaderDatabase.insert(ReaderTable.NAME, null, values);
    }

    public class ReaderCursorWrapper extends CursorWrapper {
        public ReaderCursorWrapper(Cursor cursor) {
            super(cursor);
        }

        public Reader getReader() {
            String id = getString(getColumnIndex(ReaderTable.Cols.ID));
            String readerName = getString(getColumnIndex(ReaderTable.Cols.ReaderName));
            String sex = getString(getColumnIndex(ReaderTable.Cols.sex));
            Reader reader = new Reader(id, readerName, sex);
            return reader;
        }
    }

    private SetReaderList.ReaderCursorWrapper queryReaders(String whereClause, String[] whereArgs) {
        Cursor cursor = mReaderDatabase.query(
                ReaderTable.NAME,
                null, //null代表选择所有行
                whereClause,
                whereArgs,
                null,
                null,
                null
        );
        return new SetReaderList.ReaderCursorWrapper(cursor);
    }
    public List<Reader> getReaders() {
        List<Reader> readers = new ArrayList<>();

        SetReaderList.ReaderCursorWrapper cursor = queryReaders(null, null);
        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                readers.add(cursor.getReader());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
        return readers;
    }
}
