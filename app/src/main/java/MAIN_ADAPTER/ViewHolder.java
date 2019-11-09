package MAIN_ADAPTER;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.daniel.liquid_democracy.ItemClickListener;
import com.example.daniel.liquid_democracy.R;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    View v;
    ItemClickListener itemClickListener;

    public ViewHolder(View itemView) {
        super(itemView);
        v = itemView;
        itemView.setOnClickListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener){
        this.itemClickListener = itemClickListener;
    }

    public void setIssue(Context c, String issue_number, String time_dayshours) {
        TextView issuenumber = v.findViewById(R.id.issue_number);
        TextView time = v.findViewById(R.id.time);

        time.setText(time_dayshours);
        issuenumber.setText(issue_number);
    }


    @Override
    public void onClick(View v) {
        Log.d("CHECK", String.valueOf(getAdapterPosition()));
        itemClickListener.onClick(v,getAdapterPosition());
    }
}
