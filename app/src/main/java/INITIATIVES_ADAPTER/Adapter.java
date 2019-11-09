package INITIATIVES_ADAPTER;


import android.content.Context;
import android.graphics.BitmapFactory;
import android.support.annotation.LayoutRes;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.daniel.liquid_democracy.R;

import java.util.ArrayList;

import MAIN_CLASSES.Group;
import MAIN_CLASSES.Initiative;

/** populate view from arraylist
 *
 */
public class Adapter extends ArrayAdapter<Initiative>{
    //extends BaseAdapter{

    Context c;
    ArrayList<Initiative> initiatives;

    public Adapter(Context c, @LayoutRes int resource, ArrayList<Initiative> initiatives){
        super(c, resource, initiatives);
        this.c=c;
        this.initiatives=initiatives;

    }

    /*public void onClick(View v) {
        int position=(Integer) v.getTag();
        Object object= getItem(position);
        Group temp=(Group) object;

        switch (v.getId())
        {
            case R.id.groupimage:
                Snackbar.make(v, "Name of the group is " +temp.getName(), Snackbar.LENGTH_LONG)
                        .setAction("No action", null).show();
                break;
        }
    }*/


    @Override
    public int getCount() {
        return initiatives.size();
    }

    /* @Override
     public Object getItem(int position) {
         return groups.get(position);
     }
 */
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) c.getSystemService(c.LAYOUT_INFLATER_SERVICE);
        convertView= inflater.inflate(R.layout.initiative_row,parent,false);

        TextView init_nr = convertView.findViewById(R.id.initiative_number);
        TextView init_description = convertView.findViewById(R.id.initiative_description);
        Log.d("VIEW position", String.valueOf(position));
       // Log.d("VIEW name",groups.get(position).getName());

        //set text and image of the group
        init_nr.setText(initiatives.get(position).getID());
        init_description.setText(initiatives.get(position).getDescription());

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(c,initiatives.get(position).getID(),Toast.LENGTH_SHORT).show();
            }
        });

        return convertView;
    }
}
