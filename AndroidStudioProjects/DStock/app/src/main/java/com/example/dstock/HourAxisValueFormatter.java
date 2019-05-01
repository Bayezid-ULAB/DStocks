package com.example.dstock;


import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Yasir on 02/06/16.
 */
public class HourAxisValueFormatter implements IAxisValueFormatter
{
    long ref;
    HourAxisValueFormatter(float refe){
        this.ref=(long)refe;
    }

    /**
     * Called when a value from an axis is to be formatted
     * before being drawn. For performance reasons, avoid excessive calculations
     * and memory allocations inside this method.
     *
     * @param value the value to be formatted
     * @param axis  the axis the value belongs to
     * @return
     */
    @Override
    public String getFormattedValue(float value, AxisBase axis) {
       axis.setLabelCount(5,true);
       DateFormat formatter= new SimpleDateFormat("mm");
       String date;
       String minutes=formatter.format(new Date(((long)value+ref)*60000));
       formatter=new SimpleDateFormat("HH");
       String hour=formatter.format(new Date(((long)value+ref)*60000));
       if(Double.parseDouble(hour)>12){
           hour=String.valueOf(Integer.parseInt(hour)-12);
           date=hour+":"+minutes+" PM";
       }
       else if(Double.parseDouble(hour)==12){
           date=hour+":"+minutes+ " PM";
       }
       else date=hour+":"+minutes+" AM";
       return date;

    }


}