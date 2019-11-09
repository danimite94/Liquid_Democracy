package CREATE_ISSUES_ADAPTER;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.daniel.liquid_democracy.ItemClickListener;
import com.example.daniel.liquid_democracy.R;

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

    public void setPolicy(Context c, String name, int admission_time, int discussion_time, int quorum_admission, int quorum_verification) {
        //TextView policyname = v.findViewById(R.id.pol);
        //policyname.setText(name);

        TextView quorum_admission_txt = v.findViewById(R.id.quorum_admission);
        quorum_admission_txt.setText(Integer.toString(quorum_admission));

        TextView quorum_verification_txt = v.findViewById(R.id.quorum_verification);
        quorum_verification_txt.setText(Integer.toString(quorum_verification));

        TextView time_admission_txt = v.findViewById(R.id.admission_time);
        time_admission_txt.setText(Integer.toString(admission_time));

        TextView time_discussion_txt = v.findViewById(R.id.discussion_time);
        time_discussion_txt.setText(Integer.toString(discussion_time));
    }


    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v,getAdapterPosition());
    }
}
