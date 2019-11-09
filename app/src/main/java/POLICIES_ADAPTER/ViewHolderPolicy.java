package POLICIES_ADAPTER;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.daniel.liquid_democracy.ItemClickListener;
import com.example.daniel.liquid_democracy.R;
import com.squareup.picasso.Picasso;

public class ViewHolderPolicy extends RecyclerView.ViewHolder implements View.OnClickListener{

    View v;
    ItemClickListener itemClickListener;

    public ViewHolderPolicy(View itemView) {
        super(itemView);
        v = itemView;
        itemView.setOnClickListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener){
        this.itemClickListener = itemClickListener;
    }

    public void setIssue(Context c, String name) {
        TextView issuename = v.findViewById(R.id.issuename);
        issuename.setText(name);
    }

    public void setDetails(Context c, String name, String description, String url){
        TextView groupname = v.findViewById(R.id.nameTxt);
        TextView descriptionTxt = v.findViewById(R.id.descriptionTxt);
        ImageView groupimage = v.findViewById(R.id.rImageView);

        descriptionTxt.setText(description);
        groupname.setText(name);
        Picasso.get().load(url).into(groupimage);
    }

    @Override
    public void onClick(View v) {
        Log.d("CHECK", String.valueOf(getAdapterPosition()));
        itemClickListener.onClick(v,getAdapterPosition());
    }
}
