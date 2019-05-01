package com.example.dstock;

import android.content.Context;
import android.graphics.Canvas;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class YourMarkerView  extends MarkerView {
    private TextView time;
    private TextView value;
    private LineChart mc;
    private int uiScreenWidth;
    DateFormat formatter= new SimpleDateFormat("HH:mm");

    public void draw(){

    }

    public YourMarkerView(Context context, int layoutResource, LineChart mc) {
        super(context, layoutResource);
        this.mc=mc;
        uiScreenWidth = getResources().getDisplayMetrics().widthPixels;
        time = (TextView) findViewById(R.id.time);
        value=(TextView) findViewById(R.id.value);
    }
    /*@Override
    public void draw(Canvas canvas, float posX, float posY){

    canvas.translate(100,50);
    draw(canvas);
    canvas.translate(-posX,-posY);
}*/
  /*      @Override
        public void draw(Canvas canvas, float posx, float posy)
        {
            // Check marker position and update offsets.
            int w = getWidth();
            if((posx>=((int)(uiScreenWidth/2)))) {
                canvas.translate(95,50);
            }
            else if(posx<((int)(uiScreenWidth/2))){
            canvas.translate(uiScreenWidth-370,50);
        }

            // translate to the correct position and draw
            draw(canvas);
            canvas.translate(-posx, -posy);
        }
*/
    // callbacks everytime the MarkerView is redrawn, can be used to update the
    // content (user-interface)
    @Override
    public void draw(Canvas canvas, float posx, float posy)
    {
        // Check marker position and update offsets.
        int w = getWidth();
        if((uiScreenWidth-posx-w) < w) {
            posx -= w;
        }

        // translate to the correct position and draw
        canvas.translate(posx+50, posy-50);
        draw(canvas);
        canvas.translate(-(posx+50), -(posy-50));
    }

    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        DateFormat formatter= new SimpleDateFormat("mm");
        String date;
        String minutes=formatter.format(new Date(((long)e.getX()+(long)MainActivity.referenceTimestamp)*60000));
        formatter=new SimpleDateFormat("HH");
        String hour=formatter.format(new Date(((long)e.getX()+(long)MainActivity.referenceTimestamp)*60000));
        if(Double.parseDouble(hour)>=13){
            hour=String.valueOf(Integer.parseInt(hour)-12);
            date=hour+":"+minutes+" PM";
        }
        else if(Double.parseDouble(hour)==12){
            date=hour+":"+minutes+ " PM";
        }
        else date=hour+":"+minutes+" AM";
        time.setText("" + date );
        value.setText("" + String.format("%.2f", e.getY()));

        // this will perform necessary layouting
        super.refreshContent(e,highlight);
    }



    public  double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}