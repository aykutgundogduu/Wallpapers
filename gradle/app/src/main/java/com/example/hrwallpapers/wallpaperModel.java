package com.example.hrwallpapers;

import android.util.Log;

import java.io.File;
import java.util.ArrayList;

import static androidx.constraintlayout.motion.MotionScene.TAG;

public class wallpaperModel {

    String thumbSrc,originalSrc,id,HQFileName,LQFileName;
    booleanListeners isFavorite;
    ArrayList<String> tagList;
    public String resolution;
    public int tagsCurrentPage = 0;
    public int originalWidth = 0;
    public int originalHeight = 0;
    boolean isPng=false;
    private String filePath = null;


    public wallpaperModel(String id)
    {
        this.thumbSrc = thumbSrc;
        this.originalSrc = originalSrc;
        this.id = id;
        isFavorite = new booleanListeners();
        this.tagList = new ArrayList<>();

        this.HQFileName = "HQ_" + this.id + ".jpg";
        this.LQFileName = "LQ_" + this.id + ".jpg";


        prepareThumbSrc();
        prepareOriginalSrc();
    }

    private void prepareThumbSrc() {
        this.thumbSrc = String.format("https://th.wallhaven.cc/small/%s/%s.jpg",id.substring(0,2),id);
    }

    private void prepareOriginalSrc() {
        if(isPng) this.originalSrc = String.format("https://w.wallhaven.cc/full/%s/wallhaven-%s.png",id.substring(0,2),id);
        else this.originalSrc = String.format("https://w.wallhaven.cc/full/%s/wallhaven-%s.jpg",id.substring(0,2),id);

    }

    public queryModel getTagQueryModel(int tagPosition)
    {
        if(tagList.size() > tagPosition)
        {
            String tag = tagList.get(tagPosition);

            queryModel model= new queryModel(true,true,true,true,true,true,0,0,0,0,0,"","desc",tag,"random",null);
            model.setActivePage(tagsCurrentPage);
            return model;
        }
        else return null;
    }

    public void setFilePath(File file)
    {
        Log.i(TAG, "setFilePath: " + file.getPath());
        this.filePath = file.getPath();
    }

    public void setFilePath(String filePath)
    {
        Log.i(TAG, "setFilePath: " + filePath);
        this.filePath = filePath;
    }

    public String getFilePath()
    {
        return this.filePath;
    }

    public void setIsPng(boolean value) {
        this.isPng = value;
        prepareOriginalSrc();
        prepareThumbSrc();
    }
}
