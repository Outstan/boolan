package com.example.boolan.module.home;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.boolan.Adapter.Home_itemAdapter;
import com.example.boolan.Adapter.Home_oneitemAdapter;
import com.example.boolan.R;
import com.example.boolan.beans.Home;
import com.example.boolan.service.PreferencesService;
import com.example.boolan.utils.ConstantUtil;
import com.example.boolan.utils.HttpUtil;
import com.example.boolan.utils.MyScrollView;
import com.example.boolan.utils.Toast;
import com.facebook.drawee.view.SimpleDraweeView;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

@RequiresApi(api = Build.VERSION_CODES.M)
public class ContentdetailedActivity extends AppCompatActivity implements MyScrollView.OnScrollListener, View.OnClickListener {
    @BindView(R.id.home_linearlayout)
    public LinearLayout home_linearlayout;
    @BindView(R.id.home_topbutton)
    public Button back;
    @BindView(R.id.home_toptext)
    public LinearLayout top_text;
    @BindView(R.id.home_topname)
    public LinearLayout top_name;
    @BindView(R.id.home_topphoto)
    public SimpleDraweeView topuserphoto;
    @BindView(R.id.home_topnickname)
    public TextView topnicknaame;
    @BindView(R.id.home_myscrollview)
    public MyScrollView myScrollView;
    @BindView(R.id.home_photo)
    public SimpleDraweeView userphoto;
    @BindView(R.id.home_nickname)
    public TextView nicknaame;
    @BindView(R.id.home_time)
    public TextView time;
    @BindView(R.id.home_text)
    public TextView content;
    @BindView(R.id.home_layoutview)
    public LinearLayout lLayoutView;
    @BindView(R.id.home_center)
    public LinearLayout center;
    @BindView(R.id.Main_lLayoutViewTemp)
    public LinearLayout lLayoutTemp;
    @BindView(R.id.content_zf)
    public TextView contentzf;
    @BindView(R.id.content_pn)
    public TextView contentpl;
    @BindView(R.id.content_z)
    public TextView contentz;
    @BindView(R.id.refreshlayout)
    public RefreshLayout refreshLayout;
    @BindView(R.id.home_recycle_item)
    public RecyclerView recyclerView;
    @BindView(R.id.content_content)
    public LinearLayout content1;
    @BindView(R.id.home_praise)
    public ImageView praiseimg;
    @BindView(R.id.home_mypraise)
    public LinearLayout prarsebutton;
    @BindView(R.id.home_f)
    public LinearLayout zhuanfalin;
    @BindView(R.id.home_p)
    public LinearLayout commentlin;

