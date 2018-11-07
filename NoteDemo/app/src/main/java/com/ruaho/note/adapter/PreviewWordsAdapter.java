package com.ruaho.note.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ruaho.note.activity.PreviewWebviewActivity;
import com.ruaho.note.activity.R;
import com.ruaho.note.bean.PostilTag;
import com.ruaho.note.bean.PostilTagList;

import java.util.List;

public class PreviewWordsAdapter extends RecyclerView.Adapter<PreviewWordsAdapter.ViewHolder>{

    PostilTagList list;
    Context mContext;

    public PreviewWordsAdapter(Context context, PostilTagList list) {
        mContext = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        ViewHolder holder = null;
        holder = new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.preview_words_item_layout, viewGroup,
                false));
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        if(list != null && list.getList() != null){
            final PostilTag words = list.getList().get(i);
            viewHolder.mTextView.setText(words.getContent());
            viewHolder.mMainView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    goToPosition(words.getOffsetY());
                }
            });
            viewHolder.mEditTxt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    modifyCurrentWord();
                }
            });
            viewHolder.mDeleteTxt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    deleteCurrentWord();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return list.getList().size();
    }

    protected void goToPosition(int y){
        ((PreviewWebviewActivity)mContext).scrollToYforWebView(y);
    }

    protected void deleteCurrentWord(){

    }

    protected void modifyCurrentWord(){

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        View mView;
        TextView mTextView;
        View mMainView;
        TextView mEditTxt;
        TextView mDeleteTxt;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            mMainView = mView.findViewById(R.id.preview_item_main);
            mTextView = mView.findViewById(R.id.preview_word_item_txt);
            mEditTxt = mView.findViewById(R.id.preview_item_edit);
            mDeleteTxt = mView.findViewById(R.id.preview_item_delete);
        }
    }
}
