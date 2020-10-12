package com.example.boolan.module.chat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.boolan.Adapter.ChatAdapter;
import com.example.boolan.R;
import com.example.boolan.beans.Chat;
import com.example.boolan.module.common.MainActivity;
import com.example.boolan.service.PreferencesService;
import com.example.boolan.utils.ConstantUtil;
import com.example.boolan.utils.HttpUtil;
import com.example.boolan.utils.Toast;
import com.example.boolan.utils.WsStatusListener;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okio.ByteString;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener {

    private static List<Chat> msgList = new ArrayList<>();;
    private EditText inputText;
    private Button send,back;
    private TextView nicknametext;
    private static RecyclerView msgRecyclerView;
    private static ChatAdapter adapter;
    private static String id;
    private String url = ConstantUtil.SERVER_ADDRESS,nickname;
    private Map<String, String> userdata;
    private static Context context;

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        id = getIntent().getStringExtra("id");
        nickname = getIntent().getStringExtra("name");
        context=ChatActivity.this;
        initview();
        initMsgs();
    }

    private void initview() {
        nicknametext=findViewById(R.id.news_nickname);
        nicknametext.setText(nickname);
        userdata = new PreferencesService(ChatActivity.this).getdengluflag();
        inputText = (EditText) findViewById(R.id.input_txet);
        back = findViewById(R.id.news_back);
        send = (Button) findViewById(R.id.send);
        msgRecyclerView = (RecyclerView) findViewById(R.id.msg_recycle_view);
        send.setOnClickListener(this);
        back.setOnClickListener(this);
    }

    private void initMsgs() {
        Map params = new HashMap();
        params.put("id1", userdata.get("id"));
        params.put("id", id);
        HttpUtil.getAsyncPostBody(url + "/Home/chatnews", params, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                sendMessage(1, e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                sendMessage(2, response.body().string());
            }
        });
    }

    private void sendMessage(int what, Object obj) {
        Message msg = Message.obtain();
        msg.what = what;
        msg.obj = obj;
        handler.sendMessage(msg);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    Toast.getInstance(ChatActivity.this).show("连接服务器失败", R.drawable.tips);
                    break;
                case 2:
                    handleChatResult(msg.obj);
                    break;
            }
        }
    };

    private void handleChatResult(Object obj) {
        String datajson = (String) obj, data = null;
        JSONArray juser = null;
        try {
            msgList.clear();
            data = datajson.substring(1, datajson.length() - 3);
            juser = new JSONArray(data);
            for (int i = 0; i < juser.length(); i++) {
                JSONObject jsonObject = juser.getJSONObject(i);
                Chat chat = new Chat();
                chat.setContent(jsonObject.getString("connect"));
                chat.setType(jsonObject.getString("id"));
                msgList.add(chat);
            }
            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            msgRecyclerView.setLayoutManager(layoutManager);
            adapter = new ChatAdapter(ChatActivity.this, msgList);
            msgRecyclerView.setAdapter(adapter);
            msgRecyclerView.scrollToPosition(msgList.size() - 1);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static WsStatusListener wsBaseStatusListener = new WsStatusListener() {
        @Override
        public void onOpen(Response response) {
            super.onOpen(response);
            System.out.println(response);
            //协议初始化  心跳等
        }

        //消息处理
        @Override
        public void onMessage(String text) {
            super.onMessage(text);
            String id0 = text.substring(0, text.indexOf(";"));
            String content = text.substring(text.indexOf(";") + 1, text.length());
            if (id0.equals(id)) {
                Chat msg = new Chat(content, id);
                msgList.add(msg);
                if (msgList.size()==1){
                    LinearLayoutManager layoutManager = new LinearLayoutManager(context);
                    msgRecyclerView.setLayoutManager(layoutManager);
                    adapter = new ChatAdapter(context, msgList);
                    msgRecyclerView.setAdapter(adapter);
                }else {
                    //当有新消息，刷新RecyclerVeiw的显示
                    adapter.notifyItemInserted(msgList.size() - 1);
                    //将RecyclerView定位到最后一行
                    msgRecyclerView.scrollToPosition(msgList.size() - 1);
                    //清空输入框内容
                }
            }else {
                Intent intent = new Intent("com.example.boolan.CHAT");
                intent.putExtra("resource", id0);
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
            }
        }

        @Override
        public void onMessage(ByteString bytes) {
            super.onMessage(bytes);
            System.out.println(bytes + "2");
            //消息处理
        }

        @Override
        public void onClosing(int code, String reason) {
            super.onClosing(code, reason);
            System.out.println(reason);
        }

        @Override
        public void onClosed(int code, String reason) {
            super.onClosed(code, reason);
            System.out.println(reason);
        }

        @Override
        public void onFailure(Throwable t, Response response) {
            super.onFailure(t, response);
            System.out.println(t);
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.send:
                String content = inputText.getText().toString();
                if (!"".equals(content)) {
                    Boolean a = MainActivity.wsManage.sendMessage(userdata.get("id"),id,content);
                    if (a) {
                        Chat msg = new Chat(content, userdata.get("id"));
                        //跟list添加数据
                        msgList.add(msg);
                        if (msgList.size()==1){
                            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
                            msgRecyclerView.setLayoutManager(layoutManager);
                            adapter = new ChatAdapter(ChatActivity.this, msgList);
                            msgRecyclerView.setAdapter(adapter);
                        }else {
                            //当有新消息，刷新RecyclerVeiw的显示
                            adapter.notifyItemInserted(msgList.size() - 1);
                            //将RecyclerView定位到最后一行
                            msgRecyclerView.scrollToPosition(msgList.size() - 1);
                            //清空输入框内容
                        }
                        inputText.setText("");
                    } else {
                        System.out.println("失败");
                    }
                }
                break;
            case R.id.news_back:
                Intent intent = new Intent("com.example.boolan.CHAT");
                intent.putExtra("resource", "");
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                finish();
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            Intent intent = new Intent("com.example.boolan.CHAT");
            intent.putExtra("resource", "");
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
            finish();
            id="";
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}