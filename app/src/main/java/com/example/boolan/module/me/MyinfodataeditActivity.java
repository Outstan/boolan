package com.example.boolan.module.me;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.boolan.R;
import com.example.boolan.beans.User;
import com.example.boolan.module.common.LoginActivity;
import com.example.boolan.service.PreferencesService;
import com.example.boolan.utils.ConstantUtil;
import com.example.boolan.utils.HttpUtil;
import com.example.boolan.utils.Toast;
import com.lljjcoder.Interface.OnCityItemClickListener;
import com.lljjcoder.bean.CityBean;
import com.lljjcoder.bean.DistrictBean;
import com.lljjcoder.bean.ProvinceBean;
import com.lljjcoder.citywheel.CityConfig;
import com.lljjcoder.style.citylist.Toast.ToastUtils;
import com.lljjcoder.style.citypickerview.CityPickerView;
import com.zyao89.view.zloading.ZLoadingDialog;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.zyao89.view.zloading.Z_TYPE.CHART_RECT;

public class MyinfodataeditActivity extends AppCompatActivity implements View.OnClickListener{
    @BindView(R.id.edit_back)
    public TextView back;//返回
    @BindView(R.id.myinfo_data_birthday)
    public TextView birthday;//出生日期
    @BindView(R.id.myinfo_data_age)
    public TextView age;//年龄
    @BindView(R.id.myinfo_data_City)
    public TextView city;//地址
    @BindView(R.id.myinfo_data_hometown)
    public TextView hometown;//家乡
    @BindView(R.id.myinfo_data_name)
    public EditText nickname;//昵称
    @BindView(R.id.myinfo_data_phone)
    public EditText phone;//电话
    @BindView(R.id.myinfo_data_synopsis)
    public EditText synopsis;//简介
    @BindView(R.id.myinfo_data_radioGroup)
    public RadioGroup sex;//性别
    @BindView(R.id.myinfo_data_email)
    public EditText email;//邮件
    @BindView(R.id.submit_btn)
    public Button submitbutton;//修改按钮
    private User user;
    private Map<String,String> mmap,s;
    private CityPickerView mPicker=new CityPickerView();
    private String url= ConstantUtil.SERVER_ADDRESS;
    private ZLoadingDialog dialog = new ZLoadingDialog(MyinfodataeditActivity.this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.myinfo_data_edit);
        ButterKnife.bind(this);//绑定黄油刀
        mPicker.init(this);//预先加载仿iOS滚轮实现的全部数据
        initview();

