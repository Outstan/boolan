package com.example.boolan.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.boolan.R;
import com.example.boolan.beans.User;
import com.example.boolan.module.me.MyinfodataActivity;
import com.example.boolan.service.PreferencesService;
import com.example.boolan.utils.ConstantUtil;
import com.example.boolan.utils.HttpUtil;
import com.example.boolan.utils.Toast;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class FollowAdapter extends RecyclerView.Adapter<FollowAdapter.ViewHolder> {
    private Context contexts;
    private List<User> mUserList;
    private String url= ConstantUtil.SERVER_ADDRESS;
    private Map<String, String> s;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public SimpleDraweeView simpleDraweeView;
        public TextView nickname,synopsis;
        public LinearLayout follow,linearLayout,yfollow;

        public ViewHolder(View view) {
            super(view);
            nickname = view.findViewById(R.id.fo_nickname);
            synopsis = view.findViewById(R.id.fo_synopsis);
            yfollow = view.findViewById(R.id.fo_yfollow);
            follow = view.findViewById(R.id.fo_follow);
            simpleDraweeView = view.findViewById(R.id.fo_photo);
            linearLayout = view.findViewById(R.id.fo_liner);
        }
    }

    @NonNull
    @Override
    public FollowAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Fresco.initialize(contexts);
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.follow_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        s=new PreferencesService(contexts).getdengluflag();
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull FollowAdapter.ViewHolder holder, int position) {
        User user = mUserList.get(position);
        holder.nickname.setText(user.getU_nickname());
        holder.synopsis.setText(user.getU_synopsis());
        Uri imageUri = Uri.parse(url+"/"+user.getU_img()+".png");
        holder.simpleDraweeView.setImageURI(imageUri);

        if (user.getFollow().equals("false")){
            holder.yfollow.setVisibility(View.GONE);
            holder.follow.setVisibility(View.VISIBLE);
        }else {
            holder.yfollow.setVisibility(View.VISIBLE);
            holder.follow.setVisibility(View.GONE);
        }

        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(contexts, MyinfodataActivity.class);
                Bundle build = new Bundle();
                build.putSerializable("user", user);
                intent.putExtras(build);
                contexts.startActivity(intent);
            }
        });

        holder.yfollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removfollow(user.getU_id());
                holder.yfollow.setVisibility(View.GONE);
                holder.follow.setVisibility(View.VISIBLE);
                user.setFollow("false");
            }
        });

        holder.follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (s.get("dengluflag").equals("true")) {
                    addfollow(user.getU_id());
                    holder.yfollow.setVisibility(View.VISIBLE);
                    holder.follow.setVisibility(View.GONE);
                    user.setFollow("true");
                }else Toast.getInstance(contexts).show("未登录", R.drawable.tips);
            }
        });

        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setUri(imageUri)
                .setTapToRetryEnabled(true)
                .setOldController(holder.simpleDraweeView.getController())
                .build();
        holder.simpleDraweeView.setController(controller);
    }

    private void addfollow(String u_id) {
        if (s.get("dengluflag").equals("true")) {
            HttpUtil.getAsyncGET(url + "/Follow/getaddFollow?id=" + s.get("id") + "&idb=" + u_id, new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                }
            });
        }else Toast.getInstance(contexts).show("未登录！！！",R.drawable.tips);
    }

    private void removfollow(String idb) {
        HttpUtil.getAsyncGET(url + "/Follow/getremoveFollow?id=" + s.get("id") + "&idb=" + idb, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

            }
        });
    }

    @Override
    public int getItemCount() {
        return mUserList.size();
    }

    public FollowAdapter(Context context, List<User> fruitList) {
        mUserList = fruitList;
        this.contexts = context;
    }
}
