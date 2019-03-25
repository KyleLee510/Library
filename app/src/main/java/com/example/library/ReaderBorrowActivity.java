package com.example.library;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ReaderBorrowActivity extends AppCompatActivity {

    private Button back_Button;
    private String ID;  //表示当前activity的读者，借阅情况
    RecyclerView borrowBookRecyclerView;
    SQLiteDatabase BorrowBookDatabase;
    SetBorrowerList borrowerList;
    SetReaderList readerList;
    SetBookList bookList;
    BorrowBooksAdapetr adapetr;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.readerborrow_layout);
        back_Button = findViewById(R.id.backReaders);
        back_Button.setOnClickListener(new View.OnClickListener() {
            @Override //返回books界面
            public void onClick(View v) {
                Intent intent = new Intent(ReaderBorrowActivity.this, ReaderActivity.class);
                startActivity(intent);
            }
        });
        Intent intent = getIntent();
        ID = intent.getStringExtra("ID"); //获取上一个页面的isbn
        BorrowBookDatabase = new ReaderBaseHelper(this).getWritableDatabase();
        borrowerList = new SetBorrowerList(this, BorrowBookDatabase);
        readerList = new SetReaderList(this, BorrowBookDatabase);
        bookList = new SetBookList(this, BorrowBookDatabase);
        initView();
    }

    void initView() {
        borrowBookRecyclerView = findViewById(R.id.recycleView_Borrow_books);
        borrowBookRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        //设置添加或删除item时的动画，这里使用默认动画
        borrowBookRecyclerView.setItemAnimator(new DefaultItemAnimator());
        borrowBookRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL)); //添加水平分割线
        adapetr = new BorrowBooksAdapetr(borrowerList.getBorrowersByID(ID));
        //设置适配器
        borrowBookRecyclerView.setAdapter(adapetr);
    }

    public void AddnewBorrowBook(View view) {
        final View newView =  LayoutInflater.from(this).inflate(R.layout.addnewborrowbook,null);
        final AlertDialog.Builder builder = new AlertDialog.Builder(ReaderBorrowActivity.this);
        final AlertDialog dialog = builder.create();
        dialog.setView(newView);
        dialog.show();

        final EditText ISBN_ET = dialog.findViewById(R.id.borrowISBN_Edit);

        Button submit_Button = (Button) dialog.findViewById(R.id.borrowbookSumbit);
        Button cancel_Button = (Button) dialog.findViewById(R.id.borrowbookCancel);
        submit_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String isbn = ISBN_ET.getText().toString();
                new SQlietSerarch().execute(isbn);
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

    public class BorrowBookHolder extends RecyclerView.ViewHolder {

        TextView bookname;
        TextView borrowDate;
        TextView returnDate;
        Button returnBook;

        public BorrowBookHolder(@NonNull View itemView) {
            super(itemView);
            bookname = itemView.findViewById(R.id.item_borrowBookName);
            borrowDate = itemView.findViewById(R.id.item_borrowBook_borrowDate);
            returnDate = itemView.findViewById(R.id.item_borrowBook_returnDate);
            returnBook = itemView.findViewById(R.id.item_Reader_return_button);
        }
        private  Borrower mborrower;
        public void bind(Borrower borrower){
            mborrower = borrower;
            bookname.setText("BookName:" + mborrower.bookName);
            borrowDate.setText("BorrowDate:" + mborrower.borrowerDate);
            returnDate.setText("ReturnDate:" + mborrower.returnDate);
            if(mborrower.isReturn.equals("1")) {  //书籍归还状态下设置为隐藏状态，不可见，不占位
                returnBook.setVisibility(View.GONE);
            }
        }
    }
    //Adapter负责创建ViewHolder,从模型层获取数据
    public class BorrowBooksAdapetr extends RecyclerView.Adapter<ReaderBorrowActivity.BorrowBookHolder> {
        private List<Borrower> mborrowBooks;
        public  BorrowBooksAdapetr(List<Borrower> borrowBooks) {
            mborrowBooks = borrowBooks;
        }

        @Override
        public ReaderBorrowActivity.BorrowBookHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.borrowbook_item_layout, parent, false);
            return new BorrowBookHolder(view);
        }

        @Override
        public void onBindViewHolder(final ReaderBorrowActivity.BorrowBookHolder holder, int position) {
            final Borrower borrowbook = mborrowBooks.get(position);
            holder.bind(borrowbook);  //调用holder的数值赋予
            holder.returnBook.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    borrowbook.isReturn = "1";
                    borrowbook.returnDate = getDate();
                    //Log.d("returnDate", borrower.returnDate);
                    holder.returnDate.setText("ReturnDate:" + borrowbook.returnDate);
                    holder.returnBook.setVisibility(View.GONE);
                    borrowerList.upDateReturnDate(borrowbook.onlykey);
                    bookList.upDateSurplus(borrowbook.isbn, true); //更新对应书籍里的剩余量
                }
            });
        }
        //获取List的数量来决定显示数量
        @Override
        public int getItemCount() {
            return mborrowBooks.size();
        }
        //添加新的借阅信息
        public void addData(int position, Borrower newborrowBook) {
            mborrowBooks.add(newborrowBook);
            //添加动画
            notifyItemInserted(position);
        }

    }

    private class SQlietSerarch extends AsyncTask<String, Void, Book> {

        @Override
        protected Book doInBackground(String... strings) {
            Book book = bookList.queryBook(strings[0]); //获取用户输入的读者ID

            return book;
        }

        //UI thread中调用
        @Override
        protected void onPostExecute(Book book) {
            super.onPostExecute(book);
            //Update the temperature displayed
            if (book == null) {
                return;
            }
            else {
                String borrowerDate = getDate();
                bookList.upDateSurplus(book.ISBN, false);
                Reader reader = readerList.queryReader(ID);
                String onlyKey = ID + book.ISBN + borrowerDate;
                Borrower borrower = new Borrower(onlyKey, ID, book.ISBN, borrowerDate, " ", "0", reader.readerName, reader.sex, book.bookname);
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
