package com.example.boolan.module.MainActivityfragment;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.example.boolan.Adapter.FindHistoryAdapter;
import com.example.boolan.Adapter.FollowAdapter;
import com.example.boolan.Adapter.HomeAdapter;
import com.example.boolan.R;
import com.example.boolan.base.BaseFragment;
import com.example.boolan.beans.Home;
import com.example.boolan.beans.User;
import com.example.boolan.helper.RecordSQLiteOpenHelper;
import com.example.boolan.module.find.FindcontentFragment;
import com.example.boolan.module.find.FindcountFragment;
import com.example.boolan.service.PreferencesService;
import com.example.boolan.utils.ConstantUtil;
import com.example.boolan.utils.FlowLayout;
import com.example.boolan.utils.HttpUtil;
import com.example.boolan.utils.Toast;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static android.content.Context.INPUT_METHOD_SERVICE;

@SuppressLint("WrongConstant")
public class FindFragment extends BaseFragment implements View.OnClickListener {
    @BindView(R.id.find_search1)
    public LinearLayout search1;
    @BindView(R.id.find_search2)
    public LinearLayout search2;
    @BindView(R.id.find_searchtext)
    public EditText search;
    @BindView(R.id.find_remove)
    public TextView remove;
    @BindView(R.id.mFlowLayout)
    public FlowLayout mFlowLayout;
    @BindView(R.id.search_recycle_history)
    public RecyclerView historyRecycle;
    @BindView(R.id.find_tabs)
    public TabLayout tabLayout;
    @BindView(R.id.find_view)
    public ViewPager viewPager;
    @BindView(R.id.find_searchcontent)
    public LinearLayout searchContent;
    @BindView(R.id.find_top)
    public LinearLayout top;
    private SQLiteDatabase db;
    private RecordSQLiteOpenHelper helper;
    private List<String> mList = new ArrayList<>();
    private FindHistoryAdapter findHistoryAdapter;
    private String search_namee,url= ConstantUtil.SERVER_ADDRESS;
    private LinearLayoutManager layoutManager;
    private ArrayList<User> datas = new ArrayList<>();
    private ArrayList<Home> datass = new ArrayList<>();
    private List<Fragment> fragments = new ArrayList<>();
    private String[] strings = new String[]{"用户","推荐"};
    private FindcontentFragment findcontentFragment;
    private FindcountFragment findcountFragment;
    private HomeFragment homeFragment;
    private Map<String, String> s;

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_find;
    }

    @Override
    public void finishCreateView(Bundle state) {
        s = new PreferencesService(getActivity()).getdengluflag();
        initview();
        Hostcontent();
        queryData("");
        lazyLoad();
        visible();

        search.setOnKeyListener(new View.OnKeyListener() {// 输入完后按键盘上的搜索键
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                    ((InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    search_namee = search.getText().toString().trim();
                    boolean hasData = hasData(search_namee);
                    if (!TextUtils.isEmpty(search_namee)) {
                        if (!hasData) {
                            insertData(search.getText().toString().trim());
                            queryData("");
                            findHistoryAdapter.notifyDataSetChanged();
                        }
                        submit(search_namee,"1",url+"/user/searchuser");
                        submit1(search_namee,"1",url+"/Home/getsearch");
                        hideKeyboard(v.getWindowToken());
                        top.setVisibility(View.GONE);
                    }
                }
                return false;
            }
        });
    }

    @Override
    public void lazyLoad() {
        historyRecycle.setLayoutManager(layoutManager);
        findHistoryAdapter = new FindHistoryAdapter(getActivity(), mList);
        historyRecycle.setAdapter(findHistoryAdapter);
        RecycleOnclick();
    }

    private void initview() {
        search1.setOnClickListener(this);
        remove.setOnClickListener(this);
        helper = new RecordSQLiteOpenHelper(getActivity());
        layoutManager = new LinearLayoutManager(getActivity());
    }

    private void Hostcontent() {
        final List<String> list = new ArrayList<>();
        list.add("木杉");
        list.add("今天是");
        list.add("张三");
        list.add("理事");
        list.add("你不爱我了");
        mFlowLayout.setAlignByCenter(FlowLayout.AlienState.CENTER);
        mFlowLayout.setAdapter(list, R.layout.search_item, new FlowLayout.ItemView<String>() {
            @Override
            protected void getCover(String item, FlowLayout.ViewHolder holder, View inflate, final int position) {
                holder.setText(R.id.tv_label_name, item);
                inflate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Onclicks(list.get(position));
                    }
                });
            }
        });
    }

    private void submit(String SearchContent,String start,String url){
        Map params = new HashMap();
        params.put("sccontent",SearchContent);
        params.put("start",start);
        params.put("id",s.get("id"));
        HttpUtil.getAsyncPostBody(url, params, new Callback() {
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

    private void submit1(String SearchContent,String start,String url){
        Map params = new HashMap();
        params.put("sccontent",SearchContent);
        params.put("start",start);
        params.put("id",s.get("id"));
        HttpUtil.getAsyncPostBody(url, params, new Callback() {
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
                    handleSearchcontentResult(msg.obj);
                    break;
                case 3:
                    handleSearchcountResult(msg.obj);
                    break;
            }
        }
    };

    @SuppressLint("ResourceAsColor")
    private void handleSearchcontentResult(Object obj) {
        String datajson = (String) obj;
        JSONArray juser = null;
        if (!datajson.equals("")) {
            try {
                datas.clear();
                juser = new JSONArray(datajson);
                for (int i = 0; i<juser.length(); i++) {
                    User user = new Gson().fromJson(juser.get(i).toString(), User.class);
                    datas.add(user);
                }
                RecyclerView recyclerView = getActivity().findViewById(R.id.find_recycle);
                LinearLayoutManager layoutManager= new LinearLayoutManager(getActivity());
                recyclerView.setLayoutManager(layoutManager);
                FollowAdapter adapter = new FollowAdapter(getActivity(),datas);
                recyclerView.setAdapter(adapter);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleSearchcountResult(Object obj) {
        String datajson = (String) obj;
        JSONArray juser = null;
        if (!datajson.equals("")) {
            try {
                datass.clear();
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
                RecyclerView recyclerView = getActivity().findViewById(R.id.find_recycle1);
                LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                recyclerView.setLayoutManager(layoutManager);
                HomeAdapter adapter = new HomeAdapter(getActivity(), datass);
                recyclerView.setAdapter(adapter);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void visible(){
        fragments.add(findcountFragment.newInstance());
        fragments.add(findcontentFragment.newInstance());
        viewPager.setAdapter(new MyAdapter(getActivity().getSupportFragmentManager()));
        tabLayout.setupWithViewPager(viewPager);//此方法就是让tablayout和ViewPager联动
    }

    public class MyAdapter extends FragmentPagerAdapter {
        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return strings.length;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return strings[position];
        }
    }

    private void RecycleOnclick(){
        findHistoryAdapter.setOnItemDeleteClickListener(new FindHistoryAdapter.onItemDeleteListener() {
            @Override
            public void onDeleteClick(int position) {
                deleteDataa(mList.get(position));
                mList.remove(position);
                findHistoryAdapter.notifyDataSetChanged();
            }

            @Override
            public void ListOnclick(String content) {
                Onclicks(content);
            }
        });
    }

    private void Onclicks(String content){
        if (search1.getVisibility()==0){
            search1.setVisibility(View.GONE);
            search2.setVisibility(View.VISIBLE);
        }
        search.setText(content);
        search.setSelection(search.getText().length());
        showInput(search);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.find_search1:
                Onclicks("");
                break;
            case R.id.find_remove:
                search1.setVisibility(View.VISIBLE);
                search2.setVisibility(View.GONE);
                top.setVisibility(View.VISIBLE);
                search.setText("");
                hideKeyboard(v.getWindowToken());
                break;
        }
    }

    /**
     * 删除数据
     */
    private void deleteDataa(String uname) {
        db = helper.getWritableDatabase();
        db.delete("records", "name= ?", new String[]{uname});
        db.close();
    }

    /**
     * 插入数据
     */
    private void insertData(String tempName) {
        db = helper.getWritableDatabase();
        db.execSQL("insert into records(name) values('" + tempName + "')");
        db.close();
    }

    /**
     * 模糊查询数据
     */
    private void queryData(String tempName) {
        mList.clear();
        Cursor cursor = helper.getReadableDatabase().rawQuery(
                "select id ,name from records where name like '%" + tempName + "%' order by id desc ", null);
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            int nameColum = cursor.getColumnIndex("name");
            mList.add(cursor.getString(nameColum));
        }
    }

    /**
     * 检查数据库中是否已经有该条记录
     */
    private boolean hasData(String tempName) {
        Cursor cursor = helper.getReadableDatabase().rawQuery(
                "select id ,name from records where name =?", new String[]{tempName});
        //判断是否有下一个
        return cursor.moveToNext();
    }

    /**
     * 清空数据
     */
    private void deleteData() {
        db = helper.getWritableDatabase();
        db.execSQL("delete from records");
        db.close();
    }

    /**
     * 显示键盘
     *
     * @param et 输入焦点
     */
    public void showInput(final EditText et) {
        et.requestFocus();
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
        imm.showSoftInput(et, InputMethodManager.SHOW_IMPLICIT);
    }

    /**
     * 隐藏软键盘
     */
    private void hideKeyboard(IBinder token) {
        if (token != null) {
            InputMethodManager im = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(token, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public static FindFragment newInstance() {
        FindFragment fragment = new FindFragment();
        return fragment;
    }
}
