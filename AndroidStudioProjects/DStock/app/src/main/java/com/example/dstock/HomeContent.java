package com.example.dstock;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.IMarker;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.Utils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import androidx.annotation.Nullable;

import static android.content.Context.MODE_PRIVATE;
import static com.example.dstock.MainActivity.referenceTimestamp;

public class HomeContent extends AsyncTask<Void, Void, Void> {
    private String x1,x2,x3,s1,s2,s3,t1,t2,t3,tr1,tr2,tr3,i1,i2,i3;
    private Document company;
    private ArrayList<Entry> yvalues=new ArrayList<Entry>();
    private Context context;
    private LineChart mChart;
    private SwipeRefreshLayout refreshLayout;
    private ArrayList<dateAndValue> valuesDseX=new ArrayList<dateAndValue>();
    private ArrayList<dateAndValue> valuesDseS=new ArrayList<dateAndValue>();
    private ArrayList<dateAndValue> valuesDse30=new ArrayList<dateAndValue>();
    private String lastTab;
    HomeContent(Context context,LineChart mChart,String lastTab,SwipeRefreshLayout refreshLayout){
        this.context=context;
        this.refreshLayout=refreshLayout;
        this.mChart=mChart;
        this.lastTab=lastTab;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        //Toast.makeText(getContext(), "Hello", Toast.LENGTH_SHORT).show();
    }


