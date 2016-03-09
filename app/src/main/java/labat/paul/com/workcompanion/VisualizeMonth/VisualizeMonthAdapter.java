package labat.paul.com.workcompanion.VisualizeMonth;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import labat.paul.com.workcompanion.DateUtils;
import labat.paul.com.workcompanion.R;

public class VisualizeMonthAdapter extends RecyclerView.Adapter<VisualizeMonthAdapter.ViewHolder>{

    private static final String DATE_ARRIVEE = "date_arrivee";

    private static final String DATE_DEPART = "date_depart";
    private static final String DUREE_TOTAL = "duree_total";
    private static final String DAY = "day_date";


    private List<DataDay> dataDayList;

    private String fileName;

    private Context context;

    public VisualizeMonthAdapter(@NonNull String filename, @NonNull Context context){
        super();
        dataDayList = new ArrayList<>();
        this.fileName = filename;
        this.context = context;

        FileInputStream inputStream;
        String tmp = "";

        try {
            inputStream = context.openFileInput(fileName);
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, "utf8"));
            String str;
            while ((str = br.readLine()) != null) {
                tmp += str;
            }
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


        //create json to parse
        JSONArray jsonArray;
        try {
            jsonArray = new JSONArray(tmp);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(getClass().getName(), "Could not parse json file: " + fileName);
            jsonArray = null;
        }
        if (jsonArray != null) {
            for (int i = 0; i < jsonArray.length(); i++) {
                DataDay dataDay = new DataDay();
                try {

                    dataDay.setDate(jsonArray.getJSONObject(i).getString(DAY));
                    dataDay.setDate_arrivee(DateUtils.getTime(jsonArray.getJSONObject(i).getLong(DATE_ARRIVEE)));
                    dataDay.setDate_depart(DateUtils.getTime(jsonArray.getJSONObject(i).getLong(DATE_DEPART)));
                    dataDay.setDuree(jsonArray.getJSONObject(i).getString(DUREE_TOTAL));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                dataDayList.add(dataDay);
            }
        }

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.visualize_month_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        DataDay dataDay = dataDayList.get(position);
        holder.arrivee.setText(String.valueOf(dataDay.getDate_arrivee()));
        holder.depart.setText(String.valueOf(dataDay.getDate_depart()));
        holder.duree.setText(dataDay.getDuree());
        holder.day.setText(dataDay.getDate());
    }

    @Override
    public int getItemCount() {
        return dataDayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView duree, depart, arrivee, day;

        public ViewHolder(View itemView) {
            super(itemView);
            duree = (TextView)itemView.findViewById(R.id.duree);
            depart = (TextView)itemView.findViewById(R.id.date_depart);
            arrivee = (TextView)itemView.findViewById(R.id.date_arrivee);
            day = (TextView)itemView.findViewById(R.id.day);
        }
    }
}
