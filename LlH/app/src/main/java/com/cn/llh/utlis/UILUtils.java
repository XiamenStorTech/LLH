package com.cn.llh.utlis;

/**
 * Created by zfh on 2016/1/14.
 * 网络图片下载类
 */

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import com.cn.llh.activity.Myapp;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


public final class UILUtils {

    public static final String URL_HEAD = "http://stonelogistic.stoneonline.com";

    private UILUtils(){}

    /**
     * @param loadingDrawable
     * @param failDrawable
     * @param defaultDrawable
     * @return
     * 根据3个图片资源文件创建一个图片显示的操作
     */
    private static DisplayImageOptions createOptions(int loadingDrawable,
                                                     int failDrawable, int defaultDrawable){
        return new DisplayImageOptions.Builder()
                .showImageOnLoading(loadingDrawable)
                .showImageForEmptyUri(failDrawable)
                .showImageOnFail(defaultDrawable)
                .cacheInMemory(true).cacheOnDisk(true)
                .considerExifParams(true).bitmapConfig(Config.RGB_565)
                .displayer(new FadeInBitmapDisplayer(0)).build();
    }

    /**
     * @param loadingDrawable
     * @param failDrawable
     * @param defaultDrawable
     * @param radiu
     * @return
     * 根据3个图片资源文件以及圆角半径创建一个圆角图片显示的操作
     */
    private static DisplayImageOptions createRoundImgOptions(int loadingDrawable,
                                                             int failDrawable, int defaultDrawable,int radiu){
        return new DisplayImageOptions.Builder()
                .showImageOnLoading(loadingDrawable)
                .showImageForEmptyUri(failDrawable)
                .showImageOnFail(defaultDrawable)
                .cacheInMemory(true).cacheOnDisk(true)
                .considerExifParams(true).bitmapConfig(Config.RGB_565)
                .displayer(new RoundedBitmapDisplayer(radiu)).build();
    }

    /**
     * @author Administrator
     * 图片加载完成第一次显示的动画
     */
    public static class AnimateFirstDisplayListener extends
            SimpleImageLoadingListener {

        static final List<String> displayedImages = Collections
                .synchronizedList(new LinkedList<String>());

        @Override
        public void onLoadingComplete(String imageUri, View view,
                                      Bitmap loadedImage) {
            if (loadedImage != null) {
                ImageView imageView = (ImageView) view;
                boolean firstDisplay = !displayedImages.contains(imageUri);
                if (firstDisplay) {
                    FadeInBitmapDisplayer.animate(imageView, 0);
                    displayedImages.add(imageUri);
                }
            }
        }
    }

    private static AnimateFirstDisplayListener l = new AnimateFirstDisplayListener();

    /**
     * @param url
     * @param view
     * @param defaultImageRes
     * 加载网络图片/本地图片
     */
    public static void loadImg(String url,ImageView view,int defaultImageRes){
        if(TextUtils.isEmpty(url) || url.equals("null")){
            view.setImageResource(defaultImageRes);
            return;
        }
        String uri = getHoleUri(url,view);
        if(Myapp.DEBUG){
            Log.e("loadImg", uri);
        }
        DisplayImageOptions options = createOptions(defaultImageRes, defaultImageRes, defaultImageRes);
        ImageLoader.getInstance().displayImage(uri, view, options, l);
    }

    /**
     * @param url
     * @param view
     * @param defaultImageRes
     * @param radiu
     * 加载网络图片/本地图片，最终以圆角显示
     */
    public static void loadRoundImg(String url,ImageView view,int defaultImageRes,int radiu){
        DisplayImageOptions options = createRoundImgOptions(defaultImageRes, defaultImageRes, defaultImageRes, radiu);
        if(TextUtils.isEmpty(url) || url.equals("null")){
            ImageLoader.getInstance().displayImage("drawable://" + defaultImageRes, view, options, l);
            return;
        }
        String uri = getHoleUri(url,view);
        if(Myapp.DEBUG){
            Log.e("loadImg", uri);
        }
        ImageLoader.getInstance().displayImage(uri, view, options, l);
    }

    /**
     * @param url
     * @param view
     * @return
     * 获取完整的图片路径，若本地文件，直接返回改路径参数本身，若为网络图片，根据控件的大小去获取完整路径
     */
    public static String getHoleUri(String url,View view) {
        if(url.startsWith("file://")){
            return url;
        }

        if(view == null){
            //return URL_HEAD + "/img/getimg?url=" + url + "&width=96&height=96&type=2";
            return url;
        }

        int height = view.getHeight();
        height = height <= 96 ? 96 : height;
        int width = view.getWidth();
        width = width <= 96 ? 96 : width;
        //return URL_HEAD + "/img/getimg?url=" + url + "&width=" + width + "&height=" + height + "&type=2";
        return url;
    }

    /**
     * @param url
     * @return
     * 获取完整的图片路径
     */
    public static String getHoleUri(String url){
        return getHoleUri(url, null);
    }
}
