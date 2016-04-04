package labat.paul.com.workcompanion;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {

    @NonNull
    public static String getFullDate(@NonNull Long l){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(l));
        int tmpInt = calendar.get(Calendar.DAY_OF_MONTH);
        String tmp;

        if(tmpInt < 10){
            tmp = "0" + String.valueOf(tmpInt) + "-";
        }else{
            tmp = String.valueOf(tmpInt) + "-";
        }

        tmpInt = calendar.get(Calendar.MONTH)+1;
        if(tmpInt < 10){
            tmp += "0" + String.valueOf(tmpInt);
        }else{
            tmp += String.valueOf(tmpInt);
        }
        tmp += "-";
        tmp += String.valueOf(calendar.get(Calendar.YEAR));
        return tmp;


    }

    @NonNull
    public static String calculateFullLenght(@NonNull Long date1, @NonNull Long date2, @NonNull Context context){
        FileManager fileManager = new FileManager(context);
        String time = getLunchTime(context);

        int tmpHour = Integer.parseInt(time.split(":")[0]);
        int tmpMin = Integer.parseInt(time.split(":")[1]);

        long milisec = date1 - date2;


        int hours =(int) milisec / (1000 * 60 * 60);
        int mins =(int) milisec / (60 * 1000) % 60;

        int total;
        if(fileManager.getIsHalfDay(new Date(date1))){
            total = hours * 60 + mins;
        }else{
            total = hours * 60 + mins - tmpHour * 60 - tmpMin;
        }

        hours = total / 60;
        mins = total % 60;

        String hoursS, minsS;
        if (hours < 10){
            hoursS = "0"+String.valueOf(hours);
        }else{
            hoursS = String.valueOf(hours);
        }

        if (mins < 10){
            minsS = "0"+String.valueOf(mins);
        }else{
            minsS = String.valueOf(mins);
        }

        return hoursS+":"+minsS;
    }

    @NonNull
    public static String getTime(@NonNull Long date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(date));
        String hour = String.valueOf(calendar.get(Calendar.HOUR_OF_DAY));
        hour += ":";
        if(calendar.get(Calendar.MINUTE) < 10){
            hour += "0";
        }
        hour += String.valueOf(calendar.get(Calendar.MINUTE));
        return hour;

    }
    @NonNull
    public static String getTime(@NonNull Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        String hour = String.valueOf(calendar.get(Calendar.HOUR_OF_DAY));
        hour += ":";
        if(calendar.get(Calendar.MINUTE) < 10){
            hour += "0";
        }
        hour += String.valueOf(calendar.get(Calendar.MINUTE));
        return hour;

    }

    @NonNull
    public static String getDay(@NonNull Date date) {
        String fileName;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        fileName = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
        fileName += "-" + String.valueOf(calendar.get(Calendar.MONTH)+1);
        fileName += "-" + String.valueOf(calendar.get(Calendar.YEAR));
        return fileName;
    }


    @NonNull
    public static String getLunchTime(@NonNull Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String lunchTime = preferences.getString("lunch_time", "2");
        String [] values = context.getResources().getStringArray(R.array.choice_length_values);
        String [] time = context.getResources().getStringArray(R.array.choice_length);

        int i;
        for(i = 0; i< values.length; i++){
            if(lunchTime.equals(values[i])){
                break;
            }
        }
        return time[i];
    }


    @NonNull
    public static Date modifyDateTime(@NonNull Integer hour, @NonNull Integer min){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(System.currentTimeMillis()));
        calendar.set(Calendar.MINUTE, min);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        return calendar.getTime();
    }

    @NonNull
    public static Integer getCurrentIntHour(){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(System.currentTimeMillis()));
        return calendar.get(Calendar.HOUR_OF_DAY);
    }
    @NonNull
    public static Integer getCurrentIntMin(){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(System.currentTimeMillis()));
        return calendar.get(Calendar.MINUTE);
    }

}
