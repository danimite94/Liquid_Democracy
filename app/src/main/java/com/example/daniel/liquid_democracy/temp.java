package com.example.daniel.liquid_democracy;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class temp extends AppCompatActivity {


    Button back;

    private String initID;
    private Intent data;
    private static final int MY_REQUEST = 1001;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_postslist);

        back = findViewById(R.id.back_button);

        // create suggestion button

        // create (un)support initiative

        //description

        //date

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();

                //go back to issue activity
                setResult(RESULT_OK, intent);

                //notifyDataSetChanged();
                finish();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // ... Check for some data from the intent
        if (requestCode == MY_REQUEST) {
            //update view with image uploaded from groups
            initID = data.getStringExtra("ID");
            Log.d("checker name", initID);

        }
    }

}

