package com.johnyhawkdesigns.a61_quran16line.ui.utils;

public class MenuModel {

    public String menuName;
    public int pageNo;
    public boolean hasChildren, isGroup;

    public MenuModel(String menuName, boolean isGroup, boolean hasChildren, int pageNo) {

        this.menuName = menuName;
        this.pageNo = pageNo;
        this.isGroup = isGroup;
        this.hasChildren = hasChildren;
    }
}