    private ZhuanfaFragment zhuanfaFragment;
    private CommentFragment commentFragment;
    private PraiseFragment praiseFragment;
    private Fragment fragment;
    private Fragment[] mFragments;
    private FragmentManager fm = getSupportFragmentManager();
    private Home home;
    private int posi = 1;
    private String url = ConstantUtil.SERVER_ADDRESS, id,zfnumber,commentnum,praisenum;
    private Uri imageUri;
    private Map<String, String> mydata;
    public static AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_contentdetailed);
        ButterKnife.bind(this);//绑定黄油刀
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            home = (Home) bundle.getSerializable("key");
            id = home.getId();
            mFragments = new Fragment[]{zhuanfaFragment.newInstance(id), commentFragment.newInstance(id),
                    praiseFragment.newInstance(id)};
            fragment = commentFragment.newInstance(id);
        }
        init();
        update();
        submit();
        viewsAddListener();
    }

    private void submit() {
        HttpUtil.getAsyncGET(url + "/Home/number?id=" + id, new Callback() {
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
                    Toast.getInstance(ContentdetailedActivity.this).show("连接服务器失败", R.drawable.tips);
                    break;
                case 2:
                    show(msg.obj);
                    break;
                case 3:
                    String s = (String) msg.obj;
                    if (s.substring(0, 1).equals("1")) {
                        int p = Integer.parseInt(praisenum);
                        p++;
                        praisenum=p+"";
                        contentz.setText("赞 " + p);
                        praiseimg.setImageResource(R.drawable.mypraise);
                        home.setPdpraise("true");
                    }
                    break;
                case 4:
                    s = (String) msg.obj;
                    if (s.substring(0, 1).equals("1")) {
                        int p = Integer.parseInt(praisenum);
                        p--;
                        praisenum=p+"";
                        contentz.setText("赞 " + p);
                        praiseimg.setImageResource(R.drawable.mypraise1);
                        home.setPdpraise("false");
                    }
                    break;
                case 5:
                    s = (String) msg.obj;
                    if (s.substring(0, 1).equals("1")) {
                        dialog.dismiss();
                        hideKeyboard(getCurrentFocus().getWindowToken());
                    }
                    break;
            }
        }
    };

    private void show(Object obj) {
        String num = (String) obj;
        zfnumber = num.substring(0, num.indexOf(","));
        commentnum = num.substring(num.indexOf(",") + 1, num.indexOf(";"));
        praisenum = num.substring(num.indexOf(";") + 1, num.length() - 2);
        contentzf.setText("转发 " + zfnumber);
        contentpl.setText("评论 " + commentnum);
        contentz.setText("赞 " + praisenum);
    }

    @SuppressLint("WrongConstant")
    private void init() {
        mydata = new PreferencesService(this).getdengluflag();
        showfragment(1);
        contentpl.setTextColor(ContentdetailedActivity.this.getResources().getColor(R.color.black));
        contentpl.getPaint().setFakeBoldText(true);
        back.setOnClickListener(this);
        contentzf.setOnClickListener(this);
        contentz.setOnClickListener(this);
        contentpl.setOnClickListener(this);
        prarsebutton.setOnClickListener(this);
        zhuanfalin.setOnClickListener(this);
        commentlin.setOnClickListener(this);
        if (home.getPdpraise().equals("true")){
            praiseimg.setImageResource(R.drawable.mypraise);
        }else praiseimg.setImageResource(R.drawable.mypraise1);
        if (home.getU_id().equals(home.getYid())) {
            imageUri = Uri.parse(url + "/" + home.getHead() + ".png");
            topnicknaame.setText(home.getName());
            nicknaame.setText(home.getName());
            time.setText(home.getTime());
        }else {
            imageUri = Uri.parse(url + "/" + home.getImg() + ".png");
            topnicknaame.setText(home.getYnickname());
            nicknaame.setText(home.getYnickname());
            time.setText(home.getYtime());
        }
        topuserphoto.setImageURI(imageUri);
        userphoto.setImageURI(imageUri);
        content.setText(home.getConnect());
        if (home.getPhoto().length==1) {
            GridLayoutManager gridLayoutManager = new GridLayoutManager(ContentdetailedActivity.this, 1);
            //设置RecycleView显示的方向是水平还是垂直 GridLayout.HORIZONTAL水平  GridLayout.VERTICAL默认垂直
            gridLayoutManager.setOrientation(GridLayout.VERTICAL);
            recyclerView.setLayoutManager(gridLayoutManager);
            Home_oneitemAdapter adapter = new Home_oneitemAdapter(ContentdetailedActivity.this, home.getPhoto());
            recyclerView.setAdapter(adapter);
        }else if (home.getPhoto().length==4) {
            DisplayMetrics dm = ContentdetailedActivity.this.getResources().getDisplayMetrics();
            int width = dm.widthPixels;
            ViewGroup.LayoutParams lp=ContentdetailedActivity.this.recyclerView.getLayoutParams();
            lp.width=(width/2)+(width/2/2);
            recyclerView.setLayoutParams(lp);
            GridLayoutManager gridLayoutManager = new GridLayoutManager(ContentdetailedActivity.this, 2);
            //设置RecycleView显示的方向是水平还是垂直 GridLayout.HORIZONTAL水平  GridLayout.VERTICAL默认垂直
            gridLayoutManager.setOrientation(GridLayout.VERTICAL);
            recyclerView.setLayoutManager(gridLayoutManager);
            Home_itemAdapter adapter = new Home_itemAdapter(ContentdetailedActivity.this, home.getPhoto());
            recyclerView.setAdapter(adapter);
        }else{
            GridLayoutManager gridLayoutManager = new GridLayoutManager(ContentdetailedActivity.this, 3);
            //设置RecycleView显示的方向是水平还是垂直 GridLayout.HORIZONTAL水平  GridLayout.VERTICAL默认垂直
            gridLayoutManager.setOrientation(GridLayout.VERTICAL);
            recyclerView.setLayoutManager(gridLayoutManager);
            Home_itemAdapter adapter = new Home_itemAdapter(ContentdetailedActivity.this, home.getPhoto());
            recyclerView.setAdapter(adapter);
        }
    }

    private void viewsAddListener() {
        //当布局的状态或者控件的可见性发生改变回调的接口
        home_linearlayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                //这一步很重要，使得上面的布局和下面的布局重合
                onScroll(myScrollView.getScrollY());
            }
        });
        myScrollView.setOnScrollListener(this);
    }

    private void initview() {
        contentzf.setTextColor(ContentdetailedActivity.this.getResources().getColor(R.color.darkGrey));
        contentzf.getPaint().setFakeBoldText(false);
        contentpl.setTextColor(ContentdetailedActivity.this.getResources().getColor(R.color.darkGrey));
        contentpl.getPaint().setFakeBoldText(false);
        contentz.setTextColor(ContentdetailedActivity.this.getResources().getColor(R.color.darkGrey));
        contentz.getPaint().setFakeBoldText(false);
    }

    @Override
    public void onScroll(int scrollY) {
        int mBuyLayout2ParentTop = Math.max(scrollY, lLayoutTemp.getTop());
        lLayoutView.layout(0, mBuyLayout2ParentTop, lLayoutView.getWidth(), mBuyLayout2ParentTop + lLayoutView.getHeight());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.content_zf:
                initview();
                contentzf.setTextColor(ContentdetailedActivity.this.getResources().getColor(R.color.black));
                contentzf.getPaint().setFakeBoldText(true);
                showfragment(0);
                break;
            case R.id.content_pn:
                initview();
                contentpl.setTextColor(ContentdetailedActivity.this.getResources().getColor(R.color.black));
                contentpl.getPaint().setFakeBoldText(true);
                showfragment(1);
                break;
            case R.id.content_z:
                initview();
                contentz.setTextColor(ContentdetailedActivity.this.getResources().getColor(R.color.black));
                contentz.getPaint().setFakeBoldText(true);
                showfragment(2);
                break;
            case R.id.home_topbutton:
                finish();
                break;
            case R.id.home_mypraise:
                praise();
                break;
            case R.id.home_f:
                zhuanfa();
                break;
            case R.id.home_p:
                commentdialog();
                break;
        }
    }

    private void commentdialog() {
        int width = getResources().getDisplayMetrics().widthPixels;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        dialog = builder.create();
        View view = LayoutInflater.from(this).inflate(R.layout.content_comment_dialog, null);
        dialog.setView(view);
        EditText editText = view.findViewById(R.id.comment_editext);
        Button button = view.findViewById(R.id.comment_submit);
        Window window = dialog.getWindow();
        window.setWindowAnimations(R.style.Animationsharedialog);
        window.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = width;
        //params.dimAmount=0.1f;
        window.setBackgroundDrawableResource(R.color.white);
        window.setGravity(Gravity.BOTTOM);
        dialog.show();

        button.setOnClickListener(v -> {
            comment(editText.getText().toString());
        });
    }

    private void comment(String content0) {
        if (mydata.get("dengluflag").equals("true")) {
            HttpUtil.getAsyncGET(url + "/Home/setcomment?u_id=" + mydata.get("id")+"&b_id="+home.getId()+"&content="+content0, new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    sendMessage(1, e.getMessage());
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    sendMessage(5, response.body().string());
                }
            });
        } else {
            Toast.getInstance(this).show("未登录", R.drawable.tips);
        }
    }

    private void zhuanfa() {
        if (!home.getU_id().equals(mydata.get("id"))) {
            if (mydata.get("dengluflag").equals("true")) {
                Intent intent = new Intent(this, ShareActivity.class);
                Bundle build = new Bundle();
                build.putSerializable("key", home);
                intent.putExtras(build);
                this.startActivity(intent);
            } else {
                Toast.getInstance(this).show("未登录", R.drawable.tips);
            }
        } else {
            Toast.getInstance(this).show("不可以转发自己的内容", R.drawable.tips);
        }
    }

    private void praise() {
        if (mydata.get("dengluflag").equals("true")) {
            if (home.getPdpraise().equals("false")) {
                HttpUtil.getAsyncGET(url + "/Home/setpraise?u_id=" + mydata.get("id") + "&b_id=" + home.getId(), new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        sendMessage(1, e.getMessage());
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        sendMessage(3, response.body().string());
                    }
                });
            } else {
                HttpUtil.getAsyncGET(url + "/Home/removepraise?u_id=" + mydata.get("id") + "&b_id=" + home.getId(), new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        sendMessage(1, e.getMessage());
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        sendMessage(4, response.body().string());
                    }
                });
            }
        } else Toast.getInstance(this).show("未登录", R.drawable.tips);
    }

    private void showfragment(int position) {
        Fragment currentFragment = mFragments[position];
        if (currentFragment != null) {
            //添加渐隐渐现的动画
            FragmentTransaction ft = fm.beginTransaction();
            if (!currentFragment.isAdded()) {  // 先判断是否被add过
                ft.hide(fragment).add(R.id.content_content, currentFragment).commit(); // 隐藏当前的fragment，add下一个到Activity中
            } else {
                ft.hide(fragment).show(currentFragment).commit(); // 隐藏当前的fragment，显示下一个
            }
            fragment = currentFragment;
            posi = position;
        }
    }

    private void update() {
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                submit();
                mFragments = new Fragment[]{zhuanfaFragment.newInstance(id), commentFragment.newInstance(id),
                        praiseFragment.newInstance(id)};
                showfragment(posi);
                refreshLayout.finishRefresh(true);
            }
        });
    }

    /**
     * 隐藏软键盘
     */
    private void hideKeyboard(IBinder token) {
        if (token != null) {
            InputMethodManager im = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(token, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

}
