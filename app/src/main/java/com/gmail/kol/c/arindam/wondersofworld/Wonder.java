package com.gmail.kol.c.arindam.wondersofworld;

/**
 * Created by ARINDAM on 26-03-2018.
 */

public class Wonder {
    private int imageId;
    private String name;
    private String [] hintStringArray;

    Wonder (int imageId, String name, String[] hintStringArray) {
        this.imageId = imageId;
        this.name=name;
        this.hintStringArray=hintStringArray;
    }

    public int getImageId () { return imageId; }
    public String getName () {return name;}
    public String getHint (int pos) {
        return hintStringArray[pos];
    }
}
