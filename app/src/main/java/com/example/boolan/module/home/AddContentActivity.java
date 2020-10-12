package com.example.boolan.module.home;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.boolan.Adapter.AddcontentAdapter;
import com.example.boolan.R;
import com.example.boolan.service.PreferencesService;
import com.example.boolan.utils.ConstantUtil;
import com.example.boolan.utils.HttpUtil;
import com.example.boolan.utils.Toast;
import com.facebook.drawee.backends.pipeline.Fresco;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.finalteam.rxgalleryfinal.RxGalleryFinal;
import cn.finalteam.rxgalleryfinal.bean.MediaBean;
import cn.finalteam.rxgalleryfinal.imageloader.ImageLoaderType;
import cn.finalteam.rxgalleryfinal.rxbus.RxBusResultDisposable;
import cn.finalteam.rxgalleryfinal.rxbus.event.ImageMultipleResultEvent;
import cn.finalteam.rxgalleryfinal.ui.RxGalleryListener;
import cn.finalteam.rxgalleryfinal.ui.base.IMultiImageCheckedListener;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.yalantis.ucrop.util.BitmapLoadUtils.calculateInSampleSize;

@SuppressLint("WrongConstant")
public class AddContentActivity extends AppCompatActivity implements View.OnClickListener {
    @BindView(R.id.addc_recylerview)
    public RecyclerView recyclerView;
    @BindView(R.id.addc_back)
    public TextView back;
    @BindView(R.id.addc_send)
    public TextView send;
    @BindView(R.id.addc_edittext)
    public EditText editText;
    private AddcontentAdapter addcontentAdapter;
    private List<MediaBean> list = null;
    private List<File> files;
    private String url= ConstantUtil.SERVER_ADDRESS;
    private Map<String, String> userdata;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(AddContentActivity.this);
        setContentView(R.layout.home_addcontent);
        ButterKnife.bind(this);//绑定黄油刀
        intview();
        back.setOnClickListener(this);
        send.setOnClickListener(this);
        userdata = new PreferencesService(AddContentActivity.this).getdengluflag();
        //多选事件的回调
        RxGalleryListener
                .getInstance()
                .setMultiImageCheckedListener(
                        new IMultiImageCheckedListener() {
                            @Override
                            public void selectedImg(Object t, boolean isChecked) {
                                Toast.getInstance(AddContentActivity.this).show1(isChecked ? "选中" : "取消选中",R.drawable.tips);
                            }

                            @Override
                            public void selectedImgMax(Object t, boolean isChecked, int maxSize) {
                                Toast.getInstance(AddContentActivity.this).show1("你最多只能选择" + maxSize + "张图片",R.drawable.tips);
                            }
                        });
    }

    private void intview() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(AddContentActivity.this, 3);
        //设置RecycleView显示的方向是水平还是垂直 GridLayout.HORIZONTAL水平  GridLayout.VERTICAL默认垂直
        gridLayoutManager.setOrientation(GridLayout.VERTICAL);
        recyclerView.setLayoutManager(gridLayoutManager);
        addcontentAdapter = new AddcontentAdapter(AddContentActivity.this,list);
        recyclerView.setAdapter(addcontentAdapter);
        addcontentAdapterevent();
    }

    private void addcontentAdapterevent() {
        addcontentAdapter.setAddadapterevent(new AddcontentAdapter.Addadapterevent() {
            @Override
            public void AddOnClick() {
                openMulti();
            }

            @Override
            public void remove(int position) {
                list.remove(position);
                intview();
            }
        });
    }

    /**
     * 自定义多选
     */
    private void openMulti() {
        RxGalleryFinal rxGalleryFinal = RxGalleryFinal
                .with(AddContentActivity.this)
                .image()
                .multiple();
        if (list != null && !list.isEmpty()) {
            rxGalleryFinal
                    .selected(list);
        }
        rxGalleryFinal.maxSize(6)
                .imageLoader(ImageLoaderType.FRESCO)
                .subscribe(new RxBusResultDisposable<ImageMultipleResultEvent>() {

                    @Override
                    protected void onEvent(ImageMultipleResultEvent imageMultipleResultEvent) throws Exception {
                        list = imageMultipleResultEvent.getResult();
                        intview();
                        Toast.getInstance(AddContentActivity.this).show("已选择" + imageMultipleResultEvent.getResult().size() + "张图片",R.drawable.tips);
                    }

                    @Override
                    public void onComplete() {
                        super.onComplete();
                        Toast.getInstance(AddContentActivity.this).show1("OVER",R.drawable.tips);
                    }
                })
                .openGallery();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.addc_back:
                hideKeyboard(v.getWindowToken());
                finish();
                break;
            case R.id.addc_send:
                if(userdata.get("dengluflag").equals("false")){
                    Toast.getInstance(AddContentActivity.this).show("未登录",R.drawable.tips);
                    return;
                }
                files = new ArrayList<>();
                if (list!=null) {
                    for (MediaBean mediaBean : list) {
                        files.add(new File(compressImage(mediaBean.getOriginalPath())));
                    }
                }else files = null;
                Map params = new HashMap();
                params.put("connect",editText.getText().toString());
                params.put("user_id",userdata.get("id"));
                HttpUtil.sendMultipart(url+"/Home/setconnect", params, "img", files, new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        sendMessage(1,null);
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        sendMessage(2,response.body().string());
                    }
                });
                break;
        }
    }

    private void sendMessage(int what, Object obj) {
        Message message = Message.obtain();
        message.what = what;
        message.obj = obj;
        handler.sendMessage(message);
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    Toast.getInstance(AddContentActivity.this).show("连接服务器失败",R.drawable.tips);
                    break;
                case 2:
                    String s = (String) msg.obj;
                    String info = s.substring(0,1);
                    if (info.equals("1")){
                        Toast.getInstance(AddContentActivity.this).show("发送成功！",R.drawable.tips);
                        finish();
                    }
                    break;
            }
        }
    };
    /**
     * 隐藏软键盘
     */
    private void hideKeyboard(IBinder token) {
        if (token != null) {
            InputMethodManager im = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(token, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**
     * 图片压缩-质量压缩
     *
     * @param filePath 源图片路径
     * @return 压缩后的路径
     */

    public static String compressImage(String filePath) {

        //原文件
        File oldFile = new File(filePath);

        File sdCard = Environment.getExternalStorageDirectory();
        File directory_pictures = new File(sdCard, "Pictures");
        File directory_pictures1 = new File(directory_pictures, "boolan");
        //压缩文件路径 照片路径/
        String targetPath = oldFile.getPath();
        int quality = 70;//压缩比例0-100
        Bitmap bm = getSmallBitmap(filePath);//获取一定尺寸的图片

        File outputFile = new File(directory_pictures1.getPath(),"img.png");
        try {
            if (!outputFile.exists()) {
                outputFile.getParentFile().mkdirs();
                //outputFile.createNewFile();
            } else {

            }
            outputFile.delete();
            FileOutputStream out = new FileOutputStream(outputFile);
            bm.compress(Bitmap.CompressFormat.JPEG, quality, out);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
            return filePath;
        }
        return outputFile.getPath();
    }

    /**
     * 根据路径获得图片信息并按比例压缩，返回bitmap
     */
    public static Bitmap getSmallBitmap(String filePath) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inJustDecodeBounds = true;//只解析图片边沿，获取宽高
//        BitmapFactory.decodeFile(filePath, options);
//        // 计算缩放比
//        options.inSampleSize = calculateInSampleSize(options, 480, 800);
//        // 完整解析图片返回bitmap
//        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filePath, options);
    }
}
