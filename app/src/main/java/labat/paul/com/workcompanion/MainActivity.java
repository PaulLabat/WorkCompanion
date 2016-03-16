package labat.paul.com.workcompanion;

import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.File;
import java.util.Date;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.android.AuthActivity;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;

import labat.paul.com.workcompanion.DropBox.DownloadFiles;
import labat.paul.com.workcompanion.DropBox.UploadFiles;
import labat.paul.com.workcompanion.ListMonth.ListActivity;
import labat.paul.com.workcompanion.Preferences.Settings;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private TextView arriveTextView, departTextView, fullLenght;
    private CheckBox halfDay;
    @Nullable
    private Menu menu;

    //DropBox part
    private static final String APP_KEY = "";
    private static final String APP_SECRET = "";

    private static final String ACCOUNT_PREFS_NAME = "prefs";
    private static final String ACCESS_KEY_NAME = "ACCESS_KEY";
    private static final String ACCESS_SECRET_NAME = "ACCESS_SECRET";

    private static final boolean USE_OAUTH1 = false;

    DropboxAPI<AndroidAuthSession> mApi;

    private boolean mLoggedIn;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // We create a new AuthSession so that we can use the Dropbox API.
        AndroidAuthSession session = buildSession();
        mApi = new DropboxAPI<AndroidAuthSession>(session);


        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Button arrive = (Button) findViewById(R.id.arrivee);
        Button depart = (Button) findViewById(R.id.depart);
        arriveTextView = (TextView)findViewById(R.id.date_arrivee);
        departTextView = (TextView)findViewById(R.id.date_depart);
        fullLenght = (TextView)findViewById(R.id.full_lenght_text);
        halfDay = (CheckBox)findViewById(R.id.half_day);

        String [] current = FileManager.getInstance().getCurrentDayToDisplay(this);
        arriveTextView.setText(current[0]);
        departTextView.setText(current[1]);
        fullLenght.setText(current[2]);

        getSupportActionBar().setTitle(DateUtils.getFullDate(System.currentTimeMillis()));

        arrive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (arriveTextView.getText().equals("-")) {
                    Date date = new Date(System.currentTimeMillis());
                    arriveTextView.setText(DateUtils.getTime(date));
                    FileManager.getInstance().saveDateArrivee(getApplicationContext(), date, false);
                }
            }
        });

        depart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (departTextView.getText().equals("-")) {
                    Date date = new Date(System.currentTimeMillis());
                    departTextView.setText(DateUtils.getTime(date));
                    FileManager.getInstance().saveDateDepart(getApplicationContext(), date, false);
                    fullLenght.setText(FileManager.getInstance().getFullLenght());
                    halfDay.setEnabled(false);
                }
            }
        });


        if (!fullLenght.getText().equals("-")){
            halfDay.setEnabled(false);
        }
        halfDay.setChecked(FileManager.getInstance().getIsHalfDay(this, new Date(System.currentTimeMillis())));
        halfDay.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                boolean res = FileManager.getInstance().saveIsHalfDay(getApplicationContext(), new Date(System.currentTimeMillis()), isChecked);
                if (!res) {
                    halfDay.setChecked(false);
                }
            }
        });


        checkAppKeySetup();
        setLoggedIn(mApi.getSession().isLinked());


    }

    @Override
    protected void onResume() {
        super.onResume();
        AndroidAuthSession session = mApi.getSession();

        // The next part must be inserted in the onResume() method of the
        // activity from which session.startAuthentication() was called, so
        // that Dropbox authentication completes properly.
        if (session.authenticationSuccessful()) {
            try {
                // Mandatory call to complete the auth
                session.finishAuthentication();

                // Store it locally in our app for later use
                storeAuth(session);
                setLoggedIn(true);
            } catch (IllegalStateException e) {
                Toast.makeText(this,"Couldn't authenticate with Dropbox:" + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                Log.i(TAG, "Error authenticating", e);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(broadcastReceiver, intentFilter);

    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(broadcastReceiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        this.menu = menu;
        setLoggedIn(mApi.getSession().isLinked());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_list:
                Intent intent = new Intent(this, ListActivity.class);
                startActivity(intent);
                return true;

            case R.id.settings:
                Intent tmp = new Intent(this, Settings.class);
                startActivity(tmp);
                return true;

            case R.id.action_change_day_arriving_time:
                new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        FileManager.getInstance().saveDateArrivee(getApplicationContext(), DateUtils.modifyDateTime(hourOfDay, minute), true);
                    }
                }, DateUtils.getCurrentIntHour(), DateUtils.getCurrentIntMin(), true).show();
                return true;

            case R.id.action_change_day_departure_time:
                new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        FileManager.getInstance().saveDateDepart(getApplicationContext(), DateUtils.modifyDateTime(hourOfDay, minute), true);
                    }
                }, DateUtils.getCurrentIntHour(), DateUtils.getCurrentIntMin(), true).show();
                return true;

            case R.id.dropbox:
                if (mLoggedIn) {
                    logOut();
                } else {
                    // Start the remote authentication
                    if (USE_OAUTH1) {
                        mApi.getSession().startAuthentication(MainActivity.this);
                    } else {
                        mApi.getSession().startOAuth2Authentication(MainActivity.this);
                    }
                }
                return true;

            case R.id.sync:
                File[] list = new File(getFilesDir().getPath()).listFiles();
                if(list != null && list.length > 0){
                    for (int i = 0; i< list.length; i++){
                        UploadFiles upload = new UploadFiles(this, mApi, "/", list[i]);
                        upload.execute();
                    }
                }
                return true;
            case R.id.download:
                DownloadFiles download = new DownloadFiles(this, mApi, getFilesDir().getPath());
                download.execute();
                return true;
            default:
                Log.w(TAG, "Action menu non prise en charge");
        }

        return super.onOptionsItemSelected(item);
    }


    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals("action_refresh")){
                String [] current = FileManager.getInstance().getCurrentDayToDisplay(getApplicationContext());
                arriveTextView.setText(current[0]);
                departTextView.setText(current[1]);
                fullLenght.setText(current[2]);
                halfDay.setEnabled(!fullLenght.getText().equals("-"));
            }
        }
    };

    IntentFilter intentFilter = new IntentFilter("action_refresh");

    private void logOut() {
        // Remove credentials from the session
        mApi.getSession().unlink();

        // Clear our stored keys
        clearKeys();
        // Change UI state to display logged out version
        setLoggedIn(false);
    }

    /**
     * Convenience function to change UI state based on being logged in
     */
    private void setLoggedIn(boolean loggedIn) {
        mLoggedIn = loggedIn;
        if (loggedIn) {
            if(menu != null) {
                menu.findItem(R.id.dropbox).setChecked(true);
            }
        } else {
            if(menu != null) {
                menu.findItem(R.id.dropbox).setChecked(false);
            }
        }
    }


    private void checkAppKeySetup() {
        // Check to make sure that we have a valid app key
        if (APP_KEY.startsWith("CHANGE") ||
                APP_SECRET.startsWith("CHANGE")) {
            Toast.makeText(MainActivity.this, "You must apply for an app key and secret from developers.dropbox.com, and add them to the app before trying it.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Check if the app has set up its manifest properly.
        Intent testIntent = new Intent(Intent.ACTION_VIEW);
        String scheme = "db-" + APP_KEY;
        String uri = scheme + "://" + AuthActivity.AUTH_VERSION + "/test";
        testIntent.setData(Uri.parse(uri));
        PackageManager pm = getPackageManager();
        if (0 == pm.queryIntentActivities(testIntent, 0).size()) {
            Toast.makeText(MainActivity.this,"URL scheme in your app's " +
                    "manifest is not set up correctly. You should have a " +
                    "com.dropbox.client2.android.AuthActivity with the " +
                    "scheme: " + scheme, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Shows keeping the access keys returned from Trusted Authenticator in a local
     * store, rather than storing user name & password, and re-authenticating each
     * time (which is not to be done, ever).
     */
    private void loadAuth(AndroidAuthSession session) {
        SharedPreferences prefs = getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
        String key = prefs.getString(ACCESS_KEY_NAME, null);
        String secret = prefs.getString(ACCESS_SECRET_NAME, null);
        if (key == null || secret == null || key.length() == 0 || secret.length() == 0) return;

        if (key.equals("oauth2:")) {
            // If the key is set to "oauth2:", then we can assume the token is for OAuth 2.
            session.setOAuth2AccessToken(secret);
        } else {
            // Still support using old OAuth 1 tokens.
            session.setAccessTokenPair(new AccessTokenPair(key, secret));
        }
    }

    /**
     * Shows keeping the access keys returned from Trusted Authenticator in a local
     * store, rather than storing user name & password, and re-authenticating each
     * time (which is not to be done, ever).
     */
    private void storeAuth(AndroidAuthSession session) {
        // Store the OAuth 2 access token, if there is one.
        String oauth2AccessToken = session.getOAuth2AccessToken();
        if (oauth2AccessToken != null) {
            SharedPreferences prefs = getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
            SharedPreferences.Editor edit = prefs.edit();
            edit.putString(ACCESS_KEY_NAME, "oauth2:");
            edit.putString(ACCESS_SECRET_NAME, oauth2AccessToken);
            edit.commit();
            return;
        }
        // Store the OAuth 1 access token, if there is one.  This is only necessary if
        // you're still using OAuth 1.
        AccessTokenPair oauth1AccessToken = session.getAccessTokenPair();
        if (oauth1AccessToken != null) {
            SharedPreferences prefs = getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
            SharedPreferences.Editor edit = prefs.edit();
            edit.putString(ACCESS_KEY_NAME, oauth1AccessToken.key);
            edit.putString(ACCESS_SECRET_NAME, oauth1AccessToken.secret);
            edit.commit();
        }
    }

    private void clearKeys() {
        SharedPreferences prefs = getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
        SharedPreferences.Editor edit = prefs.edit();
        edit.clear();
        edit.commit();
    }

    private AndroidAuthSession buildSession() {
        AppKeyPair appKeyPair = new AppKeyPair(APP_KEY, APP_SECRET);

        AndroidAuthSession session = new AndroidAuthSession(appKeyPair);
        loadAuth(session);
        return session;
    }


}
