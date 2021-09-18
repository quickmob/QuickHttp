package com.lookballs.app.http;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.blankj.utilcode.util.IntentUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.lookballs.app.http.bean.BaseBean;
import com.lookballs.app.http.bean.UploadBean;
import com.lookballs.app.http.bean.UploadFilesBean;
import com.lookballs.app.http.bean.banner.BannerBean;
import com.lookballs.app.http.bean.banner.TestBean1;
import com.lookballs.app.http.bean.banner.TestBean2;
import com.lookballs.app.http.bean.banner.TestBean3;
import com.lookballs.app.http.http.CustomHttpCallback;
import com.lookballs.app.http.http.converter.GsonDataConverter;
import com.lookballs.app.http.util.gson.GsonUtil;
import com.lookballs.http.QuickHttp;
import com.lookballs.http.core.lifecycle.ApplicationLifecycle;
import com.lookballs.http.core.listener.OnDownloadListener;
import com.lookballs.http.core.listener.OnHttpListener;
import com.lookballs.http.core.listener.OnUploadListener;
import com.lookballs.http.core.model.DownloadInfo;
import com.lookballs.http.core.model.UploadInfo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

public class MainActivity extends BaseActivity {

    private ImageView showBitmap;
    private TextView tv_progress, tv_progress1, tv_progress2;
    private ProgressBar downloadProgress, downloadProgress1, downloadProgress2;

    private String text = "{\"data\":[{\"desc\":\"一起来做个App吧\",\"id\":10,\"imagePath\":\"https://www.wanandroid.com/blogimgs/50c115c2-cf6c-4802-aa7b-a4334de444cd.png\",\"isVisible\":1,\"order\":1,\"title\":\"一起来做个App吧\",\"type\":0,\"url\":\"https://www.wanandroid.com/blog/show/2\"},{\"desc\":\"\",\"id\":6,\"imagePath\":\"https://www.wanandroid.com/blogimgs/62c1bd68-b5f3-4a3c-a649-7ca8c7dfabe6.png\",\"isVisible\":1,\"order\":1,\"title\":\"我们新增了一个常用导航Tab~\",\"type\":1,\"url\":\"https://www.wanandroid.com/navi\"},{\"desc\":\"\",\"id\":20,\"imagePath\":\"https://www.wanandroid.com/blogimgs/90c6cc12-742e-4c9f-b318-b912f163b8d0.png\",\"isVisible\":1,\"order\":2,\"title\":\"flutter 中文社区 \",\"type\":1,\"url\":\"https://flutter.cn/\"}],\"errorCode\":0,\"errorMsg\":\"\"}";

    private Activity mActivity = this;

