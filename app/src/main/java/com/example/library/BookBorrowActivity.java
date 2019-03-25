package com.example.library;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


//关于一本书的借阅情况信息
public class BookBorrowActivity extends AppCompatActivity {

    private Button back_Button;
    private String ISBN;  //表示当前activity的书籍，借阅情况
    RecyclerView borrowerRecyclerView;
    SQLiteDatabase BorrowerDatabase;
    SetBorrowerList borrowerList;
    SetReaderList readerList;
    SetBookList bookList;
    BorrowersAdapetr adapetr;
    //List<Borrower> borrowers = new ArrayList<>(); //用来进行recycleView展示的List
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bookborrow_layout);
        back_Button = findViewById(R.id.backBooks);
        back_Button.setOnClickListener(new View.OnClickListener() {
            @Override //返回books界面
            public void onClick(View v) {
                Intent intent = new Intent(BookBorrowActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        Intent intent = getIntent();
        ISBN = intent.getStringExtra("ISBN"); //获取上一个页面的isbn
        BorrowerDatabase = new ReaderBaseHelper(this).getWritableDatabase();
        borrowerList = new SetBorrowerList(this, BorrowerDatabase);
        readerList = new SetReaderList(this, BorrowerDatabase);
        bookList = new SetBookList(this, BorrowerDatabase);
        initView();
    }

    //初始化recycle View
    void initView() {
        borrowerRecyclerView = findViewById(R.id.recycleView_Book_Borrower);
        borrowerRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        //设置添加或删除item时的动画，这里使用默认动画
        borrowerRecyclerView.setItemAnimator(new DefaultItemAnimator());
        borrowerRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL)); //添加水平分割线
        adapetr = new BookBorrowActivity.BorrowersAdapetr(borrowerList.getBorrowersByISBN(ISBN));
        //设置适配器
        borrowerRecyclerView.setAdapter(adapetr);
    }




    //添加新的借阅人
    public void AddnewBorrower(View view) {
        final View newView =  LayoutInflater.from(this).inflate(R.layout.addnewborrower,null);
        final AlertDialog.Builder builder = new AlertDialog.Builder(BookBorrowActivity.this);
        final AlertDialog dialog = builder.create();
        dialog.setView(newView);
        dialog.show();

        final EditText ID_ET = dialog.findViewById(R.id.borrowerID_Edit);

        Button submit_Button = (Button) dialog.findViewById(R.id.borrowerSumbit);
        Button cancel_Button = (Button) dialog.findViewById(R.id.borrowerCancel);
        submit_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = ID_ET.getText().toString();
                new SQlietSerarch().execute(id);
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

    public class BorrowerHolder extends RecyclerView.ViewHolder {

        TextView id;
        TextView name;
        TextView sex;
        TextView borrowDate;
        TextView returnDate;
        Button returnBook;

        public BorrowerHolder(@NonNull View itemView) {
            super(itemView);
            id = itemView.findViewById(R.id.item_borrowerID);
            name = itemView.findViewById(R.id.item_borrowerName);
            sex = itemView.findViewById(R.id.item_borrowerSex);
            borrowDate = itemView.findViewById(R.id.item_borrowDate);
            returnDate = itemView.findViewById(R.id.item_returnDate);
            returnBook = itemView.findViewById(R.id.item_returBook_button);
        }
        private  Borrower mborrower;
        public void bind(Borrower borrower){
            mborrower = borrower;
            id.setText("ID:" + mborrower.id);
            name.setText("Name:" + mborrower.name);
            sex.setText("Sex:" + mborrower.sex);
            borrowDate.setText("BorrowDate:" + mborrower.borrowerDate);
            returnDate.setText("ReturnDate:" + mborrower.returnDate);
            if(mborrower.isReturn.equals("1")) {  //书籍归还状态下设置为隐藏状态，不可见，不占位
                returnBook.setVisibility(View.GONE);
            }
        }
    }
    //Adapter负责创建ViewHolder,从模型层获取数据
    public class BorrowersAdapetr extends RecyclerView.Adapter<BorrowerHolder> {

        private List<Borrower> mborrowers;
        //private ButtonInterface buttonInterface;

        public  BorrowersAdapetr(List<Borrower> borrowers) {
            mborrowers = borrowers;
        }


        @Override
        public BorrowerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.borrower_item_layout, parent, false);
            return new BorrowerHolder(view);
        }

        @Override
        public void onBindViewHolder(final BorrowerHolder holder, int position) {
            final Borrower borrower = mborrowers.get(position);
            holder.bind(borrower);  //调用holder的数值赋予
            holder.returnBook.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    borrower.isReturn = "1";
                    borrower.returnDate = getDate();
                    //Log.d("returnDate", borrower.returnDate);
                    holder.returnDate.setText("ReturnDate:" + borrower.returnDate);
                    holder.returnBook.setVisibility(View.GONE);
                    borrowerList.upDateReturnDate(borrower.onlykey);
                    bookList.upDateSurplus(ISBN, true);
                }
            });
        }
        //获取List的数量来决定显示数量
        @Override
        public int getItemCount() {
            return mborrowers.size();
        }
        public void addData(int position, Borrower newBorrower) {
            mborrowers.add(newBorrower);
            //添加动画
            notifyItemInserted(position);
        }

    }

    private class SQlietSerarch extends AsyncTask<String, Void, Reader> {

        @Override
        protected Reader doInBackground(String... strings) {
            Reader reader = readerList.queryReader(strings[0]); //获取用户输入的读者ID

            return reader;
        }

        //UI thread中调用
        @Override
        protected void onPostExecute(Reader reader) {
            super.onPostExecute(reader);
            //Update the temperature displayed
            if (reader == null) {
                return;
            }
            else {
                String borrowerDate = getDate();
                bookList.upDateSurplus(ISBN, false);
                String onlyKey = reader.ID + ISBN + borrowerDate; //传入主键
                Borrower borrower = new Borrower(onlyKey, reader.ID, ISBN, borrowerDate, " ", "0", reader.readerName, reader.sex, bookList.queryBook(ISBN).bookname);
                borrowerList.addBorrower(borrower);
                adapetr.addData(borrowerList.getBorrowers().size(), borrower);
            }
        }
    }

    public String getDate() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        //获取当前时间
        Date date = new Date(System.currentTimeMillis());//若为空也表示当前时间
        String today = simpleDateFormat.format(date);
        return today;
    }
}
