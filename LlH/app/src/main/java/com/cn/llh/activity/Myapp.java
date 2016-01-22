package com.cn.llh.activity;

import android.app.Application;
import android.content.Context;
import android.widget.Toast;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

/**
 * Created by zfh on 2016/1/14.
 */
public class Myapp extends Application {
    public static final boolean DEBUG = false;
    @Override
    public void onCreate() {
        super.onCreate();
        initImageLoader(getApplicationContext());
    }
    public static void initImageLoader(Context context) {
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                context).threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .diskCacheSize(50 * 1024 * 1024)
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .build();
        ImageLoader.getInstance().init(config);
    }

    public static void showToast(Context context, String Msg) {
        Toast.makeText(context.getApplicationContext(), Msg, Toast.LENGTH_SHORT).show();
    }

    public static void showToastlong(Context context, String Msg) {
        Toast.makeText(context.getApplicationContext(), Msg, Toast.LENGTH_LONG).show();
    }
}