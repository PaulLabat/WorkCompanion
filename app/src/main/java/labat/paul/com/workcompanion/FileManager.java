package labat.paul.com.workcompanion;


import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.Date;

public class FileManager {

    private final String TAG = getClass().getName();
    private final String DATE_ARRIVEE = "date_arrivee";

    private static final String DATE_DEPART = "date_depart";
    private static final String DUREE_TOTAL = "duree_total";
    private static final String DATA = "data";
    private static final String DAY = "day_date";

    private static FileManager ourInstance = new FileManager();

    public static FileManager getInstance() {
        return ourInstance;
    }

    public void saveDateArrivee(@NonNull Context context, @NonNull Date date){

        String fileName = getFileName(date);

        if(checkIfFileExist(context, date)) {
            //File exist
            Log.d(TAG, "file exist");
            FileInputStream inputStream;
            String tmp="";

            try {
                inputStream = context.openFileInput(fileName);
                BufferedReader br = new BufferedReader(new InputStreamReader(inputStream,"utf8"));
                String str;
                while ((str = br.readLine()) != null) {
                    tmp += str;
                }
                inputStream.close();
            }catch (IOException e) {
                e.printStackTrace();
            }
            //create json to parse
            JSONArray jsonArray;
            try {
                jsonArray = new JSONArray(tmp);
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e(TAG, "Could not parse json file: " + fileName);
                jsonArray = null;
            }
            boolean exist = false;
            if(jsonArray != null){
                for (int i = 0; i < jsonArray.length(); i++){
                    try {
                        if(getDay(date).equals(jsonArray.getJSONObject(i).getString(DAY))){
                            try{
                                jsonArray.getJSONObject(i).get(DATE_ARRIVEE);
                                exist = true;
                                Log.d(TAG, "date arrivee exist deja");
                            }catch (JSONException e){
                                Log.e(TAG, "date_arrivee does not exist");
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                if(!exist) {
                    //if day not saved yet
                    JSONObject currentDay = new JSONObject();
                    try {
                        currentDay.put(DAY, getDay(date));
                        currentDay.put(DATE_ARRIVEE, date.getTime());
                        jsonArray.put(currentDay);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                if(!exist) {
                    //writing in file
                    FileOutputStream outputStream;

                    try {
                        outputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE);
                        outputStream.write(jsonArray.toString().getBytes());
                        outputStream.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(context, "An error occured while saving the entry", Toast.LENGTH_LONG).show();
                    }
                }
            }



        }else{
            Log.d(TAG, "file does not exist, Creating it...");
            File file =new File(context.getFilesDir(), fileName);
            try {
                boolean res = file.createNewFile();
                Log.d(TAG, "file created : " + res);
                if(res){
                    JSONArray data = new JSONArray();
                    JSONObject day = new JSONObject();
                    try {
                        day.put(DAY, getDay(date));
                        day.put(DATE_ARRIVEE, date.getTime());
                        data.put(0, day);

                        Log.d(TAG, data.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    //writing in file
                    FileOutputStream outputStream;

                    try {
                        outputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE);
                        outputStream.write(data.toString().getBytes());
                        outputStream.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(context, "An error occured while saving the entry", Toast.LENGTH_LONG).show();
                    }


                }

            } catch (IOException e) {
                Log.d(TAG, "error while creating file");

                e.printStackTrace();
            }
        }


    }


    public void saveDateDepart(@NonNull Context context, @NonNull Date date){

        String fileName = getFileName(date);
        //Read file
        FileInputStream inputStream;
        String tmp="";

        try {
            inputStream = context.openFileInput(fileName);
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream,"utf8"));
            String str;
            while ((str = br.readLine()) != null) {
                tmp += str;
            }
            inputStream.close();
        }catch (IOException e) {
            e.printStackTrace();
        }
        //create json to parse
        JSONArray jsonArray;
        try {
            jsonArray = new JSONArray(tmp);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "Could not parse json file: " + fileName);
            jsonArray = null;
        }
        if(jsonArray != null){
            for (int i = 0; i < jsonArray.length(); i++){
                try {
                    if(getDay(date).equals(jsonArray.getJSONObject(i).getString(DAY))){
                        try{
                            jsonArray.getJSONObject(i).getLong(DATE_DEPART);
                            Log.d(TAG, "date depart exist deja");
                        }catch (JSONException e){
                            Log.d(TAG, "date depart n'existe pas, writing it");
                            jsonArray.getJSONObject(i).put(DATE_DEPART, date.getTime());
                            long milisec = date.getTime() - jsonArray.getJSONObject(i).getLong(DATE_ARRIVEE);
                            long hours = milisec / (1000 * 60 * 60);
                            long mins = milisec / (60 * 1000) % 60;
                            jsonArray.getJSONObject(i).put(DUREE_TOTAL, hours + ":" + mins);
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            //writing in file
            FileOutputStream outputStream;

            try {
                outputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE);
                outputStream.write(jsonArray.toString().getBytes());
                outputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(context, "An error occured while saving the entry", Toast.LENGTH_LONG).show();
            }
        }
    }


    private boolean checkIfFileExist(@NonNull Context context, @NonNull Date date){
        File file = new File(context.getFilesDir(), getFileName(date));
        return file.exists();
    }


    private String getFileName(@NonNull Date date){
        String fileName;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        fileName = String.valueOf(calendar.get(Calendar.YEAR));
        fileName += "-"+String.valueOf(calendar.get(Calendar.MONTH));
        fileName += ".json";
        return fileName;


    }


    private String getDay(@NonNull Date date){
        String fileName;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        fileName = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
        fileName += "-"+String.valueOf(calendar.get(Calendar.MONTH));
        return fileName;
    }


}