        birthday.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void afterTextChanged(Editable editable) { age.setText(age()+""); }
        });
    }

    private void initview() {
        back.setOnClickListener(this);
        birthday.setOnClickListener(this);
        city.setOnClickListener(this);
        hometown.setOnClickListener(this);
        submitbutton.setOnClickListener(this);
        s = new PreferencesService(MyinfodataeditActivity.this).getdengluflag();
        user = (User) getIntent().getExtras().getSerializable("user");
        if (user.getU_nickname().equals("null")){
            nickname.setText("");
        }else nickname.setText(user.getU_nickname());

        if (user.getU_synopsis().equals("null")){
            synopsis.setText("");
        }else synopsis.setText(user.getU_synopsis());

        if (user.getU_phone().equals("null")) {
            phone.setText("");
        } else phone.setText(user.getU_phone());

        if (user.getU_sex().equals("男")){
            sex.check(R.id.btnMan);
        }else sex.check(R.id.btnWoman);

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
            city.setText("");
        }else city.setText(user.getU_localtion());

        if (user.getU_hometown().equals("null")){
            hometown.setText("");
        }else hometown.setText(user.getU_hometown());

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.edit_back:
                Intent intent = new Intent();
                intent.putExtra("respond", (Serializable)mmap);
                setResult(Activity.RESULT_OK, intent);
                finish();
                break;
            case R.id.myinfo_data_birthday:
                showDatePickerDialog(MyinfodataeditActivity.this,0,birthday,Stringc(birthday.getText().toString()));
                break;
            case R.id.myinfo_data_City:
                selectAddress(city);
                break;
            case R.id.myinfo_data_hometown:
                selectAddress(hometown);
                break;
            case R.id.submit_btn:
                submit();
                break;
        }
    }

    private void submit() {
        RadioButton radioButton = findViewById(sex.getCheckedRadioButtonId());
        String[] keys = new String[]{"userId","sex","email","age","phone","hometown","nickname","birthday","synopsis","location"};
        String[] values = new String[]{s.get("id"),radioButton.getText().toString(),email.getText().toString(),age.getText().toString(),phone.getText().toString(),hometown.getText().toString(),nickname.getText().toString(),birthday.getText().toString(),synopsis.getText().toString(),city.getText().toString()};
        mmap = new HashMap<>();
        for (int i = 0;i<keys.length;i++){
            mmap.put(keys[i],values[i]);
        }
        dialog.setLoadingBuilder(CHART_RECT)//设置类型
                .setLoadingColor(Color.BLACK)//颜色
                .setHintText("Loading...")
                .setHintTextSize(18) // 设置字体大小 dp
                .setHintTextColor(Color.BLACK)  // 设置字体颜色
                .setDurationTime(0.5) // 设置动画时间百分比 - 0.5倍
                .setDialogBackgroundColor(Color.parseColor("#00111111")) // 设置背景色，默认白色
                .show();
        HttpUtil.getAsyncPostBody(url+"/user/edit", mmap, new Callback() {
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

    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    dialog.dismiss();
                    Toast.getInstance(MyinfodataeditActivity.this).show("连接服务器失败",R.drawable.tips);
                    break;
                case 2:
                    dialog.dismiss();
                    String i = (String) msg.obj;
                    String msg1 = i.substring(0,i.length()-2);
                    if (msg1.equals("1")) {
                        Intent intent = new Intent();
                        intent.putExtra("respond", (Serializable) mmap);
                        setResult(Activity.RESULT_OK, intent);
                        finish();
                    }else Toast.getInstance(MyinfodataeditActivity.this).show("修改失败！！！",R.drawable.tips);
                    break;
            }
        }
    };

    private void selectAddress(TextView textView) {
        CityConfig cityConfig = new CityConfig.Builder().build();
        mPicker.setConfig(cityConfig);
        //监听选择点击事件及返回结果
        mPicker.setOnCityItemClickListener(new OnCityItemClickListener() {
            @Override
            public void onSelected(ProvinceBean province, CityBean city, DistrictBean district) {
                textView.setText(province + "-" + city + "-" + district);
            }

            @Override
            public void onCancel() {
                ToastUtils.showLongToast(MyinfodataeditActivity.this, "已取消");
            }
        });
        mPicker.showCityPicker( );
    }

    public static void showDatePickerDialog(Activity activity, int themeResId, final TextView tv, Calendar calendar) {
        // 直接创建一个DatePickerDialog对话框实例，并将它显示出来
        new DatePickerDialog(activity, themeResId, new DatePickerDialog.OnDateSetListener() {
            // 绑定监听器(How the parent is notified that the date is set.)
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                // 此处得到选择的时间，可以进行你想要的操作
                tv.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
            }
        }
                // 设置初始日期
                , calendar.get(Calendar.YEAR)
                , calendar.get(Calendar.MONTH)
                , calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private Calendar Stringc(String str){
        if (!str.equals("")&&!TextUtils.isEmpty(str)&&!str.equals("null")) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date date = null;
            try {
                date = sdf.parse(str);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            return calendar;
        }
        else {
            Calendar now = Calendar.getInstance();
            return now;
        }
    }

    private int age() {
        Calendar now = Calendar.getInstance();
        String data1 = birthday.getText().toString();
        if (data1 != null) {
            Calendar calendar1 = Stringc(data1);
            int year = now.get(Calendar.YEAR) - calendar1.get(Calendar.YEAR);
            int month = now.get(Calendar.MONTH) - calendar1.get(Calendar.MONTH);
            int day = now.get(Calendar.DAY_OF_MONTH) - calendar1.get(Calendar.DAY_OF_MONTH);
            int age = year;// 先大致赋值
            if (year <= 0) {
                age = 0;
            }
            if (month < 0) {
                age = age - 1;
            } else if (month == 0) {
                if (day < 0) {
                    age = age - 1;
                }
            }
            return age;
        }
        else{
            return 0;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
            Intent intent = new Intent();
            intent.putExtra("respond", (Serializable)mmap);
            setResult(Activity.RESULT_OK, intent);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }
}
