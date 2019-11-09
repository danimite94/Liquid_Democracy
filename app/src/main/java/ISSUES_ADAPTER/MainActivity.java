package ISSUES_ADAPTER;

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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import GROUPS_ADAPTER.ViewHolder;
import INITIATIVES_ADAPTER.IssueActivity;
import MAIN_CLASSES.Group;

public class MainActivity extends AppCompatActivity {

    private Button AddIssue;

    RecyclerView list;
    RecyclerView.LayoutManager layoutManager;
    ImageView group_image;
    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;
    int row_index = -1; //default no row chosen
    StorageReference storageRef, imagesRef;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mRef;
    private static final int MY_REQUEST = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        group_image = findViewById(R.id.image_group);
        list = findViewById(R.id.recycler_issues);
        //swipe horizontally the recycler view elements
        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(list);

        list.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        list.setLayoutManager(layoutManager);

        storageRef = FirebaseStorage.getInstance().getReference();
        imagesRef = storageRef.child("images/groups");
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabase.getReference("Groups");

        //button visible after animation of the UI
        // Create Issue Button
        AddIssue = findViewById(R.id.addissue);
        AddIssue.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, Main_issue_creator.class);
            startActivity(intent);
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        if (checkPermissionREAD_EXTERNAL_STORAGE(this)) {
            Log.d("checkerreadstorage", " permission");
        }
        /*
        FirebaseRecyclerOptions<Group> options =
                new FirebaseRecyclerOptions.Builder<Group>()
                        .setQuery(mRef, Group.class)
                        .build();

        FirebaseRecyclerAdapter<Group, ViewHolder> fra = new FirebaseRecyclerAdapter<Group, ViewHolder>(
                options) {
            @Override
            protected void onBindViewHolder(@NonNull ViewHolder holder, final int position, @NonNull Group model) {

                Log.d("checker", "entrou");
                holder.setDetails(getApplicationContext(), model.getName(), model.getImage());

                //Select only one item from groups by clicking (for issue creation)
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        row_index = position;

                        if (row_index == position) {

                            Intent intent = new Intent(MainActivity.this, IssueActivity.class);
                            Log.d("checker row index", String.valueOf(row_index));

                            intent.putExtra("issueID", getRef(row_index).getKey());
                            intent.putExtra("userID", currentUserID);

                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivityForResult(intent, MY_REQUEST);
                        }

                    }

                });
                holder.itemView.setBackgroundColor(Color.parseColor("#F8F8FA"));
            }

            @NonNull
            @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.feed_row, parent, false);

                return new ViewHolder(view);
            }
        };
        fra.startListening();
        list.setAdapter(fra);
        */

        FirebaseRecyclerOptions<Group> options =
                new FirebaseRecyclerOptions.Builder<Group>()
                        .setQuery(mRef, Group.class)
                        .build();

        FirebaseRecyclerAdapter<Group, ViewHolder> fra = new FirebaseRecyclerAdapter<Group, ViewHolder>(
                options) {
            @Override
            protected void onBindViewHolder(@NonNull ViewHolder holder, final int position, @NonNull Group model) {

                Log.d("checker", "entrou");
                holder.setDetails(getApplicationContext(), model.getName(), model.getImage());

                //Select only one item from groups by clicking (for issue creation)
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        row_index = position;

                        if (row_index == position) {

                            Intent intent = new Intent(MainActivity.this, IssueActivity.class);
                            Log.d("checker row index", String.valueOf(row_index));

                            intent.putExtra("issueID", getRef(row_index).getKey());
                            intent.putExtra("userID", "-LP8-f0bsmowXhMtStSB");

                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivityForResult(intent, MY_REQUEST);
                        }

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


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // do your
                    Toast.makeText(MainActivity.this, "GET_ACCOUNTS ALLOWED!!!",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "GET_ACCOUNTS Denied",
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
