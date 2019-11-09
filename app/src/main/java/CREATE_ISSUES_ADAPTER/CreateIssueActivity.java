package CREATE_ISSUES_ADAPTER;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.daniel.liquid_democracy.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import GROUPS_ADAPTER.GroupsActivity;
import MAIN_CLASSES.Initiative;
import MAIN_CLASSES.Issue;

import static com.example.daniel.liquid_democracy.Liquid.channel4ID;

public class CreateIssueActivity extends AppCompatActivity {
    private static final int MY_REQUEST = 1001;
    private static final int POLICY_REQUEST = 1005;

    private EditText description;
    private ImageButton groupBtn;
    private Button policyBtn;
    private Button createissueBtn;
    private String newToken, currentUserID, code, url, groupID, policyID, currentDate, policyName;
    private Intent intent;
    Calendar calendar = Calendar.getInstance();
    private Issue issuetobesaved;
    private FirebaseDatabase mFirebase;
    private DatabaseReference mRefPolicy, mRefGroups, mRefIssue, mRefInit, mNotification;

    private Initiative firstinit;
    private HashMap<String, Boolean> Policy, Group;
    private NotificationManagerCompat notificationManager;
    private ArrayList<String> Groups;
    private static final int uniqueID = 45612;
    private NotificationCompat.Builder notification;
    private String tokenid;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createinit);

        createissueBtn = findViewById(R.id.saveBtn);
        policyBtn = findViewById(R.id.policyBtn);
        groupBtn = findViewById(R.id.addgroup);
        description = findViewById(R.id.initiative_description);

        //set notification manager
        notificationManager = NotificationManagerCompat.from(this);
        notification = new NotificationCompat.Builder(this, channel4ID);

        //Set firebase references
        mFirebase = FirebaseDatabase.getInstance();
        mRefPolicy = mFirebase.getReference().child("Policies");
        mRefIssue = mFirebase.getReference("Issues");
        mRefInit = mFirebase.getReference("Initiatives");
        mNotification = mFirebase.getReference().child("Notifications");

        issuetobesaved = new Issue();
        firstinit = new Initiative();

        intent = getIntent();
        Bundle b = intent.getExtras();

        if (b.get("userID") != null) { //debug purposes
            currentUserID = (String) b.get("userID"); //mAuth.getCurrentUser().getUid();
        } else {
            currentUserID = "-LP8-f0bsmowXhMtStSB";

            //TODO add tokenid in the registration
            FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
                @Override
                public void onSuccess(InstanceIdResult instanceIdResult) {
                    tokenid = instanceIdResult.getToken();
                    mFirebase.getReference().child("Users").child(currentUserID).child("device_token").setValue(tokenid);

                    Log.d("liquiddemo token", tokenid);

                }
            });


            //TODO replace currentuserID with this one
            //currentUserID = "Daniel";
        }

        issuetobesaved.setModerator(currentUserID);

        groupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CreateIssueActivity.this, GroupsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("request_Code", MY_REQUEST);
                intent.putExtra("userID", currentUserID);
                startActivityForResult(intent, MY_REQUEST);
            }
        });


        //Select policy
        policyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CreateIssueActivity.this, PoliciesActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("request_Code", POLICY_REQUEST);
                startActivityForResult(intent, POLICY_REQUEST);
            }
        });

        //TODO remove these set enabled and setvisibility of the code
        //button is visible to create issue
        createissueBtn.setVisibility(View.VISIBLE);
        createissueBtn.setEnabled(true);

        createissueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                saveIssue(currentUserID);

            }
        });
    }


    //TODO set alarms to alarmmanager
    private void saveIssue(final String currentUserID) {

        //when group has been selected, all item3 props are stored
        //save initiatives properties for firebase
        calendar.add(Calendar.DATE, 1);
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
        // Output "Wed Sep 26 14:23:28 EST 2012"
        currentDate = format1.format(calendar.getTime());
        //currentDate = DateFormat.getDateInstance().format(calendar.getTime());

        firstinit.setCurrentDate(currentDate);
        firstinit.setOwner(currentUserID);
        //set number 0 init to issue
        firstinit.setID(0);
        firstinit.setDescription(description.getText().toString());

        Log.d("checker groups in issue", String.valueOf(issuetobesaved.getGroups()));
        //define properties to be saved in variables
        //mAuth = FirebaseAuth.getInstance();
        //list of supporters

        //push of new initiative relative to that issue and corresponding issue
        //key ID for new issue
        final String key = mRefIssue.push().getKey();

        final HashMap<String, Boolean> initid = new HashMap<>();

        //save Issue properties
        mRefIssue.child(key).setValue(issuetobesaved)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Write was successful!
                        Toast.makeText(CreateIssueActivity.this, "Success to upload data", Toast.LENGTH_SHORT).show();

                        //get key for next push
                        String keyinit = mRefIssue.child(key).child("Initiatives").push().getKey();
                        initid.put(keyinit, true);
                        //push new initiative
                        mRefIssue.child(key).child("Initiatives").setValue(initid);

                        //save Initiative properties
                        mRefInit.child(keyinit).setValue(firstinit);

                        //save notification as belonging to the group
                        HashMap<String, String> notificationData = new HashMap<>();
                        notificationData.put("moderator", currentUserID);

                        notificationData.put("type", "Issue creation");
                        groupID = "casa do benfica condeixa";
                        mNotification.child(groupID).child(key).push().setValue(notificationData).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                                Log.d("liquiddemo success", "issue creation");
                                //TODO uncomment these 3 lines of the code
                                //Intent intent = new Intent(CreateIssueActivity.this, IssueActivity.class);
                                //intent.putExtra("userID",currentUserID);
                                //startActivity(intent);

                            }
                        });

                        //TODO uncomment these 3 lines of the code
                        //Intent intent = new Intent(CreateIssueActivity.this, IssueActivity.class);
                        //intent.putExtra("userID",currentUserID);
                        //startActivity(intent);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Write failed
                        Toast.makeText(CreateIssueActivity.this, "Failed to upload data", Toast.LENGTH_SHORT).show();
                    }
                });


    }

    public void create_notification(long time, Intent intent, Context context, NotificationCompat.Builder notification, String channel_id, int uniqueID, String message, String Title) {

        notification.setSmallIcon(R.mipmap.ic_launcher);
        notification.setTicker("This is the ticker");
        notification.setWhen(time);
        notification.setContentTitle(Title);
        notification.setContentText(message);
        notification.setAutoCancel(true);
        notification.setOnlyAlertOnce(true);
        //Intent intent = new Intent(this, ScoresActivity.class);
        //requestcode = 2 ensures this notification only deals with votation updates
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 2, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        notification.setContentIntent(pendingIntent);

        //Builds notification and issues it
        notificationManager.notify(uniqueID, notification.build());
        Log.d("checker ", "notified");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        // Check for groupsID
        if (requestCode == MY_REQUEST) {
            //update view with image uploaded from groups
            if (data != null) {
                url = data.getStringExtra("url");
                groupID = data.getStringExtra("id");
                Log.d("checker group id", groupID);

            }

            if (url != null && groupID != null) {
                Toast.makeText(this, "Handled the result successfully", Toast.LENGTH_SHORT).show();

                //change group image image
                Log.d("checker width", String.valueOf(groupBtn.getWidth()));
                Log.d("checker height", String.valueOf(groupBtn.getHeight()));

                Picasso.get().load(url).placeholder(R.drawable.loading).resize(150, 150).centerCrop().into(groupBtn);

                //button is visible to create issue
                policyBtn.setVisibility(View.VISIBLE);
                policyBtn.setEnabled(true);

                //save to issuetobesaved
                Group = new HashMap<>();
                Group.put(groupID, true);
                issuetobesaved.setGroups(groupID);

            } else {
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();

            }

        }
        // Check for policyID
        else if (requestCode == POLICY_REQUEST) {
            if (data != null) {
                policyID = data.getStringExtra("id");
                Log.d("checker policyID", policyID);
            }

            if (policyID != null) {
                Toast.makeText(this, "Handled the result successfully", Toast.LENGTH_SHORT).show();

                //set issue to be saved
                Policy = new HashMap<>();
                Policy.put(policyID, true);
                issuetobesaved.setPolicies(policyID);

                mRefPolicy.child(policyID).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String policyname = dataSnapshot.child("name").getValue().toString();
                        policyBtn.setText(policyname);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                //button is visible to create issue
                createissueBtn.setVisibility(View.VISIBLE);
                createissueBtn.setEnabled(true);

            } else {
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();

            }
        }
    }


}
