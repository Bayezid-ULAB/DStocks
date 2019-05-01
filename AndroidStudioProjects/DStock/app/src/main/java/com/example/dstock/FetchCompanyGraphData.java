package com.example.dstock;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.google.gson.Gson;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import static android.content.Context.MODE_PRIVATE;

public class FetchCompanyGraphData extends AsyncTask<Void,Void,Void> {
    private String formattedDate;
    private String code;
    private String [] s;
    private Context context;
    private ArrayList<dateAndValue> dateAndValues=new ArrayList<>();
    FetchCompanyGraphData(String code, Context context,String date){
        this.code=code;
        this.formattedDate=date;
        this.context=context;
    //    System.out.println(code);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try{
            try{
                dateAndValues.clear();
            }catch (Exception e){

            }
            for(int iterator=0;iterator<6;iterator++){
                Document file=null;
                if(iterator==0){
                    file= Jsoup.connect("http://dsebd.org/php_graph/monthly_graph.php?inst="+code+"&duration=1&type=vol").get();
                }
                else if(iterator==1){
                    file= Jsoup.connect("http://dsebd.org/php_graph/monthly_graph.php?inst="+code+"&duration=3&type=vol").get();
                }
                else if(iterator==2){
                    file= Jsoup.connect("http://dsebd.org/php_graph/monthly_graph.php?inst="+code+"&duration=6&type=vol").get();
                }
                else if(iterator==3){
                    file= Jsoup.connect("http://dsebd.org/php_graph/monthly_graph.php?inst="+code+"&duration=9&type=vol").get();
                }
                else if(iterator==4){
                    file= Jsoup.connect("http://dsebd.org/php_graph/monthly_graph.php?inst="+code+"&duration=12&type=vol").get();
                }
                else if(iterator==5){
                    file= Jsoup.connect("http://dsebd.org/php_graph/monthly_graph.php?inst="+code+"&duration=24&type=vol").get();
                }
                file.outputSettings().indentAmount(0).prettyPrint(false);
                Elements scripts=file.select("script");
                try{
                    dateAndValues.clear();
                }catch (Exception e){
                    System.out.println(e);
                }
                for(Element script:scripts) {
                    try { if (script.data().toString().contains("Date")) {
                        String dataActual=script.data().toString().substring(script.data().toString().indexOf("+")+1,script.data().toString().indexOf("{"));
                        dataActual = dataActual.replaceAll("\\n", "");
                        dataActual = dataActual.replaceAll("\\r", "");
                        String[] a = dataActual.split("\\+");

                        int k = -1;
                        for (int i = 0; i < a.length; i++) {
                            a[i] = a[i].substring(a[i].indexOf("\"")+1, a[i].indexOf("n") - 1);
                            s=a[i].split(",");
                            dateAndValues.add(new dateAndValue(s[0],Double.parseDouble(s[1])));
                        }
                    }
                    } catch (Exception e) {
                        String d=e.toString();
                        System.out.println(d);
                    }
                }
                if(iterator==0)saveGraphInfo(dateAndValues,code,"vol");
                else if(iterator==1)saveGraphInfo(dateAndValues,code,"vol3");
                else if(iterator==2)saveGraphInfo(dateAndValues,code,"vol6");
                else if(iterator==3)saveGraphInfo(dateAndValues,code,"vol9");
                else if(iterator==4)saveGraphInfo(dateAndValues,code,"vol1y");
                else if(iterator==5)saveGraphInfo(dateAndValues,code,"vol2y");
               // System.out.println(code);




            }
        }catch (IOException e){
            String d=e.toString();
            System.out.println(e.toString());
        }



