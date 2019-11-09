package INITIATIVES_ADAPTER;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.daniel.liquid_democracy.R;

import com.example.daniel.liquid_democracy.ItemClickListener;

public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public View mView;
    ItemClickListener itemClickListener;
    public TextView userID;

    public ViewHolder(View itemView) {
        super(itemView);

        mView = itemView;
        itemView.setOnClickListener(this);
        userID = itemView.findViewById(R.id.username);


    }


    public void setUser(Context ctx, String user) {
        userID.setText(user);
    }

    public void setDetails(Context ctx, String number, String description) {

        TextView initNumber = mView.findViewById(R.id.initiative_number);
        TextView initDescription = mView.findViewById(R.id.initiative_description);

        initNumber.setText(number);
        initDescription.setText(description);
    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v, getAdapterPosition());
    }
}
