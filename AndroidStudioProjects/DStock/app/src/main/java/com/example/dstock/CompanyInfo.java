package com.example.dstock;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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

import java.lang.reflect.Type;
import java.net.InetAddress;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import static android.content.Context.CONNECTIVITY_SERVICE;
import static com.example.dstock.MainActivity.mChart;
import static com.example.dstock.MainActivity.referenceTimestamp;



public class CompanyInfo extends AppCompatActivity {
    private TextView name;
    private TextView code;
    private TextView ltp;
    private TextView ycp;
    private TextView tcp;
    private TextView volume;
    private TextView trade;
    private TextView value;
    private TextView high;
    private TextView low;
    private TextView change;
    private TextView changeP;
    private LineChart cChart;
    private int tabNum=0;
    private boolean bypass=false;
    private Button oneM;
    private Button threeM;
    private Button sixM;
    private Button nineM;
    private Button oneY;
    private Button twoY;
    private String curDuration="1m";
    private Context context;
    private ArrayList<Entry> yvalues=new ArrayList<Entry>();
    private Company company=null;
    public static boolean available=false;
    public static boolean connected=false;
    private static ArrayList<dateAndValue> trd1=new ArrayList<dateAndValue>();
    private TabLayout typeTabs;
    private static ArrayList<dateAndValue> trd3=new ArrayList<dateAndValue>();
    private static ArrayList<dateAndValue> trd6=new ArrayList<dateAndValue>();
    private static ArrayList<dateAndValue> trd9=new ArrayList<dateAndValue>();
    private static ArrayList<dateAndValue> trd1y=new ArrayList<dateAndValue>();
    private static ArrayList<dateAndValue> trd2y=new ArrayList<dateAndValue>();
    private String formattedDate;
    private static ArrayList<dateAndValue> vol;

    private static ArrayList<dateAndValue> vol3;
    private static ArrayList<dateAndValue> vol6;
    private static ArrayList<dateAndValue> vol9;
    private static ArrayList<dateAndValue> vol1y;
    private static ArrayList<dateAndValue> vol2y;



    private static ArrayList<dateAndValue> lcp;

    private static ArrayList<dateAndValue> lcp3;
    private static ArrayList<dateAndValue> lcp6;
    private static ArrayList<dateAndValue> lcp9;

    public static void setLcp(ArrayList<dateAndValue> lcp) {
        CompanyInfo.lcp = new ArrayList<>(lcp);
    }

    public static void setLcp3(ArrayList<dateAndValue> lcp3) {
        CompanyInfo.lcp3 = new ArrayList<>(lcp3);
    }

    public static void setLcp6(ArrayList<dateAndValue> lcp6) {
        CompanyInfo.lcp6  = new ArrayList<>(lcp6);
    }

    public static void setLcp9(ArrayList<dateAndValue> lcp9) {
        CompanyInfo.lcp9  = new ArrayList<>(lcp9);
    }

    public static void setLcp1y(ArrayList<dateAndValue> lcp1y) {
        CompanyInfo.lcp1y  = new ArrayList<>(lcp1y);
    }

    public static void setLcp2y(ArrayList<dateAndValue> lcp2y) {
        CompanyInfo.lcp2y  = new ArrayList<>(lcp2y);
    }

    private static ArrayList<dateAndValue> lcp1y;
    private static ArrayList<dateAndValue> lcp2y;

    public static void setVol(ArrayList<dateAndValue> vol) {
        CompanyInfo.vol = new ArrayList<>(vol);
    }

    public static void setVol3(ArrayList<dateAndValue> vol3) {
        CompanyInfo.vol3 = new ArrayList<>(vol3);
    }

    public static void setVol6(ArrayList<dateAndValue> vol6) {
        CompanyInfo.vol6 = new ArrayList<>(vol6);
    }

    public static void setVol9(ArrayList<dateAndValue> vol9) {
        CompanyInfo.vol9 = new ArrayList<>(vol9);
    }

