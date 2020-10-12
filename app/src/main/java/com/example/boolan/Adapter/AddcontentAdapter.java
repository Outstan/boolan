package com.example.boolan.Adapter;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.boolan.R;
import com.example.boolan.utils.SquaresimpleDraweeView;

import java.util.List;

import cn.finalteam.rxgalleryfinal.bean.MediaBean;

public class AddcontentAdapter extends RecyclerView.Adapter<AddcontentAdapter.ViewHolder> {
    private Addadapterevent addadapterevent;
    private Context context;
    private List<MediaBean> list;
    private int i=0,size=1;

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public SquaresimpleDraweeView imageView;
        public ImageView delete;
        public ViewHolder(View view){
            super(view);
            imageView = view.findViewById(R.id.imageview);
            delete = view.findViewById(R.id.delete);
        }
    }
    @NonNull
    @Override
    public AddcontentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_hone_add_photo, parent, false);
        final AddcontentAdapter.ViewHolder holder = new AddcontentAdapter.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull AddcontentAdapter.ViewHolder holder, int position) {
        if (i>=6){
            String filePath=list.get(position).getOriginalPath();
            holder.imageView.setImageURI("file://"+filePath);
        }else {
            if (position<i){
                String filePath=list.get(position).getOriginalPath();
                holder.imageView.setImageURI("file://"+filePath);
            }
        }
        if (position==i){
            holder.imageView.setImageResource(R.drawable.add_photo);
            holder.delete.setVisibility(View.GONE);
            holder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addadapterevent.AddOnClick();
                }
            });
        }
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addadapterevent.remove(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (list!=null) {
            if (list.size() >= 6) {
                i=list.size();
                size = i;
            } else {
                i = list.size();
                size= i + 1;
            }
        }
        return size;
    }

    public void setAddadapterevent (Addadapterevent addadapterevent){
        this.addadapterevent=addadapterevent;
    }

    public interface Addadapterevent{
        void AddOnClick();

        void remove(int position);
    }

    public AddcontentAdapter(Context contex, List<MediaBean> lis){
        this.context = contex;
        this.list = lis;
    }
}
