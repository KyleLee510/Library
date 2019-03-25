package com.example.library;

import android.os.AsyncTask;
import android.util.Log;

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

public class NetLink{

    //解析用户提供的ISBN,网络查看是否存在
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
}
