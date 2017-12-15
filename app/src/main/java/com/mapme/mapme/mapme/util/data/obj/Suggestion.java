package com.mapme.mapme.mapme.util.data.obj;

import java.io.Serializable;

/**
 * Created by Sillybob on 12/13/2017.
 */

public class Suggestion implements Serializable {
    //place_id
    private String id;
    //main_text
    private String mainText;
    //secondary_text
    private String secondaryText;

    public Suggestion(String id, String mainText, String seconderyText) {
        this.id = id;
        this.mainText = mainText;
        this.secondaryText = seconderyText;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
