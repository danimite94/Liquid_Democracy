package INITIATIVES_ADAPTER;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.daniel.liquid_democracy.InitiativeActivity;
import com.example.daniel.liquid_democracy.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Downloader;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

//import INITIATIVES_ADAPTER.ViewHolder;
import ISSUES_ADAPTER.MainActivity;
import MAIN_ADAPTER.FeedActivity;
import MAIN_CLASSES.Initiative;
import MAIN_CLASSES.Item;
import MAIN_CLASSES.Policy;
import VOTATION_ADAPTER.ScoresActivity;
import VOTATION_ADAPTER.VotationActivity;
import okhttp3.internal.Util;

public class IssueActivity extends AppCompatActivity {
    private Button optionsButton;
    private Handler handler = new Handler();

    RecyclerView list;
    RecyclerView.LayoutManager layoutManager;
    List<Item> items = new ArrayList<>();
    DatabaseReference mRefIssue, mRefInit, mRefIssues;
    FirebaseDatabase mFirebaseDatabase;
    List<Initiative> initiatives = new ArrayList<>();

    //list of initiatives that are included in an issue
    List<String> initiatives_inissues = new ArrayList<>();

    int row_index = -1; //default no row chosen

    TextView issue_number;
    private static final int MY_REQUEST = 1001;

