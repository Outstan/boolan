package com.example.boolan.module.me;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.boolan.Adapter.FollowAdapter;
import com.example.boolan.Adapter.HomeAdapter;
import com.example.boolan.R;
import com.example.boolan.beans.Home;
import com.example.boolan.beans.User;
import com.example.boolan.service.PreferencesService;
import com.example.boolan.utils.ConstantUtil;
import com.example.boolan.utils.HttpUtil;
import com.example.boolan.utils.Toast;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class FollowActivity extends AppCompatActivity implements View.OnClickListener {
    @BindView(R.id.follow_search)
    public LinearLayout search;
    @BindView(R.id.follow_back)
    public Button back;
    @BindView(R.id.follow_text)
    public TextView text;
    @BindView(R.id.follow_text1)
    public TextView text1;
    @BindView(R.id.follow_text2)
    public TextView text2;
    @BindView(R.id.follow_recycler)
    public RecyclerView recyclerView;
    private Map<String, String> s;
    private String url= ConstantUtil.SERVER_ADDRESS,Url;
    private ArrayList<User> datas = new ArrayList<>();
    private ArrayList<Home> datass = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myfollow);
        ButterKnife.bind(this);//绑定黄油刀
        s=new PreferencesService(FollowActivity.this).getdengluflag();
        initview();
    }

    private void initview() {
        back.setOnClickListener(this);
        search.setOnClickListener(this);
        int flag = getIntent().getIntExtra("flag",0);
        if (flag==1){
            Url = url+"/Follow/getFollows?id="+s.get("id");
            getdata();
        }else if (flag==2){
            text.setText("粉丝");
            text1.setText("全部粉丝");
            text2.setText("搜全部粉丝");
            Url = url+"/Follow/getbFollows?id="+s.get("id");
            getdata();
        }else if (flag==3){
            text.setText("我发布的内容");
            text1.setText("我的全部内容");
            text2.setText("搜全部内容");
            Url = url+"/Home/getmycontent?id="+s.get("id");
            submit();
        }else if (flag==4){
            String id = getIntent().getStringExtra("id");
            String nickname = getIntent().getStringExtra("nickname");
            text.setText(nickname+"的内容");
            text1.setText(nickname+"的全部内容");
            text2.setText("搜Ta的全部内容");
            Url = url+"/Home/getmycontent?id="+id;
            submit();
        }
    }

    private void submit(){
        HttpUtil.getAsyncGET(Url, new Callback() {
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

    private void getdata() {
        HttpUtil.getAsyncGET(Url, new Callback() {
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
                    Toast.getInstance(FollowActivity.this).show("连接服务器失败",R.drawable.tips);
                    break;
                case 2:
                    String adaptdata = (String) msg.obj;
                    adapterdata(adaptdata);
                    break;
                case 3:
                    handlemyinfocontentResult(msg.obj);
                    break;
            }
        }
    };

    private void handlemyinfocontentResult(Object obj) {
        String datajson = (String) obj;
        JSONArray juser = null;
        if (!datajson.equals("")) {
            try {
                juser = new JSONArray(datajson);
                for (int i = 0; i < juser.length(); i++) {
                    JSONObject jsonObject = juser.getJSONObject(i);
                    if (!jsonObject.getString("time").equals("null")) {
                        Home home = new Home();
                        home.setId(jsonObject.getString("id"));
                        home.setName(jsonObject.getString("name"));
                        home.setConnect(jsonObject.getString("connect"));
                        home.setHead(jsonObject.getString("head"));
                        String[] photo = {jsonObject.getString("photo1"), jsonObject.getString("photo2"), jsonObject.getString("photo3"), jsonObject.getString("photo4"), jsonObject.getString("photo5"), jsonObject.getString("photo6"),};
                        int l = 0, k = 0;
                        for (int j = 0; j < photo.length; j++) {
                            if (!photo[j].equals("null")) {
                                l++;
                            }
                        }
                        String[] pho = new String[l];
                        for (int j = 0; j < photo.length; j++) {
                            if (!photo[j].equals("null")) {
                                pho[k] = photo[j];
                                k++;
                            }
                        }
                        home.setPhoto(pho);
                        home.setTime(jsonObject.getString("time"));
                        home.setZnumber(jsonObject.getString("znumber"));
                        home.setZfnumber(jsonObject.getString("zfnumber"));
                        home.setCommentnumber(jsonObject.getString("commentnumber"));
                        home.setImg(jsonObject.getString("img"));
                        home.setYnickname(jsonObject.getString("ynickname"));
                        home.setYtime(jsonObject.getString("ytime"));
                        home.setZcontent(jsonObject.getString("zcontent"));
                        home.setU_id(jsonObject.getString("u_id"));
                        home.setYid(jsonObject.getString("yid"));
                        home.setFollow(jsonObject.getString("follow"));
                        home.setPdpraise(jsonObject.getString("pdpraise"));
                        datass.add(home);
                    }
                }
                LinearLayoutManager layoutManager = new LinearLayoutManager(FollowActivity.this);
                recyclerView.setLayoutManager(layoutManager);
                HomeAdapter adapter = new HomeAdapter(FollowActivity.this, datass);
                recyclerView.setAdapter(adapter);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void adapterdata(String adaptdata) {
        JSONArray jsonArray=null;
            try {
                jsonArray = new JSONArray(adaptdata);
                for (int i = 0; i<jsonArray.length(); i++) {
                    User user = new Gson().fromJson(jsonArray.get(i).toString(), User.class);
                    datas.add(user);
                }
            } catch (JSONException e) {
                e.printStackTrace();
        }
        LinearLayoutManager layoutManager= new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        FollowAdapter adapter = new FollowAdapter(FollowActivity.this,datas);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.follow_search:

                break;
            case R.id.follow_back:
                finish();
                break;
        }
    }
}
