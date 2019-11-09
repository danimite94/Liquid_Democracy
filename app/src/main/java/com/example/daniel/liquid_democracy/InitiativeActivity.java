package com.example.daniel.liquid_democracy;

import android.app.Dialog;
import android.app.FragmentTransaction;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import GROUPS_ADAPTER.GroupsActivity;
import GROUPS_ADAPTER.MembersActivity;
import INITIATIVES_ADAPTER.ViewHolder;
import MAIN_CLASSES.Group;
import MAIN_CLASSES.Initiative;
import SUGGESTIONS_ADAPTER.Main_suggestion_creator;
import butterknife.internal.Utils;
import okhttp3.internal.Util;

public class InitiativeActivity extends AppCompatActivity {

    Button back;
    RecyclerView list;
    RecyclerView.LayoutManager layoutManager;
    private String initID, userID, issueID;
    private Intent data;
    private static final int MY_REQUEST = 1001;
    private TextView initiative_number, initiative_description;
    private DatabaseReference mRefOppose;
    private DatabaseReference mRefUsers;
    private DatabaseReference mRefDeleg;
    private DatabaseReference mRefActions;
    private DatabaseReference mRefSug;
    private DatabaseReference mRefInit_sug;
    private DatabaseReference mRefInit;
    private DatabaseReference mRefFollow;
    private DatabaseReference mRefSupport;
    private FirebaseDatabase mFirebaseDatabase;
    private FrameLayout fragmentContainer;
    private Boolean mLike;
    private Button create_suggestion;
    private Button support;
    private Button oppose;

    Intent intent;
    private String description;

