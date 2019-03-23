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



public class ReaderActivity extends AppCompatActivity {

    private Button skipReader_Button;
    RecyclerView readerRecyclerView;
    SQLiteDatabase ReaderDatabase;
    SetReaderList readerList;

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
        ReaderDatabase = new ReaderBaseHelper(this).getWritableDatabase();
        readerList = new SetReaderList(this, ReaderDatabase);
        upDateUI();
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
                upDateUI();

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
        readerRecyclerView = findViewById(R.id.recycleViewReaders);
        readerRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        //设置添加或删除item时的动画，这里使用默认动画
        readerRecyclerView.setItemAnimator(new DefaultItemAnimator());

        ReaderActivity.ReadersAdapetr madapter = new ReaderActivity.ReadersAdapetr(readerList.getReaders());
        //设置适配器
        readerRecyclerView.setAdapter(madapter);
    }

    //recycleView设置
    //ViewHold容纳View
    public class ReaderHolder extends RecyclerView.ViewHolder {
        TextView ID;
        TextView readerName;
        TextView sex;

        public ReaderHolder(@NonNull View itemView) {
            super(itemView);
            ID = itemView.findViewById(R.id.item_readerID);
            readerName = itemView.findViewById(R.id.item_readerName);
            sex = itemView.findViewById(R.id.item_Sex);
        }
        private  Reader mreader;
        public void bind(Reader reader){
            mreader = reader;
            ID.setText("ID:" + mreader.ID);
            readerName.setText("Name:"+ mreader.readerName);
            sex.setText("Sex:"+ mreader.sex);
        }
    }

    //Adapter负责创建ViewHolder,从模型层获取数据
    public class ReadersAdapetr extends RecyclerView.Adapter<ReaderActivity.ReaderHolder> {
        private List<Reader> mreaders;
        public  ReadersAdapetr(List<Reader> readers) {
            mreaders = readers;
        }

        @Override
        public ReaderActivity.ReaderHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.readerrecycleview_layout, parent, false);
            return new ReaderActivity.ReaderHolder(view);
        }

        @Override
        public void onBindViewHolder(ReaderActivity.ReaderHolder holder, int position) {
            Reader reader = mreaders.get(position);
            holder.bind(reader);  //调用holder的数值赋予
        }
        //获取List的数量来决定显示数量
        @Override
        public int getItemCount() {
            return mreaders.size();
        }
    }

}
