package com.example.library;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;



public class ReaderActivity extends AppCompatActivity implements ReadersAdapter.IonSlidingViewClickListener {

    private Button skipReader_Button;
    private Button skipCamera_Button;
    RecyclerView readerRecyclerView;
    SQLiteDatabase ReaderDatabase;
    SetReaderList readerList;
    SetBorrowerList borrowerList;
    ReadersAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reader);

        //界面切换控制
        skipReader_Button = (Button) findViewById(R.id.skipBook);
        skipReader_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReaderActivity.this, MainActivity.class );
                startActivity(intent);
            }
        });
        skipCamera_Button = (Button) findViewById(R.id.skipCamera);
        skipCamera_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReaderActivity.this, CaptureActivity.class);
                startActivity(intent);
            }
        });
        ReaderDatabase = new ReaderBaseHelper(this).getWritableDatabase();
        readerList = new SetReaderList(this, ReaderDatabase);
        initView();
    }


    public void AddnewReader(View view) {

        final View newView =  LayoutInflater.from(this).inflate(R.layout.addnewreader_layout,null);
        final AlertDialog.Builder builder = new AlertDialog.Builder(ReaderActivity.this);

        final AlertDialog dialog = builder.create();
        dialog.setView(newView);
        dialog.show();

        final EditText id_ET = dialog.findViewById(R.id.id_Edit);
        final EditText name_ET = dialog.findViewById(R.id.name_Edit);
        final EditText sex_ET = dialog.findViewById(R.id.sex_Edit);


        Button submit_Button = (Button) dialog.findViewById(R.id.Sumbit);
        Button cancel_Button = (Button) dialog.findViewById(R.id.Cancel);
        submit_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = id_ET.getText().toString();
                String name = name_ET.getText().toString();
                String sex = sex_ET.getText().toString();
                Reader newReader = new Reader(id, name, sex);
                readerList.addReader(newReader);
                adapter.addData(readerList.getReaders().size(), newReader);
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
    void initView() {
        readerRecyclerView = findViewById(R.id.recycleViewReaders);
        readerRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        //设置添加或删除item时的动画，这里使用默认动画
        readerRecyclerView.setItemAnimator(new DefaultItemAnimator());
        readerRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL)); //添加水平分割线
        adapter = new ReadersAdapter(readerList.getReaders(), this);
        //设置适配器
        readerRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onItemClick(View view, int position) {
        //Log.i("test", "点击项：" + position);
    }

    @Override
    public void onDeleteBtnCilck(View view, int position) {
        borrowerList = new SetBorrowerList(ReaderActivity.this, ReaderDatabase);
        Reader reader = readerList.getReaders().get(position);
        List<Borrower> borrowers = borrowerList.getBorrowersByISBN(reader.ID);
        if(borrowers != null) {  //检查
            borrowerList.deleteBorrowerByID(reader.ID);
        }
        readerList.deleteReader(reader.ID);
        adapter.removeData(position, reader);
    }
}
