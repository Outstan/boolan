package com.example.boolan.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.boolan.R;
import com.example.boolan.beans.Home;
import com.example.boolan.module.home.ContentdetailedActivity;
import com.example.boolan.module.home.ShareActivity;
import com.example.boolan.service.PreferencesService;
import com.example.boolan.utils.ConstantUtil;
import com.example.boolan.utils.HttpUtil;
import com.example.boolan.utils.Toast;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.ViewHolder> {
    public Context contexts;
    public List<Home> mUserList;
    private Uri imageUri;
    private String url = ConstantUtil.SERVER_ADDRESS;
    public static AlertDialog dialog;
    private Map<String, String> s;
    private TextView praisee;
    private ImageView praiseimagev;
    private Home home1;

    static class ViewHolder extends RecyclerView.ViewHolder {
        private SimpleDraweeView userphoto;
        private TextView nickname, text, time, praisenumber, commentnumber, number, textView;
        private RecyclerView recyclerView;
        private LinearLayout biglinearLayout, sharelinerlayout, linearLayout, deletebutton, myinfo, yfollow, follow, praiselinerLayout;
        private ImageView praiseimage;

        public ViewHolder(View view) {
            super(view);
            userphoto = view.findViewById(R.id.home_photo);
            nickname = view.findViewById(R.id.home_nickname);
            time = view.findViewById(R.id.home_time);
            text = view.findViewById(R.id.home_text);
            recyclerView = view.findViewById(R.id.home_recycle_item);
            praisenumber = view.findViewById(R.id.home_mypraise_number);
            commentnumber = view.findViewById(R.id.home_p_number);
            number = view.findViewById(R.id.home_f_number);
            biglinearLayout = view.findViewById(R.id.home_itemonclick);
            sharelinerlayout = view.findViewById(R.id.home_f);
            textView = view.findViewById(R.id.home_te);
            linearLayout = view.findViewById(R.id.home_liner);
            deletebutton = view.findViewById(R.id.content_delete_button);
            myinfo = view.findViewById(R.id.home_item_myinfo);
            yfollow = view.findViewById(R.id.home_item_yfollow);
            follow = view.findViewById(R.id.home_item_follow);
            praiselinerLayout = view.findViewById(R.id.home_mypraise);
            praiseimage = view.findViewById(R.id.home_praise);
        }
    }

    @NonNull
    @Override
    public HomeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_home_item, parent, false);
        final HomeAdapter.ViewHolder holder = new HomeAdapter.ViewHolder(view);
        s = new PreferencesService(contexts).getdengluflag();
        return holder;
    }

    @SuppressLint({"WrongConstant", "ResourceAsColor"})
    @Override
    public void onBindViewHolder(@NonNull HomeAdapter.ViewHolder holder, int position) {
        Home home = mUserList.get(position);
        if (home.getPdpraise().equals("true")) {
            holder.praiseimage.setImageResource(R.drawable.mypraise);
        }
        if (home.getU_id().equals(home.getYid())) {
            holder.linearLayout.setBackgroundColor(Color.parseColor("#ffffff"));
            holder.textView.setVisibility(View.GONE);
            holder.nickname.setText(home.getName());
            holder.text.setText(home.getConnect());
            holder.time.setText(home.getTime());
            holder.praisenumber.setText(home.getZnumber());
            holder.commentnumber.setText(home.getCommentnumber());
            holder.number.setText(home.getZfnumber());
            imageUri = Uri.parse(url +"/"+  home.getHead() + ".png");
            holder.userphoto.setImageURI(imageUri);
            if (home.getU_id().equals(s.get("id"))) {
                holder.myinfo.setVisibility(View.VISIBLE);
                holder.yfollow.setVisibility(View.GONE);
                holder.follow.setVisibility(View.GONE);
            } else if (home.getFollow().equals("false")) {
                holder.myinfo.setVisibility(View.GONE);
                holder.yfollow.setVisibility(View.GONE);
                holder.follow.setVisibility(View.VISIBLE);
            } else {
                holder.myinfo.setVisibility(View.GONE);
                holder.yfollow.setVisibility(View.VISIBLE);
                holder.follow.setVisibility(View.GONE);
            }
        } else {
            holder.linearLayout.setBackgroundColor(Color.parseColor("#1a8a8a8a"));
            holder.textView.setVisibility(View.VISIBLE);
            holder.nickname.setText(home.getName());
            holder.text.setText(home.getZcontent());
            holder.time.setText(home.getTime());
            holder.praisenumber.setText(home.getZnumber());
            holder.commentnumber.setText(home.getCommentnumber());
            holder.number.setText(home.getZfnumber());
            String str = "<font color='#2196F3'>@" + home.getYnickname() + "</font>：" + home.getConnect();
            holder.textView.setText(Html.fromHtml(str));
            imageUri = Uri.parse(url + "/" + home.getHead() + ".png");
            holder.userphoto.setImageURI(imageUri);
            if (home.getU_id().equals(s.get("id"))) {
                holder.myinfo.setVisibility(View.VISIBLE);
                holder.yfollow.setVisibility(View.GONE);
                holder.follow.setVisibility(View.GONE);
            } else if (home.getFollow().equals("false")) {
                holder.myinfo.setVisibility(View.GONE);
                holder.yfollow.setVisibility(View.GONE);
                holder.follow.setVisibility(View.VISIBLE);
            } else {
                holder.myinfo.setVisibility(View.GONE);
                holder.yfollow.setVisibility(View.VISIBLE);
                holder.follow.setVisibility(View.GONE);
            }
        }


        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setUri(imageUri)
                .setTapToRetryEnabled(true)
                .setOldController(holder.userphoto.getController())
                .build();
        holder.userphoto.setController(controller);

        holder.biglinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(contexts, ContentdetailedActivity.class);
                Bundle build = new Bundle();
                build.putSerializable("key", home);
                intent.putExtras(build);
                contexts.startActivity(intent);
            }
        });

        holder.sharelinerlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int[] location = new int[2];
                holder.sharelinerlayout.getLocationOnScreen(location);
                showshareDialog(location[1], home.getId(), home.getU_id(), home);
            }
        });

        holder.deletebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (home.getU_id().equals(s.get("id"))) {
                    if (home.getU_id().equals(home.getYid())) {
                        showdeleteDialog(true, home.getId(), home.getTime(), position);
                    } else showdeleteDialog(false, home.getU_id(), home.getTime(), position);
                } else if (home.getFollow().equals("false")) {
                    if (s.get("dengluflag").equals("true")) {
                        addfollow(home.getU_id());
                        holder.myinfo.setVisibility(View.GONE);
                        holder.yfollow.setVisibility(View.VISIBLE);
                        holder.follow.setVisibility(View.GONE);
                        home.setFollow("true");
                    } else Toast.getInstance(contexts).show("未登录！！！", R.drawable.tips);
                } else {
                }
            }
        });

        holder.praiselinerLayout.setOnClickListener(v -> {
            praisee = holder.praisenumber;
            home1 = home;
            praiseimagev = holder.praiseimage;
            if (s.get("dengluflag").equals("true")) {
                if (home.getPdpraise().equals("false")) {
                    HttpUtil.getAsyncGET(url + "/Home/setpraise?u_id=" + s.get("id") + "&b_id=" + home.getId(), new Callback() {
                        @Override
                        public void onFailure(@NotNull Call call, @NotNull IOException e) {
                            sendMessage(1, e.getMessage());
                        }

                        @Override
                        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                            sendMessage(3, response.body().string());
                        }
                    });
                } else {
                    HttpUtil.getAsyncGET(url + "/Home/removepraise?u_id=" + s.get("id") + "&b_id=" + home.getId(), new Callback() {
                        @Override
                        public void onFailure(@NotNull Call call, @NotNull IOException e) {
                            sendMessage(1, e.getMessage());
                        }

                        @Override
                        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                            sendMessage(4, response.body().string());
                        }
                    });
                }
            } else Toast.getInstance(contexts).show("未登录", R.drawable.tips);
        });

        if (home.getPhoto().length == 1) {
            GridLayoutManager gridLayoutManager = new GridLayoutManager(contexts, 1);
            //设置RecycleView显示的方向是水平还是垂直 GridLayout.HORIZONTAL水平  GridLayout.VERTICAL默认垂直
            gridLayoutManager.setOrientation(GridLayout.VERTICAL);
            holder.recyclerView.setLayoutManager(gridLayoutManager);
            Home_oneitemAdapter adapter = new Home_oneitemAdapter(contexts, home.getPhoto());
            holder.recyclerView.setAdapter(adapter);
        } else if (home.getPhoto().length == 4) {
            DisplayMetrics dm = contexts.getResources().getDisplayMetrics();
            int width = dm.widthPixels;
            ViewGroup.LayoutParams lp = holder.recyclerView.getLayoutParams();
            lp.width = (width / 2) + (width / 2 / 2);
            holder.recyclerView.setLayoutParams(lp);
            GridLayoutManager gridLayoutManager = new GridLayoutManager(contexts, 2);
            //设置RecycleView显示的方向是水平还是垂直 GridLayout.HORIZONTAL水平  GridLayout.VERTICAL默认垂直
            gridLayoutManager.setOrientation(GridLayout.VERTICAL);
            holder.recyclerView.setLayoutManager(gridLayoutManager);
            Home_itemAdapter adapter = new Home_itemAdapter(contexts, home.getPhoto());
            holder.recyclerView.setAdapter(adapter);
        } else {
            GridLayoutManager gridLayoutManager = new GridLayoutManager(contexts, 3);
            //设置RecycleView显示的方向是水平还是垂直 GridLayout.HORIZONTAL水平  GridLayout.VERTICAL默认垂直
            gridLayoutManager.setOrientation(GridLayout.VERTICAL);
            holder.recyclerView.setLayoutManager(gridLayoutManager);
            Home_itemAdapter adapter = new Home_itemAdapter(contexts, home.getPhoto());
            holder.recyclerView.setAdapter(adapter);
        }
    }

    private void addfollow(String u_id) {
        HttpUtil.getAsyncGET(url + "/Follow/getaddFollow?id=" + s.get("id") + "&idb=" + u_id, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                sendMessage(1, e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

            }
        });
    }

    public void showdeleteDialog(Boolean dlag, String id, String time, int position) {
        int width = contexts.getResources().getDisplayMetrics().widthPixels;
        AlertDialog.Builder builder = new AlertDialog.Builder(contexts);
        dialog = builder.create();
        View view = LayoutInflater.from(contexts).inflate(R.layout.content_delete_dialog, null);
        dialog.setView(view);
        LinearLayout linearLayout = view.findViewById(R.id.content_delete_dismiss);
        LinearLayout linearLayout1 = view.findViewById(R.id.content_delete_dele);
        Window window = dialog.getWindow();
        window.setWindowAnimations(R.style.Animationsharedialog);
        window.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = width;
        //params.dimAmount=0.1f;
        window.setBackgroundDrawableResource(android.R.color.transparent);
        window.setGravity(Gravity.BOTTOM);
        dialog.show();

        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        linearLayout1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dlag) {
                    HttpUtil.getAsyncGET(url + "/Home/getdeletecontent?id=" + id, new Callback() {
                        @Override
                        public void onFailure(@NotNull Call call, @NotNull IOException e) {
                            sendMessage(1, e.getMessage());
                        }

                        @Override
                        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        }
                    });
                    mUserList.remove(position);
                    dialog.dismiss();
                    notifyDataSetChanged();
                } else {
                    HttpUtil.getAsyncGET(url + "/Home/getdeletezfcontent?id=" + id + "&time=" + time, new Callback() {
                        @Override
                        public void onFailure(@NotNull Call call, @NotNull IOException e) {
                            sendMessage(1, e.getMessage());
                        }

                        @Override
                        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        }
                    });
                    mUserList.remove(position);
                    dialog.dismiss();
                    notifyDataSetChanged();
                }
            }
        });
    }


    public void showshareDialog(int i, String id, String id1, Home home) {
        int width = contexts.getResources().getDisplayMetrics().widthPixels;
        int height = contexts.getResources().getDisplayMetrics().heightPixels;
        AlertDialog.Builder builder = new AlertDialog.Builder(contexts);
        dialog = builder.create();
        View view = LayoutInflater.from(contexts).inflate(R.layout.share_dialog, null);
        dialog.setView(view);
        LinearLayout imageView = view.findViewById(R.id.sharedia_img);
        LinearLayout imageView1 = view.findViewById(R.id.sharedia_img1);
        LinearLayout linearLayout0 = view.findViewById(R.id.sharedia_kz);
        LinearLayout linearLayout = view.findViewById(R.id.sharedia_z);
        LinearLayout linearLayout1 = view.findViewById(R.id.sharedia_share);
        Window window = dialog.getWindow();
        window.getDecorView().setPadding(0, 0, 0, 0);
        window.setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();
        WindowManager.LayoutParams params = window.getAttributes();
        params.x = 0;
        params.width = width;
        if (i < height / 2) {
            params.y = i;
            window.setAttributes(params);
            imageView1.setVisibility(View.GONE);
            imageView.setVisibility(View.VISIBLE);
            imageView.setPadding((width / 3) / 2 - dip2px(contexts, 8), 0, 0, 0);
            window.setGravity(Gravity.TOP);
        } else {
            params.y = height - i - dip2px(contexts, 5);
            window.setAttributes(params);
            imageView.setVisibility(View.GONE);
            imageView1.setVisibility(View.VISIBLE);
            imageView1.setPadding((width / 3) / 2 - dip2px(contexts, 8), 0, 0, 0);
            window.setGravity(Gravity.BOTTOM);
        }

        linearLayout0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!id1.equals(s.get("id"))) {
                    if (s.get("dengluflag").equals("true")) {
                        HttpUtil.getAsyncGET(url + "/Home/getshare0?b_id=" + id + "&u_id=" + s.get("id"), new Callback() {
                            @Override
                            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                                sendMessage(1, e.getMessage());
                            }

                            @Override
                            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                                sendMessage(2, response.body().string());
                            }
                        });
                    } else {
                        Toast.getInstance(contexts).show("未登录", R.drawable.tips);
                        dialog.dismiss();
                    }
                } else {
                    Toast.getInstance(contexts).show("不可以转发自己的内容", R.drawable.tips);
                    dialog.dismiss();
                }
            }
        });

        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!id1.equals(s.get("id"))) {
                    if (s.get("dengluflag").equals("true")) {
                        Intent intent = new Intent(contexts, ShareActivity.class);
                        Bundle build = new Bundle();
                        build.putSerializable("key", home);
                        intent.putExtras(build);
                        contexts.startActivity(intent);
                        dialog.dismiss();
                    } else {
                        Toast.getInstance(contexts).show("未登录", R.drawable.tips);
                        dialog.dismiss();
                    }
                } else {
                    Toast.getInstance(contexts).show("不可以转发自己的内容", R.drawable.tips);
                    dialog.dismiss();
                }
            }
        });
    }

    private void sendMessage(int what, Object obj) {
        Message msg = Message.obtain();
        msg.what = what;
        msg.obj = obj;
        handler.sendMessage(msg);
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    Toast.getInstance(contexts).show("连接服务器失败", R.drawable.tips);
                    break;
                case 2:
                    String i = (String) msg.obj;
                    String j = i.substring(0, i.length() - 2);
                    if (j.equals("1")) {
                        Toast.getInstance(contexts).show("转发成功", R.drawable.tips);
                    } else Toast.getInstance(contexts).show("转发失败！", R.drawable.tips);
                    dialog.dismiss();
                    break;
                case 3:
                    String s = (String) msg.obj;
                    if (s.substring(0, 1).equals("1")) {
                        int p = Integer.parseInt(home1.getZnumber());
                        p++;
                        home1.setZnumber(p + "");
                        praisee.setText(p + "");
                        praiseimagev.setImageResource(R.drawable.mypraise);
                        home1.setPdpraise("true");
                    }
                    break;
                case 4:
                    s = (String) msg.obj;
                    if (s.substring(0, 1).equals("1")) {
                        int p = Integer.parseInt(home1.getZnumber());
                        p--;
                        home1.setZnumber(p + "");
                        praisee.setText(p + "");
                        praiseimagev.setImageResource(R.drawable.mypraise1);
                        home1.setPdpraise("false");
                    }
                    break;
            }
        }
    };

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    @Override
    public int getItemCount() {
        return mUserList.size();
    }

    public HomeAdapter(Context context, ArrayList<Home> fruitList) {
        this.mUserList = fruitList;
        this.contexts = context;
    }
}
