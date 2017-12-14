package com.mapme.mapme.mapme.util;

/**
 * Created by Sillybob on 12/13/2017.
 */

public class Suggestion {
    private String id;
    private String mainText;
    private String secondaryText;

    public Suggestion(String description) {
        int nameIndex = description.indexOf(',');
        if (nameIndex != -1) {
            mainText = description.substring(0, nameIndex);
            secondaryText = description.substring(nameIndex + 2);
        } else {
            mainText = description;
            secondaryText = "";
        }

    }

    public Suggestion(String mainText, String seconderyText) {
        this.mainText = mainText;
        this.secondaryText = seconderyText;
    }

    public Suggestion(String id, String mainText, String seconderyText) {
        this.id = id;
        this.mainText = mainText;
        this.secondaryText = seconderyText;
    }

    public String getMainText() {
        return mainText;
    }

    public void setMainText(String mainText) {
        this.mainText = mainText;
    }

    public String getSecondaryText() {
        return secondaryText;
    }

    public void setSecondaryText(String secondaryText) {
        this.secondaryText = secondaryText;
    }

    @Override
    public String toString() {
        return "Suggestion{" +
                "mainText='" + mainText + '\'' +
                ", secondaryText='" + secondaryText + '\'' +
                '}';
    }
}
