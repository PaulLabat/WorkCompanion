package labat.paul.com.workcompanion;


import android.support.annotation.NonNull;

import java.util.Calendar;
import java.util.Date;

public class DateUtils {

    @NonNull
    public static String getFullDate(@NonNull Long l){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(l));
        int tmpInt = calendar.get(Calendar.DAY_OF_MONTH);
        String tmp="";

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
    public static String calculateFullLenght(@NonNull Long date1, @NonNull Long date2){
        long milisec = date1 - date2;
        long hours = milisec / (1000 * 60 * 60);
        long mins = milisec / (60 * 1000) % 60;
        String hoursS, minsS;
        if (hours < 10){
            hoursS = "0"+String.valueOf(hours);
        }else{
            hoursS = String.valueOf(hours);
        }

        if (hours < 10){
            minsS = "0"+String.valueOf(mins);
        }else{
            minsS = String.valueOf(mins);
        }

        return hoursS+":"+minsS;
    }

    @NonNull
    public static String getHour(@NonNull Long date){
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
    public static String getHour(@NonNull Date date){
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

}
