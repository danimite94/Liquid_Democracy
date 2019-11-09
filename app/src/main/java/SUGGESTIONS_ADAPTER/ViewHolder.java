package SUGGESTIONS_ADAPTER;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.daniel.liquid_democracy.R;

import com.example.daniel.liquid_democracy.ItemClickListener;

public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    View mView;
    ItemClickListener itemClickListener;

    public ViewHolder(View itemView) {
        super(itemView);

        mView = itemView;
        itemView.setOnClickListener(this);

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