    public static void setVol1y(ArrayList<dateAndValue> vol1y) {
        CompanyInfo.vol1y = new ArrayList<>(vol1y);
    }

    public static void setVol2y(ArrayList<dateAndValue> vol2y) {
        CompanyInfo.vol2y = new ArrayList<>(vol2y);
    }


    public void setChart(LineChart mChart){
        IMarker mv;
        if(tabNum==0){
            mv=new DateMarkerView(context,R.layout.markerdate,mChart,"Price: ");
        }
        else if(tabNum==1){
             mv=new DateMarkerView(context,R.layout.markerdate,mChart,"Trades: ");
        }

        else{
             mv=new DateMarkerView(context,R.layout.markerdate,mChart,"Volume: ");
        }
        mChart.setMarker(mv);
        mChart.setPinchZoom(true);
        mChart.setDragEnabled(true);
        mChart.setTouchEnabled(true);
        mChart.setScaleEnabled(false); //make it true to enable zooming
        mChart.setNoDataText("Data are being loaded");
        mChart.getDescription().setEnabled(false);
        IAxisValueFormatter xAxisFormatter = new DateAxisValueFormatter(referenceTimestamp);
        XAxis xAxis = mChart.getXAxis();
        xAxis.setValueFormatter(xAxisFormatter);
        xAxis.setTextSize(6f);
        mChart.getAxisLeft().setTextSize(6f);
        mChart.getAxisRight().setDrawLabels(false);
        ArrayList<ILineDataSet> dataSets=new ArrayList<ILineDataSet>();
        LineDataSet set2=new LineDataSet(yvalues, "");
        if(tabNum==0){
            set2.setLabel("Closing Price Graph: X axis=Date     Y axis=Closing price of that Date");
        }
        else if(tabNum==1){
            set2.setLabel("Trade Graph: X axis=Date     Y axis=Total Trade Amount of that Date");


        }

        else{
            set2.setLabel("Volume Graph: X axis=Date     Y axis=Total Volume at that day");

        }
        set2.setFillAlpha(110);

        set2.setDrawHorizontalHighlightIndicator(false);
        set2.setDrawCircles(false);
        set2.setLineWidth(1);
        set2.setDrawValues(false);
        set2.setColor(Color.RED);
        set2.setDrawFilled(true);
        set2.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
        set2.setFormSize(15.f);if (Utils.getSDKInt() >= 18) {
            Drawable drawable = ContextCompat.getDrawable(context, R.drawable.fade_blue);
            set2.setFillDrawable(drawable);
        } else {
            set2.setFillColor(Color.DKGRAY);
        }dataSets.add(set2);
        LineData data=new LineData(dataSets);

        mChart.setData(data);
        mChart.notifyDataSetChanged();
        mChart.invalidate();
        //  set2.setDrawFilled(true);
        //LineDataSet set1=new LineDataSet(yvalues,"Trade of last 1 month");
        //set1.setFillAlpha(110);
        //  set1.setDrawHorizontalHighlightIndicator(false);

        // set1.setDrawCircles(false);
        // set1.setLineWidth(1);


        // set1.setColor(Color.DKGRAY);
        //dataSets.add(set1);
        // set2.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
        //  set2.setFormSize(15.f);
    }
    public static void setTrd(ArrayList<dateAndValue> tddd) {
        trd1=new ArrayList<>(tddd);
    }
    public static void setTrd9(ArrayList<dateAndValue> tddd) {
        trd9=new ArrayList<>(tddd);
    }

    public static void setTrd3(ArrayList<dateAndValue> tdddd) {
        trd3 =new ArrayList<>(tdddd);
    }

    public static void setTrd6(ArrayList<dateAndValue> tdddd) {
        trd6 =new ArrayList<>(tdddd);
    }

    public static void setTrd1y(ArrayList<dateAndValue> tdddd) {
        trd1y =new ArrayList<>(tdddd);
    }
    public static void setTrd2y(ArrayList<dateAndValue> tdddd) {
        trd2y =new ArrayList<>(tdddd);
    }

