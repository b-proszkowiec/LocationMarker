package com.bpr.pecka.fragments;

public class FragmentListSingleItem {
    private final int imageResource;
    private final String name;
    private final String description;

    public FragmentListSingleItem(int mImageResource, String text1, String text2) {
        this.imageResource = mImageResource;
        this.name = text1;
        this.description = text2;
    }

    public int getImageResource() {
        return imageResource;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
