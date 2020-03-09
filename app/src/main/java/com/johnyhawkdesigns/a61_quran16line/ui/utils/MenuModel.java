package com.johnyhawkdesigns.a61_quran16line.ui.utils;

public class MenuModel {

    public String menuName;
    public int parahNo;
    public boolean hasChildren, isGroup;

    public MenuModel(String menuName, boolean isGroup, boolean hasChildren, int parahNo) {

        this.menuName = menuName;
        this.parahNo = parahNo;
        this.isGroup = isGroup;
        this.hasChildren = hasChildren;
    }
}
