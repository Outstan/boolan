package com.example.boolan.module.MainActivityfragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.example.boolan.R;
import com.example.boolan.base.BaseFragment;
import com.example.boolan.beans.User;
import com.example.boolan.module.common.LoginActivity;
import com.example.boolan.module.common.SettingActivity;
import com.example.boolan.module.me.FollowActivity;
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
import java.util.Map;

import butterknife.BindView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class Mefragment extends BaseFragment implements View.OnClickListener {
    @BindView(R.id.me_medata)
    public LinearLayout medata;//用户详情
    @BindView(R.id.me_fans)
    public LinearLayout fans;//粉丝
    @BindView(R.id.me_follow)
    public LinearLayout follow;//关注
    @BindView(R.id.me_release)
    public LinearLayout relrase;//微博
    @BindView(R.id.me_photo)
    public SimpleDraweeView mephoto;//用户头像
    @BindView(R.id.me_nickname)
    public TextView nickname;//用户昵称
    @BindView(R.id.me_synopsis)
    public TextView synopsis;//用户简介
    @BindView(R.id.me_releasenum)
    public TextView releasenum;//用户发布数量
    @BindView(R.id.me_follownum)
    public TextView follownum;//用户关注数量
    @BindView(R.id.me_fansnum)
    public TextView fansnum;//用户粉丝数量
    @BindView(R.id.me_set)
    public Button setting;
    Map<String, String> userdata;
    private String url= ConstantUtil.SERVER_ADDRESS,follownumb,fansinumb;
    private boolean dengluflag=false;
    private String id1,nickname1,phone1,sex1,age1,email1,localtion1,birthday1,synopsis1,hometown1,img1;
    private Uri imageUri;

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_me;
    }

    @Override
    public void finishCreateView(Bundle state) {
        follow.setOnClickListener(this);
        fans.setOnClickListener(this);
        medata.setOnClickListener(this);
        setting.setOnClickListener(this);
        relrase.setOnClickListener(this);
        datashow();
        lazyLoad();
    }

    @Override
    public void lazyLoad() {
        if (dengluflag) {
            HttpUtil.getAsyncGET(url + "/Follow/getFollow?id=" + userdata.get("id"), new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) { }
                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    sendMessage(2,response.body().string());
                }
            });
            HttpUtil.getAsyncGET(url + "/Follow/getbFollow?id=" + userdata.get("id"), new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    sendMessage(1,e.getMessage());
                }
                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    sendMessage(3,response.body().string());
                }
            });
            HttpUtil.getAsyncGET(url + "/Follow/getmycontentnum?id=" + userdata.get("id"), new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    sendMessage(1,e.getMessage());
                }
                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    sendMessage(4,response.body().string());
                }
            });
        }
    }

    private void datashow() {
        userdata = new PreferencesService(getActivity()).getdengluflag();
        id1=userdata.get("id");
        phone1=userdata.get("phone");
        sex1=userdata.get("sex");
        age1=userdata.get("age");
        email1=userdata.get("email");
        localtion1=userdata.get("localtion");
        nickname1=userdata.get("nickname");
        birthday1=userdata.get("birthday");
        synopsis1=userdata.get("synopsis");
        hometown1=userdata.get("hometown");
        img1=userdata.get("img");
        if (!nickname1.equals("")) {
            nickname.setText(nickname1);
        } else nickname.setText("未登录");
        if (!synopsis1.equals("")) {
            synopsis.setText("简介:" + synopsis1);
        } else synopsis.setText("简介:");
        if (!img1.equals("")) {
            //创建将要下载的图片的URI
            imageUri = Uri.parse(url+"/"+img1+".png");
            //开始下载
            mephoto.setImageURI(imageUri);
        } else mephoto.setImageDrawable(mephoto.getResources().getDrawable(R.mipmap.user));

        if (!userdata.get("dengluflag").equals("true")){
            dengluflag=false;
            follownum.setText("0");
            fansnum.setText("0");
            releasenum.setText("0");
        } else dengluflag=true;

        if (dengluflag) {
            DraweeController controller = Fresco.newDraweeControllerBuilder()
                    .setUri(imageUri)
                    .setTapToRetryEnabled(true)
                    .setOldController(mephoto.getController())
                    .build();
            mephoto.setController(controller);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public void onClick(View view) {
        Intent intent=null;
        switch (view.getId()) {
            case R.id.me_fans:
                if (dengluflag) {
                    intent = new Intent(getActivity(), FollowActivity.class);
                    intent.putExtra("flag",2);
                    startActivity(intent);
                }else Toast.getInstance(getActivity()).show("未登录",R.drawable.tips);
                break;
            case R.id.me_follow:
                if (dengluflag) {
                    intent = new Intent(getActivity(), FollowActivity.class);
                    intent.putExtra("flag",1);
                    startActivity(intent);
                }else Toast.getInstance(getActivity()).show("未登录",R.drawable.tips);
                break;
            case R.id.me_medata:
                if (!dengluflag) {
                    intent = new Intent(getActivity(), LoginActivity.class);
                    startActivityForResult(intent, 0);
                } else {
                    intent = new Intent(getActivity(), MyinfodataActivity.class);
                    User user0 = new  User(id1,phone1,sex1,age1, email1,hometown1,nickname1,birthday1,synopsis1,localtion1,img1);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("user", user0);
                    intent.putExtras(bundle);
                    startActivityForResult(intent, 0);
                }
                break;
            case R.id.me_set:
                intent = new Intent(getActivity(), SettingActivity.class);
                startActivityForResult(intent, 0);
                break;
            case R.id.me_release:
                if (dengluflag) {
                    intent = new Intent(getActivity(), FollowActivity.class);
                    intent.putExtra("flag",3);
                    startActivity(intent);
                }else Toast.getInstance(getActivity()).show("未登录",R.drawable.tips);
                break;
        }
    }

    private void sendMessage(int what, Object obj) {
        Message msg = Message.obtain();
        msg.what = what;
        msg.obj = obj;
        handler.sendMessage(msg);
    }

    private Handler handler=new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    Toast.getInstance(getActivity()).show("连接服务器失败",R.drawable.tips);
                    break;
                case 2:
                    follownumb = (String) msg.obj;
                    follownum.setText(follownumb);
                    break;
                case 3:
                    fansinumb = (String) msg.obj;
                    fansnum.setText(fansinumb);
                    break;
                case 4:
                    fansinumb = (String) msg.obj;
                    releasenum.setText(fansinumb);
                    break;
            }
        }
    };

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        switch (requestCode) {
            case 0:
                datashow();
                lazyLoad();
                break;
        }
    }

    public static Mefragment newInstance() {
        Mefragment fragment = new Mefragment();
        return fragment;
    }
}