    private String TAG = "issue checker";
    private String issueID, userID;
    private String initIDinissue;
    private String initID;
    private String description;
    private Button delegateButton, createInitButton;
    private Button follow;
    private DatabaseReference mRefDeleg;
    private DatabaseReference mRefActions;
    private DatabaseReference mRefFollow;
    private DatabaseReference mRefSupport;
    private DatabaseReference mRefOppose;
    private DatabaseReference mRefPolicies, mRefGroups;
    private String oldaction;
    private String delegateID, groupID, policyID;
    private ArrayList<String> initiativesID;
    private ArrayList<String> membersID, active_membersID;
    private Intent intent;
    private long diff_phases;
    private Date currentTime;
    private ProgressBar phasesprogBar;
    private ProgressBar quorumprogBar;
    private String creation_date;
    private int allsupporters, supporters;
    private ArrayList<String> inits;
    private HashMap<String, Integer> quorum_supporters;
    private ArrayList<Integer> quorum_allsupporters;
    private Thread t;
    private Policy policy;
    private Integer admission_time, discussion_time, verification_time, voting_time, quorum_admission, quorum_verification;
    private Integer untilvotingTime;
    private Integer maxquorum, progress, phase_progress;
    private ArrayList<String> VotationInits;
    private String phase;
    private SimpleDateFormat sdf;
    private Date startDateValue;
    private String votingDate, verificationDate, admissionDate, discussionDate, scoreDate;
    private Button saveBtn;
    private EditText descriptionTxt;
    private String Delegate;
    private int number_delegations ;
    private int numberInits;
    private String time, status;
    private TextView time_view, issue_stage_view;
    private long stage_time, time_gone;
    private Date change_status_date;
    private Date current_time_date;
    private Boolean clicked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_issue);

        time_view = findViewById(R.id.time);
        issue_number = findViewById(R.id.issue_number);
        delegateButton = findViewById(R.id.delegate);
        createInitButton = findViewById(R.id.create_init);
        follow = findViewById(R.id.follow);
        issue_stage_view = findViewById(R.id.issue_stage);
        optionsButton = findViewById(R.id.optionsButton);

        phasesprogBar = findViewById(R.id.phase);
        quorumprogBar = findViewById(R.id.quorum);
        quorumprogBar.setMax(100);
        phasesprogBar.setMax(100);

        clicked = false;
        intent = getIntent();
        //initID = intent.getStringExtra("ID");
        Bundle b = intent.getExtras();
        if (b.get("userID") != null) {
            userID = (String) b.get("userID");
            issueID = (String) b.get("issueID");
            //TODO change to regular check from current java class
            time= (String) b.get("time");
        } else { //for debuggind purposes
            userID = "-LP8-f0bsmowXhMtStSB";
            issueID = "-LSMOE2zTM0yhU2DsEwp";
            time= "03:45";
        }

        //TODO display voters, participants,
        //Database connection
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRefInit = mFirebaseDatabase.getReference("Initiatives");
        mRefActions = mFirebaseDatabase.getReference().child("Action");
        mRefDeleg = mFirebaseDatabase.getReference().child("Delegate");
        mRefFollow = mFirebaseDatabase.getReference().child("Follow");
        mRefSupport = mFirebaseDatabase.getReference().child("Support");
        mRefOppose = mFirebaseDatabase.getReference().child("Oppose");
        mRefPolicies = mFirebaseDatabase.getReference().child("Policies");
        mRefGroups = mFirebaseDatabase.getReference().child("Groups");

        //Persistence of data
        mRefInit.keepSynced(true);
        mRefFollow.keepSynced(true);
        mRefActions.keepSynced(true);
        mRefDeleg.keepSynced(true);

        //check status of progress of issue (voting phase intent only starts if the checkpoints have been
        //successfully passed
        current_time_date = Calendar.getInstance().getTime();
        policy = new Policy();
        mRefIssues = mFirebaseDatabase.getReference("Issues");
        time_view.setText(time);

        //connect to database and ask for the specific issue's initiatives
        mRefIssue = mFirebaseDatabase.getReference("Issues").child(issueID).child("Initiatives");

        //VotationInits = new ArrayList<>();

        active_membersID = new ArrayList<>();
        membersID = new ArrayList<>();

        //collect all the initiatives from the regarded issue
        //list all initiatives in issue

        /*
        Log.d("checker date", mRefIssues.child(issueID).child("currentDate").getKey());
        //check for ruling policy and difference in time (begin-now)
        mRefIssues.child(issueID).child("currentDate").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //convert creation date from string to date object

                creation_date = dataSnapshot.getValue().toString();
                startDateValue = StringtoDate(creation_date);
                Log.d("checker after time ", creation_date);

                //difference in days between creation and current phase
                diff_phases = TimeUnit.DAYS.toHours(currentTime.getTime() - startDateValue.getTime());

                mRefIssues.child(issueID).child("policies").addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            policyID = ds.getKey();
                        }
                        Log.d("checker policy", policyID);
                        policy = getPolicy(policyID);
                        Log.d("checker policy admission time", policy.getAdmission_time().toString());
                        untilvotingTime = policy.getAdmission_time() + policy.getDiscussion_time() + policy.getVerification_time();
                        //13
                        votingDate = addDate(creation_date, untilvotingTime);
                        //2018-12-03
                        Log.d("checker initiate voting date", votingDate);

                        discussionDate = addDate(creation_date, policy.getAdmission_time());
                        verificationDate = addDate(creation_date, policy.getAdmission_time() + policy.getDiscussion_time());
                        scoreDate = addDate(creation_date,policy.getAdmission_time() + policy.getDiscussion_time() + policy.getVerification_time() + policy.getVoting_time());


                        if (currentTime.after(StringtoDate(discussionDate)) && maxquorum < policy.getQuorum_admission()) {
                            //1st quorum not achieved
                            //TODO change status of issue to "undone" in firebase
                            Intent intent = new Intent(IssueActivity.this, MainActivity.class);
                            startActivity(intent);
                        } else if (currentTime.before(StringtoDate(discussionDate)) && maxquorum > policy.getQuorum_admission()) { //admission max quorum has been reached and discussion starts before planned
                            currentTime = StringtoDate(addDate(creation_date, policy.getAdmission_time()));
                            phase = "discussion";
                        } else if (currentTime.before(StringtoDate(discussionDate)) && maxquorum < policy.getQuorum_admission()) {
                            phase = "admission";
                        } else if (currentTime.before(StringtoDate(verificationDate))) {
                            phase = "discussion";
                        } else if (currentTime.before(StringtoDate(votingDate))) {
                            phase = "verification";
                        } else if (!VotationInits.isEmpty() && currentTime.after(StringtoDate(votingDate))) { //Votation phase
                            Intent intent = new Intent(IssueActivity.this, VotationActivity.class);
                            intent.putExtra("InitiativesID", VotationInits);
                            startActivity(intent);
                        } else if (VotationInits.isEmpty() && currentTime.after(StringtoDate(votingDate))) { //didnt reach votation phase
                            //TODO change status of issue to "undone" in firebase
                            Intent intent = new Intent(IssueActivity.this, MainActivity.class);
                            startActivity(intent);
                        }else if (currentTime.after(StringtoDate(scoreDate))){
                            Intent intent = new Intent(IssueActivity.this, ScoresActivity.class);
                            intent.putExtra("issueID",issueID);
                            startActivity(intent);

                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //count participants from each initiative (that belongs to the regarded issue)
        mRefActions.child("Initiatives").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                quorum_allsupporters = new ArrayList<>();
                quorum_supporters = new HashMap<>();

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String initiative = ds.getKey();
                    Log.d("checker init", ds.getKey());
                    allsupporters = 0;
                    supporters = 0;
                    if (inits.contains(initiative)) {
                        Log.d("checker inside", " issues that matter");

                        for (DataSnapshot dsinit : ds.getChildren()) {
                            String action = (String) ds.getValue(); //eg. support or oppose or suggest
                            Delegate = ds.getKey();

                            if (Util.equal(action, "support")) {
                                supporters = supporters + 1;
                                allsupporters = allsupporters + 1;
                                //TODO considering 2x the vote if delegations have been made in verification quorum
                                mRefDeleg.child(issueID).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        //check whether supporting participant has been delegated with
                                        if (dataSnapshot.hasChild(Delegate)) {
                                            number_delegations = (int) dataSnapshot.child(Delegate).getChildrenCount();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                            } else if (!Util.equal(action, "oppose")) {
                                allsupporters = allsupporters + 1;
                            }

                            //TODO consider supporters with suggestions only once
                            Log.d("checker participant", dsinit.getKey());
                        }
                        quorum_allsupporters.add(allsupporters);
                        quorum_supporters.put(initiative, supporters);
                    }
                }

                //set current max quorum
                maxquorum = Collections.max(quorum_allsupporters);

                //TODO select which inits from quorum_supporters have bigger quorum than quorum_verification
                Set<String> keys = quorum_supporters.keySet();
                for (String key : keys) {
                    Log.d("checker key", quorum_supporters.get(key).toString());
                    if (quorum_supporters.get(key) > quorum_verification) {
                        Log.d("checker key", key);
                        VotationInits.add(key);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        */

        //Initialize recycler view with all initiatives
        list = findViewById(R.id.recycler);
        //swipe horizontally the recycler view elements
        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(list);
        list.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        list.setLayoutManager(layoutManager);

        //set progressbars
        mRefIssues.child(issueID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                change_status_date = FeedActivity.StringtoDate(dataSnapshot.child("change_status_date").getValue().toString(), "yyyy-MM-dd HH");
                Log.d("change date ", java.lang.String.valueOf(change_status_date));
                groupID = dataSnapshot.child("groups").getValue().toString();
                policyID= dataSnapshot.child("policies").getValue().toString();
                status = dataSnapshot.child("status").getValue().toString();
                issue_stage_view.setText(status);

                mRefGroups.child(groupID).child("members").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot member:dataSnapshot.getChildren()){
                            membersID.add(member.getKey());
                        }
                        Log.d("1904 members ", String.valueOf(membersID.size()));

                        mRefActions.child("Issues").child(issueID).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot member:dataSnapshot.getChildren()){
                                    active_membersID.add(member.getKey());
                                }

                                //Log.d("1904 active_membersID ", String.valueOf(active_membersID.size()));
                                //Log.d("1904 setProgress ", String.valueOf(100*active_membersID.size()/membersID.size()));

                                //quorumprogBar.setProgress(active_membersID.size()/membersID.size());
                                progress = 100 * active_membersID.size()/membersID.size();
                                quorumprogBar.setProgress(progress);

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                mRefPolicies.child(policyID).child(status + "_time").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        time_gone = current_time_date.getTime() - change_status_date.getTime();

                        stage_time = TimeUnit.HOURS.toMillis((Long) dataSnapshot.getValue());

                        phase_progress = Math.toIntExact(100 * time_gone / ((Long) stage_time).intValue() );

                        phasesprogBar.setProgress(phase_progress);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //issueID referencing
        issue_number.setText(issueID);

        //TODO Main_initiative_creator is
        optionsButton.setOnClickListener(v -> options_Init());

        createInitButton.setOnClickListener(v-> create_Init());

        //related with wanting to receive notifications
        follow.setOnClickListener(v -> mRefActions.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //Stop from receiving notifications (eventhough i might still have my vote on it)
                if (Util.equal(dataSnapshot.child("Issues").child(issueID).child(userID).getValue(), "follow")) {
                    //user has acted upon current initiative in the past, so...
                    //TODO  follow.setBackgroundResource(R.mipmap.black);

                    //remove child with userID from actions DB
                    mRefActions.child("Issues").child(issueID).child(userID).removeValue();

                    //remove child with user ID from opposers DB
                    mRefFollow.child(issueID).child(userID).removeValue();

                } else if (!dataSnapshot.child("Issues").child(issueID).child(userID).exists()) {//add follow to initiative if neither the user has voted nor delegated
                    //either delegating or voting, the "follow" is implicit
                    //TODO  follow.setBackgroundResource(R.mipmap.red);
                    //check if userID has already participated in this initiative
                    /*if (dataSnapshot.child("Issues").child(issueID).child(userID).exists()) {

                        Log.d("checker", "already a participant/watcher");
                        //get what the action was before: support || oppose || {number} || 0 (abstinent) || delegate || follow
                        oldaction = dataSnapshot.child("Initiatives").child(initID).child(userID).getValue().toString();
                        Log.d("checker", oldaction);

                        updateDSOADatabase(issueID, userID, oldaction);
                    }*/
                    //update actions DB
                    //Either the initiative didnt exist or the user had not participated actively
                    mRefActions.child("Issues").child(issueID).child(userID).setValue("follow");

                    //insert child with user ID to support DB
                    mRefFollow.child(issueID).child(userID).setValue(true);

                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        }));



        // delegate initiative (implies follow)
        delegateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //open fragment with recyclerview of current users to whom i can delegate
                //this code will go to that fragment
                //remove support from initiative
                mRefActions.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (Util.equal(dataSnapshot.child("Issues").child(issueID).child(userID).getValue(), "delegate")) {
                            //user has acted upon current initiative in the past, so...
                            //TODO  oppose.setBackgroundResource(R.mipmap.black);

                            //remove child with userID from actions DB
                            //TODO VERIFY INITIATIVES being recognized
                            if (!initiativesID.equals(null)) {
                                for (String init : initiativesID) {
                                    mRefActions.child("Initiatives").child(init).child(userID).removeValue();
                                }
                            }
                            mRefActions.child("Issues").child(issueID).child(userID).removeValue();

                            //remove child with user ID from opposers DB
                            mRefDeleg.child(issueID).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (DataSnapshot delegateID:dataSnapshot.getChildren()){
                                        if(delegateID.child(userID).exists()){
                                            delegateID.child(userID).getRef().removeValue();
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                        } else {
                            final Intent intent = new Intent(IssueActivity.this, DelegatesListActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                            intent.putExtra("groupID", groupID);
                            startActivityForResult(intent, MY_REQUEST);


                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });
    }

    public void onStart() {
        super.onStart();

        Log.d("1904 " ,"passou aqui");

        reset_Controls();

        FirebaseRecyclerOptions<Boolean> options =
                new FirebaseRecyclerOptions.Builder<Boolean>()
                        .setQuery(mRefIssue, Boolean.class)
                        .build();
        Log.d(TAG, " recycler options done ");

        FirebaseRecyclerAdapter<Boolean, ViewHolder> fra = new FirebaseRecyclerAdapter<Boolean, ViewHolder>(
                options) {
            @Override
            protected void onBindViewHolder(@NonNull final ViewHolder holder, final int position, @NonNull final Boolean model) {

                //Log.d("checkernew1", model.getInitiatives().toString());

                //call parent node
                final String rowinitID = getRef(position).getKey();
                Log.d("checker", rowinitID);
                mRefInit.child(rowinitID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //Change members value to true
                        Log.d("checker", dataSnapshot.getKey());
                        initiativesID = new ArrayList<>();
                        if (dataSnapshot.hasChild("description")) {
                            Log.d("checkernew2", dataSnapshot.getValue().toString());
                            description = dataSnapshot.child("description").getValue().toString();
                            holder.setDetails(getApplicationContext(), rowinitID, description);

                            Log.d("checker initiative random key", dataSnapshot.getKey());
                            initiativesID.add(dataSnapshot.getKey().toString());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                Log.d("checker", description + "boy");
                //add models (that have appeared on screen) to listarray
                //initiatives.add(model);

                //Select only one item from groups by clicking (for issue creation)
                holder.itemView.setOnClickListener(new View.OnClickListener()

                {
                    @Override
                    public void onClick(View v) {

                        Log.d("checkernew1", getRef(position).getKey());
                        final String selectedinitID = getRef(position).getKey();
                        Intent intent = new Intent(IssueActivity.this, InitiativeActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        //url for group image
                        intent.putExtra("ID", selectedinitID);
                        intent.putExtra("userID", userID);
                        intent.putExtra("issueID", issueID);
                        intent.putExtra("phase", phase);

                        startActivity(intent);

                        //notifyDataSetChanged();
                        //finish();
                    }

                });

                holder.itemView.setBackgroundColor(Color.parseColor("#F8F8FA"));

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

    public void saveInit(String description) {

        try {
            String createdinitID = mRefInit.push().getKey();

            mRefInit.child(createdinitID).child("description").setValue(description);
            mRefInit.child(createdinitID).child("owner").setValue(userID);

            //get number of inits
            mRefIssue.child(issueID).child("Initiatives").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    numberInits = (int) dataSnapshot.getChildrenCount();
                    Log.d("checker number of inits", String.valueOf(numberInits));

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            mRefInit.child(createdinitID).child("id").setValue(numberInits + 1);
            String date;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            date = sdf.format(currentTime);
            Log.d("checker date", date);
            mRefInit.child(createdinitID).child("currentDate").setValue(date);

            Toast.makeText(this, "Initiative created", Toast.LENGTH_SHORT).show();

        } catch (DatabaseException e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();

        }

    }

    public void options_Init() {

        //TODO take delegate and createinit buttons to another view that can be enabled/disabled
        if(clicked==false) {
            delegateButton.setEnabled(true);
            delegateButton.setVisibility(View.VISIBLE);
            createInitButton.setVisibility(View.VISIBLE);
            createInitButton.setEnabled(true);
            optionsButton.setBackgroundColor(Color.TRANSPARENT);

            clicked = true;
        }
        else{
            delegateButton.setEnabled(false);
            delegateButton.setVisibility(View.INVISIBLE);
            createInitButton.setVisibility(View.INVISIBLE);
            createInitButton.setEnabled(false);
            optionsButton.setBackground(ContextCompat.getDrawable(this, R.drawable.roundedbutton));

            clicked = false;
        }
    }

    public void reset_Controls(){
        delegateButton.setEnabled(false);
        delegateButton.setVisibility(View.INVISIBLE);
        createInitButton.setVisibility(View.INVISIBLE);
        createInitButton.setEnabled(false);
        optionsButton.setBackground(ContextCompat.getDrawable(this, R.drawable.roundedbutton));

    }
    public void create_Init(){
        final Dialog d = new Dialog(this);
        d.setTitle("Create initiative");
        d.setContentView(R.layout.editinit_dialog);
        //helper = new FirebaseHelper(mRef);

        saveBtn = d.findViewById(R.id.saveBtn);
        descriptionTxt = d.findViewById(R.id.descriptionTxt);

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //TODO verify if description is being saved in groups firebase
                //TODO invite proposed members and wait for their confirmal to have them as new members in the groups
                saveInit(descriptionTxt.getText().toString());

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

    public String addDate(String date1, Integer days) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(sdf.parse(date1));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        c.add(Calendar.DATE, days);  // number of days to add, can also use Calendar.DAY_OF_MONTH in place of Calendar.DATE
        String output = sdf.format(c.getTime());

        return output;
    }

    public void updateProgressBar(final ProgressBar progBar, int value) {

        new Thread(() -> {
            progBar.setProgress(value);
            while (value < 100) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Log.d("1904 ", String.valueOf(progBar.getProgress()));

                handler.post(() -> progBar.setProgress(value));
            }
            handler.post(new Runnable() {
                public void run() {
                    // ---0 - VISIBLE; 4 - INVISIBLE; 8 - GONE---
                    progBar.setVisibility(View.VISIBLE);
                }
            });

        }).start();

           /*
        t = new Thread(() -> {
            Log.d("progBar.getProgress()  ", String.valueOf(progBar.getProgress()));
            progBar.setProgress(0);
            while (progBar.getProgress() < 100) {
                runOnUiThread(() -> {
                    progBar.setMax(100);
                    Log.d("1904 ", "entrou? ");
                    progBar.setProgress(value);
                    Log.d("progBar.getProgress()2  ", String.valueOf(progBar.getProgress()));

                    // progressText.setText(value + " %");
                });
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        });
        t.start();*/
    }

    public Policy getPolicy(String policyID) {

        final Policy policy = new Policy();

        mRefPolicies.child(policyID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    policy.setVoting_time((Integer) ds.child("voting_time").getValue());
                    policy.setVerification_time((Integer) ds.child("verification_time").getValue());
                    policy.setDiscussion_time((Integer) ds.child("discussion_time").getValue());
                    policy.setAdmission_time((Integer) ds.child("admission_time").getValue());

                    policy.setQuorum_admission((Integer) ds.child("quorum_admission").getValue());
                    policy.setQuorum_verification((Integer) ds.child("quorum_verification").getValue());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return policy;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // ... Check for some data from the intent
        // .. lets toast again
        if (data != null) {
            delegateID = data.getStringExtra("delegateID");
            Log.d("checker name", delegateID);
        }

        if (delegateID != null) {
            Toast.makeText(this, "Handled the result successfully", Toast.LENGTH_SHORT).show();
            mRefActions.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot ds) {
                    //TODO  oppose.setBackgroundResource(R.mipmap.red);
                    //check if userID has already participated in this issue
                    if (ds.child("Issues").child(issueID).child(userID).exists()) {

                        //get what the action was before: support || oppose || {number} || 0 (abstinent) || delegate || follow
                        oldaction = ds.child("Issues").child(issueID).child(userID).getValue().toString();
                        updateDSOADatabase(issueID, userID, oldaction);
                    }
                    //update actions DB
                    //Either the initiative didnt exist or the user had not participated actively
                    mRefActions.child("Issues").child(issueID).child(userID).setValue("delegate");

                    //insert child with user ID to support DB
                    mRefDeleg.child(issueID).child(delegateID).child(userID).setValue(true);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        } else {
            Toast.makeText(this, "Any delegate was assigned", Toast.LENGTH_SHORT).show();
        }


    }

    //update database of either Delegations || Support || Oppose || Abstinence
    public void updateDSOADatabase(final String issueID, final String userID, String oldaction) {

       /* if (oldaction.equals("delegate")) {
            mRefDeleg.child(issueID).child(userID).removeValue();
            // TODO delegate.setBackgroundResource();

        } else*/
        if (oldaction.equals("follow")) {
            DatabaseReference mDatabaseRef = mRefFollow.child(issueID);
            if (!mDatabaseRef.getKey().equals("")) {
                DatabaseReference mDatabaseRef2 = mRefFollow.child(userID);
                if (!mDatabaseRef2.getKey().equals("")) {
                    mDatabaseRef2.removeValue();
                }
            }
        } else if (oldaction.equals("active")) { //remove oppositions/supports of any initiative

            //list all initiatives in issue
            mRefIssue.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot initiative : dataSnapshot.getChildren()) {

                        Log.d("checker initID", initiative.getKey());

                        //if issues' initiatives had been previously supported/opposed, then...
                        mRefOppose.child(initiative.getKey()).child(userID).removeValue();
                        /*getKey().isEmpty() ){
                            .child(initID).child(userID).setValue(true);
                        }*/
                        mRefSupport.child(initiative.getKey()).child(userID).removeValue();
                        //getKey().isEmpty() ){

                        mRefActions.child("Initiatives").child(initiative.getKey()).child(userID).removeValue();
                        //}
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        } else {
            Log.d("checker", "fodeu");
        }
    }

}


