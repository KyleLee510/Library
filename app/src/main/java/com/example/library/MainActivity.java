package com.example.library;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
//import android.view.LayoutInflater;
import android.util.Log;
import android.view.*;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.lang.String;

//import java.util.ArrayList;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
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



public class MainActivity extends AppCompatActivity {

    private Button skipReader_Button;
    RecyclerView recyclerView;
    //public Book j = new Book("1234","C语言", "莫言", "10",R.drawable.a123,10,2);
    SQLiteDatabase BookDatabase;
    SetBookList bookList;

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
            }
        });
        BookDatabase = new ReaderBaseHelper(this).getWritableDatabase();
        bookList = new SetBookList(this, BookDatabase);
        upDateUI();
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

    //更新UI
    void upDateUI() {
        recyclerView = findViewById(R.id.recycleViewBooks);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        //设置添加或删除item时的动画，这里使用默认动画
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        BooksAdapetr madapter = new BooksAdapetr(bookList.getBooks());
        //设置适配器
        recyclerView.setAdapter(madapter);
    }

    //ViewHold容纳View
    public class BookHolder extends RecyclerView.ViewHolder {
        ImageView bookCover;
        TextView bookname;
        TextView isbn;
        TextView author;
        TextView price;
        TextView Inventory;
        TextView surplus;

        public BookHolder(@NonNull View itemView) {
            super(itemView);
            bookCover = itemView.findViewById(R.id.item_cover);
            bookname = itemView.findViewById(R.id.item_bookname);
            isbn = itemView.findViewById(R.id.item_ISBN);
            author = itemView.findViewById(R.id.item_author);
            price = itemView.findViewById(R.id.item_price);
            Inventory = itemView.findViewById(R.id.item_inventory);
            surplus = itemView.findViewById(R.id.item_surplus);
        }
        private  Book mbook;
        public void bind(Book book){
            mbook = book;
            bookCover.setImageResource(mbook.bookcover);
            bookname.setText("BookName:" + mbook.bookname);
            isbn.setText("ISBN:" + mbook.ISBN);
            author.setText("Author:" + mbook.author);
            price.setText("Price:" + mbook.price);
            Inventory.setText("Inventor:" + String.valueOf(mbook.inventory));
            surplus.setText("Surplus:" + String.valueOf(mbook.surplus));
        }
    }

    //Adapter负责创建ViewHolder,从模型层获取数据
    public class BooksAdapetr extends RecyclerView.Adapter<BookHolder> {
        private List<Book> mbooks;
        public  BooksAdapetr(List<Book> books) {
            mbooks = books;
        }

        @Override
        public BookHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bookrecycleyview_layout, parent, false);
            return new BookHolder(view);
        }

        @Override
        public void onBindViewHolder(BookHolder holder, int position) {
            Book book = mbooks.get(position);
            holder.bind(book);  //调用holder的数值赋予
        }
        //获取List的数量来决定显示数量
        @Override
        public int getItemCount() {
            return mbooks.size();
        }

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
            //Log.d("要开始GSON？","come");
            reader = new BufferedReader(new InputStreamReader(inputStream));
            //Json 的读取
            GsonBuilder gsonBuilder = new GsonBuilder(); //声明Gson
            Gson gson = gsonBuilder.create();
            NetBook netBook = gson.fromJson(reader, NetBook.class);
            //getImage(netBook.PhotoUrl);  //下载对应的图片至drawable
            int nums = Integer.valueOf(amount);
            Book book = new Book(netBook.ISBN, netBook.BookName, netBook.Author, netBook.Price, R.drawable.a123, nums, 10);
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

    public void getImage(String PhotoURL) {
        String stringUrl = "http://isbn.szmesoft.com/ISBN/GetBookPhoto?ID=" + PhotoURL;
        String savePath;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(stringUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            //通过输入流获取图片数据
            InputStream inputStream = urlConnection.getInputStream();
            //得到图片的二进制数据，以二进制封装得到数据，具有通用性
            byte[] data = readInputStream(inputStream);
            File imageFile = new File("/Library/app/src/main/res/drawable" + PhotoURL + ".png");
            //创建输出流
            FileOutputStream outStream = new FileOutputStream(imageFile);
            //写入数据
            outStream.write(data);
            //关闭输出流
            outStream.close();
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

    }

    public static byte[] readInputStream(InputStream inStream) {
        try {
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            //创建一个Buffer字符串
            byte[] buffer = new byte[1024];
            //每次读取的字符串长度，如果为-1，代表全部读取完毕
            int len = 0;
            //使用一个输入流从buffer里把数据读取出来
            while( (len=inStream.read(buffer)) != -1 ){
                //用输出流往buffer里写入数据，中间参数代表从哪个位置开始读，len代表读取的长度
                outStream.write(buffer, 0, len);
            }
            //关闭输入流
            inStream.close();
            //把outStream里的数据写入内存
            return outStream.toByteArray();

        }
        catch (IOException e) {
            Log.d("3","kong");
            e.printStackTrace();
        }
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
                    bookList.addBook(newBook);
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
                upDateUI();
            }
        }

    }


}