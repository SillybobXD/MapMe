package com.mapme.mapme.mapme.util.data.obj;

/**
 * Created by Sillybob on 12/14/2017.
 */

public class Photo {
    //photo_reference
    private String reference;
    //height
    private int maxHeight;
    //width
    private int maxWidth;

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public int getMaxHeight() {
        return maxHeight;
    }

    public void setMaxHeight(int maxHeight) {
        this.maxHeight = maxHeight;
    }

    public int getMaxWidth() {
        return maxWidth;
    }

    public void setMaxWidth(int maxWidth) {
        this.maxWidth = maxWidth;
    }

    @Override
    public String toString() {
        return "Photo{" +
                "reference='" + reference + '\'' +
                ", maxHeight=" + maxHeight +
                ", maxWidth=" + maxWidth +
                '}';
    }
}
