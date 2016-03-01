package labat.paul.com.workcompanion.VisualizeMonth;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import labat.paul.com.workcompanion.R;

public class VisualizeMonth extends AppCompatActivity{

    private RecyclerView listView;
    private VisualizeMonthAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualize_month);

        Bundle bundle = getIntent().getExtras();
        String fileName = bundle.getString("fileName");

        getSupportActionBar().setTitle(fileName.replace(".json", ""));

        listView = (RecyclerView)findViewById(R.id.list);
        listView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getApplicationContext());
        listView.setLayoutManager(mLayoutManager);
        mAdapter = new VisualizeMonthAdapter(fileName, getApplicationContext());
        listView.setAdapter(mAdapter);


    }
}
