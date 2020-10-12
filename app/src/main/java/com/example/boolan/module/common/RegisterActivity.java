package com.example.boolan.module.common;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.boolan.R;
import com.example.boolan.utils.ConstantUtil;
import com.example.boolan.utils.HttpUtil;
import com.example.boolan.utils.Toast;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.zyao89.view.zloading.ZLoadingDialog;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.zyao89.view.zloading.Z_TYPE.CHART_RECT;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    @BindView(R.id.tv_back)
    public Button back;
    @BindView(R.id.et_user_name)
    public MaterialEditText phone;
    @BindView(R.id.et_psw)
    public MaterialEditText password;
    @BindView(R.id.et_pswa)
    public MaterialEditText password1;
    @BindView(R.id.show)
    public Button pas1show;
    @BindView(R.id.fin)
    public Button pas1fin;
    @BindView(R.id.reshow)
    public Button pas2show;
    @BindView(R.id.refin)
    public Button pas2fin;
    @BindView(R.id.btn_Reg)
    public Button register;
    private ZLoadingDialog dialog;
    private String url= ConstantUtil.SERVER_ADDRESS,phonecontent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);//绑定黄油刀
        initview();
    }

    private void initview() {
        dialog = new ZLoadingDialog(RegisterActivity.this);
        back.setOnClickListener(this);
        register.setOnClickListener(this);
        pas1show.setOnClickListener(this);
        pas1fin.setOnClickListener(this);
        pas2show.setOnClickListener(this);
        pas2fin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_back:
                finish();
                break;
            case R.id.show:
                password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());//显示密码
                password.setSelection(password.getText().length()); //TextView默认光标在最左端，这里控制光标在最右端
                v.setVisibility(View.GONE);
                pas1fin.setVisibility(View.VISIBLE);
                break;
            case R.id.fin:
                password.setTransformationMethod(PasswordTransformationMethod.getInstance()); //隐藏密码
                password.setSelection(password.getText().length());//TextView默认光标在最左端，这里控制光标在最右端
                v.setVisibility(View.GONE);
                pas1show.setVisibility(View.VISIBLE);
                break;
            case R.id.reshow:
                password1.setTransformationMethod(HideReturnsTransformationMethod.getInstance());//显示密码
                password1.setSelection(password1.getText().length()); //TextView默认光标在最左端，这里控制光标在最右端
                v.setVisibility(View.GONE);
                pas2fin.setVisibility(View.VISIBLE);
                break;
            case R.id.refin:
                password1.setTransformationMethod(PasswordTransformationMethod.getInstance()); //隐藏密码
                password1.setSelection(password1.getText().length());//TextView默认光标在最左端，这里控制光标在最右端
                v.setVisibility(View.GONE);
                pas2show.setVisibility(View.VISIBLE);
                break;
            case R.id.btn_Reg:
                submit();
                break;
        }
    }

    private void submit(){
        phonecontent = phone.getText().toString().trim();
        String passwordcontent = password.getText().toString().trim();
        String passwordcontent1 = password1.getText().toString().trim();
        if (TextUtils.isEmpty(passwordcontent)){
            Toast.getInstance(RegisterActivity.this).show("手机号为空",R.drawable.tips);
            return;
        }
        if (!isChinaPhoneLegal(phonecontent)){
            Toast.getInstance(RegisterActivity.this).show("手机号格式错误",R.drawable.tips);
            return;
        }
        if (TextUtils.isEmpty(passwordcontent)) {
            Toast.getInstance(RegisterActivity.this).show("密码不能为空",R.drawable.tips);
            return;
        }
        if (TextUtils.isEmpty(passwordcontent1)) {
            Toast.getInstance(RegisterActivity.this).show("请再次输入密码",R.drawable.tips);
            return;
        }
        if (!passwordcontent1.equals(passwordcontent)){
            Toast.getInstance(RegisterActivity.this).show("两次密码不一致",R.drawable.tips);
            return;
        }

        dialog.setLoadingBuilder(CHART_RECT)//设置类型
                .setLoadingColor(Color.BLACK)//颜色
                .setHintText("Loading...")
                .setHintTextSize(18) // 设置字体大小 dp
                .setHintTextColor(Color.BLACK)  // 设置字体颜色
                .setDurationTime(0.5) // 设置动画时间百分比 - 0.5倍
                .setDialogBackgroundColor(Color.parseColor("#00111111")) // 设置背景色，默认白色
                .show();
        Map params = new HashMap();
        params.put("phone",phonecontent);
        params.put("password",passwordcontent);
        HttpUtil.getAsyncPostBody(url+"/user/register", params, new Callback() {
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
                    dialog.dismiss();
                    Toast.getInstance(RegisterActivity.this).show("连接服务器失败", R.drawable.tips);
                    break;
                case 2:
                    handleRegisterResult((String) msg.obj);
                    dialog.dismiss();
            }
        }
    };

    private void handleRegisterResult(String obj) {
        String state = null;
        JSONObject juser = null;
        try {
            state = obj.substring(obj.indexOf("{"), obj.indexOf("}") + 1);
            juser = new JSONObject(state);
            String stat = juser.getString("state");
            implement(stat);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void implement(final String stat){
        if (stat.equals("existence")){
            Toast.getInstance(RegisterActivity.this).show("手机号已注册", R.drawable.tips);
            return;
        }
        else if (stat.equals("error")){
            Toast.getInstance(RegisterActivity.this).show("未知错误", R.drawable.tips);
            return;
        }
        else {
            Toast.getInstance(RegisterActivity.this).show("注册成功", R.drawable.tips);
            Intent intent = new Intent();
            intent.setClass(RegisterActivity.this, LoginActivity.class);
            intent.putExtra("Uname",phonecontent);//第一个参数是键值，第二个参数是被传递的值
            startActivity(intent);
            finish();
        }
    }

    public static boolean isChinaPhoneLegal(String str)
            throws PatternSyntaxException {
        String regex = "(1[0-9][0-9]|15[0-9]|18[0-9])\\d{8}";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(str);
        return m.matches();
    }
}
