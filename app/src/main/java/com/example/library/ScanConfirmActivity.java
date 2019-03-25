package com.example.library;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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

public class ScanConfirmActivity extends AppCompatActivity {
    private Button backMain;
    private String ISBN;
    SQLiteDatabase BookDatabase;
    SetBookList bookList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scanconfirm_layout);
        backMain = (Button) findViewById(R.id.backMain);
        backMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ScanConfirmActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        Intent intent = getIntent();
        ISBN = intent.getStringExtra("isbn"); //获取相机扫描而来的isbn
        BookDatabase = new ReaderBaseHelper(this).getWritableDatabase();
        bookList = new SetBookList(this, BookDatabase);
    }

    public void confirm(View view) {
        new Download().execute();
        Intent intent = new Intent(ScanConfirmActivity.this, MainActivity.class);
        startActivity(intent);
    }

    public void dealISBN(Book newbook) {
        TextView isbn_textView = findViewById(R.id.scan_ISBN);
        isbn_textView.setText("ISBN:" + ISBN);
        TextView bookName_textView = findViewById(R.id.scan_bookname);
        TextView author_textView = findViewById(R.id.scan_author);
        TextView price_textView = findViewById(R.id.scan_price);
        EditText amount_ET = findViewById(R.id.Scan_amount_Edit);
        TextView inventory_textView = findViewById(R.id.scan_inventory);
        TextView isExist_textView = findViewById(R.id.item_isExist);
        if (newbook == null) {
            bookName_textView.setText("Can't find the Book");
            amount_ET.setVisibility(View.GONE); //使编辑框失效
            isExist_textView.setText("");
            return;
        }
        bookName_textView.setText("BookName:" + newbook.bookname);
        author_textView.setText("Author:" + newbook.author);
        price_textView.setText("Price:" +newbook.price);
        Book oldBook = bookList.queryBook(ISBN);
        if (oldBook != null) {  //针对存在的Book进行处理
            inventory_textView.setText("Inventory:"+oldBook.inventory);
            isExist_textView.setText("existing");
            amount_ET.setVisibility(View.GONE); //使编辑框失效
            return;
        }
        else {  //针对不存在进行书籍添加
            isExist_textView.setText("");
            String amount = amount_ET.getText().toString();
            int inventory = Integer.valueOf(amount);
            newbook.inventory = inventory;
            newbook.surplus = inventory;
            bookList.addBook(newbook);
        }
    }

    //解析用户提供的ISBN,网络查看是否存在
    public Book CheckISBNOnline(String ISBN) {
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
                //查询书籍不存在
                return null;
            }

            //Json 的读取
            GsonBuilder gsonBuilder = new GsonBuilder(); //声明Gson
            Gson gson = gsonBuilder.create();
            NetBook netBook = gson.fromJson(line, NetBook.class);
            Book book = new Book(netBook.ISBN, netBook.BookName, netBook.Author, netBook.Price, netBook.PhotoUrl, 1, 1);
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

    private class Download extends AsyncTask<String, Void, Book> {

        //处理异步操作进行网络访问
        @Override
        protected Book doInBackground(String... strings) {
            if (isConnectIsNomarl()) {
                Book newbook = CheckISBNOnline(ISBN);
                return newbook;
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
            dealISBN(newBook);
        }

    }
}
