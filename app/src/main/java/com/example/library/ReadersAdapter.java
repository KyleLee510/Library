package com.example.library;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

//Adapter负责创建ViewHolder,从模型层获取数据
public class ReadersAdapter extends RecyclerView.Adapter<ReadersAdapter.ReaderHolder> implements SlidingButtonView.IonSlidingButtonListener{
    private Context mContext;
    private List<Reader> mreaders;
    private IonSlidingViewClickListener mIDeleteBtnClickListener;
    private SlidingButtonView mMenu = null;
    public  ReadersAdapter(List<Reader> readers, Context context) {
        mreaders = readers;
        mContext = context;
        mIDeleteBtnClickListener = (IonSlidingViewClickListener) context;
    }

    @Override
    public ReaderHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.readerrecycleview_layout, parent, false);
        return new ReaderHolder(view);
    }

    @Override
    public void onBindViewHolder(final ReaderHolder holder, int position) {
        final Reader reader = mreaders.get(position);
        holder.bind(reader);  //调用holder的数值赋予
        holder.reader_content.getLayoutParams().width = Utils.getScreenWidth(mContext);

        holder.reader_content.setOnClickListener(new View.OnClickListener() {
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

        holder.readerSet_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // 点击事件
                Intent intent = new Intent(mContext, ReaderBorrowActivity.class);
                intent.putExtra("ID", reader.ID);
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
        return mreaders.size();
    }

    //添加新读者
    public void addData(int position, Reader newReader) {
        mreaders.add(newReader);
        //添加动画
        notifyItemInserted(position);
    }

    //删除数据
    public void removeData(int position, Reader deleteReader) {
        mreaders.remove(deleteReader);
        //删除动画
        notifyItemRemoved(position);
        notifyDataSetChanged();
    }

    //ViewHold容纳View
    public class ReaderHolder extends RecyclerView.ViewHolder {
        TextView ID;
        TextView readerName;
        TextView sex;

        ViewGroup reader_content;
        ViewGroup buttons_content;
        TextView readerSet_text;
        TextView delete_text;

        public ReaderHolder(@NonNull View itemView) {
            super(itemView);
            ID = itemView.findViewById(R.id.item_readerID);
            readerName = itemView.findViewById(R.id.item_readerName);
            sex = itemView.findViewById(R.id.item_Sex);

            reader_content = itemView.findViewById(R.id.readerContent_layout);

            readerSet_text = itemView.findViewById(R.id.reader_set);
            delete_text = itemView.findViewById(R.id.reader_delete);
            buttons_content = itemView.findViewById(R.id.book_buttonTexts_content);
            ((SlidingButtonView) itemView).setSlidingButtonListener(ReadersAdapter.this);
        }
        private  Reader mreader;
        public void bind(Reader reader){
            mreader = reader;
            ID.setText("ID:" + mreader.ID);
            readerName.setText("Name:"+ mreader.readerName);
            sex.setText("Sex:"+ mreader.sex);
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
