package com.example.boolan.Adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.boolan.R;
import com.example.boolan.utils.ConstantUtil;
import com.facebook.drawee.view.SimpleDraweeView;

public class Home_oneitemAdapter extends RecyclerView.Adapter<HomeAdapter.ViewHolder> {
    public Context contexts;
    public String[] pho;
    private SimpleDraweeView imageView;
    private String url= ConstantUtil.SERVER_ADDRESS;
    @NonNull
    @Override
    public HomeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_home_item_onephoto, parent, false);
        imageView=view.findViewById(R.id.imageview);
        final HomeAdapter.ViewHolder holder = new HomeAdapter.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull HomeAdapter.ViewHolder holder, int position) {
        imageView.setPadding(0,0,0,0);
        Uri imageUri = Uri.parse(url+"/"+pho[position]+".jpg");
        imageView.setImageURI(imageUri);
    }

    @Override
    public int getItemCount() {
        return pho.length;
    }

    public Home_oneitemAdapter(Context context, String[] photo) {
        pho = photo;
        this.contexts = context;
    }
}
