package com.example.daniel.liquid_democracy;


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

import java.util.ArrayList;

import MAIN_CLASSES.Group;

/** populate view from arraylist
 *
 */
public class ListAdapter extends ArrayAdapter<Group>{
        //extends BaseAdapter{

    Context c;
    ArrayList<Group> groups;

    public ListAdapter(Context c, @LayoutRes int resource, ArrayList<Group> groups){
        super(c, resource, groups);
        this.c=c;
        this.groups=groups;

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
        return groups.size();
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
        convertView= inflater.inflate(R.layout.activity_listitems,parent,false);

        TextView nameTxt= convertView.findViewById(R.id.groupname);
        ImageView image = convertView.findViewById(R.id.groupimage);
        Log.d("VIEW position", String.valueOf(position));
        Log.d("VIEW name",groups.get(position).getName());

        //set text and image of the group
        nameTxt.setText(groups.get(position).getName());
        byte[] decodedBytes = Base64.decode(groups.get(position).getImage(), 0);
        image.setImageBitmap(BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length));

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(c,groups.get(position).getName(),Toast.LENGTH_SHORT).show();
            }
        });

        return convertView;
    }
}