    private ApplicationLifecycle applicationLifecycle = new ApplicationLifecycle();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        showBitmap = findViewById(R.id.showBitmap);
        downloadProgress = findViewById(R.id.downloadProgress);
        tv_progress = findViewById(R.id.tv_progress);
        downloadProgress1 = findViewById(R.id.downloadProgress1);
        tv_progress1 = findViewById(R.id.tv_progress1);
        downloadProgress2 = findViewById(R.id.downloadProgress2);
        tv_progress2 = findViewById(R.id.tv_progress2);
    }

    /***********************************类型传递***********************************/

    public void get1Request(View view) {
        //方式一：使用泛型类型去解析数据
        //这种方式一定需要在OnHttpListener中指定类型，如果不指定则会报类型错误
        QuickHttp.get("banner/json")
                .bindLife(MainActivity.this)
                .async(new OnHttpListener<TestBean1<List<BannerBean>>>() {
                    @Override
                    public void onStart(Call call) {
                        showDialog();
                    }

                    @Override
                    public void onSucceed(TestBean1<List<BannerBean>> result) {
                        ToastUtils.showShort("请求结果" + GsonUtil.toJson(result));
                    }

                    @Override
                    public void onError(int code, Exception e) {
                        ToastUtils.showShort("请求失败：" + e.getMessage());
                    }

                    @Override
                    public void onEnd(Call call) {
                        hideDialog();
                    }
                });
    }

    public void get2Request(View view) {
        //方式一：使用泛型类型去解析数据
        //这种方式一定需要在OnHttpListener中指定类型，如果不指定则会报类型错误
        QuickHttp.get("banner/json")
                .async(new OnHttpListener<TestBean2<BannerBean>>() {
                    @Override
                    public void onStart(Call call) {
                        showDialog();
                    }

                    @Override
                    public void onSucceed(TestBean2<BannerBean> result) {
                        ToastUtils.showShort("请求结果" + GsonUtil.toJson(result));
                    }

                    @Override
                    public void onError(int code, Exception e) {
                        ToastUtils.showShort("请求失败：" + e.getMessage());
                    }

                    @Override
                    public void onEnd(Call call) {
                        hideDialog();
                    }
                });
    }

    public void get3Request(View view) {
        //方式一：使用泛型类型去解析数据
        //这种方式一定需要在OnHttpListener中指定类型，如果不指定则会报类型错误
        QuickHttp.get("banner/json")
                .async(new OnHttpListener<TestBean3>() {
                    @Override
                    public void onStart(Call call) {
                        showDialog();
                    }

                    @Override
                    public void onSucceed(TestBean3 result) {
                        ToastUtils.showShort("请求结果" + GsonUtil.toJson(result));
                    }

                    @Override
                    public void onError(int code, Exception e) {
                        ToastUtils.showShort("请求失败：" + e.getMessage());
                    }

                    @Override
                    public void onEnd(Call call) {
                        hideDialog();
                    }
                });
    }

    public void get4Request(View view) {
        //方式二：使用指定类型去解析数据
        //这种方式需要传递指定的XXX.class，OnHttpListener中可指定类型也可不指定类型
        QuickHttp.get("banner/json")
                .async(TestBean1.class, new OnHttpListener<TestBean1>() {
                    @Override
                    public void onStart(Call call) {
                        showDialog();
                    }

                    @Override
                    public void onSucceed(TestBean1 result) {
                        ToastUtils.showShort("请求结果" + GsonUtil.toJson(result));
                    }

                    @Override
                    public void onError(int code, Exception e) {
                        ToastUtils.showShort("请求失败：" + e.getMessage());
                    }

                    @Override
                    public void onEnd(Call call) {
                        hideDialog();
                    }
                });
    }

    public void get5Request(View view) {
        //方式二：使用指定类型去解析数据
        //这种方式需要传递指定的XXX.class，OnHttpListener中可指定类型也可不指定类型
        QuickHttp.get("banner/json")
                .async(TestBean2.class, new OnHttpListener<TestBean2>() {
                    @Override
                    public void onStart(Call call) {
                        showDialog();
                    }

                    @Override
                    public void onSucceed(TestBean2 result) {
                        ToastUtils.showShort("请求结果" + GsonUtil.toJson(result));
                    }

                    @Override
                    public void onError(int code, Exception e) {
                        ToastUtils.showShort("请求失败：" + e.getMessage());
                    }

                    @Override
                    public void onEnd(Call call) {
                        hideDialog();
                    }
                });
    }

    public void get6Request(View view) {
        //方式二：使用指定类型去解析数据
        //这种方式需要传递指定的XXX.class，OnHttpListener中可指定类型也可不指定类型
        QuickHttp.get("banner/json")
                .async(TestBean3.class, new OnHttpListener<TestBean3>() {
                    @Override
                    public void onStart(Call call) {
                        showDialog();
                    }

                    @Override
                    public void onSucceed(TestBean3 result) {
                        ToastUtils.showShort("请求结果" + GsonUtil.toJson(result));
                    }

                    @Override
                    public void onError(int code, Exception e) {
                        ToastUtils.showShort("请求失败：" + e.getMessage());
                    }

                    @Override
                    public void onEnd(Call call) {
                        hideDialog();
                    }
                });
    }

    /***********************************请求参数***********************************/

    public void getRequest(View view) {
        QuickHttp.get("article/list/0/json")
                .addUrlParam("author", "鸿洋")
                .dataConverter(new GsonDataConverter())
                .async(Object.class, new CustomHttpCallback(this) {
                    @Override
                    public void onSucceed(Object result) {
                        ToastUtils.showShort("请求结果" + GsonUtil.toJson(result));
                    }
                });
    }

    public void postRequest(View view) {
        QuickHttp.post("user/login")
                .addParam("username", "lookballs")
                .addParam("password", "lookballs")
                .async(new CustomHttpCallback<BaseBean>(this) {
                    @Override
                    public void onSucceed(BaseBean result) {
                        ToastUtils.showShort("请求结果" + GsonUtil.toJson(result));
                    }
                });
    }

    public void syncRequest(View view) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showDialog();
                    }
                });

                try {
                    BaseBean baseBean = QuickHttp.post("user/login")
                            .addParam("username", "lookballs")
                            .addParam("password", "lookballs")
                            .sync(BaseBean.class);
                    ToastUtils.showShort("请求结果" + GsonUtil.toJson(baseBean));
                } catch (Exception e) {
                    e.printStackTrace();
                    ToastUtils.showShort(e.getMessage());
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hideDialog();
                    }
                });
            }
        }).start();
    }

    public void uploadRequest(View view) {
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), getString(R.string.app_name) + ".png");
        if (!file.exists()) {
            drawableToFile(ContextCompat.getDrawable(this, R.drawable.bg), file);
        }

        QuickHttp.post("https://graph.baidu.com/upload/")
                .addParam("image", file)
                .async(UploadBean.class, new OnUploadListener<UploadBean>() {
                    @Override
                    public void onStart(Call call) {

                    }

                    @Override
                    public void onProgress(UploadInfo info) {
                        downloadProgress.setProgress(info.getProgress());
                        tv_progress.setText(info.getTextPreciseProgress() + "%");
                    }

                    @Override
                    public void onSucceed(UploadBean result) {
                        if (result.getStatus() == 0) {
                            ToastUtils.showShort("上传成功");
                        } else {
                            ToastUtils.showShort("上传失败：" + result.getMsg());
                        }
                    }

                    @Override
                    public void onError(int code, Exception e) {
                        ToastUtils.showShort("上传失败：" + e.getMessage());
                    }

                    @Override
                    public void onEnd(Call call) {

                    }
                });
    }

    public void uploadMoreRequest(View view) {
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), getString(R.string.app_name) + ".png");
        if (!file.exists()) {
            drawableToFile(ContextCompat.getDrawable(this, R.drawable.bg), file);
        }

        List<File> files = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            files.add(file);
        }
        Map<String, Object> params = new HashMap<>();
        params.put("type", 6);
        //params.put("files", files);
        for (int i = 0; i < files.size(); i++) {
            params.put("files" + "[" + i + "]", files.get(i));
        }

        QuickHttp.post("http://xxxx/v1/upload/files")
                .addParam(params)
                .async(UploadFilesBean.class, new OnUploadListener<UploadFilesBean>() {
                    @Override
                    public void onStart(Call call) {

                    }

                    @Override
                    public void onProgress(UploadInfo info) {
                        downloadProgress.setProgress(info.getProgress());
                        tv_progress.setText(info.getTextPreciseProgress() + "%");
                    }

                    @Override
                    public void onSucceed(UploadFilesBean result) {
                        if (result.getCode() == 0) {
                            ToastUtils.showShort("上传成功");
                        } else {
                            ToastUtils.showShort("上传失败：" + result.getMessage());
                        }
                    }

                    @Override
                    public void onError(int code, Exception e) {
                        ToastUtils.showShort("上传失败：" + e.getMessage());
                    }

                    @Override
                    public void onEnd(Call call) {

                    }
                });
    }

    public void bitmapRequest(View view) {
        QuickHttp.get("http://imgs.1tu.com/images/23/85/93/26/3037863825.jpg-450h.jpg")
                .async(new CustomHttpCallback<Bitmap>(this) {
                    @Override
                    public void onSucceed(Bitmap bitmap) {
                        if (bitmap != null) {
                            showBitmap.setImageBitmap(bitmap);
                        }
                    }
                });
    }

    public void download1Request(View view) throws IOException {
        if ("1".equals(view.getTag())) {
            view.setTag("0");
            QuickHttp.cancel(applicationLifecycle);
        } else {
            view.setTag("1");
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "王者荣耀.apk");
            if (!file.exists()) {
                file.createNewFile();
            }
            QuickHttp.download("https://imtt.dd.qq.com/16891/apk/B168BCBBFBE744DA4404C62FD18FFF6F.apk?fsname=com.tencent.tmgp.sgame_1.61.1.6_61010601.apk&csr=1bbd")
                    .bindLife(applicationLifecycle)
                    .file(file)
                    .fileMd5("")
                    .breakpoint(0, false)
                    .start(new OnDownloadListener() {
                        @Override
                        public void onStart(Call call) {

                        }

                        @Override
                        public void onEnd(Call call) {

                        }

                        @Override
                        public void onProgress(DownloadInfo downloadInfo) {
                            downloadProgress1.setProgress(downloadInfo.getProgress());
                            tv_progress1.setText(downloadInfo.getTextPreciseProgress() + "%");
                        }

                        @Override
                        public void onComplete(DownloadInfo downloadInfo) {
                            if (downloadInfo.isFinish()) {
                                ToastUtils.showShort("下载完成");
                                startActivity(IntentUtils.getInstallAppIntent(downloadInfo.getFilePath()));
                            }
                        }

                        @Override
                        public void onError(DownloadInfo downloadInfo, Exception e) {
                            ToastUtils.showShort("下载失败：" + e.getMessage());
                        }
                    });
        }

    }

    public void download2Request(View view) throws IOException {
        if ("1".equals(view.getTag())) {
            view.setTag("0");
            QuickHttp.cancel(MainActivity.this);
        } else {
            view.setTag("1");
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "爱奇艺.apk");
            if (!file.exists()) {
                file.createNewFile();
            }
            QuickHttp.download("https://d79f3d4c21d82eaeff5c767e7b36a903.dlied1.cdntips.net/imtt.dd.qq.com/16891/apk/BD2E03CA753137D287A6E6CDA4DF99FD.apk?mkey=60c2dcff7159069c&f=1ea3&fsname=com.qiyi.video_12.5.6_800120556.apk&csr=1bbd&cip=113.89.32.105&proto=https")
                    .bindLife(MainActivity.this)
                    .file(file)
                    .fileMd5("bd2e03ca753137d287a6e6cda4df99fd")
                    .breakpoint(file.length(), true)
                    .start(new OnDownloadListener() {
                        @Override
                        public void onStart(Call call) {

                        }

                        @Override
                        public void onEnd(Call call) {

                        }

                        @Override
                        public void onProgress(DownloadInfo downloadInfo) {
                            downloadProgress2.setProgress(downloadInfo.getProgress());
                            tv_progress2.setText(downloadInfo.getTextPreciseProgress() + "%");
                        }

                        @Override
                        public void onComplete(DownloadInfo downloadInfo) {
                            if (downloadInfo.isFinish()) {
                                ToastUtils.showShort("下载完成");
                                startActivity(IntentUtils.getInstallAppIntent(downloadInfo.getFilePath()));
                            }
                        }

                        @Override
                        public void onError(DownloadInfo downloadInfo, Exception e) {
                            ToastUtils.showShort("下载失败：" + e.getMessage());
                        }
                    });

        }
    }

    public void cancelRequest(View view) {
        QuickHttp.cancel(MainActivity.this);
    }

    private void drawableToFile(Drawable drawable, File file) {
        if (drawable == null) {
            return;
        }
        try {
            if (file.exists()) {
                file.delete();
            }
            if (!file.exists()) {
                file.createNewFile();
            }
            FileOutputStream out;
            out = new FileOutputStream(file);
            ((BitmapDrawable) drawable).getBitmap().compress(Bitmap.CompressFormat.PNG, 100, out);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
