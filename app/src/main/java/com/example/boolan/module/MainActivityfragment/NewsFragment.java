package com.example.boolan.module.MainActivityfragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.boolan.Adapter.NewsAdapter;
import com.example.boolan.Adapter.NewsAdapter.Onclickitem;
import com.example.boolan.R;
import com.example.boolan.base.BaseFragment;
import com.example.boolan.beans.Home;
import com.example.boolan.module.chat.ChatActivity;
import com.example.boolan.service.PreferencesService;
import com.example.boolan.utils.ConstantUtil;
import com.example.boolan.utils.HttpUtil;
import com.example.boolan.utils.Toast;
import com.google.gson.Gson;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class NewsFragment extends BaseFragment {
    @BindView(R.id.mews_recycle_view)
    public RecyclerView recyclerView;
    @BindView(R.id.news_refreshlayout)
    public RefreshLayout refreshLayout;
    private String url = ConstantUtil.SERVER_ADDRESS;
    private List<Home> datas = new ArrayList<>();
    private Map<String, String> userdata;
    private NewsAdapter newsAdapter;
    private LocalBroadcastManager broadcastManager;

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_news;
    }

    @Override
    public void finishCreateView(Bundle state) {
        userdata = new PreferencesService(getActivity()).getdengluflag();
        broadcastManager = LocalBroadcastManager.getInstance(getActivity());
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.example.boolan.CHAT");
        broadcastManager.registerReceiver(mAdDownLoadReceiver, intentFilter);
        lazyLoad();
        update();
    }

    private void update() {
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                userdata = new PreferencesService(getActivity()).getdengluflag();
                datas.clear();
                lazyLoad();
            }
        });
    }

    BroadcastReceiver mAdDownLoadReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            userdata = new PreferencesService(getActivity()).getdengluflag();
            String resource = (String) intent.getSerializableExtra("resource");
            datas.clear();
            lazyLoad();
        }
    };

    @Override
    public void lazyLoad() {
        HttpUtil.getAsyncGET(url + "/Home/chat?id=" + userdata.get("id"), new Callback() {
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
                    refreshLayout.finishRefresh(false);
                    Toast.getInstance(getActivity()).show("连接服务器失败", R.drawable.tips);
                    break;
                case 2:
                    refreshLayout.finishRefresh(true);
                    handleNewResult(msg.obj);
                    break;
            }
        }
    };

    private void handleNewResult(Object obj) {
        String datajson = (String) obj, data = null;
        JSONArray juser = null;
        try {
            data = datajson.substring(1, datajson.length() - 3);
            if (!data.equals("")) {
                juser = new JSONArray(data);
                for (int i = 0; i < juser.length(); i++) {
                    Home home = new Gson().fromJson(juser.get(i).toString(), Home.class);
                    datas.add(home);
                }
                LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                recyclerView.setLayoutManager(layoutManager);
                newsAdapter = new NewsAdapter(getActivity(), datas);
                recyclerView.setAdapter(newsAdapter);
                AdapterOnclick();
            }else recyclerView.setAdapter(null);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void AdapterOnclick() {
        newsAdapter.setOnItemClickListener(new Onclickitem() {
            @Override
            public void Onclick(Home home) {
                Intent intent = new Intent(getActivity(), ChatActivity.class);
                intent.putExtra("id", home.getId());
                intent.putExtra("name", home.getName());
                startActivity(intent);
            }

            @Override
            public void search() {
//                Intent intent = new Intent(getActivity(), Activity.class);
//                startActivity(intent);
            }

            @Override
            public String data(String time) {
                String data = null;
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = null;
                try {
                    date = sdf.parse(time);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                Calendar now = Calendar.getInstance();
                if (now.get(Calendar.YEAR) - calendar.get(Calendar.YEAR) == 0) {
                    if (now.get(Calendar.MONTH) - calendar.get(Calendar.MONTH) == 0) {
                        if (now.get(Calendar.DATE) - calendar.get(Calendar.DATE) == 0) {
                            if (calendar.get(Calendar.AM_PM) == 1) {
                                data = "下午" + calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE);
                            } else
                                data = "上午" + calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE);
                        } else if (now.get(Calendar.DATE) - calendar.get(Calendar.DATE) == 1) {
                            data = "昨天";
                        } else if (now.get(Calendar.DATE) - calendar.get(Calendar.DATE) == 2) {
                            data = "前天";
                        } else if (now.get(Calendar.DATE) - calendar.get(Calendar.DATE) > 2 && now.get(Calendar.DATE) - calendar.get(Calendar.DATE) < 7) {
                            if (calendar.get(Calendar.DAY_OF_WEEK) == 1) {
                                data = "星期日";
                            } else if (calendar.get(Calendar.DAY_OF_WEEK) == 2) {
                                data = "星期一";
                            } else if (calendar.get(Calendar.DAY_OF_WEEK) == 3) {
                                data = "星期二";
                            } else if (calendar.get(Calendar.DAY_OF_WEEK) == 4) {
                                data = "星期三";
                            } else if (calendar.get(Calendar.DAY_OF_WEEK) == 5) {
                                data = "星期四";
                            } else if (calendar.get(Calendar.DAY_OF_WEEK) == 6) {
                                data = "星期五";
                            } else if (calendar.get(Calendar.DAY_OF_WEEK) == 7) {
                                data = "星期六";
                            }
                        } else
                            data = calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH) + 1) + "-" + calendar.get(Calendar.DATE);
                    } else
                        data = calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH) + 1) + "-" + calendar.get(Calendar.DATE);
                } else data = calendar.get(Calendar.YEAR) + "年";
                return data;
            }
        });
    }

    public static NewsFragment newInstance() {
        NewsFragment fragment = new NewsFragment();
        return fragment;
    }
}