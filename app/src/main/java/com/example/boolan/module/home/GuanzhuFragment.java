package com.example.boolan.module.home;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.boolan.Adapter.HomeAdapter;
import com.example.boolan.R;
import com.example.boolan.base.BaseFragment;
import com.example.boolan.beans.Home;
import com.example.boolan.service.PreferencesService;
import com.example.boolan.utils.ConstantUtil;
import com.example.boolan.utils.HttpUtil;
import com.example.boolan.utils.Toast;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import butterknife.BindView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class GuanzhuFragment extends BaseFragment {
    @BindView(R.id.home_recycle_view)
    RecyclerView recyclerView;
    @BindView(R.id.refreshlayout)
    RefreshLayout refreshLayout;
    private boolean isPrepared;
    /**
     * 是否已被加载过一次，第二次就不再去请求数据了
     */
    private boolean mHasLoadedOnce;
    private ArrayList<Home> datas = new ArrayList<>();
    private String url= ConstantUtil.SERVER_ADDRESS;
    private Map<String, String> s;
    private int start;
    private HomeAdapter adapter;
    @Override
    public int getLayoutResId() {
        return R.layout.fragment_home_guanzhu_fragment;
    }

    @Override
    public void finishCreateView(Bundle state) {
        isPrepared = true;
        start=1;
        lazyLoad();
        update();
    }

    @Override
    public void lazyLoad() {
        if (!isPrepared || !isVisible || mHasLoadedOnce) {
            return;
        }
        //填充各控件的数据
        mHasLoadedOnce = true;
        s = new PreferencesService(getActivity()).getdengluflag();
        submit0();
    }

    private void submit0() {

        HttpUtil.getAsyncGET(url + "/Home/followconnect?id="+s.get("id")+"&start="+start, new Callback() {
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
                    refreshLayout.finishRefresh(false);
                    refreshLayout.finishLoadMore(false);
                    Toast.getInstance(getActivity()).show("连接服务器失败", R.drawable.tips);
                    break;
                case 2:
                    refreshLayout.finishRefresh(true);
                    refreshLayout.finishLoadMore(true);
                    handleLoginResult(msg.obj);
                    break;
                case 3:
                    if (start==1) {
                        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                        recyclerView.setLayoutManager(layoutManager);
                        adapter = new HomeAdapter(getActivity(), datas);
                        recyclerView.setAdapter(adapter);
                    }else adapter.notifyItemChanged(datas.size()-1);
                    break;
            }
        }
    };

    public void handleLoginResult(Object obj) {
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
                        datas.add(home);
                    }
                }
                sendMessage(3,datas);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else recyclerView.setAdapter(null);
    }

    private void update(){
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                mHasLoadedOnce=false;
                datas.clear();
                start=1;
                lazyLoad();
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                mHasLoadedOnce=false;
                start++;
                lazyLoad();
            }
        });
    }
}
