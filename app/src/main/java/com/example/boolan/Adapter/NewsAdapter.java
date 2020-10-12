package com.example.boolan.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.boolan.R;
import com.example.boolan.beans.Home;
import com.example.boolan.utils.ConstantUtil;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {
    private Context contexts;
    private List<Home> mUserList;
    private Onclickitem onclickitem = null;
    private String url= ConstantUtil.SERVER_ADDRESS;

    public class ViewHolder extends RecyclerView.ViewHolder {
        View vview;
        SimpleDraweeView userphoto;
        TextView nickname;
        TextView news;
        TextView time;
        LinearLayout searchlinearLayout;

        public ViewHolder(View view) {
            super(view);
            vview = view;
            userphoto = view.findViewById(R.id.nwes_photo);
            nickname = view.findViewById(R.id.nwes_nickname);
            news = view.findViewById(R.id.nwes_last);
            time = view.findViewById(R.id.news_time);
            searchlinearLayout = view.findViewById(R.id.news_search_li);
        }
    }

    @NonNull
    @Override
    public NewsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_news_item, parent, false);
        final NewsAdapter.ViewHolder holder = new NewsAdapter.ViewHolder(view);

        holder.searchlinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onclickitem.search();
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull NewsAdapter.ViewHolder holder, int position) {
        if (position != 0) {
            holder.searchlinearLayout.setVisibility(View.GONE);
        } else holder.searchlinearLayout.setVisibility(View.VISIBLE);
        Home home = mUserList.get(position);
        holder.nickname.setText(home.getName());
        holder.news.setText(home.getConnect());
        holder.time.setText(onclickitem.data(home.getTime()));
        holder.userphoto.setImageURI(url+"/"+home.getHead()+".png");

        holder.vview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onclickitem.Onclick(home);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mUserList.size();
    }

    public NewsAdapter(Context context, List<Home> fruitList) {
        mUserList = fruitList;
        this.contexts = context;
    }

    public void setOnItemClickListener(Onclickitem onItemClickListener) {
        this.onclickitem = onItemClickListener;
    }

    public interface Onclickitem {
        void Onclick(Home home);
        void search();
        String data(String time);
    }
}
