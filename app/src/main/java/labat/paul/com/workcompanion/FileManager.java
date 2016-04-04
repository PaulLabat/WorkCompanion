package labat.paul.com.workcompanion;


import android.content.Context;
import android.content.Intent;
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
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class FileManager {

    private final String TAG = getClass().getName();
    private final String DATE_ARRIVEE = "date_arrivee";

    private static final String DATE_DEPART = "date_depart";
    private static final String DUREE_TOTAL = "duree_total";
    private static final String DAY = "day_date";
    private static final String HALF_DAY = "half_day";

    @NonNull
    private Context context;

    @Nullable
    private String fullLenght;

    public FileManager(@NonNull Context context){
        this.context = context;
        Log.d(TAG, "Constructor");
    }

    public void saveDateArrivee(@NonNull Date date, boolean modify) {
        String fileName = getFileName(date);

        String dayDate = DateUtils.getDay(date);

        if (checkIfFileExist(date)) {
            //File exist
            Log.d(TAG, "file exist");
            JSONArray jsonArray = getJSONArray(fileName);
            boolean exist = false;
            if (jsonArray != null) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    try {
                        if (dayDate.equals(jsonArray.getJSONObject(i).getString(DAY))) {
                            try {
                                jsonArray.getJSONObject(i).get(DATE_ARRIVEE);
                                exist = true;
                                Log.d(TAG, "La date d'arrivée existe déjà !");
                                if (modify){
                                    jsonArray.getJSONObject(i).remove(DATE_ARRIVEE);
                                    jsonArray.getJSONObject(i).put(DATE_ARRIVEE, date.getTime());
                                    try {
                                        jsonArray.getJSONObject(i).get(DUREE_TOTAL);
                                        jsonArray.getJSONObject(i).remove(DUREE_TOTAL);
                                        String fullLenghtTemp = DateUtils.calculateFullLenght(jsonArray.getJSONObject(i).getLong(DATE_DEPART),date.getTime(), context);
                                        jsonArray.getJSONObject(i).put(DUREE_TOTAL, fullLenghtTemp);
                                        fullLenght = fullLenghtTemp;

                                    }catch (JSONException e){
                                        // n'existe pas, ne fait rien
                                    }

                                }
                                break;
                            } catch (JSONException e) {
                                Log.d(TAG, "date_arrivee does not exist");
                                break;
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                if (!exist) {
                    //if day not saved yet
                    JSONObject currentDay = new JSONObject();
                    try {
                        currentDay.put(DAY, dayDate);
                        currentDay.put(DATE_ARRIVEE, date.getTime());
                        jsonArray.put(currentDay);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                if (!exist || modify) {
                    //writing in file
                    saveEntry(fileName, jsonArray);
                    if (modify){
                        context.sendBroadcast(new Intent("action_refresh"));
                    }
                }
            }


        } else {
            Log.d(TAG, "file does not exist, Creating it...");
            File file = new File(context.getFilesDir(), fileName);
            try {
                boolean res = file.createNewFile();
                Log.d(TAG, "file created : " + res);
                if (res) {
                    JSONArray data = new JSONArray();
                    JSONObject day = new JSONObject();
                    try {
                        day.put(DAY, dayDate);
                        day.put(DATE_ARRIVEE, date.getTime());
                        data.put(0, day);

                        Log.d(TAG, data.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    //writing in file
                   saveEntry(fileName, data);
                }

            } catch (IOException e) {
                Log.e(TAG, "Érreur à la création du fichier");
                e.printStackTrace();
            }
        }


    }

    public void saveDateDepart(@NonNull Date date, boolean modify) {

        String fileName = getFileName(date);

        if(checkIfFileExist(date)) {

            //Read file
            JSONArray jsonArray = getJSONArray(fileName);
            if (jsonArray != null) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    try {
                        if (DateUtils.getDay(date).equals(jsonArray.getJSONObject(i).getString(DAY))) {
                            try {
                                jsonArray.getJSONObject(i).getLong(DATE_DEPART);
                                Log.d(TAG, "La date de départ existe déjà !");
                                if (modify) {
                                    jsonArray.getJSONObject(i).remove(DATE_DEPART);
                                    jsonArray.getJSONObject(i).remove(DUREE_TOTAL);
                                    jsonArray.getJSONObject(i).put(DATE_DEPART, date.getTime());
                                    String fullLenghtTemp = DateUtils.calculateFullLenght(date.getTime(),
                                            jsonArray.getJSONObject(i).getLong(DATE_ARRIVEE), context);
                                    jsonArray.getJSONObject(i).put(DUREE_TOTAL, fullLenghtTemp);
                                    fullLenght = fullLenghtTemp;

                                }

                                break;
                            } catch (JSONException e) {
                                Log.d(TAG, "date depart n'existe pas, writing it");
                                jsonArray.getJSONObject(i).put(DATE_DEPART, date.getTime());
                                String fullLenghtTemp = DateUtils.calculateFullLenght(date.getTime(),
                                        jsonArray.getJSONObject(i).getLong(DATE_ARRIVEE), context);
                                jsonArray.getJSONObject(i).put(DUREE_TOTAL, fullLenghtTemp);
                                fullLenght = fullLenghtTemp;
                                break;
                            }

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                //writing in file
                saveEntry(fileName, jsonArray);
                if (modify) {
                    context.sendBroadcast(new Intent("action_refresh"));
                }
            }
        }else{
            Log.e(TAG, "fichier non existant : " + fileName);
        }
    }


    public boolean saveIsHalfDay(@NonNull Date date, boolean isHalfDay){
        String fileName = getFileName(date);

        if(checkIfFileExist(date)) {

            //Read file
            JSONArray jsonArray = getJSONArray(fileName);

            if (jsonArray != null) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    try {
                        if (DateUtils.getDay(date).equals(jsonArray.getJSONObject(i).getString(DAY))) {
                            try {
                                jsonArray.getJSONObject(i).getBoolean(HALF_DAY);
                                //si la donnée existe
                                jsonArray.getJSONObject(i).remove(HALF_DAY);
                                jsonArray.getJSONObject(i).put(HALF_DAY, isHalfDay);

                                break;
                            } catch (JSONException e) {
                                Log.d(TAG, "date depart n'existe pas, writing it");
                                jsonArray.getJSONObject(i).put(HALF_DAY, isHalfDay);
                                break;
                            }

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                //writing in file
                saveEntry(fileName, jsonArray);
                return true;
            }else{
                return false;
            }

        }else{
            return false;
        }
    }

    public boolean getIsHalfDay(@NonNull Date date) {
        String fileName = getFileName(date);

        if (checkIfFileExist(date)) {

            //Read file
            JSONArray jsonArray = getJSONArray(fileName);

            if (jsonArray != null) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    try {
                        if (DateUtils.getDay(date).equals(jsonArray.getJSONObject(i).getString(DAY))) {
                            try {
                                return jsonArray.getJSONObject(i).getBoolean(HALF_DAY);
                            } catch (JSONException e) {
                                return false;
                            }

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return false;
    }


    private boolean checkIfFileExist(@NonNull Date date) {
        File file = new File(context.getFilesDir(), getFileName(date));
        return file.exists();
    }

    @NonNull
    private String getFileName(@NonNull Date date) {
        String fileName;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        fileName = String.valueOf(calendar.get(Calendar.YEAR));
        if(calendar.get(Calendar.MONTH) < 10) {
            fileName += "-0" + String.valueOf(calendar.get(Calendar.MONTH)+1);
        }else {
            fileName += "-" + String.valueOf(calendar.get(Calendar.MONTH)+1);
        }
        fileName += ".json";
        return fileName;
    }

    @Nullable
    private JSONArray getJSONArray(@NonNull String fileName){
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
            Log.e(TAG, "Could not parse json file: " + fileName);
            jsonArray = null;
        }
        return jsonArray;
    }

    private void saveEntry(@NonNull String fileName, @NonNull JSONArray data){
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

    @NonNull
    public String[] getCurrentDayToDisplay() {
        String dateBegin = "-", dateEnd = "-", fullLenght = "-";


        Timestamp stamp = new Timestamp(System.currentTimeMillis());
        Date date = new Date(stamp.getTime());

        if (checkIfFileExist(date)) {

            //create json to parse
            JSONArray jsonArray = getJSONArray(getFileName(date));
            if (jsonArray != null) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    try {
                        if (DateUtils.getDay(date).equals(jsonArray.getJSONObject(i).getString(DAY))) {
                            try {
                                dateBegin = DateUtils.getTime(jsonArray.getJSONObject(i).getLong(DATE_ARRIVEE));
                                dateEnd = DateUtils.getTime(jsonArray.getJSONObject(i).getLong(DATE_DEPART));
                                fullLenght = jsonArray.getJSONObject(i).getString(DUREE_TOTAL);
                            } catch (JSONException e) {
                                Log.w(TAG, "date non trouvee");
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

        }

        return new String[]{dateBegin, dateEnd, fullLenght};
    }

    @Nullable
    public String getFullLenght(){
        return fullLenght;
    }
}
