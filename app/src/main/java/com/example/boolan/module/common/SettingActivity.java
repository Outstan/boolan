package com.example.boolan.module.common;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.boolan.R;
import com.example.boolan.service.PreferencesService;

import java.net.URLConnection;
import java.util.Map;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView Back;
    private RelativeLayout exit;
    private RelativeLayout set_pass;
    Map<String, String> s;
    PreferencesService preferencesService;
    private URLConnection urlConnection;
    private String serverUrl = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.myinfo_setting);
        init();
    }

    private void init() {
        Back = findViewById(R.id.back);
        exit = findViewById(R.id.set_exitt);
//        set_pass = findViewById(R.id.set_password);
        Back.setOnClickListener(this);
//        set_pass.setOnClickListener(this);
        exit.setOnClickListener(this);
        preferencesService = new PreferencesService(SettingActivity.this);
        s=preferencesService.getdengluflag();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back:
                Intent intent = new Intent();
                setResult(Activity.RESULT_OK, intent);
                finish();
                break;
            case R.id.set_exitt:
                exit();
                break;
//            case R.id.set_password:
//                Intent intent = new Intent(this, XGPassActivity.class);
//                startActivity(intent);
//                finish();
//                break;
        }
    }
    private void exit(){
        if (s.get("dengluflag").equals("true")) {
            String id = "";
            String name = s.get("username");
            String password = "";
            String phone = "";
            String email = "";
            int age = 0;
            String sex = "";
            String img = "";
            String adress = "";
            String nickname = "";
            String birthday = "";
            String synopsis = "";
            String dengluflag = "false";
            String localtion="";
            preferencesService = new PreferencesService(SettingActivity.this); //将信息存入本地xml种
            preferencesService.saveuser(id, name, password, sex, age, phone, email, img, dengluflag,adress,nickname,birthday,synopsis,localtion);
            Intent intent = new Intent();
            setResult(Activity.RESULT_OK, intent);
            finish();
        }
    }
}
