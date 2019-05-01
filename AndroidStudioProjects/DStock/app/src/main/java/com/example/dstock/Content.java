package com.example.dstock;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.google.gson.Gson;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.widget.Toast;

import static android.content.Context.MODE_PRIVATE;

public class Content extends AsyncTask<Void, Void, Void>{
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }
    private Context context;
    private boolean setData=true;

    private ArrayList<Company> companyList;
    private ArrayList<Company> topCompanyListByTrade;
    private ArrayList<Company> topCompanyListByValue;
    private ArrayList<Company> topCompanyListByVolume;
    private SwipeRefreshLayout refresh;
    Content(ArrayList<Company> companyList,ArrayList<Company> topCompanyListByTrade,ArrayList<Company> topCompanyListByValue,ArrayList<Company> topCompanyListByVolume,Context context,SwipeRefreshLayout refresh,boolean Setvalue){
        this.companyList=companyList;
        this.context=context;
        this.setData=Setvalue;
        this.topCompanyListByTrade=topCompanyListByTrade;
        this.topCompanyListByValue=topCompanyListByValue;
        this.topCompanyListByVolume=topCompanyListByVolume;
        this.refresh=refresh;


    }
    @Override
    protected Void doInBackground(Void... voids)  {
    try{
        companyList.clear();
        topCompanyListByVolume.clear();
        topCompanyListByValue.clear();
        topCompanyListByTrade.clear();
    }catch (Exception e){

    }
        try{
            Document company=null;
           try{
               company=Jsoup.connect("http://dsebd.org/latest_share_price_all.php").get();
            }catch (SocketTimeoutException e){
               try{
                   Toast.makeText(context,e.getStackTrace().toString(),Toast.LENGTH_LONG);
               }catch (Exception fe){

               }
           }
            Elements importantLinks=company.select("tr");
            boolean first=true;
            for(Element data:importantLinks){

                if(!first){
                    String[] d=data.text().split(" ");
                    String code = d[1];
                    double ltp;
                    try{
                        ltp =Double.parseDouble(d[2].replace(",",""));
                    }catch (Exception a){
                        ltp =0;
                    }
                    double high;
                    try{
                        high =Double.parseDouble(d[3].replace(",",""));
                    }catch (Exception b){
                        high =0;
                    }

                    double low;
                    try{
                        low =Double.parseDouble(d[4].replace(",",""));
                    }catch (Exception c){
                        low =0;
                    }

                    double closep;
                    try{
                        closep =Double.parseDouble(d[5].replace(",",""));
                    }catch (Exception de) {
                        closep = 0;
                    }

                    double ycp;
                    try{
                        ycp =Double.parseDouble(d[6].replace(",",""));
                    }catch (Exception e){
                        ycp =0;
                    }
                    double change;
                    try{
                        change =Double.parseDouble(d[7].replace(",",""));
                    }catch (Exception f){
                        change =0;
                    }
                    double changeP;
                    if(ltp >0) changeP =this.round((((ltp - ycp)*100)/ ycp),2);
                    else changeP =0;
                    double trade;
                    try{
                        trade =Double.parseDouble(d[8].replace(",",""));
                    }catch (Exception g){
                        trade =0;
                    }

                    double value;
                    try {
                        value =Double.parseDouble(d[9].replace(",",""));
                    }catch (Exception j){
                        value =0;
                    }
                    double volume;
                    try{
                        volume =Double.parseDouble(d[10].replace(",",""));
                    }catch (Exception h){
                        volume =0;
                    }
                    companyList.add(new Company(code, ltp, high, low, closep, ycp, change, changeP, trade, value, volume));
                    //System.out.println(ltp);

                }


                first=false;
            }


            company = Jsoup.connect("http://www.dsebd.org/company%20listing.php").get();

             importantLinks = company.getElementsByTag("font");
            for (Element link : importantLinks) {
                if (link.text().toString().contains("(")) {
                    String linkText = link.text().toString().substring((link.text().toString().indexOf(' ') + 2),link.text().toString().lastIndexOf(')'));
                    String code=link.text().toString().substring(0,link.text().toString().indexOf(' '));

                    //System.out.println(linkText+"+"+code);//THIS IS THE TEXT I WANTED...
                    if (code.toUpperCase().equals(code) && !code.contains("T20") && !code.contains("T05") && !code.contains("T10") && !code.contains("T15") && !code.contains("T5") && code.length() > 1) {
                        String name = linkText;
                        //System.out.println(name);
                        for(Company a:companyList){
                            if(a.getCode().toString().equals(code.toString())){
                                a.setName(CompanyInfo.toTitleCase(name));
                                //System.out.println("Company Name: "+a.getName()+"\nCompany Code:"+a.getCode());
                                break;
                            }
                        }

                        //companyList.add(new Company(name, ltp, change, changeP));
                        //System.out.println(name);

                    }


                }

            }

            saveOfflineCompanyData(companyList,"companyList");
            ArrayList<Company> byTrade=new ArrayList<>(companyList);

            Collections.sort(byTrade,new Comparator<Company>(){

                @Override
                public int compare(Company c1, Company c2) {
                    return Double.compare(c1.getTrade(),c2.getTrade());
                }
            });
            for(int i=byTrade.size()-1;i>=byTrade.size()-20;i--)topCompanyListByTrade.add(byTrade.get(i));

            saveOfflineCompanyData(topCompanyListByTrade,"topCompanyListByTrade");
            byTrade.clear();


            byTrade=new ArrayList<>(companyList);
            Collections.sort(byTrade,new Comparator<Company>(){

                @Override
                public int compare(Company c1, Company c2) {
                    return Double.compare(c1.getVolume(),c2.getVolume());
                }
            });
            for(int i=byTrade.size()-1;i>=byTrade.size()-20;i--)topCompanyListByVolume.add(byTrade.get(i));
            saveOfflineCompanyData(topCompanyListByVolume,"topCompanyListByVolume");
            byTrade.clear();


            byTrade=new ArrayList<>(companyList);
            Collections.sort(byTrade,new Comparator<Company>(){

                @Override
                public int compare(Company c1, Company c2) {
                    return Double.compare(c1.getValue(),c2.getValue());
                }
            });
            for(int i=byTrade.size()-1;i>=byTrade.size()-20;i--)topCompanyListByValue.add(byTrade.get(i));
            saveOfflineCompanyData(topCompanyListByValue,"topCompanyListByValue");
            byTrade.clear();

        }catch (IOException e){
            String d=e.getStackTrace().toString();
            System.out.print(d);
        }

        //Toast.makeText(getContext(),valuesDse30.get(0).toString(),Toast.LENGTH_SHORT).show();
        return null;

    }

    private void saveCompanyPosition(String code,int pos){
        SharedPreferences sharedPreferences=context.getSharedPreferences("companies",MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putInt(code,pos);
        editor.apply();
    }

    private void saveOfflineCompanyData(ArrayList<Company> myList,String dataName){
        SharedPreferences sharedPreferences=context.getSharedPreferences("obj",MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        Gson string=new Gson();
        String json=string.toJson(myList);
        editor.putString(dataName,json);
        editor.apply();
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
           try{
               refresh.setRefreshing(false);
           }catch (Exception e){

           }
           if(setData){

               topCompanyListByTrade = MainActivity.loadCompanyData(topCompanyListByTrade, "topCompanyListByTrade");
               companyList = MainActivity.loadCompanyData(companyList, "companyList");
               topCompanyListByValue = MainActivity.loadCompanyData(topCompanyListByValue, "topCompanyListByValue");
               topCompanyListByVolume = MainActivity.loadCompanyData(topCompanyListByVolume, "topCompanyListByVolume");
           }

    }
    public  double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}