        try{

            try{
                dateAndValues.clear();
            }catch (Exception e){

                System.out.println(e);
            }
            for(int iterator=0;iterator<6;iterator++){
                Document file=null;
                if(iterator==0){
                    file= Jsoup.connect("http://dsebd.org/php_graph/monthly_graph.php?inst="+code+"&duration=1&type=trd").get();
                }
                else if(iterator==1){
                    file= Jsoup.connect("http://dsebd.org/php_graph/monthly_graph.php?inst="+code+"&duration=3&type=trd").get();
                }
                else if(iterator==2){
                    file= Jsoup.connect("http://dsebd.org/php_graph/monthly_graph.php?inst="+code+"&duration=6&type=trd").get();
                }
                else if(iterator==3){
                    file= Jsoup.connect("http://dsebd.org/php_graph/monthly_graph.php?inst="+code+"&duration=9&type=trd").get();
                }
                else if(iterator==4){
                    file= Jsoup.connect("http://dsebd.org/php_graph/monthly_graph.php?inst="+code+"&duration=12&type=trd").get();
                }
                else if(iterator==5){
                    file= Jsoup.connect("http://dsebd.org/php_graph/monthly_graph.php?inst="+code+"&duration=24&type=trd").get();
                }
                file.outputSettings().indentAmount(0).prettyPrint(false);
                Elements scripts=file.select("script");
                try{
                    dateAndValues.clear();
                }catch (Exception e){

                }
                for(Element script:scripts) {
                    try { if (script.data().toString().contains("Date")) {
                        String dataActual=script.data().toString().substring(script.data().toString().indexOf("+")+1,script.data().toString().indexOf("{"));
                        dataActual = dataActual.replaceAll("\\n", "");
                        dataActual = dataActual.replaceAll("\\r", "");
                        String[] a = dataActual.split("\\+");

                        int k = -1;
                        for (int i = 0; i < a.length; i++) {
                            a[i] = a[i].substring(a[i].indexOf("\"")+1, a[i].indexOf("n") - 1);
                            s=a[i].split(",");
                            dateAndValues.add(new dateAndValue(s[0],Double.parseDouble(s[1])));
                        }
                    }
                    } catch (Exception e) {
                        String d=e.toString();
                        System.out.println(d);
                    }
                    try{
                        if(iterator==0)saveGraphInfo(dateAndValues,code,"trd");
                        else if(iterator==1)saveGraphInfo(dateAndValues,code,"trd3");
                        else if(iterator==2)saveGraphInfo(dateAndValues,code,"trd6");
                        else if(iterator==3)saveGraphInfo(dateAndValues,code,"trd9");
                        else if(iterator==4)saveGraphInfo(dateAndValues,code,"trd1y");
                        else if(iterator==5)saveGraphInfo(dateAndValues,code,"trd2y");

                    }catch (Exception e){

                    }
                }





            }
        }catch (IOException e){

        }



        try{

            try{
                dateAndValues.clear();
            }catch (Exception e){

                System.out.println(e);
            }
            for(int iterator=0;iterator<6;iterator++){
                Document file=null;
                if(iterator==0){
                    file= Jsoup.connect("http://dsebd.org/php_graph/monthly_graph.php?inst="+code+"&duration=1&type=price").get();
                }
                else if(iterator==1){
                    file= Jsoup.connect("http://dsebd.org/php_graph/monthly_graph.php?inst="+code+"&duration=3&type=price").get();
                }
                else if(iterator==2){
                    file= Jsoup.connect("http://dsebd.org/php_graph/monthly_graph.php?inst="+code+"&duration=6&type=price").get();
                }
                else if(iterator==3){
                    file= Jsoup.connect("http://dsebd.org/php_graph/monthly_graph.php?inst="+code+"&duration=9&type=price").get();
                }
                else if(iterator==4){
                    file= Jsoup.connect("http://dsebd.org/php_graph/monthly_graph.php?inst="+code+"&duration=12&type=price").get();
                }
                else if(iterator==5){
                    file= Jsoup.connect("http://dsebd.org/php_graph/monthly_graph.php?inst="+code+"&duration=24&type=price").get();
                }
                file.outputSettings().indentAmount(0).prettyPrint(false);
                Elements scripts=file.select("script");
                try{
                    dateAndValues.clear();
                }catch (Exception e){

                    System.out.println(e);
                }
                for(Element script:scripts) {
                    try { if (script.data().toString().contains("Date")) {
                        String dataActual=script.data().toString().substring(script.data().toString().indexOf("+")+1,script.data().toString().indexOf("{"));
                        dataActual = dataActual.replaceAll("\\n", "");
                        dataActual = dataActual.replaceAll("\\r", "");
                        String[] a = dataActual.split("\\+");

                        int k = -1;
                        for (int i = 0; i < a.length; i++) {
                            a[i] = a[i].substring(a[i].indexOf("\"")+1, a[i].indexOf("n") - 1);
                            s=a[i].split(",");
                            dateAndValues.add(new dateAndValue(s[0],Double.parseDouble(s[1])));
                        }
                    }
                    } catch (Exception e) {
                        String d=e.toString();
                        System.out.println(d);
                    }
                }

                if(iterator==0)saveGraphInfo(dateAndValues,code,"lcp");
                else if(iterator==1)saveGraphInfo(dateAndValues,code,"lcp3");
                else if(iterator==2)saveGraphInfo(dateAndValues,code,"lcp6");
                else if(iterator==3)saveGraphInfo(dateAndValues,code,"lcp9");
                else if(iterator==4)saveGraphInfo(dateAndValues,code,"lcp1y");
                else if(iterator==5)saveGraphInfo(dateAndValues,code,"lcp2y");





            }
        }catch (IOException e){

        }
        return null;
    }
    private void saveGraphInfo(ArrayList<dateAndValue> list,String companyName,String dataListName){
        SharedPreferences sharedPreferences=context.getSharedPreferences(formattedDate+companyName,MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        Gson string=new Gson();
        String json=string.toJson(list);
        editor.putString(dataListName,json);
        editor.apply();
    }
}
