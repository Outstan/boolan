package com.example.boolan.module.home;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.boolan.Adapter.HomecontentcommentAdapter;
import com.example.boolan.R;
import com.example.boolan.base.BaseFragment;
import com.example.boolan.beans.Home;
import com.example.boolan.utils.ConstantUtil;
import com.example.boolan.utils.HttpUtil;
import com.example.boolan.utils.Toast;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class CommentFragment extends BaseFragment {
    @BindView(R.id.contentzf_recycle_view)
    public RecyclerView recyclerView;
    @BindView(R.id.height)
    public LinearLayout h;
    private String url = ConstantUtil.SERVER_ADDRESS,id;
    public ArrayList<Home> datas = new ArrayList<>();
    @Override
    public int getLayoutResId() {
        return R.layout.home_contentdynamic;
    }

    @Override
    public void finishCreateView(Bundle state) {
        id = getArguments().getString("data");
        submit();
    }

    @Override
    public void lazyLoad() { }

    public void submit(){
        HttpUtil.getAsyncGET(url + "/Home/comment?id="+id, new Callback() {
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
                    Toast.getInstance(getActivity()).show("连接服务器失败", R.drawable.tips);
                    break;
                case 2:
                    handleLoginResult(msg.obj);
                    break;
            }
        }
    };

    private void handleLoginResult(Object obj) {
        String datajson = (String) obj,data=null;
        JSONArray juser = null;
        try {
            data = datajson.substring(1,datajson.length()-3);
            if (!data.equals("")) {
                juser = new JSONArray(data);
                for (int i = 0; i < juser.length(); i++) {
                    JSONObject jsonObject = juser.getJSONObject(i);
                    Home home = new Home();
                    home.setName(jsonObject.getString("name"));
                    home.setConnect(jsonObject.getString("connect"));
                    home.setHead(jsonObject.getString("head"));
                    home.setTime(jsonObject.getString("time"));
                    datas.add(home);
                }
                LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                recyclerView.setLayoutManager(layoutManager);
                HomecontentcommentAdapter adapter = new HomecontentcommentAdapter(getActivity(), datas);
                recyclerView.setAdapter(adapter);

                LinearLayout.LayoutParams linearParams =(LinearLayout.LayoutParams) recyclerView.getLayoutParams();
                linearParams.weight=recyclerView.getWidth();
                linearParams.height = h.getHeight()*juser.length();
                recyclerView.setLayoutParams(linearParams);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static CommentFragment newInstance(String param) {
        CommentFragment fragment = new CommentFragment();
        Bundle args = new Bundle();
        args.putString("data", param);
        fragment.setArguments(args);
        return fragment;
    }
}