    private String oldaction;
    private String delegateID;
    private Button search;
    private Button editInit;
    private String initowner;
    private String phase;
    private Button saveBtn;
    private EditText descriptionTxt;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initiative);

        //init variables
        back = findViewById(R.id.back_button);
        create_suggestion = findViewById(R.id.create_suggestion);
        initiative_number = findViewById(R.id.initiative_number);
        initiative_description = findViewById(R.id.initiative_description);
        support = findViewById(R.id.support);
        oppose = findViewById(R.id.against);
        fragmentContainer = findViewById(R.id.fragment_container);
        search = findViewById(R.id.google_button);
        editInit = findViewById(R.id.edit_inititiative);

        //set recycler view of suggestions
        //Initialize recycler view with all initiatives
        list = findViewById(R.id.recycler);

        //swipe horizontally the recycler view elements
        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(list);

        list.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        list.setLayoutManager(layoutManager);

        //set initiative and user number
        intent = getIntent();
        //initID = intent.getStringExtra("ID");
        Bundle b = intent.getExtras();
        if (b.get("ID") != null) {
            initID = (String) b.get("ID");
            userID = (String) b.get("userID");
            issueID = (String) b.get("issueID");
            phase = (String) b.get("phase");
            Log.d("checker init1", initID);
        } else { //for debuggind purposes
            userID = "Foden";
            initID = "new";
            issueID = "-LRIsjB3ZaBfJ3DP1lrl";
            phase = "admission";
            Log.d("checker init2", initID);
        }

        if (phase == "admission" && Util.equal(userID, initowner)) {
            editInit.setEnabled(true);
        } else if (phase == "discussion" && Util.equal(userID, initowner)) {
            editInit.setEnabled(true);
        } else if (phase == "verification") {
            editInit.setEnabled(false);

        }

        //set textview with initiative ID
        initiative_number.setText(initID);

        //Database connection
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRefSug = mFirebaseDatabase.getReference("Suggestions");
        mRefInit = mFirebaseDatabase.getReference("Initiatives");
        mRefUsers = mFirebaseDatabase.getReference("Users");
        mRefInit_sug = mFirebaseDatabase.getReference("Initiatives").child(initID).child("Suggestions");
        mRefActions = mFirebaseDatabase.getReference().child("Action");
        mRefSupport = mFirebaseDatabase.getReference().child("Support");
        mRefDeleg = mFirebaseDatabase.getReference().child("Delegate");
        mRefOppose = mFirebaseDatabase.getReference().child("Oppose");
        mRefFollow = mFirebaseDatabase.getReference().child("Follow");

        //Persistence of data
        mRefSug.keepSynced(true);
        mRefInit.keepSynced(true);
        mRefInit_sug.keepSynced(true);
        mRefUsers.keepSynced(true);

        mRefFollow.keepSynced(true);
        mRefActions.keepSynced(true);
        mRefDeleg.keepSynced(true);
        mRefOppose.keepSynced(true);
        mRefSupport.keepSynced(true);

        //set description
        mRefInit.child(initID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Change members value to true
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Log.d("checker", ds.getKey());
                    if (ds.getKey().equals("description")) {
                        Log.d("checkernew2", ds.getValue().toString());
                        initiative_description.setText(ds.getValue().toString());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // create suggestion button
        create_suggestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFragment(userID, initID);
            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InitiativeActivity.this, GoogleSearchFragment.class);
                startActivityForResult(intent, 1);
            }
        });

        Log.d("checker", "chega aos botoes");

        // (un)support initiative
        support.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //real time DB requires this boolean to stop it from processing again,again....

                //TODO check for status of issue. If status is admission, compare supporters of init with quorum_admission
                //and if supporters reached quorum, change status to "discussion". send notification on this
                //and cancel alarmmanager set for passage admission=>discussion
                mRefActions.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        //remove support from initiative
                        if (Util.equal(dataSnapshot.child("Initiatives").child(initID).child(userID).getValue(), "support")) {
                            //user has acted upon current initiative in the past, so...
                            //TODO  support.setBackgroundResource(R.mipmap.black);
                            Log.d("checker support", dataSnapshot.child("Initiatives").child(initID).getKey().toString());

                            //remove child with userID from actions DB (issues)
                            mRefActions.child("Issues").child(issueID).child(userID).removeValue();
                            mRefActions.child("Initiatives").child(initID).child(userID).removeValue();

                            //remove child with user ID from supporters DB
                            mRefSupport.child(initID).child(userID).removeValue();

                        } else {
                            Log.d("checker", "entrou else");
                            //TODO  support.setBackgroundResource(R.mipmap.red);
                            //check if userID has already participated in this initiative
                            if (dataSnapshot.child("Initiatives").child(initID).child(userID).exists()) {
                                Log.d("checker", "entrou support");

                                //get what the action was before: support || oppose || {number} || 0 (abstinent) || delegate || follow
                                oldaction = dataSnapshot.child("Initiatives").child(initID).child(userID).getValue().toString();
                                updateDSOADatabase(initID, userID, oldaction);
                                Log.d("checker", oldaction);

                            }
                            //check if user had already followed or delegated issue
                            else if (dataSnapshot.child("Issues").child(issueID).child(userID).exists()) {
                                //get what the action was before: support || oppose || {number} || 0 (abstinent) || delegate || follow
                                oldaction = dataSnapshot.child("Issues").child(issueID).child(userID).getValue().toString();
                                updateDSOADatabase(issueID, userID, oldaction);
                                Log.d("checker", oldaction);

                            }
                            Log.d("checker issueID", issueID);

                            //update actions DB
                            //Either the initiative didnt exist or the user had not participated actively
                            mRefActions.child("Initiatives").child(initID).child(userID).setValue("support");
                            mRefActions.child("Issues").child(issueID).child(userID).setValue("active");

                            //insert child with user ID to support DB
                            mRefSupport.child(initID).child(userID).setValue(true);

                        }
                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

        oppose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mRefActions.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot ds) {

                        //remove support from initiative
                        if (Util.equal(ds.child("Initiatives").child(initID).child(userID).getValue(), "oppose")) {
                            //user has acted upon current initiative in the past, so...
                            //TODO  oppose.setBackgroundResource(R.mipmap.black);

                            //remove child with userID from actions DB
                            mRefActions.child("Initiatives").child(initID).child(userID).removeValue();
                            mRefActions.child("Issues").child(issueID).child(userID).removeValue();

                            //remove child with user ID from opposers DB
                            mRefOppose.child(initID).child(userID).removeValue();

                        } else {
                            //TODO  oppose.setBackgroundResource(R.mipmap.red);
                            //check if userID has already participated in this initiative
                            if (ds.child("Initiatives").child(initID).child(userID).exists()) {

                                //get what the action was before: support || oppose || {number} || 0 (abstinent) || delegate || follow
                                oldaction = ds.child("Initiatives").child(initID).child(userID).getValue().toString();
                                updateDSOADatabase(initID, userID, oldaction);
                            } else if (ds.child("Issues").child(issueID).child(userID).exists()) {
                                //get what the action was before: support || oppose || {number} || 0 (abstinent) || delegate || follow
                                oldaction = ds.child("Issues").child(issueID).child(userID).getValue().toString();
                                updateDSOADatabase(issueID, userID, oldaction);
                                Log.d("checker", oldaction);

                            }
                            //update actions DB
                            //Either the initiative didnt exist or the user had not participated actively
                            mRefActions.child("Initiatives").child(initID).child(userID).setValue("oppose");
                            mRefActions.child("Issues").child(issueID).child(userID).setValue("active");

                            //insert child with user ID to support DB
                            mRefOppose.child(initID).child(userID).setValue(true);

                        }
                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent = new Intent();

                //go back to issue activity
                //setResult(RESULT_OK, intent);
                Log.d("checker", "finito");
                //notifyDataSetChanged();
                finish();
            }
        });


        //TODO dialogbox to edit initiative
        //retrieve initowner in firebase
        //initowner=
        editInit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editInit();
            }
        });

    }

    public void onStart() {
        super.onStart();

        Log.d("checker2", "passou1");
        FirebaseRecyclerOptions<Boolean> options =
                new FirebaseRecyclerOptions.Builder<Boolean>()
                        .setQuery(mRefInit_sug, Boolean.class)
                        .build();
        Log.d("checker", " recycler options done ");

        FirebaseRecyclerAdapter<Boolean, ViewHolder> fra = new FirebaseRecyclerAdapter<Boolean, ViewHolder>(
                options) {
            @Override
            protected void onBindViewHolder(@NonNull final ViewHolder holder, final int position, @NonNull final Boolean model) {

                //Log.d("checkernew1", model.getInitiatives().toString());

                //call parent node
                final String rowinitID = getRef(position).getKey();
                Log.d("checker3", rowinitID);
                mRefSug.child(rowinitID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //Change members value to true
                        Log.d("checker", dataSnapshot.getKey());
                        if (dataSnapshot.hasChild("text")) {
                            Log.d("checkernew2", dataSnapshot.getValue().toString());
                            description = dataSnapshot.child("text").getValue().toString();
                            holder.setDetails(getApplicationContext(), rowinitID, description);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                Log.d("checker", description + "boy");

            }

            @NonNull
            @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.initiative_row, parent, false);

                return new ViewHolder(view);
            }
        };
        fra.startListening();
        list.setAdapter(fra);
    }

    public void saveInitdescription(String description) {

        try {
            mRefInit.child(initID).child("description").setValue(description);
            Toast.makeText(this, "Initiative edited", Toast.LENGTH_SHORT).show();

        } catch (DatabaseException e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();

        }

    }

    //TODO edit initiative with corresponding xml
    public void editInit() {

        final Dialog d = new Dialog(this);
        d.setTitle("Changed mind?");
        d.setContentView(R.layout.editinit_dialog);
        //helper = new FirebaseHelper(mRef);

        saveBtn = d.findViewById(R.id.saveBtn);
        descriptionTxt = d.findViewById(R.id.descriptionTxt);

        //show text from firebase
        mRefInit.child(initID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                descriptionTxt.setText(dataSnapshot.child("description").getValue().toString());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //TODO verify if description is being saved in groups firebase
                //TODO invite proposed members and wait for their confirmal to have them as new members in the groups

                saveInitdescription(descriptionTxt.getText().toString());

            }
        });

        descriptionTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                //TODO ADD String that stores edittext value?

            }
        });

        d.show();

    }

    //update database of either Delegations || Support || Oppose || Abstinence
    public void updateDSOADatabase(String occurrenceID, String userID, String oldaction) {

        if (oldaction.equals("support")) {
            mRefSupport.child(occurrenceID).child(userID).removeValue();

        } else if (oldaction.equals("oppose")) {
            mRefOppose.child(occurrenceID).child(userID).removeValue();

        } else if (oldaction.equals("follow")) {
            mRefFollow.child(occurrenceID).child(userID).removeValue();

        } else if (oldaction.equals("delegate")) {
            mRefDeleg.child(occurrenceID).child(userID).removeValue();

        } else {
            Log.d("checker", "fodeu");
        }

    }

    public void openFragment(String userID, String initID) {

        //set arguments to send to fragment
        Bundle args = new Bundle();
        args.putString("user", userID);
        args.putString("init", initID);

        Main_suggestion_creator fragment = new Main_suggestion_creator();
        fragment.setArguments(args);

        //fragmentContainer.setId(R.id.fragment_mainsuggestioncreator);
        //setContentView(fragmentContainer);

        //getSupportFragmentManager().beginTransaction().add(1,fragment).commit();
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container, fragment, "SUGGESTION_CREATOR");
        transaction.addToBackStack(null);
        transaction.commit();

    }

}
