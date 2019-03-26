package com.example.library;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
//import android.view.LayoutInflater;
import android.text.TextUtils;
import android.util.Log;
import android.view.*;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.lang.String;

//import java.util.ArrayList;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.FieldPosition;
import java.util.ArrayList;
import java.util.List;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.FutureTarget;
import com.bumptech.glide.request.target.Target;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.file.attribute.FileAttribute;
import java.lang.String;
import java.lang.*;
import java.util.Calendar;
import java.util.List;
import java.io.ByteArrayOutputStream;



public class MainActivity extends AppCompatActivity implements BooksAdapter.IonSlidingViewClickListener  {

    private Button skipReader_Button;
    private Button skipCamera_Button;
    RecyclerView recyclerView;
    private BooksAdapter madapter;
    SQLiteDatabase BookDatabase;
    SetBookList bookList;
    SetBorrowerList borrowerList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        skipReader_Button = (Button) findViewById(R.id.skipReader);
        skipReader_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Intent intent = new Intent(MainActivity.this, ReaderActivity.class);
               startActivity(intent);
               finish();
            }
        });
        skipCamera_Button = (Button) findViewById(R.id.skipCamera);
        skipCamera_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CaptureActivity.class);
                startActivity(intent);

            }
        });
        BookDatabase = new ReaderBaseHelper(this).getWritableDatabase();
        bookList = new SetBookList(this, BookDatabase);
        initView();

    }

    public void AddnewBook(View view) {

        final View newView =  LayoutInflater.from(this).inflate(R.layout.addnewbook_layout,null);
        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        final AlertDialog dialog = builder.create();
        dialog.setView(newView);
        dialog.show();

        final EditText ISBN_ET = dialog.findViewById(R.id.isbn_Edit);
        final EditText amount_ET = dialog.findViewById(R.id.amount_Edit);

        Button submit_Button = (Button) dialog.findViewById(R.id.bookSumbit);
        Button cancel_Button = (Button) dialog.findViewById(R.id.bookCancel);
        submit_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String isbn = ISBN_ET.getText().toString();
                String amount = amount_ET.getText().toString();
                new DownloadUpdate().execute(isbn, amount);
                dialog.dismiss();
            }
        });
        cancel_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

    }

    //初始化UI
    void initView() {
        recyclerView = findViewById(R.id.recycleViewBooks);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        //设置添加或删除item时的动画，这里使用默认动画
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL)); //添加水平分割线

        madapter = new BooksAdapter(bookList.getBooks(), this);
        //设置适配器
        recyclerView.setAdapter(madapter);
    }





    @Override
    public void onItemClick(View view, int position) {
        //Log.i("test", "点击项：" + position);
    }

    @Override
    public void onDeleteBtnCilck(View view, int position) {
        borrowerList = new SetBorrowerList(MainActivity.this, BookDatabase);
        Book book = bookList.getBooks().get(position);
        List<Borrower> borrowers = borrowerList.getBorrowersByISBN(book.ISBN);
        if(borrowers != null) {     //删除对应的借阅记录
            borrowerList.deleteBorrowerByISBN(book.ISBN);
        }
        bookList.deleteBook(book.ISBN);
        madapter.removeData(position, book);
        initView();
    }

    //解析用户提供的ISBN，
    public Book CheckISBN(String ISBN, String amount) {
        String stringUrl = "http://isbn.szmesoft.com/isbn/query?isbn=" + ISBN;
        HttpURLConnection urlConnection = null;
        BufferedReader reader;
        try {
            URL url = new URL(stringUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                Log.d("jinru","kong");
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line = " ";
            while ((line = reader.readLine()) != null){
                buffer.append(line);
            }
            line = buffer.toString();
            if(line.length() == 42) {
                //Toast();
                //查询书籍不存在
                return null;
            }

            //Json 的读取
            GsonBuilder gsonBuilder = new GsonBuilder(); //声明Gson
            Gson gson = gsonBuilder.create();
            NetBook netBook = gson.fromJson(line, NetBook.class);
            int nums = Integer.valueOf(amount);
            Book book = new Book(netBook.ISBN, netBook.BookName, netBook.Author, netBook.Price, netBook.PhotoUrl, nums, nums);
            return book;
        }catch (MalformedURLException e) {
            Log.d("1","kong");
            e.printStackTrace();
        } catch (ProtocolException e) {
            Log.d("2","kong");
            e.printStackTrace();
        } catch (IOException e) {
            Log.d("3","kong");
            e.printStackTrace();
        }
        Log.d("fail","come");
        return null;
    }

    public boolean isConnectIsNomarl() {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                this.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            //Toast.makeText(this, "No Internet", Toast.LENGTH_SHORT).show();
            return false;
        }
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        if (info == null || !info.isConnected()) {
            //Toast.makeText(this, "No Internet", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    //第一个参数定义要传递的；
    //第二个参数定义指定的是我们的异步任务在执行的时候将执行的进度返回给UI线程的参数的类型
    //指定的异步任务执行完后返回给UI线程的结果的类型
    private class DownloadUpdate extends AsyncTask<String, Void, Book> {

        //处理异步操作进行网络访问
        @Override
        protected Book doInBackground(String... strings) {

            if (isConnectIsNomarl()) {
                Book newBook = CheckISBN(strings[0], strings[1]);
                if (newBook == null) {
                    Log.d("看看","失败");
                    return null;
                }
                else {
                    bookList.addBook(newBook); //添加到数据库
                }
                return newBook;
            }
            else {
                return null;
            }
        }

        //UI thread中调用
        @Override
        protected void onPostExecute(Book newBook) {
            super.onPostExecute(newBook);
            //Update the temperature displayed
            if (newBook == null) {

                return;
            }
            else {
                madapter.addData(bookList.getBooks().size(), newBook); //添加到recycleview
                //upDateUI();
            }
        }
    }


}