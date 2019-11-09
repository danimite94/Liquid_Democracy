package VOTATION_ADAPTER;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.daniel.liquid_democracy.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import MAIN_CLASSES.Initiative;
import butterknife.BindView;
import butterknife.ButterKnife;

public class VotationActivity extends AppCompatActivity implements Listener {

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mRefInit, mRefVot,mRefActions;
    private String issueID;
    private Integer initID;
    private String userID;
    @BindView(R.id.rvTop)
    RecyclerView rvTop;
    @BindView(R.id.rvBottom)
    RecyclerView rvBottom;
    @BindView(R.id.tvEmptyListTop)
    TextView tvEmptyListTop;
    @BindView(R.id.tvEmptyListBottom)
    TextView tvEmptyListBottom;
    @BindView(R.id.vote_button)
    Button vote_button;
    ListAdapter topListAdapter;
    ListAdapter bottomListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //TODO check whether the votation phase has already ended. if yes, new intent(MainActivity.class)
        setContentView(R.layout.activity_votation);
        ButterKnife.bind(this);

        //TODO get issueID,userID from previous activity
        issueID = "-LRIsjB3ZaBfJ3DP1lrl";
        userID = "Alfa Centuri";
        //init firebase instances
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRefInit = mFirebaseDatabase.getReference().child("Initiatives");
        mRefInit.keepSynced(true);
        mRefVot = mFirebaseDatabase.getReference().child("Votation");
        mRefVot.keepSynced(true);
        mRefActions = mFirebaseDatabase.getReference().child("Actions");
        mRefActions.keepSynced(true);

        initTopRecyclerView();
        initBottomRecyclerView();

        tvEmptyListTop.setVisibility(View.GONE);
        tvEmptyListBottom.setVisibility(View.GONE);


        vote_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                //Considering all the rejected initiatives with the same category "-1"?
                for (int i=0; i<rvBottom.getAdapter().getItemCount(); i++) {
                    Log.d("checker each element id", String.valueOf(bottomListAdapter.getList().get(i).getID()));
                    initID = Integer.valueOf(bottomListAdapter.getList().get(i).getID());
                    //get positions for each ID/description element in rvBottom
                    //TODO consider ALSO random id of initiative
                    Integer neg_i = -1;
                    mRefVot.child(issueID).child(String.valueOf(initID)).child(userID).setValue(neg_i);
                    //TODO remove all databases regarding this issue in delegate, support, oppose
                    //save data in firebase
                }

                for (int i=0; i<rvTop.getAdapter().getItemCount(); i++) {
                    Log.d("checker each element id", String.valueOf(topListAdapter.getList().get(i).getID()));
                    initID = Integer.valueOf(topListAdapter.getList().get(i).getID());
                    //get positions for each ID/description element in rvBottom
                    //TODO consider ALSO random id of initiative
                    mRefVot.child(issueID).child(String.valueOf(initID)).child(userID).setValue(i+1);
                    //save data in firebase
                }

                mRefActions.child("Issues").child(issueID).child(userID).setValue("voted");

                //go to next activity
                //Intent intent = new Intent(VotationActivity.this, FeedActivity.class);
                //startActivity(intent);
            }
        });

    }

    private void initTopRecyclerView() {
        rvTop.setLayoutManager(new LinearLayoutManager(
                this, LinearLayoutManager.VERTICAL, false));

        //topList.add("A");
        //topList.add("B");
        final ArrayList<Initiative> topList = new ArrayList<>();

        //connect to firebase to add all remaining initiatives to recyclerview
        /*mRefInit.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Change members value to true
                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    Log.d("checker", ds.child("ID").getValue().toString());
                    String initID = ds.child("ID").getValue().toString();
                    String description = ds.child("description").getValue().toString();

                    init.setID(initID);
                    init.setDescription(description);

                    topList.add(init);

                    //only add initiatives that arrived this last stage of votation
                    //TODO
                    if (ds.child("ID").getValue().equals("description")) {
                        Log.d("checkernew2", ds.getValue().toString());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/


        topListAdapter = new ListAdapter(topList, this);
        rvTop.setAdapter(topListAdapter);
        tvEmptyListTop.setOnDragListener(topListAdapter.getDragInstance());
        rvTop.setOnDragListener(topListAdapter.getDragInstance());
    }

    private void initBottomRecyclerView() {
        rvBottom.setLayoutManager(new LinearLayoutManager(
                this, LinearLayoutManager.VERTICAL, false));

        //topList.add("A");
        //topList.add("B");

        //connect to firebase to add all remaining initiatives to recyclerview
        final ArrayList<Initiative> bottomList = new ArrayList<>();

        mRefInit.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Change members value to true
                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    String initID = ds.child("ID").getValue().toString();
                    String description = ds.child("description").getValue().toString();
                    Initiative init = new Initiative();


                    init.setID(Integer.valueOf(initID));
                    init.setDescription(description);

                    bottomList.add(init);
                    Log.d("checker2", bottomList.get(0).getDescription());


                    //only add initiatives that arrived this last stage of votation
                    //TODO
                    if (ds.child("ID").getValue().equals("description")) {
                        Log.d("checkernew2", ds.getValue().toString());
                    }
                }
                Log.d("checker4", String.valueOf(bottomList.get(0).getID()));
                Log.d("checker5", String.valueOf(bottomList.get(1).getID()));

                //Log.d("checker3", bottomList.get(0).getDescription());
                bottomListAdapter = new ListAdapter(bottomList, VotationActivity.this);
                rvBottom.setAdapter(bottomListAdapter);
                tvEmptyListBottom.setOnDragListener(bottomListAdapter.getDragInstance());
                rvBottom.setOnDragListener(bottomListAdapter.getDragInstance());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }


        });

    }

    @Override
    public void setEmptyListTop(boolean visibility) {
        tvEmptyListTop.setVisibility(visibility ? View.VISIBLE : View.GONE);
        rvTop.setVisibility(visibility ? View.GONE : View.VISIBLE);
    }

    @Override
    public void setEmptyListBottom(boolean visibility) {
        tvEmptyListBottom.setVisibility(visibility ? View.VISIBLE : View.GONE);
        rvBottom.setVisibility(visibility ? View.GONE : View.VISIBLE);
    }
}