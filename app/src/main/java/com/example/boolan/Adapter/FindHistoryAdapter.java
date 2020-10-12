package com.example.boolan.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.boolan.R;

import java.util.List;

public class FindHistoryAdapter extends RecyclerView.Adapter<FindHistoryAdapter.ViewHolder> {
    private Context mcontext;
    private List<String> mlist;
    private onItemDeleteListener monItemDeleteListener;

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView textView;
        public Button button;

        public ViewHolder(View view){
            super(view);
            textView = view.findViewById(R.id.text);
            button = view.findViewById(R.id.adapter_account_item_delete);
        }
    }
    @NonNull
    @Override
    public FindHistoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.login_list, parent, false);
        final FindHistoryAdapter.ViewHolder holder = new FindHistoryAdapter.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull FindHistoryAdapter.ViewHolder holder, int position) {
        holder.textView.setText(mlist.get(position));

        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                monItemDeleteListener.onDeleteClick(position);
            }
        });

        holder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                monItemDeleteListener.ListOnclick(mlist.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return mlist.size();
    }

    public FindHistoryAdapter(Context context,List<String> list) {
        this.mcontext = context;
        this.mlist = list;
    }

    public void setOnItemDeleteClickListener(onItemDeleteListener mOnItemDeleteListener) {
        this.monItemDeleteListener = mOnItemDeleteListener;
    }

    public interface onItemDeleteListener {
        void onDeleteClick(int position);
        void ListOnclick(String content);
    }
}
