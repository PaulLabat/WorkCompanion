package labat.paul.com.workcompanion.ListMonth;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import labat.paul.com.workcompanion.R;
import labat.paul.com.workcompanion.VisualizeMonth.VisualizeMonth;


public class ListMonthsAdapter extends RecyclerView.Adapter<ListMonthsAdapter.ViewHolder>{

    private final Context context;
    private List<String> listeMois;

    public ListMonthsAdapter(@NonNull Context context){
        super();
        this.context = context;
        listeMois = new ArrayList<>();

        Collections.addAll(listeMois, context.fileList());
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_months_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String tmp = listeMois.get(position);
        tmp = tmp.replace(".json", "");
        holder.dateMonth.setText(tmp);
        holder.cardView.setTag(R.string.tag_cardview, listeMois.get(position));
    }

    @Override
    public int getItemCount() {
        return listeMois.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView dateMonth;
        public CardView cardView;

        public ViewHolder(View itemView) {
            super(itemView);
            dateMonth = (TextView)itemView.findViewById(R.id.month);
            cardView = (CardView)itemView.findViewById(R.id.card_view);
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, VisualizeMonth.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("fileName", v.getTag(R.string.tag_cardview).toString());
                    context.startActivity(intent);
                }
            });
        }
    }
}