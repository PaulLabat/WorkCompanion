package labat.paul.com.workcompanion;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Pair;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

import labat.paul.com.workcompanion.ListMonth.ListActivity;

public class MainActivity extends AppCompatActivity {

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
                    Timestamp stamp = new Timestamp(System.currentTimeMillis());
                    Date date = new Date(stamp.getTime());
                    String text;
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(date);
                    text = String.valueOf(calendar.get(Calendar.HOUR_OF_DAY));
                    text += ":" + String.valueOf(calendar.get(Calendar.MINUTE));
                    arriveTextView.setText(text);
                    FileManager.getInstance().saveDateArrivee(getApplicationContext(), date);
                }
            }
        });

        depart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (departTextView.getText().equals("-")) {
                    Timestamp stamp = new Timestamp(System.currentTimeMillis());
                    Date date = new Date(stamp.getTime());
                    String text;
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(date);
                    text = String.valueOf(calendar.get(Calendar.HOUR_OF_DAY));
                    text += ":" + String.valueOf(calendar.get(Calendar.MINUTE));
                    departTextView.setText(text);
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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_list) {
            Intent intent = new Intent(this, ListActivity.class);
            startActivity(intent);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
