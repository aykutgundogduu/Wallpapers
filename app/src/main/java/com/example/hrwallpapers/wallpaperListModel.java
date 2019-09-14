package com.example.hrwallpapers;

import androidx.annotation.NonNull;

import java.util.List;

public class wallpaperListModel {

    private int ID;
    private String listName;
    private List<wallpaperModel> wallpaperModels;
    private String createDate;

    public wallpaperListModel(@NonNull String listName,@NonNull int id)
    {
        this.listName = listName;
        this.ID = id;
    }

    public void setModelList(List<wallpaperModel> modelList) {
        this.wallpaperModels = modelList;
    }

    public String getListName() {
        return listName;
    }

    public void setListName(String listName) {
        this.listName = listName;
    }

    public int getID() {
        return ID;
    }

    public List<wallpaperModel> getWallpaperModels() {
        return wallpaperModels;
    }
}