    @Override
    protected Void doInBackground(Void... voids) {

        try{
            company = Jsoup.connect("http://www.dsebd.org").get();

            Elements importantLinks = company.getElementsByClass("m_col-2");
            int i=0;
            for(Element data:importantLinks){
                if(i==0)x1=data.text().toString();
                else if(i==1)s1=data.text().toString();
                else if(i==2){
                    t1=data.text().toString();
                    break;
                }
                i++;
            }


            importantLinks = company.getElementsByClass("m_col-3");
            i=0;
            for(Element data:importantLinks){
                if(i==0)x2=data.text().toString();
                else if(i==1)s2=data.text().toString();
                else if(i==2){
                    t2=data.text().toString();
                    break;
                }
                i++;
            }


            importantLinks = company.getElementsByClass("m_col-4");
            i=0;
            for(Element data:importantLinks){
                if(i==0)x3=data.text().toString();
                else if(i==1)s3=data.text().toString();
                else if(i==2){
                    t3=data.text().toString();
                    break;
                }
                i++;
            }
            importantLinks = company.getElementsByClass("m_col-wid colorlight");
            i=0;
            for(Element data:importantLinks){
                if(i==0)tr1=data.text().toString();
                else if(i==1){
                    i1=data.text();
                    break;
                }
                i++;
            }
            importantLinks = company.getElementsByClass("m_col-wid1 colorlight");
            i=0;

            for(Element data:importantLinks){
                if(i==0) tr2=data.text().toString();
                else if(i==1){
                    i2=data.text();
                    break;
                }
                i++;
            }
            importantLinks = company.getElementsByClass("m_col-wid2 colorlight");
            i=0;

            for(Element data:importantLinks){
                if(i==0) tr3=data.text().toString();
                else if(i==1){
                    i3=data.text();
                    break;
                }
                i++;
            }
            importantLinks = company.getElementsByClass("Bodyheading");

            try{
                valuesDseS.clear();
                valuesDse30.clear();
                valuesDseX.clear();
                Document file= Jsoup.connect(context.getString(R.string.baseUrl)).get();
                file.outputSettings().indentAmount(0).prettyPrint(false);
                Elements scripts=file.select("script");
                String year=String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
                for(Element script:scripts) {
                    try {
                        if (script.data().toString().contains(String.valueOf(year))) {
                            String dataActual=script.data().replaceAll("\"","");
                            dataActual=dataActual.replaceAll("\\n","");
                            dataActual=dataActual.replaceAll("\\r","");
                            String []a=dataActual.split("\\+");
                            int k=-1;
                            for( i=0;i<a.length;i++){

                                if(a[i].contains(year)){
                                    String []s;
                                    a[i]=a[i].substring(a[i].indexOf(year),a[i].indexOf("n")-1);
                                    if(a[i].contains("10:30"))k++;
                                    if(k==0){
                                        s=a[i].split(",");
                                        valuesDseX.add(new dateAndValue(s[0],Double.parseDouble(s[1])));
                                    }
                                    else if(k==1){
                                        s=a[i].split(",");
                                        valuesDseS.add(new dateAndValue(s[0],Double.parseDouble(s[1])));

                                    }
                                    else {
                                        s=a[i].split(",");
                                        valuesDse30.add(new dateAndValue(s[0],Double.parseDouble(s[1])));

                                    }
                                }
                            }
                            saveOfflineChartData(valuesDse30,"dse30Values");
                            saveOfflineChartData(valuesDseS,"dseSValues");
                            saveOfflineChartData(valuesDseX,"dseXValues");
                        }
                    } catch (Exception e) {

                    }
                }


            }catch (IOException e){
                System.out.println(e.toString());
            }
            saveOfflineHomeData(null,x1,"x1");
            saveOfflineHomeData(null,x2,"x2");
            saveOfflineHomeData(null,x3,"x3");
            saveOfflineHomeData(null,s1,"s1");
            saveOfflineHomeData(null,s2,"s2");
            saveOfflineHomeData(null,s3,"s3");
            saveOfflineHomeData(null,t1,"t1");
            saveOfflineHomeData(null,t2,"t2");
            saveOfflineHomeData(null,t3,"t3");
            saveOfflineHomeData(null,tr1,"tr1");
            saveOfflineHomeData(null,tr2,"tr2");
            saveOfflineHomeData(null,tr3,"tr3");
            saveOfflineHomeData(null,i1,"i1");
            saveOfflineHomeData(null,i2,"i2");
            saveOfflineHomeData(null,i3,"i3");
            saveOfflineHomeData(valuesDse30,null,"valuesDse30");
            saveOfflineHomeData(valuesDseS,null,"valuesDseS");
            saveOfflineHomeData(valuesDseX,null,"valuesDseX");
        }catch (IOException e){
            String d=e.getStackTrace().toString();
            System.out.print(d);
        }

        return null;

    }
    public void setChartDSE(String type,LineChart mChart){

        int i=0;
        yvalues.clear();
        if(type=="X"){
            for(dateAndValue data:valuesDseX){
                DateFormat formatter;
                Date date;
                formatter=new SimpleDateFormat("HH:mm", Locale.getDefault());
                try {
                    String d=data.getDate().substring(data.getDate().indexOf(":")-2);
                    date=formatter.parse(d);

                    if(i==0){
                        referenceTimestamp=new Long(date.getTime()).floatValue()/60000;i++;}

                    yvalues.add(new Entry( (new Long((date.getTime())).floatValue()/60000)- referenceTimestamp,(float)data.getValues()));
                    //System.out.println(date.getTime());
                    //System.out.println(new Timestamp(date.getTime()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }
        }
        else if(type=="S"){
            for(dateAndValue data:valuesDseS){
                DateFormat formatter;
                Date date;
                formatter=new SimpleDateFormat("HH:mm", Locale.getDefault());
                try {
                    String d=data.getDate().substring(data.getDate().indexOf(":")-2);
                    date=formatter.parse(d);

                    if(i==0){
                        referenceTimestamp=new Long(date.getTime()).floatValue()/60000;i++;}

                    yvalues.add(new Entry( (new Long((date.getTime())).floatValue()/60000)- referenceTimestamp,(float)data.getValues()));
                    //System.out.println(date.getTime());
                    //System.out.println(new Timestamp(date.getTime()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }

        }
        else {
            for(dateAndValue data:valuesDse30){
                DateFormat formatter;
                Date date;
                formatter=new SimpleDateFormat("HH:mm", Locale.getDefault());
                try {
                    String d=data.getDate().substring(data.getDate().indexOf(":")-2);
                    date=formatter.parse(d);

                    if(i==0){
                        referenceTimestamp=new Long(date.getTime()).floatValue()/60000;i++;}

                    yvalues.add(new Entry( (new Long((date.getTime())).floatValue()/60000)- referenceTimestamp,(float)data.getValues()));
                    //System.out.println(date.getTime());
                    //System.out.println(new Timestamp(date.getTime()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }
        }
        try{
            setChart(mChart);
        }catch (Exception e){

        }
    }

    public  double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        try{
            refreshLayout.setRefreshing(false);
        }catch (Exception e){

        }
        loadOfflineChartData();
        loadOfflineHomeDataAll();
        try{
            setValues();
        }catch (Exception e){

        }

    }
    public void setChart(LineChart mChart){
        IMarker mv=new YourMarkerView(context,R.layout.marker,mChart);
        mChart.setMarker(mv);
        mChart.setPinchZoom(true);
        mChart.setDragEnabled(true);
        mChart.setTouchEnabled(true);
        mChart.setScaleEnabled(false); //make it true to enable zooming
        mChart.setNoDataText("Data are being loaded");
        mChart.getDescription().setEnabled(false);
        IAxisValueFormatter xAxisFormatter = new HourAxisValueFormatter(referenceTimestamp);
        XAxis xAxis = mChart.getXAxis();
        xAxis.setValueFormatter(xAxisFormatter);

        LineDataSet set1=new LineDataSet(yvalues,"Today's DSE Index Information");
        set1.setFillAlpha(110);
        set1.setDrawHorizontalHighlightIndicator(false);
        ArrayList<ILineDataSet> dataSets=new ArrayList<ILineDataSet>();

        set1.setDrawCircles(false);
        set1.setLineWidth(1);
        set1.setDrawFilled(true);
        set1.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
        set1.setFormSize(15.f);
        if (Utils.getSDKInt() >= 18) {
            Drawable drawable = ContextCompat.getDrawable(context, R.drawable.fade_blue);
            set1.setFillDrawable(drawable);
        } else {
            set1.setFillColor(Color.DKGRAY);
        }

        set1.setColor(Color.DKGRAY);

        dataSets.add(set1);
        LineData data=new LineData(dataSets);

        mChart.setData(data);
        mChart.notifyDataSetChanged();
        mChart.invalidate();
    }

    public void saveOfflineHomeData(@Nullable ArrayList<dateAndValue> myList,@Nullable  String value,String dataName){
        if(myList!=null){
            SharedPreferences sharedPreferences=context.getSharedPreferences("obj",MODE_PRIVATE);
            SharedPreferences.Editor editor=sharedPreferences.edit();
            Gson string=new Gson();
            String json=string.toJson(myList);
            editor.putString(dataName,json);
            editor.apply();
        }
        if(value!=null){

            SharedPreferences sharedPreferences=context.getSharedPreferences("obj",MODE_PRIVATE);
            SharedPreferences.Editor editor=sharedPreferences.edit();
            editor.putString(dataName,value);
            editor.apply();
        }
    }


    public void loadOfflineHomeDataAll(){
        x1=loadOfflineHomeDataIndividual(x1,"x1");
        x2=loadOfflineHomeDataIndividual(x2,"x2");
        x3=loadOfflineHomeDataIndividual(x3,"x3");
        s1=loadOfflineHomeDataIndividual(s1,"s1");
        s2=loadOfflineHomeDataIndividual(s2,"s2");
        s3=loadOfflineHomeDataIndividual(s3,"s3");
        t1=loadOfflineHomeDataIndividual(t1,"t1");
        t2=loadOfflineHomeDataIndividual(t2,"t2");
        t3=loadOfflineHomeDataIndividual(t3,"t3");
        tr1=loadOfflineHomeDataIndividual(tr1,"tr1");
        tr2=loadOfflineHomeDataIndividual(tr2,"tr2");
        tr3=loadOfflineHomeDataIndividual(tr3,"tr3");
        i1=loadOfflineHomeDataIndividual(i1,"i1");
        i2=loadOfflineHomeDataIndividual(i2,"i2");
        i3=loadOfflineHomeDataIndividual(i3,"i3");
       try{
           setValues();
       }catch (Exception set){

       }
    }

    private String loadOfflineHomeDataIndividual(String data,String dataName){
        SharedPreferences sharedPreferences=context.getSharedPreferences("obj",MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        data=sharedPreferences.getString(dataName,null);
        return data;
    }

    private void saveOfflineChartData(ArrayList<dateAndValue> myList,String dataName){
        SharedPreferences sharedPreferences=context.getSharedPreferences("obj",MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        Gson string=new Gson();
        String json=string.toJson(myList);
        editor.putString(dataName,json);
        editor.apply();
    }
    public void loadOfflineChartData(){
        SharedPreferences sharedPreferences=context.getSharedPreferences("obj",MODE_PRIVATE);
        Gson string=new Gson();
        String json=sharedPreferences.getString("dse30Values",null);
        Type type=new TypeToken<ArrayList<dateAndValue>>(){}.getType();
        valuesDse30=string.fromJson(json,type);
        string=new Gson();
        json=sharedPreferences.getString("dseXValues",null);
        valuesDseX=string.fromJson(json,type);
        string=new Gson();
        json=sharedPreferences.getString("dseSValues",null);
        valuesDseS=string.fromJson(json,type);

    }
    public void setValues(){
        MainActivity.x1.setText(x1);
        MainActivity.s1.setText(s1);
        MainActivity.t1.setText(t1);
        MainActivity.i1.setText(i1);
        MainActivity.i2.setText(i2);
        MainActivity.i3.setText(i3);
        MainActivity.x2.setText(x2);
        MainActivity.s2.setText(s2);
        MainActivity.t2.setText(t2);
        MainActivity.x3.setText(x3);
        MainActivity.s3.setText(s3);
        MainActivity.t3.setText(t3);
        MainActivity.tr1.setText(tr1);
        MainActivity.tr2.setText(tr2);
        MainActivity.tr3.setText(tr3);


    }
}