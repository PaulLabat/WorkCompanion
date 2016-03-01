package labat.paul.com.workcompanion.ListMonth;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import labat.paul.com.workcompanion.ListMonth.ListMonthsAdapter;
import labat.paul.com.workcompanion.R;


public class ListActivity extends AppCompatActivity {

    private RecyclerView listView;
    private ListMonthsAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_activity);

        getSupportActionBar().setTitle("");

        listView = (RecyclerView)findViewById(R.id.list);
        listView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getApplicationContext());
        listView.setLayoutManager(mLayoutManager);
        mAdapter = new ListMonthsAdapter(getApplicationContext());
        listView.setAdapter(mAdapter);

    }
}
