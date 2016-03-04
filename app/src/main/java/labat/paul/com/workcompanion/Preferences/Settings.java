package labat.paul.com.workcompanion.Preferences;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class Settings extends AppCompatActivity{
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();

    }
}
