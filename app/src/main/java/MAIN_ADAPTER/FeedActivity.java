package MAIN_ADAPTER;

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
import android.widget.ImageView;
import android.widget.Toast;

import com.example.daniel.liquid_democracy.Main_issue_creator;
import com.example.daniel.liquid_democracy.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import MAIN_ADAPTER.ViewHolder;
import INITIATIVES_ADAPTER.IssueActivity;
import MAIN_CLASSES.Issue;

public class FeedActivity extends AppCompatActivity {

    private Button addIssue;

    RecyclerView list;
    RecyclerView.LayoutManager layoutManager;
    ImageView group_image;
    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;
    int row_index = -1; //default no row chosen
    StorageReference storageRef, imagesRef;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mRef, mRefPolicy;
    long time_left, time_gone;
    private Date current_time_date;
    private Date change_status_date;
    private String dateString;
    private static final int MY_REQUEST = 1000;
    private static final int CREATEISSUE_REQUEST = 1001;
    private long String;
    private long stage_time;
    private String votingDate, verificationDate, admissionDate;
    Date current_status_Date, scoreDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        //RECYCLERVIEW
        list = findViewById(R.id.recycler_issues);
        //swipe horizontally the recycler view elements
        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(list);

        list.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        list.setLayoutManager(layoutManager);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabase.getReference("Issues");
        mRefPolicy = mFirebaseDatabase.getReference("Policies");

        //button visible after animation of the UI
        // Create Issue Button
        addIssue = findViewById(R.id.add_issue);
        addIssue.setOnClickListener(v -> {
            Intent intent = new Intent(FeedActivity.this, Main_issue_creator.class);
            intent.putExtra("userID", "-LP8-f0bsmowXhMtStSB");
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivityForResult(intent, CREATEISSUE_REQUEST);
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (checkPermissionREAD_EXTERNAL_STORAGE(this)) {
            Log.d("checkerreadstorage", " permission");
        }
        FirebaseRecyclerOptions<Issue> options =
                new FirebaseRecyclerOptions.Builder<Issue>()
                        .setQuery(mRef, Issue.class)
                        .build();

        FirebaseRecyclerAdapter<Issue, ViewHolder> fra = new FirebaseRecyclerAdapter<Issue, ViewHolder>(
                options) {
            @Override
            protected void onBindViewHolder(@NonNull ViewHolder holder, final int position, @NonNull Issue model) {

                change_status_date = StringtoDate(model.getChange_status_date(), "yyyy-MM-dd HH");
                Log.d("change date ", java.lang.String.valueOf(change_status_date));
                current_time_date = Calendar.getInstance().getTime();

                time_gone = current_time_date.getTime() - change_status_date.getTime();
                Log.d("time_gone ", java.lang.String.valueOf(time_gone));

                mRefPolicy.child(model.getPolicies()).child(model.getStatus() + "_time").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        stage_time = TimeUnit.HOURS.toMillis((Long) dataSnapshot.getValue());
                        Log.d("stage_time ", java.lang.String.valueOf(stage_time));

                        time_gone = stage_time - time_gone;
                        Log.d("time_gone ", java.lang.String.valueOf(time_gone));

                        //TODO change days to days-1
                        dateString = new SimpleDateFormat("dd HH:mm").format(new Date(time_gone));

                        holder.setIssue(getApplicationContext(),getRef(position).getKey(), dateString);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });



                //Select only one item from groups by clicking (for issue creation)
                holder.itemView.setOnClickListener(v -> {
                    row_index = position;

                    if (row_index == position) {

                        Intent intent = new Intent(FeedActivity.this, IssueActivity.class);
                        Log.d("checker row index", java.lang.String.valueOf(row_index));

                        intent.putExtra("issueID", getRef(row_index).getKey());
                        intent.putExtra("userID", "-LP8-f0bsmowXhMtStSB");
                        intent.putExtra("time",dateString);

                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivityForResult(intent, MY_REQUEST);
                    }

                });
                holder.itemView.setBackgroundColor(Color.parseColor("#F8F8FA"));
            }

            @NonNull
            @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.issue_cell, parent, false);

                return new ViewHolder(view);
            }
        };
        fra.startListening();
        list.setAdapter(fra);
    }

    public Date addDate(Date date1, Integer hours) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH");
        Calendar c = Calendar.getInstance();
        c.setTime(date1);
        Log.d("date ",c.toString());
        c.add(Calendar.HOUR, hours);  // number of days to add, can also use Calendar.DAY_OF_MONTH in place of Calendar.DATE
        //String output = sdf.format(c.getTime());

        return c.getTime();
    }

    public static Date StringtoDate(String strDate, String pattern) {

        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        Date dateDate = new Date();
        try {
            dateDate = sdf.parse(strDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return dateDate;
    }

    public String DatetoString(Date date) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH");
        String str_date = sdf.format(date);

        return str_date;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == CREATEISSUE_REQUEST) {
            if(resultCode == Activity.RESULT_OK){
                Toast.makeText(FeedActivity.this, "NEW ISSUE ADDED",
                        Toast.LENGTH_SHORT).show();            }

        }
    }//onActivityResult


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // do your
                    Toast.makeText(FeedActivity.this, "GET_ACCOUNTS ALLOWED!!!",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(FeedActivity.this, "GET_ACCOUNTS Denied",
                            Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions,
                        grantResults);
        }
    }


    //check permissions to access storage
    public boolean checkPermissionREAD_EXTERNAL_STORAGE(
            final Context context) {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= Build.VERSION_CODES.M) {
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
