package com.example.boolan.module.common;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.boolan.Adapter.UserdeleteAdapter;
import com.example.boolan.R;
import com.example.boolan.helper.RecordSQLiteOpenHelper;
import com.example.boolan.utils.ConstantUtil;
import com.example.boolan.utils.HttpUtil;
import com.example.boolan.service.PreferencesService;
import com.example.boolan.utils.Toast;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.zyao89.view.zloading.ZLoadingDialog;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    @BindView(R.id.login_back)
    Button back;
    @BindView(R.id.login_user_name)
    MaterialEditText name;
    @BindView(R.id.login_psw)
    MaterialEditText password;
    @BindView(R.id.login_Reg)
    Button reg;
    @BindView(R.id.login_button)
    Button login;
    @BindView(R.id.login_checkBox)
    CheckBox checkBox;
    @BindView(R.id.login_findpass)
    TextView findpass;
    @BindView(R.id.login_linearlayout)
    LinearLayout linearLayout;
    @BindView(R.id.login_show)
    Button show;
    @BindView(R.id.login_fin)
    Button fin;
    private PreferencesService preferencesService;
    private String img,url= ConstantUtil.SERVER_ADDRESS;
    private ListView list_user;
    private SQLiteDatabase db;
    private List<String> mList = new ArrayList<>();
    private RecordSQLiteOpenHelper helper = new RecordSQLiteOpenHelper(this);
    public static String basePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/okhttp";
    private ZLoadingDialog dialog = new ZLoadingDialog(LoginActivity.this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);//绑定黄油刀
        initview();
        list_user = (ListView) findViewById(R.id.list_user);
        final UserdeleteAdapter adapter = new UserdeleteAdapter(LoginActivity.this,mList);
        list_user.setAdapter(adapter);

        list_user.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                TextView textView = view.findViewById(R.id.text);
                String name1 = textView.getText().toString();
                name.setText(name1);
                name.setSelection(name.getText().length());
                list_user.setVisibility(View.GONE);
            }
        });
        adapter.setOnItemDeleteClickListener(new UserdeleteAdapter.onItemDeleteListener() {
            @Override
            public void onDeleteClick(int position) {
                deleteDataa(mList.get(position));
                mList.remove(position);
                adapter.notifyDataSetChanged();
            }
        });

        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //@在编辑框改变前调用
                queryData("");
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //@编辑框改变的时候进行调用
                list_user.setVisibility(View.VISIBLE);
                boolean hasData = hasData(name.getText().toString().trim());
                if (TextUtils.isEmpty(name.getText().toString().trim())){
                    password.setText("");
                    queryData("");
                }
                else if (!hasData) {
                    queryData(name.getText().toString().trim());
                    password.setText("");
                    checkBox.setChecked(false);
                } else {
                    final String tempname = name.getText().toString();
                    String password1 = passdata(tempname);
                    if (!password1.equals("")) {
                        password.setText(password1);
                        checkBox.setChecked(true);
                    }
                    list_user.setVisibility(View.GONE);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {
                //@编辑框改变之后进行调用

            }
        });
        name();
    }

    private void initview() {
        back.setOnClickListener(this);
        login.setOnClickListener(this);
        reg.setOnClickListener(this);
        linearLayout.setOnClickListener(this);
        show.setOnClickListener(this);
        fin.setOnClickListener(this);
    }

    public static boolean isChinaPhoneLegal(String str)
            throws PatternSyntaxException {
        String regex = "(1[0-9][0-9]|15[0-9]|18[0-9])\\d{8}";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(str);
        return m.matches();
    }

    private void submit() {
        String account = name.getText().toString().trim();
        if (TextUtils.isEmpty(account)) {
            Toast.getInstance(LoginActivity.this).show("手机号为空",R.drawable.tips);
            return;
        }
        if (!isChinaPhoneLegal(account)){
            Toast.getInstance(LoginActivity.this).show("手机号格式错误",R.drawable.tips);
            return;
        }

        String pqssword = password.getText().toString().trim();
        if (TextUtils.isEmpty(pqssword)) {
            Toast.getInstance(LoginActivity.this).show("密码为空",R.drawable.tips);
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
        params.put("username",account);
        params.put("password",pqssword);
        HttpUtil.getAsyncPostBody(url+"/user/login", params, new Callback() {
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
                    Toast.getInstance(LoginActivity.this).show("连接服务器失败",R.drawable.tips);
                    break;
                case 2:
                    handleLoginResult(msg.obj);
                    dialog.dismiss();
                    break;
            }
        }
    };

    private void handleLoginResult(Object obj) {
        String datajson = (String) obj;
        String state = null;
        JSONObject juser = null;
        try {
            if (datajson.indexOf("error") != -1) {//用户名密码错误
                Toast.getInstance(LoginActivity.this).show("用户名或密码有误",R.drawable.tips);
                dialog.dismiss();
                return;
            } else {
                state = datajson.substring(datajson.indexOf("{"), datajson.indexOf("}") + 1);
                juser = new JSONObject(state);
                onLoginSuccess(juser);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void onLoginSuccess(JSONObject json) {
        //取出第一个元素的信息，并且转化为JSONObject
        try {
            String id = json.getString("u_id");
            String name = "";
            String password = json.getString("u_password");
            String phone = json.getString("u_phone");
            String email = json.getString("u_email");
            int age = json.getInt("u_age");
            String sex = json.getString("u_sex");
            img = json.getString("u_img");
            String dengluflag="true";
            String adress = json.getString("u_hometown");
            String nickname = json.getString("u_nickname");
            String birtday = json.getString("u_birthday");
            String synopsis = json.getString("u_synopsis");
            String localtion = json.getString("u_localtion");
            preferencesService = new PreferencesService(LoginActivity.this); //将信息存入本地xml种
            preferencesService.saveuser(id, name, password, sex, age, phone, email,img,dengluflag,adress,nickname,birtday,synopsis,localtion);
            hold(phone);
            Boolean boolea = checkBox.isChecked();
            if (boolea){
                passwordata(phone,password);
            }
            else {
                passwordata(phone,"");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Intent intent = new Intent();
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.login_back:
                finish();
                break;
            case R.id.login_button:
                submit();
                break;
            case R.id.login_linearlayout:
                list_user.setVisibility(View.GONE);
                break;
            case R.id.login_show:
                password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());//显示密码
                password.setSelection(password.getText().length()); //TextView默认光标在最左端，这里控制光标在最右端
                v.setVisibility(View.GONE);
                fin.setVisibility(View.VISIBLE);
                break;
            case R.id.login_fin:
                password.setTransformationMethod(PasswordTransformationMethod.getInstance()); //隐藏密码
                password.setSelection(password.getText().length());//TextView默认光标在最左端，这里控制光标在最右端
                v.setVisibility(View.GONE);
                show.setVisibility(View.VISIBLE);
                break;
            case R.id.login_Reg:
                Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
                break;
        }
    }

    private void name(){
        Intent intent = getIntent();
        if (intent!=null){
            //从注册界面传递过来的用户名
            String userName = intent.getStringExtra("Uname");
            if (userName==null){
                name.setText("");
            }else {
                name.setText(userName);
                name.setSelection(name.getText().length());
            }
        }
    }

    private void hold(String name){
        boolean hasData = hasData(name);
        if (!hasData) {
            insertData(name);
        }
    }

    /**
     * 存入密码
     */
    private void passwordata(String tempName,String password){
        db = helper.getWritableDatabase();
        ContentValues values= new ContentValues();
        values.put("password",password);//key为字段名，value为值
        db.update("users", values,"uname=?",new String[]{tempName});
        db.close();
    }
    /**
     * 判断用户名是否存在
     */
    private boolean hasData(String tempName) {
        Cursor cursor = helper.getReadableDatabase().rawQuery(
                "select id,uname from users where uname = ?", new String[]{tempName});
        //判断是否有下一个
        return cursor.moveToNext();
    }

    /**
     * 密码查询
     */

    private String passdata(String tempName){
        Cursor cursor = helper.getReadableDatabase().rawQuery(
                "select id,password from users where uname =?", new String[]{tempName});
        for(cursor.moveToFirst();!cursor.isAfterLast();cursor.moveToNext())
        {
            int nameColumn = cursor.getColumnIndex("password");
            String name = cursor.getString(nameColumn);
            return name;
        }
        return null;
    }

    /**
     * 清空数据
     */
    private void deleteData() {
        db = helper.getWritableDatabase();
        db.execSQL("delete from users");
        db.close();
    }
    /**
     * 删除数据
     */
    private void deleteDataa(String uname) {
        db = helper.getWritableDatabase();
        db.delete("users","uname= ?",new String[]{uname});
        db.close();
    }
    /**
     * 写入
     */
    private void insertData(String tempName) {
        db = helper.getWritableDatabase();
        db.execSQL("insert into users(uname,password) values('"+tempName+"','' )");
        db.close();
    }

    /**
     * 模糊查询数据
     */
    private void queryData(String tempName) {
        mList.clear();
        Cursor cursor = helper.getReadableDatabase().rawQuery(
                "select id,uname,id from users where uname like '%" + tempName + "%' order by id desc ", null);
        for(cursor.moveToFirst();!cursor.isAfterLast();cursor.moveToNext())
        {
            int nameColum = cursor.getColumnIndex("uname");
            mList.add(cursor.getString(nameColum));
        }
    }
}
