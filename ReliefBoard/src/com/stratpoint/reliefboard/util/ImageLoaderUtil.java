package com.stratpoint.reliefboard.util;

import java.io.File;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.widget.ImageView;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.DiscCacheUtil;
import com.nostra13.universalimageloader.core.assist.MemoryCacheUtil;

public class ImageLoaderUtil {
	private static ImageLoaderUtil mImageLoaderUtil;
	private ImageLoader mImageLoader;
	private ImageLoaderConfiguration mConfig;
	private DisplayImageOptions mOptions;
	
	private ImageLoaderUtil(Context context) {
		File cacheDir = new File(context.getCacheDir(), "Cache");
		if (!cacheDir.exists())
			cacheDir.mkdir();
		
		mImageLoader = ImageLoader.getInstance();
		mConfig = new ImageLoaderConfiguration.Builder(context).enableLogging().discCache(new UnlimitedDiscCache(cacheDir)).threadPoolSize(10).build();
		mOptions = new DisplayImageOptions.Builder().showStubImage(android.R.drawable.dialog_frame).showImageForEmptyUri(Color.TRANSPARENT).cacheInMemory().cacheOnDisc().bitmapConfig(Bitmap.Config.ARGB_8888).build();
		
		mImageLoader.init(mConfig);
	}
	
	public static final ImageLoaderUtil getInstance(Context context) {
		if(mImageLoaderUtil == null)
			mImageLoaderUtil = new ImageLoaderUtil(context);
		return mImageLoaderUtil;
	}
	
	public void displayImage(String imageUrl, ImageView imageView) {
		mImageLoader.displayImage(imageUrl, imageView, mOptions);
	}
	
	public void removeFromCache(String imageUrl){
		MemoryCacheUtil.removeFromCache(imageUrl, mImageLoader.getMemoryCache());
		DiscCacheUtil.removeFromCache(imageUrl, mImageLoader.getDiscCache());
	}
}
