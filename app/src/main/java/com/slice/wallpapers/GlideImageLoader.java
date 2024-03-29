package com.slice.wallpapers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import java.io.File;


public class GlideImageLoader {

    private static final String TAG = "GlideImageLoader";
    private ImageView mImageView;
    private CircleProgressBar mProgressBar;
    private Context context;

    public GlideImageLoader(ImageView imageView, CircleProgressBar progressBar) {
        mImageView = imageView;
        mProgressBar = progressBar;
        if(mImageView.getContext() != null) this.context = mImageView.getContext();

        onConnecting();
    }

    public GlideImageLoader(ImageView mImageView,CircleProgressBar progressBar,Context context)
    {
        this.mImageView =mImageView;
        this.mProgressBar = progressBar;
        this.context = context;
    }

    public void load(final String url, final RequestOptions options,final wallpaperModel wallpaperModel) {

        boolean isFileExistsAsHQ = false;
        boolean isFileExistsAsLQ = false;

        if (url == wallpaperModel.originalSrc) isFileExistsAsHQ = MainActivity.isFileExists(wallpaperModel.id);
        else if(url == wallpaperModel.thumbSrc) isFileExistsAsLQ = MainActivity.isFileExists(wallpaperModel.id);


        if(isFileExistsAsHQ || isFileExistsAsLQ && !wallpaperModel.isLoading()) {
            File file;
            if (isFileExistsAsHQ) {
                file = MainActivity.findExistFie(wallpaperModel.id);
            } else {
                file = MainActivity.findExistFie(wallpaperModel.id);
            }

            if (file != null)
            {
                load(file,options,wallpaperModel);
            }
        }
        if (url == null || options == null) return;

        //set Listener & start
        if((!isFileExistsAsHQ && !isFileExistsAsLQ) && !wallpaperModel.isLoading())
        {
            ProgressAppGlideModule.expect(url, new ProgressAppGlideModule.UIonProgressListener() {
                @Override
                public void onProgress(long bytesRead, long expectedLength) {
                    if (mProgressBar != null) {
                        mProgressBar.setProgressWithAnimation((float) (100 * bytesRead / expectedLength));
                    }
                }

                @Override
                public float getGranualityPercentage() {
                    return 1.0f;
                }
            });

            RequestBuilder<Bitmap> requestBuilder = null;
            if (url  == wallpaperModel.originalSrc) requestBuilder = Glide.with(context).asBitmap().load(wallpaperModel.thumbSrc).thumbnail(0.2f);

            //Get Image
            Glide.with(context)
                    .asBitmap()
                    .thumbnail(requestBuilder)
                    .load(url)
                    .apply(options.skipMemoryCache(true))
                    .listener(new RequestListener<Bitmap>() {

                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                            mProgressBar.setProgress(1);
                            ProgressAppGlideModule.forget(url);
                            wallpaperModel.loading = false;

                            Runnable runnable = new Runnable() {
                                @Override
                                public void run() {
                                    load(url,options,wallpaperModel);
                                }
                            };
                            e.printStackTrace();
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                            mProgressBar.setProgress((float) 100); // trigger the loaded event when the image cames from cache

                            ProgressAppGlideModule.forget(url);
                            wallpaperModel.loading = false;
                            return false;
                        }
                    })
                    .into(mImageView);
            wallpaperModel.loading = true;
        }
    }
    private void load(final File localFile, final RequestOptions options, final wallpaperModel wallpaperModel) {
        if (localFile == null || options == null) return;

        //set Listener & start
        ProgressAppGlideModule.expect(localFile.getAbsolutePath(), new ProgressAppGlideModule.UIonProgressListener() {
            @Override
            public void onProgress(long bytesRead, long expectedLength) {
                if (mProgressBar != null) {
                    mProgressBar.setProgressWithAnimation((float) (100 * bytesRead / expectedLength));
                }
            }

            @Override
            public float getGranualityPercentage() {
                return 1.0f;
            }
        });

        //Get Image
        Glide.with(context)
                .asBitmap()
                .load(Uri.fromFile(localFile))
                .apply(options.skipMemoryCache(true))
                .listener(new RequestListener<Bitmap>() {

                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                        mProgressBar.setProgress(1);
                        ProgressAppGlideModule.forget(localFile.getAbsolutePath());
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                        mProgressBar.setProgress((float) 100); // trigger the loaded event when the image cames from cache
                        ProgressAppGlideModule.forget(localFile.getAbsolutePath());
                        return false;
                    }
                })
                .into(mImageView);
    }

    private void onConnecting() {
        if (mProgressBar != null)
        {
            mProgressBar.setVisibility(View.VISIBLE);
        }
    }
    private Bitmap BITMAP_RESIZER(Bitmap bitmap,int newWidth,int newHeight) {
        Bitmap scaledBitmap = Bitmap.createBitmap(newWidth, newHeight, Bitmap.Config.ARGB_8888);

        float ratioX = newWidth / (float) bitmap.getWidth();
        float ratioY = newHeight / (float) bitmap.getHeight();
        float middleX = newWidth / 2.0f;
        float middleY = newHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bitmap, middleX - bitmap.getWidth() / 2, middleY - bitmap.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

        return scaledBitmap;

    }
}