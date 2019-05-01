package com.example.dstock;

public class dateAndValue{
    private String date;
    private double values;
    dateAndValue(String d,double v){
        this.date=d;
        this.values=v;
    }

    public String getDate() {
        return date;
    }


    public double getValues() {
        return values;
    }
}