    private static FetchCompanyGraphData fetch;


    private void initializeData(ArrayList<dateAndValue> list, ArrayList<Entry> destination){
        try{
            destination.clear();
        }catch (Exception e){

        }
        int i=0;
        for(dateAndValue data:list){
            DateFormat formatter;
            Date date;
            formatter=new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            try {
                String d=data.getDate();
                date=formatter.parse(d);

                if(i==0){
                    referenceTimestamp=new Long(date.getTime()).floatValue()/60000;}

                destination.add(new Entry( (new Long((date.getTime())).floatValue()/60000)- referenceTimestamp,(float)data.getValues()));
                i++;
                //System.out.println(date.getTime());
                //System.out.println(new Timestamp(date.getTime()));
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    protected void onResume(){

        super.onResume();

    }
    private Drawable favorite;
    private Drawable noFavorite;
    private ArrayList<String> userfav;
    private TextView addToFav;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        favorite = this.getResources().getDrawable( R.drawable.ic_star_black_24dp );
        favorite.setBounds( 0, 0, 60, 60 );
        noFavorite=this.getResources().getDrawable(R.drawable.ic_favorite_no);
        noFavorite.setBounds(0,0,60,60);
        userfav=MainActivity.getUserFavs();
        if(userfav==null){
            userfav=new ArrayList<>();
        }
        vol=new ArrayList<>();
        vol3=new ArrayList<>();
        vol6=new ArrayList<>();
        vol9=new ArrayList<>();
        vol1y=new ArrayList<>();
        vol2y=new ArrayList<>();
        trd1=new ArrayList<>();
        trd3=new ArrayList<>();
        trd6=new ArrayList<>();
        trd9=new ArrayList<>();
        trd1y=new ArrayList<>();
        trd2y=new ArrayList<>();
        lcp=new ArrayList<>();
        lcp3=new ArrayList<>();
        lcp6=new ArrayList<>();
        lcp9=new ArrayList<>();
        lcp1y=new ArrayList<>();
        lcp2y=new ArrayList<>();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_info);
        context=this;
        String json= getIntent().getStringExtra("company");
        Gson string=new Gson();
        Type type = new TypeToken<Company>() {}.getType();
        formattedDate=getIntent().getStringExtra("formatteddate");
        company= string.fromJson(json, type);


        vol=loadCompanyData(vol,company.getCode(),"vol");
        vol3=loadCompanyData(vol3,company.getCode(),"vol3");
        vol6=loadCompanyData(vol6,company.getCode(),"vol6");
        vol9=loadCompanyData(vol9,company.getCode(),"vol9");
        vol1y=loadCompanyData(vol1y,company.getCode(),"vol1y");
        vol2y=loadCompanyData(vol2y,company.getCode(),"vol2y");

        trd1=loadCompanyData(trd1,company.getCode(),"trd");
        trd3=loadCompanyData(trd3,company.getCode(),"trd3");
        trd6=loadCompanyData(trd6,company.getCode(),"trd6");
        trd9=loadCompanyData(trd9,company.getCode(),"trd9");
        trd1y=loadCompanyData(trd1y,company.getCode(),"trd1y");
        trd2y=loadCompanyData(trd2y,company.getCode(),"trd2y");

        lcp=loadCompanyData(lcp,company.getCode(),"lcp");
        lcp3=loadCompanyData(lcp3,company.getCode(),"lcp3");
        lcp6=loadCompanyData(lcp6,company.getCode(),"lcp6");
        lcp9=loadCompanyData(lcp9,company.getCode(),"lcp9");
        lcp1y=loadCompanyData(lcp1y,company.getCode(),"lcp1y");
        lcp2y=loadCompanyData(lcp2y,company.getCode(),"lcp2y");

        name=findViewById(R.id.name);
        getSupportActionBar().setTitle("Company Info");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.BLACK));
        code=findViewById(R.id.code);
        ltp=findViewById(R.id.ltp);
        ycp=findViewById(R.id.ycp);
        tcp=findViewById(R.id.tcp);
        volume=findViewById(R.id.volume);
        trade=findViewById(R.id.trade);
        value=findViewById(R.id.value);
        high=findViewById(R.id.highest);
        low=findViewById(R.id.lowest);
        change=findViewById(R.id.change);
        changeP=findViewById(R.id.changeP);
        if(getApplicationContext().getResources().getConfiguration().orientation== Configuration.ORIENTATION_PORTRAIT) {

            addToFav=findViewById(R.id.addToFav);
            if(company.isFav()){
                addToFav.setCompoundDrawables(favorite,null,null,null);
                addToFav.setText("Remove from Favorites");
            }
            else{
                addToFav.setCompoundDrawables(noFavorite,null,null,null);
            }
            try{

            }catch (Exception e){
                String d=e.toString();
                System.out.println(d);
            }
            name.setText(toTitleCase(company.getName()));
            code.setText(company.getCode());
            ltp.setText(company.getLtp().toString());
            ycp.setText(String.valueOf(company.getYcp()));
            tcp.setText(String.valueOf(company.getClosep()));
            volume.setText((String.valueOf(company.getVolume())));
            trade.setText(String.valueOf(company.getTrade()));
            value.setText(String.valueOf(company.getValue()));
            high.setText(String.valueOf(company.getHigh()));
            low.setText(String.valueOf(company.getLow()));
            change.setText(String.valueOf(company.getChange()));
            changeP.setText(String.valueOf(company.getChangeP()) + "%");
        }
        else {
            if(MainActivity.getCurrentUser().equals("guest")){
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Guest user cannot see graphs, please sign up to use this feature!").show();

            }
            else{


                typeTabs=findViewById(R.id.typeTabs);
                try{
                    typeTabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                        @Override
                        public void onTabSelected(TabLayout.Tab tab) {
                            if (tab==typeTabs.getTabAt(0)){

                                try{
                                    tabNum=0;
                                }catch (Exception d){

                                }
                            }
                            else if(tab==typeTabs.getTabAt(1)){

                                try{
                                    tabNum=1;

                                }catch (Exception d){

                                }
                            }
                            else{
                                try{
                                    tabNum=2;
                                }catch (Exception d){

                                }
                            }
                            if(curDuration.equals("1m")){
                                try{
                                    oneMonth(null);
                                }catch (Exception e){

                                }
                            }
                            else if(curDuration.equals("3m")){
                                try{
                                    threeMonth(null);
                                }catch (Exception e){

                                }
                            }
                            else if(curDuration.equals("6m")){
                                try{
                                    sixMonth(null);
                                }catch (Exception e){

                                }
                            }
                            else if(curDuration.equals("9m")){
                                try{
                                    nineMonth(null);
                                }catch (Exception e){

                                }

                            }
                            else if(curDuration.equals("1y")){
                                try{
                                    oneYear(null);
                                }catch (Exception e){

                                }

                            }
                            else if(curDuration.equals("2y")){
                                try{
                                    twoYear(null);
                                }catch (Exception e){

                                }
                            }
                        }

                        @Override
                        public void onTabUnselected(TabLayout.Tab tab) {

                        }

                        @Override
                        public void onTabReselected(TabLayout.Tab tab) {

                        }
                    });
                }catch (Exception e){

                }
                oneM=findViewById(R.id.oneM);
                twoY=findViewById(R.id.twoY);
                oneY=findViewById(R.id.oneY);
                threeM=findViewById(R.id.threeM);
                sixM=findViewById(R.id.sixM);
                nineM=findViewById(R.id.nineM);
                mChart=findViewById(R.id.companyChart);
                try{
                    oneMonth(null);
                }catch (Exception e){

                }
            }
        }
    }

    public static String toTitleCase(String str) {

        if (str == null) {
            return null;
        }

        boolean space = true;
        StringBuilder builder = new StringBuilder(str);
        final int len = builder.length();

        for (int i = 0; i < len; ++i) {
            char c = builder.charAt(i);
            if (space) {
                if (!Character.isWhitespace(c)) {
                    // Convert to title case and switch out of whitespace mode.
                    builder.setCharAt(i, Character.toTitleCase(c));
                    space = false;
                }
            } else if (Character.isWhitespace(c)) {
                space = true;
            } else {
                builder.setCharAt(i, Character.toLowerCase(c));
            }
        }

        return builder.toString();
    }

    public void oneMonth(View view) {
        if(!MainActivity.getCurrentUser().equals("guest")) {
            try {
                if (tabNum == 0) {
                    initializeData(lcp, yvalues);

                } else if (tabNum == 1) {
                    initializeData(trd1, yvalues);

                } else {
                    initializeData(vol, yvalues);

                }
                try {
                    setChart(mChart);
                } catch (Exception e) {

                }
            } catch (Exception e) {

            }
        }
        curDuration="1m";
        oneM.setBackgroundResource(R.drawable.buttonshape);
        threeM.setBackgroundResource(R.drawable.buttonshapeunselected);
        sixM.setBackgroundResource(R.drawable.buttonshapeunselected);
        nineM.setBackgroundResource(R.drawable.buttonshapeunselected);
        oneY.setBackgroundResource(R.drawable.buttonshapeunselected);
        twoY.setBackgroundResource(R.drawable.buttonshapeunselected);
    }

    public void twoYear(View view) {
        if(!MainActivity.getCurrentUser().equals("guest")){
        try{
            if (tabNum == 0) {
                initializeData(lcp2y,yvalues);

            }
            else if(tabNum==1){
                initializeData(trd2y,yvalues);

            }
            else{
                initializeData(vol2y,yvalues);

            }
            try{
                setChart(mChart);
            }catch (Exception e){

            }
        }catch (Exception e){

        }}
        curDuration="2y";
        oneM.setBackgroundResource(R.drawable.buttonshapeunselected);
        threeM.setBackgroundResource(R.drawable.buttonshapeunselected);
        sixM.setBackgroundResource(R.drawable.buttonshapeunselected);
        nineM.setBackgroundResource(R.drawable.buttonshapeunselected);
        oneY.setBackgroundResource(R.drawable.buttonshapeunselected);
        twoY.setBackgroundResource(R.drawable.buttonshape);

    }

    public void oneYear(View view) {
        if(!MainActivity.getCurrentUser().equals("guest")){
        try{
            if (tabNum == 0) {
                initializeData(lcp1y,yvalues);

            }
            else if(tabNum==1){
                initializeData(trd1y,yvalues);

            }
            else{
                initializeData(vol1y,yvalues);

            }

            try{
                setChart(mChart);
            }catch (Exception e){

            }
        }catch (Exception e){

        }}
        curDuration="1y";
        oneM.setBackgroundResource(R.drawable.buttonshapeunselected);
        threeM.setBackgroundResource(R.drawable.buttonshapeunselected);
        sixM.setBackgroundResource(R.drawable.buttonshapeunselected);
        nineM.setBackgroundResource(R.drawable.buttonshapeunselected);
        oneY.setBackgroundResource(R.drawable.buttonshape);
        twoY.setBackgroundResource(R.drawable.buttonshapeunselected);
    }

    public void sixMonth(View view) {
        if(!MainActivity.getCurrentUser().equals("guest")){
        try{
            if (tabNum == 0) {
                initializeData(lcp6,yvalues);

            }
            else if(tabNum==1){
                initializeData(trd6,yvalues);

            }
            else{
                initializeData(vol6,yvalues);

            }

            try{
                setChart(mChart);
            }catch (Exception e){

            }
        }catch (Exception e){

        }}
        curDuration="6m";
        oneM.setBackgroundResource(R.drawable.buttonshapeunselected);
        threeM.setBackgroundResource(R.drawable.buttonshapeunselected);
        sixM.setBackgroundResource(R.drawable.buttonshape);
        nineM.setBackgroundResource(R.drawable.buttonshapeunselected);
        oneY.setBackgroundResource(R.drawable.buttonshapeunselected);
        twoY.setBackgroundResource(R.drawable.buttonshapeunselected);

    }
    public void nineMonth(View view) {

        if(!MainActivity.getCurrentUser().equals("guest")){
       try{
           if (tabNum == 0) {
               initializeData(lcp9,yvalues);

           }
           else if(tabNum==1){
               initializeData(trd9,yvalues);

           }
           else{
               initializeData(vol9,yvalues);

           }
           try{
               setChart(mChart);
           }catch (Exception e){

           }
       }catch (Exception e){

       }
        }
        curDuration="9m";
        oneM.setBackgroundResource(R.drawable.buttonshapeunselected);
        threeM.setBackgroundResource(R.drawable.buttonshapeunselected);
        sixM.setBackgroundResource(R.drawable.buttonshapeunselected);
        nineM.setBackgroundResource(R.drawable.buttonshape);
        oneY.setBackgroundResource(R.drawable.buttonshapeunselected);
        twoY.setBackgroundResource(R.drawable.buttonshapeunselected);
    }

    public void threeMonth(View view) {
        if(!MainActivity.getCurrentUser().equals("guest")){

            try{
                if (tabNum == 0) {
                    initializeData(lcp3,yvalues);

                }
                else if(tabNum==1){
                    initializeData(trd3,yvalues);

                }
                else{
                    initializeData(vol3,yvalues);

                }

                try{
                    setChart(mChart);
                }catch (Exception e){

                }
            }catch (Exception e){

            }
        }
        curDuration="3m";
        oneM.setBackgroundResource(R.drawable.buttonshapeunselected);
        threeM.setBackgroundResource(R.drawable.buttonshape);
        sixM.setBackgroundResource(R.drawable.buttonshapeunselected);
        nineM.setBackgroundResource(R.drawable.buttonshapeunselected);
        oneY.setBackgroundResource(R.drawable.buttonshapeunselected);
        twoY.setBackgroundResource(R.drawable.buttonshapeunselected);
    }

    private ArrayList<dateAndValue> loadCompanyData(ArrayList<dateAndValue> myList,String companyName,String dataName){
        SharedPreferences sharedPreferences=context.getSharedPreferences(formattedDate+companyName,MODE_PRIVATE);

        String json=sharedPreferences.getString(dataName,null);
        if(json!=null){
            Gson string=new Gson();
            Type type=new TypeToken<ArrayList<dateAndValue>>(){}.getType();
            myList=string.fromJson(json,type);

        }
        if(myList==null)return new ArrayList<dateAndValue>();
        return myList;

    }

    public void add_remove_fav(View view) {
        noFavorite.setBounds( 0, 0, 60, 60 );
        if(((Button)view).getText().toString().contains("Add")){

            userfav.add(company.getCode());
            MainActivity.setFavs(userfav);
            Toast.makeText(this,"Added to Favorites",Toast.LENGTH_SHORT).show();
            ((Button)view).setText("Remove From Favorites");
            ((Button)view).setCompoundDrawables(favorite,null,null,null);
        }
        else if(((Button)view).getText().toString().contains("Remove")){
            userfav.remove(company.getCode().toString());
            MainActivity.setFavs(userfav);
            Toast.makeText(this,"Removed from Favorites",Toast.LENGTH_SHORT).show();
            ((Button)view).setText("Add to Favorites");
            ((Button)view).setCompoundDrawables(noFavorite,null,null,null);
        }
    }
    public void rotateScreenLandscape(View view) {
        if(MainActivity.getCurrentUser().equals("guest")){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Guest user cannot add Favorites, please sign up to use this feature!").show();
        }
        else{

            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        }
    }

}
