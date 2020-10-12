package com.example.boolan.module.home;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.boolan.R;
import com.example.boolan.beans.Home;
import com.example.boolan.service.PreferencesService;
import com.example.boolan.utils.ConstantUtil;
import com.example.boolan.utils.HttpUtil;
import com.example.boolan.utils.Toast;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ShareActivity extends AppCompatActivity implements View.OnClickListener {
    @BindView(R.id.sha_edittext)
    public EditText editText;
    @BindView(R.id.sha_back)
    public TextView back;
    @BindView(R.id.sha_nickname1)
    public TextView topnickname;
    @BindView(R.id.sha_send)
    public TextView send;
    @BindView(R.id.sha_photo)
    public SimpleDraweeView photo;
    @BindView(R.id.sha_nickname)
    public TextView nickname;
    @BindView(R.id.sha_content)
    public TextView content;
    private Home home;
    private Map<String, String> s;
    private String url = ConstantUtil.SERVER_ADDRESS;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(ShareActivity.this);
        setContentView(R.layout.activity_share);
        ButterKnife.bind(this);//绑定黄油刀
        home = (Home) getIntent().getExtras().getSerializable("key");
        s = new PreferencesService(ShareActivity.this).getdengluflag();
        initView();
    }

    private void initView() {
        back.setOnClickListener(this);
        send.setOnClickListener(this);
        topnickname.setText(s.get("nickname"));
        nickname.setText("@"+home.getName());
        if (home.getPhoto().length==0){
            photo.setImageURI(url + "/img/" + home.getHead() + ".png");
        }else photo.setImageURI(url + "/img/" + home.getPhoto()[0] + ".jpg");
        content.setText(home.getConnect());
    }

    private void submit(){
        String content = editText.getText().toString().trim();
        if (TextUtils.isEmpty(content)) {
            content="转发内容";
        }
        Map params = new HashMap();
        params.put("b_id",home.getId());
        params.put("u_id",s.get("id"));
        params.put("content",content);
        HttpUtil.getAsyncPostBody(url+"/Home/getshare", params, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                sendMessage(1,e.getMessage());
            }
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                sendMessage(2,response.body().string());
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
                    Toast.getInstance(ShareActivity.this).show("连接服务器失败",R.drawable.tips);
                    break;
                case 2:
                    String i = (String) msg.obj;
                    String j = i.substring(0, i.length() - 2);
                    if (j.equals("1")){
                        Toast.getInstance(ShareActivity.this).show("转发成功",R.drawable.tips);
                        finish();
                    }
            }
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.sha_back:
                finish();
                break;
            case R.id.sha_send:
                submit();
                break;
        }
    }
}
