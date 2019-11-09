package ISSUES_ADAPTER;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.daniel.liquid_democracy.ItemClickListener;
import com.example.daniel.liquid_democracy.R;
import com.squareup.picasso.Picasso;

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

    public void setIssue(Context c, String name, String url) {
        TextView issuename = v.findViewById(R.id.issue_description);
        ImageView groupimage = v.findViewById(R.id.image_group);

        Picasso.get().load(url).into(groupimage);
        issuename.setText(name);
    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v,getAdapterPosition());
    }
}
