package labat.paul.com.workcompanion;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

import labat.paul.com.workcompanion.ListMonth.ListActivity;
import labat.paul.com.workcompanion.Preferences.Settings;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private TextView arriveTextView, departTextView, fullLenght;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Button arrive = (Button) findViewById(R.id.arrivee);
        Button depart = (Button) findViewById(R.id.depart);
        arriveTextView = (TextView)findViewById(R.id.date_arrivee);
        departTextView = (TextView)findViewById(R.id.date_depart);
        fullLenght = (TextView)findViewById(R.id.full_lenght_text);

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
                }
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(broadcastReceiver, intentFilter);

    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
            }
        }
    };

    IntentFilter intentFilter = new IntentFilter("action_refresh");


}
