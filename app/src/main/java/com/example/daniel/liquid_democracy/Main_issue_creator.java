package com.example.daniel.liquid_democracy;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import Adapter.Adapter;
import INITIATIVES_ADAPTER.IssueActivity;
import MAIN_CLASSES.Initiative;
import MAIN_CLASSES.Issue;
import MAIN_CLASSES.Item;

public class Main_issue_creator extends AppCompatActivity {
    //implements ItemClickListener {
    private static final int MY_REQUEST = 1001;
    RecyclerView list;
    RecyclerView.LayoutManager layoutManager;
    List<Item> items = new ArrayList<>();
    FirebaseAuth mAuth;
    public static String currentUserID, groupID;
    Integer firstquorum, secondquorum;
    public static Issue issuetobesaved;
    public static Initiative firstinit;

    private DatabaseReference mRef;
    Context context;
    private Intent intent;
    public Button nextBtn;
    Adapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainissuecreator);

        list = findViewById(R.id.recycler);
        nextBtn = findViewById(R.id.next_button);

        //create context
        context = Main_issue_creator.this;

        //initialize static variables
        issuetobesaved = new Issue();
        firstinit = new Initiative();

        intent = getIntent();
        //initID = intent.getStringExtra("ID");
        Bundle b = intent.getExtras();
        if (b.get("userID") != null) {
            currentUserID = (String) b.get("userID");

        } else { //degub purposes
            currentUserID = "-LP8-f0bsmowXhMtStSB";
        }

        //mAuth.getCurrentUser().getUid();
        firstinit.setOwner(currentUserID);
        issuetobesaved.setModerator(currentUserID);


        //Database
        mRef = FirebaseDatabase.getInstance().getReference();

        //swipe horizontally the recycler view elements
        //SnapHelper snapHelper = new PagerSnapHelper();
        //snapHelper.attachToRecyclerView(list);

        list.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this){
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        list.setLayoutManager(layoutManager);

        setIssue();

    }

    private void setIssue() {

        for (int i = 0; i < 4; i++) {
            Log.d("CHECKER position", String.valueOf(i));

            Item item = new Item();
            item.setType(i);
            items.add(item);
        }

        adapter = new Adapter(items,nextBtn,layoutManager);
        list.setAdapter(adapter);
    }

   /* @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // ... Check for some data from the intent
        if (requestCode == MY_REQUEST) {
            //update view with image uploaded from groups
            adapter.notifyDataSetChanged();

            //call onactivityresult from adapter
            adapter.onActivityResult(requestCode, resultCode, data);

            //button is visible to create issue
            createIssue.setVisibility(View.VISIBLE);
            createIssue.setEnabled(true);
        }
    }*/

}
