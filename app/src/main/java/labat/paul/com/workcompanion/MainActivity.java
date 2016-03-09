package labat.paul.com.workcompanion;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
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
                    FileManager.getInstance().saveDateArrivee(getApplicationContext(), date);
                }
            }
        });

        depart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (departTextView.getText().equals("-")) {
                    Date date = new Date(System.currentTimeMillis());
                    departTextView.setText(DateUtils.getTime(date));
                    FileManager.getInstance().saveDateDepart(getApplicationContext(), date);
                    fullLenght.setText(FileManager.getInstance().getFullLenght());
                }
            }
        });


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
            default:
                Log.w(TAG, "Action menu non prise en charge");
        }

        return super.onOptionsItemSelected(item);
    }


}
