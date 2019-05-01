package com.example.dstock;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class TopCompanyListAdapter extends ArrayAdapter<Company> implements Filterable {
    private ArrayList<Company> companyList;
    private static final String TAG="CompanyListAdapter";
    private Filter companyFilter;
    private Context mContext;
    private ArrayList<Company> oriList;
    private ArrayList<String> userFavs;
    int mResources;
    private String type;
    public TopCompanyListAdapter(@NonNull Context context, int resource, ArrayList<Company> objects, String type) {
        super(context, resource, objects);
        oriList=objects;
        mContext=context;
        this.type=type;
        companyList=objects;
        mResources=resource;
        userFavs=MainActivity.getUserFavs();
        if ( userFavs ==null ) userFavs=new ArrayList<>();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent){
    try{

        String name=getItem(position).getName();
        String code=getItem(position).getCode();
        double ltp=getItem(position).getLtp();
        double change=getItem(position).getChange();
        double changeP=getItem(position).getChangeP();

        LayoutInflater inflater=LayoutInflater.from(mContext);
        convertView=inflater.inflate(mResources,parent,false);
        TextView tltp=(TextView)convertView.findViewById(R.id.ltp);
        TextView changeText=convertView.findViewById(R.id.changeText);
        TextView tcode=(TextView)convertView.findViewById(R.id.code);
        TextView tchange=(TextView)convertView.findViewById(R.id.change);
        TextView tName=(TextView)convertView.findViewById(R.id.name);
        TextView tradevolumevalue= convertView.findViewById(R.id.tradevolumevalue);
        final ImageButton favBtn=(ImageButton)convertView.findViewById(R.id.favButton);
        favBtn.setFocusable(false);
        if(userFavs!=null&&userFavs.contains(getItem(position).getCode())){
            getItem(position).setFav(true);
        }
        else getItem(position).setFav(false);
        favBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Company myC=getItem(position);
                if(myC.isFav()){
                    favBtn.setBackgroundResource(R.drawable.ic_favorite_no);
                    myC.setFav(false);
                    userFavs.remove(myC.getCode().toString());
                    MainActivity.setFavs(userFavs);
                }
                else if(!myC.isFav()){
                    favBtn.setBackgroundResource(R.drawable.ic_star_black_24dp);
                    myC.setFav(true);
                    userFavs.add(myC.getCode().toString());
                    MainActivity.setFavs(userFavs);
                }
            }
        });
        if(getItem(position).isFav()){
            favBtn.setBackgroundResource(R.drawable.ic_star_black_24dp);
        }
        else if(!getItem(position).isFav()){
            favBtn.setBackgroundResource(R.drawable.ic_favorite_no);
        }
        tcode.setText("("+code+")");
        if(code.startsWith("UP")){
            System.out.println(code);
        }

        if(change<0){
            tchange.setTextColor(Color.RED);
            changeText.setTextColor(Color.RED);
        }
        else{
            tchange.setTextColor(Color.GREEN);
            changeText.setTextColor(Color.GREEN);
        }

        if(type=="companyList"){
            tchange.setText(String.valueOf(changeP+"%"));
            tltp.setText("LTP: "+ String.valueOf(ltp));
            tName.setText(String.valueOf(name));
        }
        else if(type=="byTrade"){

            tchange.setText(String.valueOf(changeP+"%"));
            tltp.setText("LTP: "+ String.valueOf(ltp));
            tName.setText(String.valueOf(name));
            tradevolumevalue.setText("Trades: "+String.valueOf(getItem(position).getTrade()));
        }
        else if(type=="byVolume"){


            tchange.setText(String.valueOf(changeP+"%"));
            tltp.setText("LTP: "+ String.valueOf(ltp));
            tName.setText(String.valueOf(name));
            tradevolumevalue.setText("Volume: "+String.valueOf(getItem(position).getVolume()));
        }
        else if(type=="byValue"){

            tchange.setText(String.valueOf(changeP+"%"));
            tltp.setText("LTP: "+ String.valueOf(ltp));
            tName.setText(String.valueOf(name));
            tradevolumevalue.setText("Value: "+String.valueOf(getItem(position).getValue()));
        }

    }catch (Exception e){

    }
return convertView;
    }
    public int getCount() {
        return companyList.size();
    }

    public Company getItem(int position) {
        return companyList.get(position);
    }

    public long getItemId(int position) {

        return companyList.get(position).hashCode();
    }
    public void resetData() {
        companyList = oriList;
    }




    /*
     * We create our filter
     */

    @Override
    public Filter getFilter() {
        if (companyFilter == null)
            companyFilter = new CompanyFilter();

        return companyFilter;
    }



    private class CompanyFilter extends Filter {


        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            // We implement here the filter logic
            if (constraint == null || constraint.length() == 0) {
                // No filter implemented we return all the list
                results.values = oriList;
                results.count = oriList.size();
            }
            else {
                // We perform filtering operation
                ArrayList<Company> nPlanetList = new ArrayList<Company>();

                for (Company p : companyList) {
                    if (p.getName().toLowerCase().startsWith(constraint.toString()))
                        nPlanetList.add(p);
                }

                results.values = nPlanetList;
                results.count = nPlanetList.size();

            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {

            // Now we have to inform the adapter about the new list filtered
            if (results.count == 0)
                notifyDataSetInvalidated();
            else {
                companyList = (ArrayList<Company>) results.values;
                notifyDataSetChanged();
            }

        }

    }
}
