package VOTATION_ADAPTER;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.daniel.liquid_democracy.MainActivity;
import com.example.daniel.liquid_democracy.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import okhttp3.internal.Util;

import static java.util.stream.Collectors.toMap;

public class ScoresActivity extends AppCompatActivity {
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mRefVot, mRefIssueParticipants, mRefUsers;
    private Intent intent;
    private String issueID,userid;

    FirebaseUser fuser;
    long sum;
    int size, currentcoins;
    HashMap<String, Double> initScores;
    HashMap<String, Double> initScoressorted;
    ArrayList<String> participants;
    private TextView firstInitNumber;
    private TextView secondInitNumber;
    private TextView thirdInitNumber;

    private TextView firstInitScore;
    private TextView secondInitScore;
    private TextView thirdInitScore;

    private Button leaveBtn;
    //APIService apiService;
    private Boolean notify = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scores);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRefVot = mFirebaseDatabase.getReference().child("Votation");
        firstInitNumber = findViewById(R.id.first_init_number);
        secondInitNumber = findViewById(R.id.second_init_number);
        thirdInitNumber = findViewById(R.id.third_init_number);
        firstInitScore = findViewById(R.id.firstinitscore);
        secondInitScore = findViewById(R.id.secondinitscore);
        thirdInitScore = findViewById(R.id.thirdinitscore);
        leaveBtn = findViewById(R.id.leaveBtn);
        participants = new ArrayList<>();

        //set notifiction channel characteristics
        String channel_id = "LD1";

        //apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
        //update Token
        //updateToken(FirebaseInstanceId.getInstance().getToken());

        //intent issueID
        intent = getIntent();
        //initID = intent.getStringExtra("ID");
        Bundle b = intent.getExtras();
        if (b.get("issueID") != null) {
            issueID = (String) b.get("issueID");
            fuser = FirebaseAuth.getInstance().getCurrentUser();

            Log.d("checker current user", fuser.getUid());
        } else { //for debuggind purposes
            issueID = "-LRIsjB3ZaBfJ3DP1lrl";
            userid = "Foden";
            Log.d("checker init2", issueID);
        }


        mRefIssueParticipants = mFirebaseDatabase.getReference().child("Action").child("Issues").child(issueID);
        mRefUsers = mFirebaseDatabase.getReference().child("Users");
        initScores = new HashMap<>();

        //get info from Votation firebase
        mRefVot.child(issueID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot initID : dataSnapshot.getChildren()) {
                    sum = 0;
                    size = 0;
                    for (DataSnapshot userID : initID.getChildren()) {
                        sum += (long) userID.getValue();
                        size += 1;
                    }
                    Log.d("checker average", String.valueOf((double) sum / size));
                    initScores.put(initID.getKey(), ((double) sum / size));
                }
                initScoressorted = initScores
                        .entrySet()
                        .stream()
                        .sorted(HashMap.Entry.comparingByValue())
                        .collect(
                                toMap(HashMap.Entry::getKey, HashMap.Entry::getValue, (e1, e2) -> e2,
                                        LinkedHashMap::new));

                //init scores
                firstInitNumber.setText(initScoressorted.keySet().toArray()[0].toString());
                secondInitNumber.setText(initScoressorted.keySet().toArray()[1].toString());
                thirdInitNumber.setText(initScoressorted.keySet().toArray()[2].toString());

                Log.d("checker score", String.valueOf(initScoressorted.get(firstInitNumber.getText())));
                Log.d("checker score", String.valueOf(Math.floor(initScoressorted.get(thirdInitNumber.getText()))));
                Log.d("checker score", String.valueOf(Math.round(initScoressorted.get(thirdInitNumber.getText()))));

                firstInitScore.setText(String.valueOf((initScoressorted.get(firstInitNumber.getText()))));
                secondInitScore.setText(String.valueOf((initScoressorted.get(secondInitNumber.getText()))));
                thirdInitScore.setText(String.valueOf((initScoressorted.get(thirdInitNumber.getText()))));

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //wallet
        mRefIssueParticipants.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot participant : dataSnapshot.getChildren()) {
                    if (!Util.equal("follow", participant.getValue().toString())) {
                        participants.add(participant.getKey());
                    }
                }

                //clickable initiative that enables to see the InitiativeActivity with everything disabled
                mRefUsers.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (String user : participants) {
                            Log.d("checker user", user);
                            mRefUsers.child(user).child("coins").child(issueID).setValue(true);
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

        leaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ScoresActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }




  /*  private void updateToken(String token){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
        Token token1 = new Token(token);
       // reference.child(fuser.getUid()).setValue(token1);
        reference.child(userid).setValue(token1);

    }

    //  sendNotification("",userid,message);
    private void sendNotification(String receiver,String username, String message){
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = tokens.orderByKey().equalTo(receiver);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                    Token token = snapshot.getValue(Token.class);
                    //Data data = new Data(fuser.getUid(),R.mipmap.ic_launcher,username+": "+message,"New Message",userid);
                    Data data = new Data(userid,R.mipmap.ic_launcher,username+": "+message,"New Message",receiver);

                    Sender sender = new Sender(data,token.getToken());
                    apiService.sendNotification(sender).enqueue(new Callback<MyResponse>() {
                        @Override
                        public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                            if (response.code()==200){
                                if(response.body().success != 1){
                                    Toast.makeText(ScoresActivity.this,"Failed!!",Toast.LENGTH_SHORT).show();

                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<MyResponse> call, Throwable t) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    */
}
