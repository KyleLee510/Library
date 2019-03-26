package com.example.library;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class BooksAdapter extends RecyclerView.Adapter<BooksAdapter.BookHolder> implements SlidingButtonView.IonSlidingButtonListener  {
    private Context mContext;
    private List<Book> mbooks;
    private IonSlidingViewClickListener mIDeleteBtnClickListener;
    private SlidingButtonView mMenu = null;


    public  BooksAdapter(List<Book> books, Context context) {
        mbooks = books;
        mContext = context;
        mIDeleteBtnClickListener = (IonSlidingViewClickListener) context;
    }

    @Override
    public BookHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.bookrecycleyview_layout, parent, false);
        return new BookHolder(view);
    }

    @Override
    public void onBindViewHolder(final BookHolder holder, int position) {
        final Book book = mbooks.get(position);
        holder.bind(book);  //调用holder的数值赋予
        holder.book_content.getLayoutParams().width = Utils.getScreenWidth(mContext);

        holder.book_content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //判断是否有菜单打开
                if (menuIsOpen()) {
                    closeMenu();//关闭菜单
                } else {
                    int n = holder.getLayoutPosition();
                    mIDeleteBtnClickListener.onItemClick(v, n);
                }
            }
        });

        holder.bookSet_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // 点击事件
                Intent intent = new Intent(mContext, BookBorrowActivity.class);
                intent.putExtra("ISBN", book.ISBN);
                mContext.startActivity(intent);

            }
        });

        holder.delete_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int n = holder.getLayoutPosition();
                mIDeleteBtnClickListener.onDeleteBtnCilck(v, n);
            }
        });

    }
    //获取List的数量来决定显示数量
    @Override
    public int getItemCount() {
        return mbooks.size();
    }

    //添加新数据
    public void addData(int position, Book newbook) {
        mbooks.add(newbook);
        //添加动画
        notifyItemInserted(position);
    }

    //删除数据
    public void removeData(int position, Book deleteBook) {
        mbooks.remove(deleteBook);
        //删除动画
        notifyItemRemoved(position);
        notifyDataSetChanged();
    }

    public class BookHolder extends RecyclerView.ViewHolder {
        ImageView bookCover;
        TextView bookName;
        TextView isbn;
        TextView author;
        TextView price;
        TextView Inventory;
        TextView surplus;

        ViewGroup book_content;
        ViewGroup buttons_content;
        TextView bookSet_text;
        TextView delete_text;

        public BookHolder(@NonNull View itemView) {
            super(itemView);
            bookCover = itemView.findViewById(R.id.item_cover);
            bookName = itemView.findViewById(R.id.item_bookname);
            isbn = itemView.findViewById(R.id.item_ISBN);
            author = itemView.findViewById(R.id.item_author);
            price = itemView.findViewById(R.id.item_price);
            Inventory = itemView.findViewById(R.id.item_inventory);
            surplus = itemView.findViewById(R.id.item_surplus);

            book_content = itemView.findViewById(R.id.bookContent_layout);

            bookSet_text = itemView.findViewById(R.id.book_set);
            delete_text = itemView.findViewById(R.id.book_delete);
            buttons_content = itemView.findViewById(R.id.book_buttonTexts_content);
            ((SlidingButtonView) itemView).setSlidingButtonListener(BooksAdapter.this);
        }
        private  Book mbook;
        public void bind(Book book){
            mbook = book;
            String strUrl = "http://isbn.szmesoft.com/ISBN/GetBookPhoto?ID=" + mbook.bookcover;
            try{
                URL url = new URL(strUrl);
                Glide.with(bookCover).load(url).into(bookCover);
            } catch (MalformedURLException e){}
            //File file = new File(getExternalCacheDir() + "/"+mbook.bookcover+".jpg");
            // Glide.with(bookCover).load(file).into(bookCover);
            bookName.setText("BookName:" + mbook.bookname);
            isbn.setText("ISBN:" + mbook.ISBN);
            author.setText("Author:" + mbook.author);
            price.setText("Price:" + mbook.price);
            Inventory.setText("Inventor:" + String.valueOf(mbook.inventory));
            surplus.setText("Surplus:" + String.valueOf(mbook.surplus));
        }
    }

    @Override
    public void onMenuIsOpen(View view) {
        mMenu = (SlidingButtonView) view;
    }

    @Override
    public void onDownOrMove(SlidingButtonView slidingButtonView) {
        if (menuIsOpen()) {
            if (mMenu != slidingButtonView) {
                closeMenu();
            }
        }
    }

    /**
     * 关闭菜单
     */
    public void closeMenu() {
        mMenu.closeMenu();
        mMenu = null;

    }

    public Boolean menuIsOpen() {
        if (mMenu != null) {
            return true;
        }
        return false;
    }

    public interface IonSlidingViewClickListener {
        void onItemClick(View view, int position);
        void onDeleteBtnCilck(View view, int position);
    }

}

