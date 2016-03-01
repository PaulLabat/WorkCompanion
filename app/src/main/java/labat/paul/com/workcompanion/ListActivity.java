package labat.paul.com.workcompanion;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ListView;


public class ListActivity extends AppCompatActivity {

    private RecyclerView listView;
    private ListMonthsAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_activity);
        listView = (RecyclerView)findViewById(R.id.list);
        listView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getApplicationContext());
        listView.setLayoutManager(mLayoutManager);
        mAdapter = new ListMonthsAdapter(getApplicationContext());
        listView.setAdapter(mAdapter);

    }
}
