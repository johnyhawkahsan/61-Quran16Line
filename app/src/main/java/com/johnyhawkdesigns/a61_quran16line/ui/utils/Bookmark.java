package com.johnyhawkdesigns.a61_quran16line.ui.utils;

import com.shockwave.pdfium.PdfDocument;

import java.util.ArrayList;
import java.util.List;

public class Bookmark {

    public Bookmark(String title, int pageIdx) {
        this.title = title;
        this.pageIdx = pageIdx;
    }

    private List<Bookmark> children = new ArrayList<>();
    String title;
    int pageIdx;

    public List<Bookmark> getChildren() {
        return children;
    }

    public boolean hasChildren() {
        return !children.isEmpty();
    }

    public String getTitle() {
        return title;
    }

    public long getPageIdx() {
        return pageIdx;
    }

    public void setChildren(List<Bookmark> children) {
        this.children = children;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setPageIdx(int pageIdx) {
        this.pageIdx = pageIdx;
    }


}
