package com.example.hrwallpapers;

import java.io.File;
import java.util.ArrayList;

public class wallpaperModel {

    private static final String TAG = "WallpaperModel" ;
    public String thumbSrc,originalSrc,id,HQFileName,LQFileName,similiarsUrl;
    booleanListeners isFavorite;
    ArrayList<String> tagList;
    public String resolution;
    public int tagsCurrentPage = 0;
    public int originalWidth = 0;
    public int originalHeight = 0;
    boolean isPng=false;
    private File imageFile = null;


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
        prepareSimiliarsUrl();
    }

    private void prepareSimiliarsUrl() {
        this.similiarsUrl = "https://wallhaven.cc/search?q=like%3A"+id;
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

    public void setImageFile(File file)
    {
        this.imageFile = file;
    }

    public void setImageFile(String filePath)
    {
        this.imageFile =new File(filePath);
    }

    public String getFolderPath()
    {
        return this.imageFile != null ? this.imageFile.getAbsolutePath().replace(this.imageFile.getName(),"") : "";
    }

    public String getAbsoulteImagePath()
    {
        return this.imageFile != null ? this.imageFile.getAbsolutePath() : "";
    }

    public String getImageName() { return this.imageFile != null ? this.imageFile.getName() : ""; }

    public void setIsPng(boolean value) {
        this.isPng = value;
        prepareOriginalSrc();
        prepareThumbSrc();
    }

    public static String fileNameToID(String name)
    {
        String id = name.replace("HQ_","").replace("LQ_","").replace(".jpg","");
        return id;
    }

    public String getId() {
        return id;
    }
}
