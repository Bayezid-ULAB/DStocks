package com.example.dstock;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;

public class ConnectivityReceiver extends BroadcastReceiver {
    private CoordinatorLayout k;
    static int i=0;
    private static Snackbar noInternet;
    public static ConnectivityReceiverListener connectivityReceiverListener;
    public ConnectivityReceiver(Context context,CoordinatorLayout d) {

        super();
        this.k=d;
        this.context=context;
    }
    public ConnectivityReceiver(){
        super();
    }
    private static Context context;
    @Override
    public void onReceive(final Context context, final Intent intent) {

        boolean connection=isConnected(context);
        if (connection && MainActivity.isOnWifi()) {
            // Do something
           try{
               new Content(MainActivity.getCompanyList(),MainActivity.getTopCompanyListByTrade(),MainActivity.getTopCompanyListByValue(),MainActivity.getTopCompanyListByVolume(),context,null,true).execute();
               new HomeContent(context,MainActivity.getmChart(),MainActivity.getLastTab(),null).execute();
               try{
                   noInternet.dismiss();
               }catch (Exception e){

               }
           }
           catch (Exception e){

           }
        }
        else if(connection && MainActivity.isAllowData()){
            new Content(MainActivity.getCompanyList(),MainActivity.getTopCompanyListByTrade(),MainActivity.getTopCompanyListByValue(),MainActivity.getTopCompanyListByVolume(),context,null,true).execute();
            new HomeContent(context,MainActivity.getmChart(),MainActivity.getLastTab(),null).execute();
            try{
                noInternet.dismiss();
            }catch (Exception e){

            }
        }
        else
        {
            try{
            noInternet=    Snackbar.make(MainActivity.coordinatorLayout,"No internet connection, Last saved data are shown",Snackbar.LENGTH_INDEFINITE);
            noInternet.show();
            }catch (Exception e){

            }
        }
    }

    public static boolean isConnected( Context context) {
        final ConnectivityManager connMgr = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        final android.net.NetworkInfo wifi = connMgr
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        final android.net.NetworkInfo mobile = connMgr
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if(!wifi.isConnectedOrConnecting()&& mobile.isConnectedOrConnecting()){
            if(!MainActivity.isAllowData()){
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                MainActivity.setAllowData(true);
                                MainActivity.setConnected(true);
                                MainActivity.trigger();
                                try{
                                    noInternet.dismiss();
                                }catch (Exception e){

                                }
                                MainActivity.setConnected(true);

                                break;

                            case DialogInterface.BUTTON_NEGATIVE:

                                MainActivity.setAllowData(false);
                                MainActivity.setConnected(false);
                                i=0;
                                //No button clicked
                                break;
                        }
                    }

                };

                AlertDialog.Builder builder = new AlertDialog.Builder(context);

                if(i==0&&!MainActivity.isAllowData())builder.setMessage("You are about to use Mobile data to fetch information, are you sure?").setPositiveButton("Yes", dialogClickListener).setNegativeButton("No", dialogClickListener).show();
                i++;
            }

        }
        else if(wifi.isConnectedOrConnecting()){
            MainActivity.setOnWifi(true);

        }
        else if(!wifi.isConnectedOrConnecting())MainActivity.setOnWifi(false);
        if(wifi.isConnectedOrConnecting()||mobile.isConnectedOrConnecting()){
            MainActivity.setConnected(true);
            return  true;
        }
        MainActivity.setConnected(false);
        return false;
    }
    public interface ConnectivityReceiverListener {
        void onNetworkConnectionChanged(boolean isConnected);
    }

}

