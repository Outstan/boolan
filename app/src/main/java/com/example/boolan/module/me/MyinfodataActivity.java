package com.example.boolan.module.me;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;

import com.example.boolan.R;
import com.example.boolan.beans.User;
import com.example.boolan.module.chat.ChatActivity;
import com.example.boolan.service.PreferencesService;
import com.example.boolan.utils.ConstantUtil;
import com.example.boolan.utils.HttpUtil;
import com.example.boolan.utils.Toast;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MyinfodataActivity extends AppCompatActivity implements View.OnClickListener {
    @BindView(R.id.myd_back)
    public Button back;
    @BindView(R.id.myd_photo)
    public SimpleDraweeView photo;
    @BindView(R.id.myd_nickname)
    public TextView nickname;
    @BindView(R.id.myd_fansi)
    public TextView fansinnum;
    @BindView(R.id.myd_synopsis)
    public TextView synopsis;
    @BindView(R.id.myd_hometown)
    public TextView hometown;
    @BindView(R.id.myd_localtion)
    public TextView localtion;
    @BindView(R.id.myd_sex)
    public TextView sex;
    @BindView(R.id.myd_birthday)
    public TextView birthday;
    @BindView(R.id.myd_age)
    public TextView age;
    @BindView(R.id.myd_email)
    public TextView email;
    @BindView(R.id.myd_phone)
    public TextView phone;
    @BindView(R.id.myd_edit)
    public TextView edit;
    @BindView(R.id.myd_bottom)
    public LinearLayout bottom;
    @BindView(R.id.myd_chat)
    public TextView chat;
    @BindView(R.id.myd_background)
    public SimpleDraweeView background;
    @BindView(R.id.myd_ta)
    public LinearLayout ta;
    @BindView(R.id.myinfo_follow_text)
    public TextView follow_text;
    private Map<String, String> s;
    private User user;
    private String url = ConstantUtil.SERVER_ADDRESS,guuanzhunum=null,fensinum=null;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(MyinfodataActivity.this);
        setContentView(R.layout.myinfo_data);
        ButterKnife.bind(this);//绑定黄油刀

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            ViewGroup rootView = (ViewGroup) ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);
            ViewCompat.setFitsSystemWindows(rootView, false);
            rootView.setClipToPadding(true);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS |
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        initview();
        datashow();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void initview() {
        back.setOnClickListener(this);
        chat.setOnClickListener(this);
        edit.setOnClickListener(this);
        ta.setOnClickListener(this);
        s = new PreferencesService(MyinfodataActivity.this).getdengluflag();
        user = (User) getIntent().getExtras().getSerializable("user");
        Uri imageUri = Uri.parse(url + "/" + user.getU_img() + ".png");
        photo.setImageURI(imageUri);
        //创建DraweeController
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                //加载的图片URI地址
                .setUri(imageUri)
                //设置点击重试是否开启
                .setTapToRetryEnabled(true)
                //设置旧的Controller
                .setOldController(photo.getController())
                //构建
                .build();
        //设置DraweeController
        photo.setController(controller);
        background.setImageURI(url + "/2.jpg");
        submit();
    }

    private void datashow() {
        if (user.getU_nickname().equals("null")){
            nickname.setText("");
        }else nickname.setText(user.getU_nickname());

        if (user.getU_synopsis().equals("null")){
            synopsis.setText("简介:");
        }else synopsis.setText("简介:" + user.getU_synopsis());

        if (user.getU_phone().equals("null")) {
            phone.setText("");
        } else phone.setText(user.getU_phone());

        sex.setText(user.getU_sex());

        if (user.getU_email().equals("null")){
            email.setText("");
        }else email.setText(user.getU_email());

        if (user.getU_birthday().equals("null")){
            birthday.setText("");
        }else birthday.setText(user.getU_birthday());

        if (user.getU_age().equals("null")){
            age.setText("");
        }else age.setText(user.getU_age());

        if (user.getU_localtion().equals("null")){
            localtion.setText("");
        }else localtion.setText(user.getU_localtion());

        if (user.getU_hometown().equals("null")){
            hometown.setText("");
        }else hometown.setText(user.getU_hometown());

        if (user.getU_id().equals(s.get("id"))) {
            bottom.setVisibility(View.GONE);
        } else {
            if (user.getFollow().equals("false")){
                follow_text.setText("+关注");
            }
            edit.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.myd_back:
                Intent intent = new Intent();
                setResult(Activity.RESULT_OK, intent);
                finish();
                break;
            case R.id.myd_chat:
                if (s.get("dengluflag").equals("true")) {
                    intent = new Intent(this, ChatActivity.class);
                    intent.putExtra("id", user.getU_id());
                    intent.putExtra("name", user.getU_nickname());
                    startActivity(intent);
                }else Toast.getInstance(MyinfodataActivity.this).show("未登录！！！",R.drawable.tips);
                break;
            case R.id.myd_edit:
                intent = new Intent(this, MyinfodataeditActivity.class);
                Bundle build = new Bundle();
                build.putSerializable("user", user);
                intent.putExtras(build);
                startActivityForResult(intent, 0);
                break;
            case R.id.myd_ta:
                intent = new Intent(MyinfodataActivity.this, FollowActivity.class);
                intent.putExtra("flag",4);
                intent.putExtra("id",user.getU_id());
                intent.putExtra("nickname",user.getU_nickname());
                startActivity(intent);
                break;
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        switch (requestCode) {
            case 0:
                if (resultCode == Activity.RESULT_OK) {
                    Map<String, String> map = (Map<String, String>) intent.getSerializableExtra("respond");
                    if (map!=null) {
                        try {
                            JSONObject json = new JSONObject(map);
                            user.setU_nickname(json.getString("nickname"));
                            user.setU_synopsis(json.getString("synopsis"));
                            user.setU_hometown(json.getString("hometown"));
                            user.setU_localtion(json.getString("location"));
                            user.setU_sex(json.getString("sex"));
                            user.setU_birthday(json.getString("birthday"));
                            user.setU_age(json.getString("age"));
                            user.setU_email(json.getString("email"));
                            user.setU_phone(json.getString("phone"));
                            datashow();
                            save();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
            Intent intent = new Intent();
            setResult(Activity.RESULT_OK, intent);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    private void save(){
        String id = s.get("id");
        String name = s.get("username");
        String password = s.get("password");
        String phone = user.getU_phone();
        String email = user.getU_email();
        int age = Integer.parseInt(user.getU_age());
        String sex = user.getU_sex();
        String img = s.get("img");
        String adress = user.getU_hometown();
        String nickname = user.getU_nickname();
        String birthday = user.getU_birthday();
        String synopsis = user.getU_synopsis();
        String dengluflag = "true";
        String localtion=user.getU_localtion();
        PreferencesService preferencesService = new PreferencesService(MyinfodataActivity.this); //将信息存入本地xml种
        preferencesService.saveuser(id, name, password, sex, age, phone, email, img, dengluflag,adress,nickname,birthday,synopsis,localtion);
    }

    private void submit(){
        HttpUtil.getAsyncGET(url + "/Follow/getFollow?id=" + user.getU_id(), new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) { }
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                sendMessage(2,response.body().string());
            }
        });
        HttpUtil.getAsyncGET(url + "/Follow/getbFollow?id=" + user.getU_id(), new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                sendMessage(1,e.getMessage());
            }
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                sendMessage(3,response.body().string());
            }
        });
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
                    Toast.getInstance(MyinfodataActivity.this).show("连接服务器失败", R.drawable.tips);
                    break;
                case 2:
                    String a = String.valueOf(msg.obj);
                    guuanzhunum = a.substring(0,1);
                    shownum();
                    break;
                case 3:
                    String b = String.valueOf(msg.obj);
                    fensinum = b.substring(0,1);
                    shownum();
                    break;
                case 4:
                    fansinnum.setText("关注 "+guuanzhunum+" | 粉丝 "+fensinum);
                    break;
            }
        }
    };

    private void shownum(){
        if (guuanzhunum!=null&&fensinum!=null){
            sendMessage(4,"");
        }
    }
}
