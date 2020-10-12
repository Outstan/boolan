package com.example.boolan.service;

/**
 * Created by Wang on 2016/5/13.
 */

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;
import java.util.Map;

public class PreferencesService {
    private Context context;

    public PreferencesService(Context context) {
        this.context = context;
    }

    //保存用户参数到user  xml文件
    public void saveuser(String id, String username, String password, String sex, int age, String phone, String email
            , String img, String dengluflag, String hometown, String nickname, String birthday, String synopsis,String localtion) {
        SharedPreferences preferences = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("id", id);
        editor.putString("username", username);
        editor.putString("password", password);
        editor.putString("sex", sex);
        editor.putInt("age", age);
        editor.putString("phone", phone);
        editor.putString("email", email);
        editor.putString("img", img);
        editor.putString("hometown", hometown);
        editor.putString("nickname", nickname);
        editor.putString("birthday", birthday);
        editor.putString("synopsis", synopsis);
        editor.putString("dengluflag", dengluflag);
        editor.putString("localtion", localtion);
        editor.commit();
    }

    // 从user中读取用户登录状态数据
    public Map<String, String> getdengluflag() {
        Map<String, String> params = new HashMap<String, String>();
        SharedPreferences preferences = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        params.put("id", preferences.getString("id", ""));
        params.put("username", preferences.getString("username", ""));
        params.put("password", preferences.getString("password", ""));
        params.put("sex", preferences.getString("sex", ""));
        params.put("age", String.valueOf(preferences.getInt("age", 0)));
        params.put("phone", preferences.getString("phone", ""));
        params.put("email", preferences.getString("email", ""));
        params.put("img", preferences.getString("img", ""));
        params.put("hometown", preferences.getString("hometown", ""));
        params.put("localtion", preferences.getString("localtion", ""));
        params.put("nickname", preferences.getString("nickname", ""));
        params.put("birthday", preferences.getString("birthday", ""));
        params.put("synopsis", preferences.getString("synopsis", ""));
        params.put("dengluflag", preferences.getString("dengluflag", ""));
        return params;
    }
}
