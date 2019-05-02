package com.example.dstock;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;



public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, OnChartGestureListener, OnChartValueSelectedListener {
    private String formattedDate;
    private int tabNum=0;
    private static boolean allowData=false;
    private static Context context;
    public static void setAllowData(boolean allowData) {
        MainActivity.allowData = allowData;
    }


    private ArrayList<Company> homeViewAndFavView=new ArrayList<>();

    public static boolean isAllowData() {
        return allowData;
    }

    public static void setConnected(boolean connected) {
        MainActivity.connected = connected;
    }

    public static String getCurrentUser() {
        return currentUser;
    }

    private static String currentUser;
    private static boolean connected=false;
    private static ArrayList<Company> userFav=new ArrayList<Company>();

    @Override
    public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

    }

    @Override
    public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

    }

    @Override
    public void onChartLongPressed(MotionEvent me) {

    }

    @Override
    public void onChartDoubleTapped(MotionEvent me) {

    }

    @Override
    public void onChartSingleTapped(MotionEvent me) {

    }

    @Override
    public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {

    }

    @Override
    public void onChartScale(MotionEvent me, float scaleX, float scaleY) {

    }

    @Override
    public void onChartTranslate(MotionEvent me, float dX, float dY) {

    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {

    }

    @Override
    public void onNothingSelected() {

    }
    @Override
    protected  void onPause(){
        super.onPause();
        if(activityCurrent.equals("companyList")){
            try{
                myListPositionCompany= myList.getFirstVisiblePosition();
            }catch (Exception e){

            }
        }

        else if(activityCurrent.equals("top20")){
            try{
                myListPositionCompany= myList.getFirstVisiblePosition();
            }catch (Exception e){

            }
        }
    }

    private int myListPositionCompany=0;
    private int myListPositionTop=0;
    public void rotateScreenLandscape(View view) {
        if(currentUser.equals("guest")){
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setMessage("Guest user cannot add Favorites, please sign up to use this feature!").show();
        }
        else{

            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        }
    }




    private CompanyListAdapter adapter;
    private TopCompanyListAdapter adapterTop;
    private ListView myList;
    private DrawerLayout myDrawer;
    private static String lastTab="X";

    public static String getLastTab() {
        return lastTab;
    }

    private ActionBarDrawerToggle mytoggle;
    public  static TextView x1,x2,x3,s1,s2,s3,t1,t2,t3,tr1,tr2,tr3,i1,i2,i3;
    private static final String TAG="MainActivity";
    private EditText searchText;
    private static ArrayList<Company> companyList=new ArrayList<Company>();
    private static ArrayList<Company> topCompanyListByTrade=new ArrayList<Company>();
    private static ArrayList<Company> topCompanyListByVolume=new ArrayList<Company>();
    private static ArrayList<Company> topCompanyListByValue=new ArrayList<Company>();
    private String activityCurrent="main";
    private long backPressedTime;
    private Toast backToast;
    private Snackbar noInternetSnackbar;
    private HomeContent homeContent;
    private Content content;
    private ConnectivityReceiver connectivityReceiver;
    static LineChart mChart;
    private boolean firstTime=true;
    public static CoordinatorLayout coordinatorLayout;
    private SwipeRefreshLayout refreshCompanyList;
    private SwipeRefreshLayout refreshTopCompanies;
    final Handler mHandler = new Handler();
    Runnable mHandlerTask;
    private SwipeRefreshLayout refreshMain;
    private SwipeRefreshLayout refreshMarket;

    private static boolean onWifi=false;
    Runnable mHandlerTaskInternet;
    private final static int INTERVAL = 1000 * 60 * 1; //1 minute
    static float referenceTimestamp = 0f;
    private TabLayout tabs;
    private static SharedPreferences sharedPreferences;


    OrientationEventListener mOrientationListener;
    public static boolean isOnWifi() {
        return onWifi;
    }

    public static void setOnWifi(boolean onWifi) {
        MainActivity.onWifi = onWifi;
    }


    public static ArrayList<Company> getCompanyList() {
        return companyList;
    }

    public static ArrayList<Company> getTopCompanyListByTrade() {
        return topCompanyListByTrade;
    }

    public static ArrayList<Company> getTopCompanyListByVolume() {
        return topCompanyListByVolume;
    }

    public static ArrayList<Company> getTopCompanyListByValue() {
        return topCompanyListByValue;
    }

    public static LineChart getmChart() {
        return mChart;
    }
    SharedPreferences prefs=null;
    SharedPreferences sharedPrefs;
    SharedPreferences.Editor ed;
    public static ArrayList<Company> loadCompanyData(ArrayList<Company> myList,String dataName){
        Gson string=new Gson();
        String json=sharedPreferences.getString(dataName,null);
        Type type=new TypeToken<ArrayList<Company>>(){}.getType();
        myList=string.fromJson(json,type);
        if(myList==null)return new ArrayList<Company>();
        return myList;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen
        if(activityCurrent=="main"){
           // mainCall();
        }
        else if(activityCurrent=="market"){
            marketOverView();
        }
        else if(activityCurrent=="top20"){
            //top20Called();
        }
        else if(activityCurrent=="companyList"){
           // companyListCalled();
        }
        else if(activityCurrent=="settings"){
           // settingsCalled();
        }
        else if(activityCurrent=="favorite"){
           // favoriteCalled();
        }
    }
    /*
    @Override
    public void onSaveInstanceState(Bundle savedState){
        super.onSaveInstanceState(savedState);
        if(activityCurrent=="companyList"){
            try{
                savedState.putParcelableArrayList("companyList",companyList);
            }catch (Exception e){

            }
        }
        else if(activityCurrent=="top20"){
            try{
                savedState.putParcelableArrayList("byValue",topCompanyListByValue);
                savedState.putParcelableArrayList("byVolume",topCompanyListByVolume);
                savedState.putParcelableArrayList("byTrade",topCompanyListByTrade);
            }catch (Exception e){

            }
        }

    }
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if(activityCurrent=="companyList"){
            try{
                companyList=savedInstanceState.getParcelableArrayList("companyList");
            }catch (Exception e){

            }
        }
        else if(activityCurrent=="top20"){
            try{
                topCompanyListByValue=savedInstanceState.getParcelableArrayList("byValue");

                topCompanyListByTrade=savedInstanceState.getParcelableArrayList("byTrade");

                topCompanyListByVolume=savedInstanceState.getParcelableArrayList("byVolume");
            }catch (Exception e){

            }
        }

    }
*/

    public static void loadFavs(){

        SharedPreferences favoriteSharedPreferences=context.getSharedPreferences("favorites",MODE_PRIVATE);
        Gson string=new Gson();
        try{
            String json=favoriteSharedPreferences.getString(currentUser,null);
            Type type = new TypeToken<ArrayList<String>>() {}.getType();
            userFavs = (string.fromJson(json, type));
        }catch (Exception e){
            userFavs=new ArrayList<>();
        }
    }
    public static void setFavs(ArrayList<String> list){
        SharedPreferences sharedPreferences=context.getSharedPreferences("favorites",MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        Gson string=new Gson();
        String json=string.toJson(list);
        editor.putString(currentUser,json);
        String d=currentUser;
        editor.apply();
        loadFavs();
    }
    public static ArrayList<String> getUserFavs() {
        return userFavs;
    }

    private static ArrayList<String> userFavs=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context=getContext();




        SharedPreferences userpreference=getSharedPreferences("obj",MODE_PRIVATE);
         currentUser=userpreference.getString("lastuser",null);
         loadFavs();
        if(companyList==null)companyList=new ArrayList<>();
        if(topCompanyListByValue==null)topCompanyListByValue=new ArrayList<>();
        if(topCompanyListByTrade==null)topCompanyListByTrade=new ArrayList<>();
        if(topCompanyListByVolume==null)topCompanyListByVolume=new ArrayList<>();
        prefs = getSharedPreferences("com.mycompany.myAppName", MODE_PRIVATE);
        sharedPreferences=getSharedPreferences("obj",MODE_PRIVATE);
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        connectivityReceiver=new ConnectivityReceiver(this,coordinatorLayout);
        registerReceiver(connectivityReceiver, filter);
        connectivityReceiver.isConnected(this);

        mHandlerTask= new Runnable()
        {
            @Override
            public void run() {

                try{
                    if(((connected && allowData)||(connected && onWifi))){
                        content=new Content(companyList,topCompanyListByTrade,topCompanyListByValue,topCompanyListByVolume,getContext(),refreshCompanyList,false);
                        if(content.getStatus()!=AsyncTask.Status.RUNNING)content.execute().get();
                        homeContent=new HomeContent(getContext(),mChart,lastTab,refreshMarket);
                        if(homeContent.getStatus()!=AsyncTask.Status.RUNNING)homeContent.execute().get();
                       /* if(!companyList.isEmpty()){
                            for(Company d:companyList){
                                if(!sharedPrefs.contains(formattedDate+d.getCode())){

                                    ed = sharedPrefs.edit();
                                    new FetchCompanyGraphData(d.getCode(),getContext(),formattedDate).execute();
                                    ed.putBoolean(formattedDate+d.getCode(), true);
                                    ed.commit();
                                }
                            }

                        }*/
                    }
                    else{
                        try{

                            content=new Content(companyList,topCompanyListByTrade,topCompanyListByValue,topCompanyListByVolume,getContext(),refreshCompanyList,false);
                            homeContent=new HomeContent(getContext(),mChart,lastTab,refreshMarket);
                        }catch (Exception e){

                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }


                mHandler.postDelayed(mHandlerTask, INTERVAL);
            }
        };


        startRepeatingTask();
        mainCall();

        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        formattedDate = df.format(c);


        try{
            companyList=loadCompanyData(companyList,"companyList");
        }catch (Exception e){

        }
        sharedPrefs= this.getSharedPreferences("dates", MODE_PRIVATE);

        try{
            if(((connected && allowData)||(connected && onWifi))){
                content=new Content(companyList,topCompanyListByTrade,topCompanyListByValue,topCompanyListByVolume,getContext(),refreshCompanyList,false);
                if(content.getStatus()!=AsyncTask.Status.RUNNING)content.execute().get();
            }
            /*if(!companyList.isEmpty()){
                for(Company d:companyList){
                    if(!sharedPrefs.contains(formattedDate+d.getCode())){

                        ed = sharedPrefs.edit();
                        new FetchCompanyGraphData(d.getCode(),getContext(),formattedDate).execute();
                        ed.putBoolean(formattedDate+d.getCode(), true);
                        ed.commit();
                    }
                }

            }*/
        }catch (Exception e){


        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(companyList==null)companyList=new ArrayList<>();
        if(topCompanyListByValue==null)topCompanyListByValue=new ArrayList<>();
        if(topCompanyListByTrade==null)topCompanyListByTrade=new ArrayList<>();
        if(topCompanyListByVolume==null)topCompanyListByVolume=new ArrayList<>();
        if (prefs.getBoolean("appHasRunBefore", false)){
            try{
                topCompanyListByTrade = loadCompanyData(topCompanyListByTrade, "topCompanyListByTrade");
                companyList = loadCompanyData(companyList, "companyList");
                topCompanyListByValue = loadCompanyData(topCompanyListByValue, "topCompanyListByValue");
                topCompanyListByVolume = loadCompanyData(topCompanyListByVolume, "topCompanyListByVolume");
                new Content(companyList,topCompanyListByTrade,topCompanyListByValue,topCompanyListByVolume,getContext(),refreshCompanyList,false).execute();
                new HomeContent(getContext(),mChart,lastTab,refreshMain).execute();

            }catch (Exception e){

            }
            /* This is not the first run */
        }
        else {
            /* Do first run stuff */
            if((connected && allowData)||(connected && onWifi)){

                new Content(companyList,topCompanyListByTrade,topCompanyListByValue,topCompanyListByVolume,getContext(),refreshCompanyList,true).execute();
                new HomeContent(getContext(),mChart,lastTab,refreshMain).execute();
            }
            prefs.edit().putBoolean("appHasRunBefore", true).commit();
        }

       /* if(activityCurrent.equals("companyList")){
            try{
                adapter = new CompanyListAdapter(this, R.layout.adapter_view, companyList,"companyList");
                myList.setAdapter(adapter);
                myList.setSelectionFromTop(myListPositionCompany,0);
            }catch (Exception e){

            }
        }

            else if(activityCurrent.equals("top20")){
            try{
                if(tabNum==0){
                    adapterTop=new TopCompanyListAdapter(getContext(), R.layout.adapter_viewtops,topCompanyListByVolume,"byVolume");
                    myList.setAdapter(adapterTop);
                    myList.setSelectionFromTop(myListPositionTop,0);
                }
                else if(tabNum==1){
                    adapterTop=new TopCompanyListAdapter(getContext(), R.layout.adapter_viewtops,topCompanyListByTrade,"byTrade");
                    myList.setAdapter(adapterTop);
                    myList.setSelectionFromTop(myListPositionTop,0);
                }
                else if(tabNum==2){
                    adapterTop=new TopCompanyListAdapter(getContext(), R.layout.adapter_viewtops,topCompanyListByValue,"byValue");
                    myList.setAdapter(adapterTop);
                    myList.setSelectionFromTop(myListPositionTop,0);
                }
            }catch (Exception e){

            }
        }*/
       if(activityCurrent.equals("main")){
           mainCall();
       }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
       try{
           mOrientationListener.disable();
       }catch (Exception e){

       }
    }

    @Override
    public void onBackPressed(){
        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            backToast.cancel();
            super.onBackPressed();
            this.finish();
        } else {
            backToast = Toast.makeText(getBaseContext(), "Press back again to exit", Toast.LENGTH_SHORT);
            backToast.show();
            myDrawer.openDrawer(Gravity.LEFT);
            //myDrawer.openDrawer(myDrawer);
        }
        backPressedTime=System.currentTimeMillis();

    }

    void startRepeatingTask()
    {
        mHandlerTask.run();
    }

   /* void stopRepeatingTask()
    {
        mHandler.removeCallbacks(mHandlerTask);
    }

     void stopRepeatingTaskInternetCheck()
    {
        mHandler.removeCallbacks(mHandlerTaskInternet);
    }

    */
    void startRepeatingTaskInternetCheck()
    {
        mHandlerTaskInternet.run();
    }


    private void mainCall(){

        try{
            topCompanyListByTrade = loadCompanyData(topCompanyListByTrade, "topCompanyListByTrade");
            companyList = loadCompanyData(companyList, "companyList");
            topCompanyListByValue = loadCompanyData(topCompanyListByValue, "topCompanyListByValue");
            topCompanyListByVolume = loadCompanyData(topCompanyListByVolume, "topCompanyListByVolume");

        }catch (Exception e){

        }
        activityCurrent="main";
        setContentView(R.layout.activity_main);
        coordinatorLayout=findViewById(R.id.mainCoordinator);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.BLACK));
        refreshMain=findViewById(R.id.refreshMain);
        myList=findViewById(R.id.favList);
        myDrawer=findViewById(R.id.drawerHome);
        mytoggle=new ActionBarDrawerToggle(this,myDrawer,R.string.open,R.string.close){
            public void onDrawerClosed(View view) {
                getSupportActionBar().setTitle("DStocks");
            }

            public void onDrawerOpened(View drawerView) {
                getSupportActionBar().setTitle("Select Option");
            }
        };

        refreshMain.setEnabled(false);
        refreshMain.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // your code

            }
        });

        myList.setOnTouchListener(new SwipeDetect(){
            @Override
            public void onSwipeRight() {
                myDrawer.openDrawer(Gravity.LEFT);

            }
            @Override
            public void onSwipeBottom(){

            }
            @Override
            public void onSwipeLeft() {
                if(myDrawer.isDrawerOpen(GravityCompat.START))myDrawer.closeDrawers();
            }
        });
        myList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parentAdapter, View view, int position,
                                    long id) {


                myList.setClickable(false);
                // We know the View is a <extView so we can cast it
                try{
                    Company selItem = (Company) adapter.getItem(position);
                    Gson string=new Gson();
                    String json=string.toJson(selItem);
                    Intent a=new Intent(getContext(),CompanyInfo.class);
                    SharedPreferences sharedPreferences=getContext().getSharedPreferences(selItem.getCode(),MODE_PRIVATE);
                    ed = sharedPrefs.edit();
                    new FetchCompanyGraphData(selItem.getCode(),getContext(),formattedDate).execute();
                    ed.putBoolean(selItem.getCode(), true);
                    ed.commit();
                    a.putExtra("company",json);
                    a.putExtra("formatteddate",formattedDate);
                    startActivity(a);
                }catch (Exception e){

                }
                myList.setClickable(true);
            }
        });
        myDrawer.addDrawerListener(mytoggle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mytoggle.syncState();
        //getSupportActionBar().setHomeButtonEnabled(true);

        myDrawer.setOnTouchListener(new SwipeDetect(){
            @Override
            public void onSwipeRight() {
                myDrawer.openDrawer(Gravity.LEFT);

            }
            @Override
            public void onSwipeBottom(){
                if(content.getStatus()!=AsyncTask.Status.RUNNING){
                    if((connected && allowData)||(connected && onWifi)){
                        homeContent=new HomeContent(getContext(),mChart,lastTab,refreshMain);
                        homeContent.execute();
                        content=new Content(companyList,topCompanyListByTrade,topCompanyListByValue,topCompanyListByVolume,getContext(),refreshCompanyList,false);
                        content.execute();
                        refreshMain.setRefreshing(true);
                    }
                }
            }
            @Override
            public void onSwipeLeft() {
                if(myDrawer.isDrawerOpen(GravityCompat.START))myDrawer.closeDrawers();
            }
        });
        NavigationView navView=findViewById(R.id.navigation);
        navView.setNavigationItemSelectedListener(this);



        if(homeViewAndFavView!=null||!homeViewAndFavView.isEmpty()){
            homeViewAndFavView.clear();
        }
        loadFavs();
        ArrayList<String > homeCompanies=new ArrayList<>();
        if((userFavs==null||userFavs.isEmpty())&&!topCompanyListByVolume.isEmpty()&&!topCompanyListByValue.isEmpty()&&!topCompanyListByTrade.isEmpty()){
            try{
                for(int i=0;i<3;){
                    if(!homeCompanies.contains(topCompanyListByTrade.get(i).getCode())){
                        homeViewAndFavView.add(topCompanyListByTrade.get(i));
                        homeCompanies.add(topCompanyListByTrade.get(i).getCode());

                    }
                    i++;
                }
                for(int i=0;i<3;){
                    if(!homeCompanies.contains(topCompanyListByVolume.get(i).getCode())){
                        homeViewAndFavView.add(topCompanyListByVolume.get(i));
                        homeCompanies.add(topCompanyListByVolume.get(i).getCode());

                    }
                    i++;
                }for(int i=0;i<3;){
                    if(!homeCompanies.contains(topCompanyListByValue.get(i).getCode())){
                        homeViewAndFavView.add(topCompanyListByValue.get(i));
                        homeCompanies.add(topCompanyListByValue.get(i).getCode());
                    }
                    i++;
                }
                adapter=new CompanyListAdapter(this,R.layout.adapter_view,homeViewAndFavView,"companyList");
                myList.setAdapter(adapter);
            }catch (Exception e){

                ((View)findViewById(R.id.viewbottom)).setVisibility(View.INVISIBLE);
                ((View)findViewById(R.id.viewTop)).setVisibility(View.INVISIBLE);
                ((TextView)findViewById(R.id.code)).setVisibility(View.INVISIBLE);
            }
        }
        else{
            companyList=loadCompanyData(companyList,"companyList");
            TextView nag=findViewById(R.id.nag);
            nag.setText("Your favorite companies below:");
            for(Company company:companyList){
                if(userFavs.contains(company.getCode().toString())){
                    homeViewAndFavView.add(company);
                }
            }
            adapter=new CompanyListAdapter(this,R.layout.adapter_view,homeViewAndFavView,"companyList");
            myList.setAdapter(adapter);
        }
    }

    private void marketOverView(){
        activityCurrent="market";
        setContentView(R.layout.activity_market_overview);
        getSupportActionBar().setTitle("Market Overview");

        try{
            try{
                mChart=findViewById(R.id.lineChart);
                mChart.setOnChartGestureListener(MainActivity.this);
                mChart.setOnChartValueSelectedListener(MainActivity.this);
            }catch (Exception e){

            }
            refreshMarket=findViewById(R.id.refreshMarket);
            t1=findViewById(R.id.t1);
            t2=findViewById(R.id.t2);
            t3=findViewById(R.id.t3);
            tr1=findViewById(R.id.tr1);
            tr2=findViewById(R.id.tr2);
            tr3=findViewById(R.id.tr3);
            s3=findViewById(R.id.s3);
            s2=findViewById(R.id.s2);
            s1=findViewById(R.id.s1);
            x1=findViewById(R.id.x1);
            x2=findViewById(R.id.x2);
            x3=findViewById(R.id.x3);
            i1=findViewById(R.id.i1);
            i2=findViewById(R.id.i2);
            i3=findViewById(R.id.i3);
            coordinatorLayout=findViewById(R.id.coor);
            tabs=findViewById(R.id.tabs);


        } catch (Exception layoutException){
            Toast.makeText(getContext(), layoutException.toString(), Toast.LENGTH_SHORT).show();

        }
        refreshMarket.setEnabled(false);
        refreshMarket.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // your code

            }
        });

        try{
            if(tabs!=null) {
                { tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                    @Override
                    public void onTabSelected(TabLayout.Tab tab) {
                        if (tab==tabs.getTabAt(0)){

                            try{
                                homeContent.setChartDSE("X",mChart);
                            }catch (Exception d){
                                Toast.makeText(getContext(), d.toString(), Toast.LENGTH_SHORT).show();
                            }
                            lastTab="X";
                        }
                        else if(tab==tabs.getTabAt(1)){

                            try{
                                homeContent.setChartDSE("S",mChart);
                            }catch (Exception d){
                                Toast.makeText(getContext(), d.toString(), Toast.LENGTH_SHORT).show();

                            }
                            lastTab="S";
                        }
                        else{
                            try{
                                homeContent.setChartDSE("30",mChart);
                            }catch (Exception d){
                                Toast.makeText(getContext(), d.toString(), Toast.LENGTH_SHORT).show();

                            }
                            lastTab="30";
                        }
                    }

                    @Override
                    public void onTabUnselected(TabLayout.Tab tab) {

                    }

                    @Override
                    public void onTabReselected(TabLayout.Tab tab) {

                    }
                });
                }
            }
        }catch (Exception e){
            Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();

        }
        if (getContext().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            if(currentUser.equals("guest")){
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage("Guest user cannot see graphs, please sign up to use this feature!").show();

            }else{
                homeContent.loadOfflineChartData();
                if (lastTab == "X") {
                    try {
                        tabs.getTabAt(1).select();
                        tabs.getTabAt(0).select();
                    } catch (Exception e) {
                        String d = e.getStackTrace().toString();
                        System.out.println(e.getStackTrace());
                    }
                } else if (lastTab == "S") {
                    tabs.getTabAt(1).select();
                } else if (lastTab == "30") {
                    tabs.getTabAt(2).select();
                }
                try{
                    homeContent.setChartDSE("X",mChart);
                }catch (Exception e){

                }
            }

        } else if (getContext().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            homeContent.loadOfflineHomeDataAll();
            homeContent.setValues();
        }

        if(!firstTime){
            if(getContext().getResources().getConfiguration().orientation==Configuration.ORIENTATION_LANDSCAPE){
                if(currentUser.equals("guest")){
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setMessage("Guest user cannot see graphs, please sign up to use this feature!").show();
                }

                else{
                    homeContent.loadOfflineChartData();
                    if(lastTab.equals("X")){
                        tabs.getTabAt(1).select();
                        tabs.getTabAt(0).select();
                    }
                    else if(lastTab.equals("S")){
                        tabs.getTabAt(1).select();
                    }
                    else if(lastTab.equals("30")){
                        tabs.getTabAt(2).select();
                    }
                    // homeContent.setChart();
                }
            }
            else if(getContext().getResources().getConfiguration().orientation==Configuration.ORIENTATION_PORTRAIT){
                if(homeContent.getStatus()==AsyncTask.Status.RUNNING)homeContent.cancel(true);
                homeContent.loadOfflineHomeDataAll();
            }
        }


        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.BLACK));
        myDrawer=(DrawerLayout)findViewById(R.id.drawer);
        mytoggle=new ActionBarDrawerToggle(this,myDrawer,R.string.open,R.string.close){
            public void onDrawerClosed(View view) {
                getSupportActionBar().setTitle("Market Overview");
            }

            public void onDrawerOpened(View drawerView) {
                getSupportActionBar().setTitle("Select Option");
            }
        };



        myDrawer.addDrawerListener(mytoggle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mytoggle.syncState();
        //getSupportActionBar().setHomeButtonEnabled(true);

        myDrawer.setOnTouchListener(new SwipeDetect(){
            @Override
            public void onSwipeRight() {
                myDrawer.openDrawer(Gravity.LEFT);

            }
            @Override
            public void onSwipeBottom(){
                if(homeContent.getStatus()!=AsyncTask.Status.RUNNING){
                    if((connected && allowData)||(connected && onWifi)){
                        homeContent=new HomeContent(getContext(),mChart,lastTab,refreshMarket);
                        homeContent.execute();
                        refreshMarket.setRefreshing(true);
                    }
                }
            }
            @Override
            public void onSwipeLeft() {
                if(myDrawer.isDrawerOpen(GravityCompat.START))myDrawer.closeDrawers();
            }
        });

        NavigationView navView=findViewById(R.id.navigation);
        navView.setNavigationItemSelectedListener(this);
        firstTime=false;
    }


    private Context getContext(){
        return this;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(mytoggle.onOptionsItemSelected(item))return true;
        return super.onOptionsItemSelected(item);
    }



    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        try{
            backToast.cancel();
        }catch (Exception e){

        }
        backPressedTime=backPressedTime-2000;
        int id=item.getItemId();
        if(id==R.id.atoz){
            if(activityCurrent!="companyList"){
                companyListCalled();
                myDrawer.closeDrawers();
            }
            else myDrawer.closeDrawers();
        }
        else if(id==R.id.userName){

            if(activityCurrent!="settings"){
                settingsCalled();
                myDrawer.closeDrawers();
            }
            else myDrawer.closeDrawers();
        }
        else if(id==R.id.top20){
            if(activityCurrent!="top20"){
                top20Called();
                myDrawer.closeDrawers();
            }
            else myDrawer.closeDrawers();
        }
        else if(id==R.id.home){
            if(activityCurrent!="main"){
                getSupportActionBar().setTitle("DStocks");
                mainCall();
                myDrawer.closeDrawers();
            }
            else myDrawer.closeDrawers();
        }
        else if (id == R.id.settings){
            if(activityCurrent!="settings"){

                settingsCalled();
                myDrawer.closeDrawers();
            }
            else myDrawer.closeDrawers();

        }
        else if(id==R.id.logout){
            logOutCalled();
        } else if (id == R.id.marketOverview) {
            if(activityCurrent!="market"){
                marketOverView();
                myDrawer.closeDrawers();
            }
            else myDrawer.closeDrawers();
        }
        return false;
    }

    private void logOutCalled(){
        SharedPreferences sharedPreferences=getSharedPreferences("obj",MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString("lastuser",null);
        editor.putBoolean("loggedIn",false);
        editor.apply();
        Intent a=new Intent(this,LoginActivity.class);
        startActivity(a);
        this.finish();
    }

    private TextView username;
    private TextView email;
    private TextView old_password;
    private TextView new_password;
    private TextView password_again;
    User user;
    private void settingsCalled(){
        if(currentUser.equals("guest")){

            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setMessage("Guest users don't have profile, please sign up to use this feature!").show();
        }
        setContentView(R.layout.activity_settings);
        coordinatorLayout=findViewById(R.id.coor);
        activityCurrent="settings";
        getSupportActionBar().setTitle("My Settings");
        myDrawer=findViewById(R.id.drawerCompany);
        mytoggle=new ActionBarDrawerToggle(this,myDrawer,R.string.open,R.string.close){
            public void onDrawerClosed(View view) {
                getSupportActionBar().setTitle("My Settings");
            }

            public void onDrawerOpened(View drawerView) {
                getSupportActionBar().setTitle("Select Option");
            }
        };

        myDrawer.setOnTouchListener(new SwipeDetect(){
            @Override
            public void onSwipeRight() {
                myDrawer.openDrawer(Gravity.LEFT);

            }
            @Override
            public void onSwipeBottom(){

            }
            @Override
            public void onSwipeLeft() {
                if(myDrawer.isDrawerOpen(GravityCompat.START))myDrawer.closeDrawers();
            }
        });
        myDrawer.addDrawerListener(mytoggle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mytoggle.syncState();
        NavigationView navView=findViewById(R.id.navigationCompany);
        navView.setNavigationItemSelectedListener(this);

        username=findViewById(R.id.name);
        email=findViewById(R.id.email);
        old_password=findViewById(R.id.password);
        new_password=findViewById(R.id.new_password);
        password_again=findViewById(R.id.new_password_again);

        SharedPreferences sharedPreferences=getSharedPreferences("obj",MODE_PRIVATE);
        Gson string=new Gson();
        String json=sharedPreferences.getString(currentUser.toLowerCase(),null);

        Type type = new TypeToken<User>() {}.getType();
        user = string.fromJson(json, type);
       try{
           username.setText(user.getName());
           email.setText(user.getEmail());
       }catch (Exception e){

       }

    }


    public boolean validate() {
        boolean valid = true;

        String name = username.getText().toString();
        // String address = _addressText.getText().toString();
        String email_add = email.getText().toString();
        // String mobile = _mobileText.getText().toString();
        String password = new_password.getText().toString();
        String oldPass = old_password.getText().toString();
        String passwordReEntered = password_again.getText().toString();
        //String reEnterPassword = _reEnterPasswordText.getText().toString();

        if (name.isEmpty() || name.length() < 3) {
            username.setError("at least 3 characters");
            valid = false;
        } else {
            username.setError(null);
        }

        // if (address.isEmpty()) {
        //      _addressText.setError("Enter Valid Address");
        //      valid = false;
        //  } else {
        //      _addressText.setError(null);
        //  }


        if (!email_add.isEmpty() && !android.util.Patterns.EMAIL_ADDRESS.matcher(email_add).matches()) {
            //if(email.isEmpty()){
            email.setError("enter a valid email");
            valid = false;
        } else {
            email.setError(null);
        }


        if (!password.isEmpty() && password.length() < 6) {
            new_password.setError("Atleast 6 Characters long");
            valid = false;
        } else {
            new_password.setError(null);
        }
        if(!password.equals(passwordReEntered)){
            password_again.setError("Passwords do not match");
            valid=false;
        }else{
            password_again.setError(null);
        }

        if(oldPass.isEmpty()&&password.isEmpty()&&passwordReEntered.isEmpty()){
            valid=true;
        }
        return valid;
    }

    public void updateInfo(View view) {
        ((Button)view).setEnabled(false);
        if (!validate()) {

            ((Button)view).setEnabled(true);
            return;
        }

        String name = username.getText().toString();
        // String address = _addressText.getText().toString();
        String email_add = email.getText().toString();
        // String mobile = _mobileText.getText().toString();
        String oldPass = old_password.getText().toString();
        String password = new_password.getText().toString();
        String passwordReEntered = password_again.getText().toString();

        if(name.equals(currentUser.toLowerCase()) && email_add.equals(user.getEmail()) && oldPass.isEmpty()&&password.isEmpty()&&passwordReEntered.isEmpty()){
            Snackbar.make(coordinatorLayout,"Nothing to Update",Snackbar.LENGTH_SHORT).show();
            ((Button)view).setEnabled(true);
            return;
        }
        String passwordHashedSalted=null;
        if(oldPass.isEmpty()&&password.isEmpty()&&passwordReEntered.isEmpty()){
            passwordHashedSalted=user.getPasswordSaltedHash();
        }
        else{
            try{
                if(LoginActivity.validatePassword(oldPass,user.getPasswordSaltedHash())){
                    passwordHashedSalted=Hashing.generateStrongPasswordHash(password);
                }
                else{
                    old_password.setError("Old password is not Correct! Try Again");

                    ((Button)view).setEnabled(true);
                    return;
                }
            }catch (Exception e){

            }
        }
        ArrayList<String> userFav=getUserFavs();
        SharedPreferences mySPrefs = getSharedPreferences("obj",MODE_PRIVATE);
        SharedPreferences.Editor editor = mySPrefs.edit();
        editor.remove(name);
        editor.apply();
        mySPrefs=getSharedPreferences("favorites",MODE_PRIVATE);
        editor=mySPrefs.edit();
        editor.remove(name);
        editor.apply();
        currentUser=name;
        SharedPreferences sharedPreferences=getSharedPreferences("obj",MODE_PRIVATE);
        User newUser=new User(name.toLowerCase(),passwordHashedSalted,email_add);
        editor=sharedPreferences.edit();
        Gson string=new Gson();
        String json=string.toJson(newUser);
        editor.putString(name.toLowerCase(),json);
        editor.apply();
        setFavs(userFav);
        SharedPreferences sharedPreferencess=getSharedPreferences("obj",MODE_PRIVATE);
        Gson strings=new Gson();
        String jsons=sharedPreferences.getString(currentUser.toLowerCase(),null);

        Type type = new TypeToken<User>() {}.getType();
        user = strings.fromJson(jsons, type);
        try{
            username.setText(user.getName());
            email.setText(user.getEmail());
            password_again.setText("");
            new_password.setText("");
            old_password.setText("");
        }catch (Exception e){

        }
        Snackbar.make(coordinatorLayout,"Successfully Updated",Snackbar.LENGTH_SHORT).show();
        ((Button)view).setEnabled(true);
    }

    public static void setTopCompanyListByTrade(ArrayList<Company> topCompanyListByTrade) {
        topCompanyListByTrade = topCompanyListByTrade;
    }

    public static void setTopCompanyListByVolume(ArrayList<Company> topCompanyListByVolume) {
        topCompanyListByVolume = topCompanyListByVolume;
    }

    public static void setTopCompanyListByValue(ArrayList<Company> topCompanyListByValue) {
        topCompanyListByValue = topCompanyListByValue;
    }

    public static void setCompanyList(ArrayList<Company> companyList) {
        companyList = companyList;
    }

    private void top20Called(){
        if(topCompanyListByVolume==null|| topCompanyListByValue.isEmpty()){
            topCompanyListByVolume=new ArrayList<Company>();
            topCompanyListByVolume=loadCompanyData(topCompanyListByVolume,"topCompanyListByVolume");
        }
        if(topCompanyListByTrade==null || topCompanyListByTrade.isEmpty()){
            topCompanyListByTrade=new ArrayList<Company>();
            topCompanyListByTrade=loadCompanyData(topCompanyListByTrade, "topCompanyListByTrade");
        }
        if(topCompanyListByValue==null|| topCompanyListByVolume.isEmpty()){
            topCompanyListByValue=new ArrayList<Company>();
            topCompanyListByValue=loadCompanyData(topCompanyListByVolume, "topCompanyListByVolume");
        }
        setContentView(R.layout.activity_top);
        coordinatorLayout=findViewById(R.id.coor);
        tabs=findViewById(R.id.tabsTop);
        refreshTopCompanies=findViewById(R.id.refreshTopCompanies);
        refreshTopCompanies.setEnabled(false);
        refreshTopCompanies.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // your code

            }
        });
        activityCurrent="top20";
        getSupportActionBar().setTitle("Top 20 Shares");
        myDrawer=(DrawerLayout)findViewById(R.id.drawerCompany);
        myList=findViewById(R.id.topCompanyList);
        mytoggle=new ActionBarDrawerToggle(this,myDrawer,R.string.open,R.string.close){
            public void onDrawerClosed(View view) {
                getSupportActionBar().setTitle("Top 20 Shares");
                myList.setClickable(true);
            }

            public void onDrawerOpened(View drawerView) {
                getSupportActionBar().setTitle("Select Option");
            }
        };

        myDrawer.addDrawerListener(mytoggle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mytoggle.syncState();
        NavigationView navView=findViewById(R.id.navigationCompany);
        navView.setNavigationItemSelectedListener(this);

        myList.setOnTouchListener(new SwipeDetect(){
            @Override
            public void onSwipeLeft() {
                if(tabs.getSelectedTabPosition()==0){
                    tabs.getTabAt(1).select();
                }
                else if (tabs.getSelectedTabPosition()==1){
                    tabs.getTabAt(2).select();
                }
            }

            @Override
            public void onSwipeBottom(){
                if(myList.getFirstVisiblePosition()==0) {
                    if (content.getStatus() != AsyncTask.Status.RUNNING) {
                        if ((connected && allowData)||(connected && onWifi)) {
                            content = new Content(companyList, topCompanyListByTrade, topCompanyListByValue, topCompanyListByVolume, getContext(), refreshTopCompanies,true);
                            refreshTopCompanies.setRefreshing(true);
                            content.execute();
                            try{
                                if(tabNum==0){
                                    tabs.getTabAt(1).select();
                                    tabs.getTabAt(0).select();

                                }
                                else if(tabNum==1){
                                    tabs.getTabAt(2).select();
                                    tabs.getTabAt(1).select();

                                }
                                else if(tabNum==2){
                                    tabs.getTabAt(1).select();
                                    tabs.getTabAt(2).select();

                                }
                            }catch (Exception e){

                            }
                        }

                    }
                }
            }

            @Override
            public void onSwipeRight() {
                if(tabs.getSelectedTabPosition()==2){
                    tabs.getTabAt(1).select();
                }
                else if (tabs.getSelectedTabPosition()==1){
                    tabs.getTabAt(0).select();
                }
                else if(tabs.getSelectedTabPosition()==0){
                    myDrawer.openDrawer(Gravity.LEFT);
                    myList.setClickable(false);
                }
            }
        });

        try{
            tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    if (tab==tabs.getTabAt(0)){
                        if(topCompanyListByVolume==null){
                            topCompanyListByVolume=new ArrayList<Company>();
                            topCompanyListByVolume=loadCompanyData(topCompanyListByVolume,"topCompanyListByVolume");
                        }
                        if(!topCompanyListByVolume.isEmpty()){
                            try{
                                adapterTop=new TopCompanyListAdapter(getContext(), R.layout.adapter_viewtops,topCompanyListByVolume,"byVolume");
                                myList.setAdapter(adapterTop);
                                tabNum=0;
                            }catch (Exception e){

                            }
                        }

                    }
                    else if(tab==tabs.getTabAt(1)){
                        if(topCompanyListByTrade==null){
                            topCompanyListByTrade=new ArrayList<Company>();
                            topCompanyListByTrade=loadCompanyData(topCompanyListByTrade, "topCompanyListByTrade");
                        }
                        if(!topCompanyListByTrade.isEmpty()){
                            try{
                                adapterTop=new TopCompanyListAdapter(getContext(), R.layout.adapter_viewtops,topCompanyListByTrade,"byTrade");
                                myList.setAdapter(adapterTop);
                                tabNum=1;
                            }catch (Exception e){

                            }
                        }

                    }
                    else {
                        if(topCompanyListByValue==null){
                            topCompanyListByValue=new ArrayList<Company>();
                            topCompanyListByValue=loadCompanyData(topCompanyListByVolume, "topCompanyListByVolume");
                        }
                        if(!topCompanyListByValue.isEmpty()){
                            try{
                                adapterTop=new TopCompanyListAdapter(getContext(), R.layout.adapter_viewtops,topCompanyListByValue,"byValue");
                                myList.setAdapter(adapterTop);
                                tabNum=2;
                            }catch (Exception e){

                            }
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

            myDrawer.setOnTouchListener(new SwipeDetect(){
                @Override
                public void onSwipeLeft(){
                    if(myDrawer.isDrawerOpen(GravityCompat.START))myDrawer.closeDrawers();
                }
            });
            //TextView stv=findViewById(R.id.sTv);
            try{
                if(!topCompanyListByVolume.isEmpty()){
                    adapterTop=new TopCompanyListAdapter(this, R.layout.adapter_viewtops,topCompanyListByVolume,"byVolume");
                    myList.setAdapter(adapterTop);
                }

                myList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    public void onItemClick(AdapterView<?> parentAdapter, View view, int position,
                                            long id) {

                        myList.setClickable(false);
                        // We know the View is a <extView so we can cast it
                        try{
                            Company selItem = (Company) adapter.getItem(position);
                            Gson string=new Gson();
                            String json=string.toJson(selItem);
                            Intent a=new Intent(getContext(),CompanyInfo.class);
                            SharedPreferences sharedPreferences=getContext().getSharedPreferences(selItem.getCode(),MODE_PRIVATE);
                            ed = sharedPrefs.edit();
                            new FetchCompanyGraphData(selItem.getCode(),getContext(),formattedDate).execute();
                            ed.putBoolean(selItem.getCode(), true);
                            ed.commit();
                            a.putExtra("company",json);
                            a.putExtra("formatteddate",formattedDate);
                            startActivity(a);
                        }catch (Exception e){

                        }
                        myList.setClickable(true);

                    }
                });
            }catch ( Exception e){

            }

        }catch (Exception e){

        }
        tabs.getTabAt(tabNum).select();

    }




    private void companyListCalled(){

        setContentView(R.layout.activity_company_list);
        coordinatorLayout=findViewById(R.id.coor);
        refreshCompanyList=findViewById(R.id.refreshComopanyList);
        refreshCompanyList.setEnabled(false);
        refreshCompanyList.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // your code

            }
        });
        loadCompanyData(companyList, "companyList");
        activityCurrent="companyList";
        getSupportActionBar().setTitle("Company List");
        myDrawer=(DrawerLayout)findViewById(R.id.drawerCompany);

        myList=findViewById(R.id.companyList);
        mytoggle=new ActionBarDrawerToggle(this,myDrawer,R.string.open,R.string.close){
            public void onDrawerClosed(View view) {
                myList.setClickable(true);
                getSupportActionBar().setTitle("Company List");
            }

            public void onDrawerOpened(View drawerView) {
                getSupportActionBar().setTitle("Select Option");
            }
        };

        myDrawer.addDrawerListener(mytoggle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mytoggle.syncState();
        NavigationView navView=findViewById(R.id.navigationCompany);
        navView.setNavigationItemSelectedListener(this);



        //TextView stv=findViewById(R.id.sTv);
        try {
            if (companyList == null) {
                companyList = new ArrayList<Company>();
                companyList=loadCompanyData(companyList, "companyList");
            }
            if(!companyList.isEmpty()){
                try{
                    adapter = new CompanyListAdapter(this, R.layout.adapter_view, companyList,"companyList");
                    myList.setAdapter(adapter);
                }catch (Exception e){

                }
            }

            myList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                public void onItemClick(AdapterView<?> parentAdapter, View view, int position,
                                        long id) {


                    myList.setClickable(false);
                    // We know the View is a <extView so we can cast it
                    try{
                        Company selItem = (Company) adapter.getItem(position);
                        Gson string=new Gson();
                        String json=string.toJson(selItem);
                        Intent a=new Intent(getContext(),CompanyInfo.class);
                        SharedPreferences sharedPreferences=getContext().getSharedPreferences(selItem.getCode(),MODE_PRIVATE);
                        ed = sharedPrefs.edit();
                        ed.putBoolean(selItem.getCode(), true);
                        ed.commit();
                        a.putExtra("company",json);
                        a.putExtra("formatteddate",formattedDate);
                        startActivity(a);
                    }catch (Exception e){

                    }
                    myList.setClickable(true);
                }
            });
        }catch (Exception e){

        }
        myList.setOnTouchListener(new SwipeDetect(){

            @Override
            public void onSwipeRight() {
                myDrawer.openDrawer(Gravity.LEFT);
                myList.setClickable(false);
            }
            @Override
            public void onSwipeBottom() {

                    if (myList.getFirstVisiblePosition() == 0) {
                        if (content.getStatus() != AsyncTask.Status.RUNNING) {
                            if ((connected && allowData)||(connected && onWifi)) {
                                if (companyList == null) companyList = new ArrayList<Company>();
                                if (topCompanyListByValue == null)
                                    topCompanyListByValue = new ArrayList<Company>();
                                if (topCompanyListByTrade == null)
                                    topCompanyListByTrade = new ArrayList<Company>();
                                if (topCompanyListByVolume == null)
                                    topCompanyListByVolume = new ArrayList<Company>();
                                content = new Content(companyList, topCompanyListByTrade, topCompanyListByValue, topCompanyListByVolume, getContext(), refreshCompanyList,false);
                                refreshCompanyList.setRefreshing(true);
                                try{
                                    content.execute().get();
                                }catch (Exception e){
                                    System.out.println(e.getStackTrace().toString());
                                }

                                adapter = new CompanyListAdapter(getContext(), R.layout.adapter_view, companyList,"companyList");
                                myList.setAdapter(adapter);
                            }

                        }
                    }


                }

        });
        myDrawer.setOnTouchListener(new SwipeDetect(){
            @Override
            public void onSwipeLeft(){
                if(myDrawer.isDrawerOpen(GravityCompat.START))myDrawer.closeDrawers();
            }

        });
        searchText=findViewById(R.id.search);
        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count < before) {
                    // We're deleting char so we need to reset the adapter data
                  try{
                      adapter.resetData();
                  }catch (Exception e){

                  }
                }

                try{
                    adapter.getFilter().filter(s.toString());
                }catch (Exception e){

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }


}

class SwipeDetect implements View.OnTouchListener {

    private final GestureDetector gestureDetector = new GestureDetector(new GestureListener());

    public boolean onTouch(final View view, final MotionEvent motionEvent) {
        return gestureDetector.onTouchEvent(motionEvent);
    }

    private final class GestureListener extends GestureDetector.SimpleOnGestureListener {

        private static final int SWIPE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            boolean result = false;
            try {
                float diffY = e2.getY() - e1.getY();
                float diffX = e2.getX() - e1.getX();
                if (Math.abs(diffX) > Math.abs(diffY)) {
                    if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffX > 0) {
                            onSwipeRight();
                        } else {
                            onSwipeLeft();
                        }
                    }
                } else {
                    if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffY > 0) {
                            onSwipeBottom();
                        } else {
                            onSwipeTop();
                        }
                    }
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
            return result;
        }
    }

    public void onSwipeRight() {
    }

    public void onSwipeLeft() {
    }

    public void onSwipeTop() {
    }

    public void onSwipeBottom() {
    }
}

