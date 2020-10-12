package com.example.boolan.utils;

import com.example.boolan.service.ProgressListener;
import com.example.boolan.service.ProgressResponseBody;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HttpUtil {
    private static HttpUtil instance;
    private static OkHttpClient mOkHttpClient;
    private static final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");

    /**
     * Get instance http util.
     *
     * @return the http util
     */
    public static synchronized HttpUtil getInstance() {
        if (instance == null) {
            synchronized (HttpUtil.class) {
                if (instance == null) {
                    instance = new HttpUtil();
                }
            }
        }
        return instance;
    }

    public HttpUtil() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.retryOnConnectionFailure(true);//允许失败重试
        builder.readTimeout(30, TimeUnit.SECONDS);//读取超时
        builder.connectTimeout(10, TimeUnit.SECONDS);//连接超时
        builder.writeTimeout(60, TimeUnit.SECONDS);//写入超时
        mOkHttpClient = builder.build();
    }

    /**
     * On async get. 异步GET请求
     */
    private void onAsyncGET(String url, Callback callback) {
        Request request = new Request.Builder()
                .get()
                .url(url)
                .build();
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(callback);
    }

    /**
     * 异步post请求 无 headers
     *
     * @param url
     * @param bodyParams
     */

    private void onAsyncPOST_Body(String url, Map<String, String> bodyParams, Callback callback) {
        Request.Builder builder = new Request.Builder();
        Request request = builder.post(getBodyParams(bodyParams))
                .url(url)
                .build();
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(callback);
    }

    /**
     * 设置bodyParams
     *
     * @param bodyParams
     * @return RequestBody
     */
    private RequestBody getBodyParams(Map<String, String> bodyParams) {
        FormBody.Builder formBody = new FormBody.Builder();
        if (bodyParams != null) {
            Iterator<String> iterator = bodyParams.keySet().iterator();
            String key = "";
            while (iterator.hasNext()) {
                key = iterator.next().toString();
                formBody.add(key, bodyParams.get(key));
            }
        }
        return formBody.build();
    }

    //下载文件方法
    private static void downloadFile(String url, ProgressListener listener, Callback callback) {
        //增加拦截器
        OkHttpClient client = mOkHttpClient.newBuilder().addNetworkInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Response response = chain.proceed(chain.request());
                return response.newBuilder().body(new ProgressResponseBody(response.body(), listener)).build();
            }
        }).build();
        Request request = new Request.Builder().url(url).build();
        client.newCall(request).enqueue(callback);
    }

    /**
     * 异步GET请求
     */
    public static void getAsyncGET(String url, Callback callback) {
        getInstance().onAsyncGET(url, callback);
    }

    /**
     * 异步post 无headers
     */
    public static void getAsyncPostBody(String url, Map<String, String> bodyParams, Callback callback) {
        getInstance().onAsyncPOST_Body(url, bodyParams, callback);
    }

    //下载文件
    public static void getdownloadFile(String url, ProgressListener listener, Callback callback) {
        getInstance().downloadFile(url, listener, callback);
    }

    /**
     * 上传多张图片及参数
     *
     * @param reqUrl  URL地址
     * @param params  参数
     * @param pic_key 上传图片的关键字
     * @param files   图片路径
     */
    public static void sendMultipart(String reqUrl, Map<String, String> params, String pic_key, List<File> files, Callback callback) {
        MultipartBody.Builder multipartBodyBuilder = new MultipartBody.Builder();
        multipartBodyBuilder.setType(MultipartBody.FORM);
        //遍历map中所有参数到builder
        if (params != null) {
            for (String key : params.keySet()) {
                multipartBodyBuilder.addFormDataPart(key, params.get(key));
            }
        }
        //遍历paths中所有图片绝对路径到builder，并约定key如“upload”作为后台接受多张图片的key
        if (files != null) {
            for (File file : files) {
                multipartBodyBuilder.addFormDataPart(pic_key, file.getName(), RequestBody.create(MEDIA_TYPE_PNG, file));
            }
        }
        //构建请求体
        RequestBody requestBody = multipartBodyBuilder.build();

        Request.Builder RequestBuilder = new Request.Builder();
        RequestBuilder.url(reqUrl);// 添加URL地址
        RequestBuilder.post(requestBody);
        Request request = RequestBuilder.build();
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(callback);
    }
}