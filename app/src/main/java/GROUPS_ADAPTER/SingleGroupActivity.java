package GROUPS_ADAPTER;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.daniel.liquid_democracy.InitiativeActivity;
import com.example.daniel.liquid_democracy.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.HashMap;

import Firebase_Cloud_Messaging_ADAPTER.APIService;
import Firebase_Cloud_Messaging_ADAPTER.Data;
import Firebase_Cloud_Messaging_ADAPTER.MyResponse;
import Firebase_Cloud_Messaging_ADAPTER.Sender;
import GROUPS_ADAPTER.ViewHolder;
import INITIATIVES_ADAPTER.IssueActivity;
import MAIN_CLASSES.Issue;
import MAIN_CLASSES.User;
import okhttp3.internal.Util;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SingleGroupActivity extends AppCompatActivity {

    private String groupID, userID, notificationTxt, notificationTitle;
    private Button backbutton, joingroup;
    private Intent intent;
    DatabaseReference mRefIssue_Grp, mRefGroup, mRefGroupReq, mRef;
    FirebaseDatabase mFirebaseDatabase;
    private Boolean member;
    private TextView description;
    private TextView checkmembers;
    private static final int MY_REQUEST = 1003;
    private ArrayList<String> issuesname;
    private Query searchedRef;
    RecyclerView list;
    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;
    private ArrayList<String> userskey;

    APIService mService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singlegroup);

        backbutton = findViewById(R.id.back_button);
        joingroup = findViewById(R.id.join_leave);
        description = findViewById(R.id.group_description);
        checkmembers = findViewById(R.id.check_members);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRefGroup = mFirebaseDatabase.getReference("Groups");

        mRefGroupReq = mFirebaseDatabase.getReference("GroupReq");
        mRef = mFirebaseDatabase.getReference("/Users");

        mService = Common.getFCMClient();

        intent = getIntent();
        //initID = intent.getStringExtra("ID");
        Bundle b = intent.getExtras();
        if (b.get("groupID") != null && b.get("userID") != null  ) {
            groupID = (String) b.get("groupID");
            userID = (String) b.get("userID");
            Log.d("checker group ID", groupID + "vamos");
            Log.d("checker user ID", userID + " vamos");

        } else { //degub purposes
            groupID = "-LPNa9xqrL98aj0N7N3Q";
            userID = "-LP8-f0bsmowXhMtStSB";

            //TODO add tokenid in the registration
            FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(instanceIdResult -> {
                Common.currentToken = instanceIdResult.getToken();
                Log.d("liquiddemo token", Common.currentToken);

            });

        }

        backbutton.setOnClickListener(v -> finish());

        member = false;

        //set recyclerview
        list = findViewById(R.id.recycler_group);
        list.setHasFixedSize(true);
        list.setLayoutManager(new LinearLayoutManager(this));

        FirebaseRecyclerOptions<Boolean> options =
                new FirebaseRecyclerOptions.Builder<Boolean>()
                        .setQuery(mRefGroup.child(groupID).child("issues"), Boolean.class)
                        .build();

        FirebaseRecyclerAdapter<Boolean, ViewHolder> fra = new FirebaseRecyclerAdapter<Boolean, GROUPS_ADAPTER.ViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ViewHolder holder, final int position, @NonNull Boolean model) {

                String issue_tobechecked = getRef(position).getKey();
                Log.d("checker issues position", issue_tobechecked);

                if (issuesname.contains(issue_tobechecked)) {
                    Log.d("checker", "reconheceu issue ");
                    holder.setIssue(getApplicationContext(), getRef(position).getKey());

                }

                //add models (that have appeared on screen) to listarray
                //initiatives.add(model);

                Log.d("checker member recycler view", member.toString());
                //Select only one item from groups by clicking (for issue creation)

                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if (member) {
                                Intent intent = new Intent(SingleGroupActivity.this, IssueActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                Log.d("checker user", userID);
                                //url for group image
                                intent.putExtra("userID", userID);
                                intent.putExtra("issueID", getRef(position).getKey());
                                startActivity(intent);
                            }
                            else{
                                Toast.makeText(getApplicationContext(), "Please join the group first", Toast.LENGTH_LONG).show();

                            }
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
                        .inflate(R.layout.issue_row, parent, false);

                return new ViewHolder(view);
            }
        };

        //check if member
        mRefGroup.child(groupID).child("members").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("checker member in group", dataSnapshot.getKey().toString());

                if (dataSnapshot.hasChild(userID)) { //if member, show all the related issues and leave group button
                    member = true;
                    joingroup.setText("LEAVE GROUP");
                    Log.d("checker member", member.toString());
                } else {        //else, show description and join group button
                    joingroup.setText("JOIN GROUP");
                    member = false;
                    Log.d("checker member", member.toString());
                } //TODO else if owner, joingroup.setText("ADD NEW MEMBER");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //description of groups in UI
        mRefGroup.child(groupID).child("description").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("checker member in group", dataSnapshot.getValue().toString());
                description.setText(dataSnapshot.getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //after leave/join group is clicked
        joingroup.setOnClickListener(v -> {
            if (member) {
                final String key = mRefGroupReq.child(groupID).push().getKey();

                //remove member from topic
                FirebaseMessaging.getInstance().unsubscribeFromTopic(groupID).addOnSuccessListener(aVoid -> {
                    Toast.makeText(getApplicationContext(), "Left group", Toast.LENGTH_LONG).show();

                    //member=false;

                    //send notifications to topic
                    HashMap<String, String> notificationData = new HashMap<>();
                    notificationData.put("member", userID);
                    notificationData.put("type", "Left Group");
                    mRefGroupReq.child(groupID).child(key).setValue(notificationData).addOnSuccessListener((OnSuccessListener<Void>) aVoid1 -> {

                        //remove member from groups' members
                        mRefGroup.child(groupID).child("members").child(userID).removeValue();

                      /*  notificationTxt = userID + " left the group " + groupID;
                        notificationTitle = "GROUPS";
                        Data notification = new Data(notificationTitle.toString(),notificationTxt.toString());
                        Sender sender = new Sender(groupID,notification);
                        Log.d("checker group ID topic", groupID);
                        mService.sendNotification(sender).enqueue(new Callback<MyResponse>() {
                            @Override
                            public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                Toast.makeText(SingleGroupActivity.this, "Left group", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFailure(Call<MyResponse> call, Throwable t) {

                            }
                        });*/
                    });
                });

            } else {
                //TODO set validation method of new member. Key? citizen ID? may depend from case to case
                //else, show description and join group button
                final String key = mRefGroupReq.child(groupID).push().getKey();

                //send notifications to topic
                FirebaseMessaging.getInstance().subscribeToTopic(groupID).addOnSuccessListener(aVoid -> {
                    HashMap<String, String> notificationData = new HashMap<>();
                    notificationData.put("member", userID);
                    notificationData.put("type", "Joined Group");
                    mRefGroupReq.child(groupID).child(key).setValue(notificationData).addOnSuccessListener((OnSuccessListener<Void>) aVoid1 -> {

                        //add member to group
                        mRefGroup.child(groupID).child("members").child(userID).setValue(true);

                       /* notificationTxt = userID + " joined the group " + groupID;
                        notificationTitle = "GROUPS";
                        Data notification = new Data(notificationTitle.toString(),notificationTxt.toString());
                        Sender sender = new Sender(groupID,notification);
                        mService.sendNotification(sender).enqueue(new Callback<MyResponse>() {
                            @Override
                            public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                Toast.makeText(SingleGroupActivity.this, "Joined group", Toast.LENGTH_SHORT).show();

                            }

                            @Override
                            public void onFailure(Call<MyResponse> call, Throwable t) {

                            }
                        });*/
                    });

                    // member = true;
                    Toast.makeText(getApplicationContext(), "Joined group", Toast.LENGTH_LONG).show();

                });

            }
        });

        //click in checkmembers
        checkmembers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SingleGroupActivity.this, MembersActivity.class);
                intent.putExtra("groupID", groupID);
                startActivityForResult(intent, MY_REQUEST);
            }
        });

        mRefGroup.child(groupID).child("issues").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                issuesname = new ArrayList<>();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    //Add all related issues with this group
                    Log.d("checker before adding to array", ds.getKey());
                    issuesname.add(ds.getKey().toString());
                }
                Log.d("checker issues name", issuesname.toString());
                //search_issue();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userskey = new ArrayList<>();
                Log.d("checker ", "ver users");
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    //Log.d("checker users metadata name", ds.child("metadata").getValue().toString());
                    //Add all related issues with this group
                    userskey.add(ds.getKey());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        fra.startListening();
        list.setAdapter(fra);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // do your
                    Toast.makeText(SingleGroupActivity.this, "GET_ACCOUNTS ALLOWED!!!",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(SingleGroupActivity.this, "GET_ACCOUNTS Denied",
                            Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions,
                        grantResults);
        }
    }


    public boolean checkPermissionREAD_EXTERNAL_STORAGE(
            final Context context) {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context,
                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        (Activity) context,
                        Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    showDialog("External storage", context,
                            Manifest.permission.READ_EXTERNAL_STORAGE);

                } else {
                    ActivityCompat
                            .requestPermissions(
                                    (Activity) context,
                                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                }
                return false;
            } else {
                return true;
            }

        } else {
            return true;
        }
    }

    public void showDialog(final String msg, final Context context,
                           final String permission) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
        alertBuilder.setCancelable(true);
        alertBuilder.setTitle("Permission necessary");
        alertBuilder.setMessage(msg + " permission is necessary");
        alertBuilder.setPositiveButton(android.R.string.yes,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions((Activity) context,
                                new String[]{permission},
                                MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                    }
                });
        AlertDialog alert = alertBuilder.create();
        alert.show();
    }


}
