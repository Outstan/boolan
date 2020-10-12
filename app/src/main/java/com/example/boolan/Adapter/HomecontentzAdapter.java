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

public class HomecontentzAdapter extends RecyclerView.Adapter<HomecontentzAdapter.ViewHolder> {
    private Context contexts;
    private ArrayList<Home> mHomeList;
    private String url= ConstantUtil.SERVER_ADDRESS;

    public class ViewHolder extends RecyclerView.ViewHolder {
        private SimpleDraweeView userphoto;
        private TextView nickname;
        private TextView synopsis;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userphoto = itemView.findViewById(R.id.home_contentz_photo);
            nickname = itemView.findViewById(R.id.home_contentz_nickname);
            synopsis = itemView.findViewById(R.id.home_contentz_synopsis);
        }
    }

    @NonNull
    @Override
    public HomecontentzAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_content_praise_item, parent, false);
        final HomecontentzAdapter.ViewHolder holder = new HomecontentzAdapter.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull HomecontentzAdapter.ViewHolder holder, int position) {
        Home home = mHomeList.get(position);
        holder.nickname.setText(home.getName());
        holder.synopsis.setText(home.getSynopsis());
        holder.userphoto.setImageURI(url+"/"+home.getHead()+".png");
    }

    @Override
    public int getItemCount() {
        return mHomeList.size();
    }

    public HomecontentzAdapter(Context context, ArrayList<Home> List) {
        mHomeList = List;
        this.contexts = context;
    }
}
