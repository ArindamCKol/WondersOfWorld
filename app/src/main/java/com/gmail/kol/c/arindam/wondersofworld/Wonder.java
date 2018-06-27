package com.gmail.kol.c.arindam.wondersofworld;

/**
 * Created by ARINDAM on 26-03-2018.
 */

// class for Wonder object
public class Wonder {
    private int imageId;
    private String name;
    private String answerType;
    private String [] hintStringArray;
    private String [] optionStringArray;

    Wonder (int imageId, String name, String answerType, String[] hintStringArray,String [] optionStringArray) {
        this.imageId = imageId;
        this.name=name;
        this.answerType=answerType;
        this.hintStringArray=hintStringArray;
        this.optionStringArray=optionStringArray;
    }

    public int getImageId () {return imageId;}
    public String getName () {return name;}
    public String getAnswerType() {return answerType;}
    //return individual element of hintStringArray at given position
    public String getHint (int pos) {return hintStringArray[pos];}
    //return individual element of optionStringArray at given position
    public  String getOption (int pos) { return optionStringArray[pos]; }
}
