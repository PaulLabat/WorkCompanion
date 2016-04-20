package labat.paul.com.workcompanion.Preferences;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.Calendar;

import labat.paul.com.workcompanion.R;
import labat.paul.com.workcompanion.UI.TimePickerPreference;


public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener{

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String lunchTime = sharedPref.getString("lunch_time", "2");
        ListPreference lunch = (ListPreference)findPreference("lunch_time");
        lunch.setValue(lunchTime);
        lunch.setSummary(lunch.getEntry());
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(key.equals("lunch_time")){
            ListPreference lunch = (ListPreference)findPreference(key);
            lunch.setSummary(lunch.getEntry());
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("lunch_time", lunch.getValue());
            editor.apply();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);

    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);

    }
}
