package com.example.boolan.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.boolan.R;
import com.example.boolan.beans.Home;
import com.example.boolan.utils.ConstantUtil;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;

public class HomecontentcommentAdapter extends RecyclerView.Adapter<HomecontentcommentAdapter.ViewHolder> {
    private Context contexts;
    private ArrayList<Home> mHomeList;
    private String url = ConstantUtil.SERVER_ADDRESS;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_content_zf_and_pl_item, parent, false);
        final HomecontentcommentAdapter.ViewHolder holder = new HomecontentcommentAdapter.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Home home = mHomeList.get(position);
        holder.nickname.setText(home.getName());
        holder.content.setText(home.getConnect());
        holder.time.setText(home.getTime());
        holder.userphoto.setImageURI(url + "/" + home.getHead() + ".png");
    }

    @Override
    public int getItemCount() {
        return mHomeList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private SimpleDraweeView userphoto;
        private TextView nickname;
        private TextView content;
        private TextView time;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userphoto = itemView.findViewById(R.id.home_contentzf_photo);
            nickname = itemView.findViewById(R.id.nickname);
            content = itemView.findViewById(R.id.content);
            time = itemView.findViewById(R.id.time);
        }
    }

    public HomecontentcommentAdapter(Context context, ArrayList<Home> List) {
        this.mHomeList = List;
        this.contexts = context;
    }
}
